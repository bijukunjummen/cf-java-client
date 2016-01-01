/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry;

import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.spring.SpringCloudFoundryClient;
import org.cloudfoundry.client.spring.SpringLoggregatorClient;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.DeleteOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.spaces.CreateSpaceRequest;
import org.cloudfoundry.client.v2.spaces.DeleteSpaceRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.CloudFoundryOperationsBuilder;
import org.cloudfoundry.operations.v2.Paginated;
import org.cloudfoundry.operations.v2.Resources;
import org.cloudfoundry.utils.test.FailingDeserializationProblemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import reactor.core.error.Exceptions;
import reactor.rx.Stream;
import reactor.rx.Streams;

import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;

@Configuration
@EnableAutoConfiguration
public class IntegrationTestConfiguration {

    private final Logger logger = LoggerFactory.getLogger("test");

    @Bean
    SpringCloudFoundryClient cloudFoundryClient(@Value("${test.host}") String host,
                                                @Value("${test.username}") String username,
                                                @Value("${test.password}") String password,
                                                @Value("${test.skipSslValidation:false}") Boolean skipSslValidation,
                                                List<DeserializationProblemHandler> deserializationProblemHandlers) {

        return SpringCloudFoundryClient.builder()
                .host(host)
                .username(username)
                .password(password)
                .skipSslValidation(skipSslValidation)
                .deserializationProblemHandlers(deserializationProblemHandlers)
                .build();
    }

    @Bean
    @DependsOn({"organizationId", "spaceId"})
    CloudFoundryOperations cloudFoundryOperations(CloudFoundryClient cloudFoundryClient,
                                                  @Value("${test.organization}") String organization,
                                                  @Value("${test.space}") String space) {
        return new CloudFoundryOperationsBuilder()
                .cloudFoundryClient(cloudFoundryClient)
                .target(organization, space)
                .build();
    }

    @Bean
    FailingDeserializationProblemHandler failingDeserializationProblemHandler() {
        return new FailingDeserializationProblemHandler();
    }

    @Bean
    SpringLoggregatorClient loggregatorClient(SpringCloudFoundryClient cloudFoundryClient) {
        return SpringLoggregatorClient.builder()
                .cloudFoundryClient(cloudFoundryClient)
                .build();
    }

    @Bean
    Stream<String> organizationId(CloudFoundryClient cloudFoundryClient, @Value("${test.organization}") String organization) throws InterruptedException {
        Streams.await(deleteOrganizations(cloudFoundryClient), 5, SECONDS); // TODO: Link together instead of doing await()

        CreateOrganizationRequest request = CreateOrganizationRequest.builder()
                .name(organization)
                .build();

        Stream<String> organizationId = Streams
                .wrap(cloudFoundryClient.organizations().create(request))
                .map(Resources::getId)
                .observeStart(s -> this.logger.info(">> ORGANIZATION <<"))
                .observeComplete(v -> this.logger.info("<< ORGANIZATION >>"))
                .cache(1);

        organizationId.next().poll();
        return organizationId;
    }

    @Bean
    Stream<String> spaceId(CloudFoundryClient cloudFoundryClient, Stream<String> organizationId, @Value("${test.space}") String space) throws InterruptedException {
        Stream<String> spaceId = organizationId
                .flatMap(orgId -> {
                    CreateSpaceRequest request = CreateSpaceRequest.builder()
                            .name(space)
                            .organizationId(orgId)
                            .build();

                    return cloudFoundryClient.spaces().create(request);
                })
                .map(Resources::getId)
                .observeStart(s -> this.logger.info(">> SPACE <<"))
                .observeComplete(v -> this.logger.info("<< SPACE >>"))
                .cache(1);

        spaceId.next().poll();
        return spaceId;
    }

    private static Stream<Void> deleteOrganizations(CloudFoundryClient cloudFoundryClient) {
        return Paginated
                .requestResources(page -> {
                    ListOrganizationsRequest request = ListOrganizationsRequest.builder()
                            .page(page)
                            .build();

                    return cloudFoundryClient.organizations().list(request);
                })
                .map(Resources::getId)
                .flatMap(organizationId -> {
                    try {
                        Streams.await(deleteSpaces(cloudFoundryClient, organizationId), 5, SECONDS); // TODO: Link together instead of doing await()
                    } catch (InterruptedException e) {
                        Exceptions.throwIfFatal(e);
                        e.printStackTrace();
                    }

                    DeleteOrganizationRequest request = DeleteOrganizationRequest.builder()
                            .id(organizationId)
                            .build();

                    return cloudFoundryClient.organizations().delete(request);
                });
    }

    private static Stream<Void> deleteSpaces(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return Paginated
                .requestResources(page -> {
                    ListSpacesRequest request = ListSpacesRequest.builder()
                            .organizationId(organizationId)
                            .build();

                    return cloudFoundryClient.spaces().list(request);
                })
                .map(Resources::getId)
                .flatMap(spaceId -> {
                    DeleteSpaceRequest request = DeleteSpaceRequest.builder()
                            .id(spaceId)
                            .build();

                    return cloudFoundryClient.spaces().delete(request);
                });
    }

}
