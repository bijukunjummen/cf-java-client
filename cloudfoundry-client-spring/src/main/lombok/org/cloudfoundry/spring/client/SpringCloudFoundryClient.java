/*
 * Copyright 2013-2016 the original author or authors.
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

package org.cloudfoundry.spring.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.ApplicationsV2;
import org.cloudfoundry.client.v2.applicationusageevents.ApplicationUsageEvents;
import org.cloudfoundry.client.v2.buildpacks.Buildpacks;
import org.cloudfoundry.client.v2.domains.Domains;
import org.cloudfoundry.client.v2.environmentvariablegroups.EnvironmentVariableGroups;
import org.cloudfoundry.client.v2.events.Events;
import org.cloudfoundry.client.v2.featureflags.FeatureFlags;
import org.cloudfoundry.client.v2.info.Info;
import org.cloudfoundry.client.v2.jobs.Jobs;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitions;
import org.cloudfoundry.client.v2.organizations.Organizations;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomains;
import org.cloudfoundry.client.v2.routemappings.RouteMappings;
import org.cloudfoundry.client.v2.routes.Routes;
import org.cloudfoundry.client.v2.securitygroups.SecurityGroups;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingsV2;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokers;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstances;
import org.cloudfoundry.client.v2.servicekeys.ServiceKeys;
import org.cloudfoundry.client.v2.serviceplans.ServicePlans;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ServicePlanVisibilities;
import org.cloudfoundry.client.v2.services.Services;
import org.cloudfoundry.client.v2.serviceusageevents.ServiceUsageEvents;
import org.cloudfoundry.client.v2.shareddomains.SharedDomains;
import org.cloudfoundry.client.v2.spacequotadefinitions.SpaceQuotaDefinitions;
import org.cloudfoundry.client.v2.spaces.Spaces;
import org.cloudfoundry.client.v2.stacks.Stacks;
import org.cloudfoundry.client.v2.userprovidedserviceinstances.UserProvidedServiceInstances;
import org.cloudfoundry.client.v2.users.Users;
import org.cloudfoundry.client.v3.applications.ApplicationsV3;
import org.cloudfoundry.client.v3.droplets.Droplets;
import org.cloudfoundry.client.v3.packages.Packages;
import org.cloudfoundry.client.v3.processes.Processes;
import org.cloudfoundry.client.v3.servicebindings.ServiceBindingsV3;
import org.cloudfoundry.client.v3.tasks.Tasks;
import org.cloudfoundry.reactor.client.v2.applications.ReactorApplicationsV2;
import org.cloudfoundry.reactor.client.v2.applicationusageevents.ReactorApplicationUsageEvents;
import org.cloudfoundry.reactor.client.v2.buildpacks.ReactorBuildpacks;
import org.cloudfoundry.reactor.client.v2.domains.ReactorDomains;
import org.cloudfoundry.reactor.client.v2.environmentvariablegroups.ReactorEnvironmentVariableGroups;
import org.cloudfoundry.reactor.client.v2.events.ReactorEvents;
import org.cloudfoundry.reactor.client.v2.featureflags.ReactorFeatureFlags;
import org.cloudfoundry.reactor.client.v2.info.ReactorInfo;
import org.cloudfoundry.reactor.client.v2.jobs.ReactorJobs;
import org.cloudfoundry.reactor.client.v2.organizationquotadefinitions.ReactorOrganizationQuotaDefinitions;
import org.cloudfoundry.reactor.client.v2.organizations.ReactorOrganizations;
import org.cloudfoundry.reactor.client.v2.privatedomains.ReactorPrivateDomains;
import org.cloudfoundry.reactor.client.v2.routemappings.ReactorRouteMappings;
import org.cloudfoundry.reactor.client.v2.routes.ReactorRoutes;
import org.cloudfoundry.reactor.client.v2.securitygroups.ReactorSecurityGroups;
import org.cloudfoundry.reactor.client.v2.servicebindings.ReactorServiceBindingsV2;
import org.cloudfoundry.reactor.client.v2.servicebrokers.ReactorServiceBrokers;
import org.cloudfoundry.reactor.client.v2.serviceinstances.ReactorServiceInstances;
import org.cloudfoundry.reactor.client.v2.servicekeys.ReactorServiceKeys;
import org.cloudfoundry.reactor.client.v2.serviceplans.ReactorServicePlans;
import org.cloudfoundry.reactor.client.v2.serviceplanvisibilities.ReactorServicePlanVisibilities;
import org.cloudfoundry.reactor.client.v2.services.ReactorServices;
import org.cloudfoundry.reactor.client.v2.serviceusageevents.ReactorServiceUsageEvents;
import org.cloudfoundry.reactor.client.v2.shareddomains.ReactorSharedDomains;
import org.cloudfoundry.reactor.client.v2.spacequotadefinitions.ReactorSpaceQuotaDefinitions;
import org.cloudfoundry.reactor.client.v2.spaces.ReactorSpaces;
import org.cloudfoundry.reactor.client.v2.stacks.ReactorStacks;
import org.cloudfoundry.reactor.client.v2.userprovidedserviceinstances.ReactorUserProvidedServiceInstances;
import org.cloudfoundry.reactor.client.v2.users.ReactorUsers;
import org.cloudfoundry.reactor.client.v3.applications.ReactorApplicationsV3;
import org.cloudfoundry.reactor.client.v3.droplets.ReactorDroplets;
import org.cloudfoundry.reactor.client.v3.packages.ReactorPackages;
import org.cloudfoundry.reactor.client.v3.processes.ReactorProcesses;
import org.cloudfoundry.reactor.client.v3.servicebindings.ReactorServiceBindingsV3;
import org.cloudfoundry.reactor.client.v3.tasks.ReactorTasks;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import org.cloudfoundry.reactor.util.ConnectionContextSupplier;
import org.cloudfoundry.reactor.util.DefaultConnectionContext;
import org.cloudfoundry.spring.util.CloudFoundryClientCompatibilityChecker;
import org.cloudfoundry.spring.util.network.ConnectionContext;
import org.cloudfoundry.spring.util.network.ConnectionContextFactory;
import org.cloudfoundry.spring.util.network.FallbackHttpMessageConverter;
import org.cloudfoundry.spring.util.network.OAuth2RestOperationsOAuth2TokenProvider;
import org.cloudfoundry.spring.util.network.OAuth2RestTemplateBuilder;
import org.cloudfoundry.spring.util.network.OAuth2TokenProvider;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpClient;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * The Spring-based implementation of {@link CloudFoundryClient}
 */
public final class SpringCloudFoundryClient implements CloudFoundryClient, ConnectionContextSupplier {

    private final ApplicationUsageEvents applicationUsageEvents;

    private final ApplicationsV2 applicationsV2;

    private final ApplicationsV3 applicationsV3;

    private final Buildpacks buildpacks;

    private final org.cloudfoundry.reactor.util.ConnectionContext connectionContext;

    private final Domains domains;

    private final Droplets droplets;

    private final EnvironmentVariableGroups environmentVariableGroups;

    private final Events events;

    private final FeatureFlags featureFlags;

    private final Info info;

    private final Jobs jobs;

    private final OrganizationQuotaDefinitions organizationQuotaDefinitions;

    private final Organizations organizations;

    private final Packages packages;

    private final PrivateDomains privateDomains;

    private final Processes processes;

    private final RouteMappings routeMappings;

    private final Routes routes;

    private final SecurityGroups securityGroups;

    private final ServiceBindingsV2 serviceBindingsV2;

    private final ServiceBindingsV3 serviceBindingsV3;

    private final ServiceBrokers serviceBrokers;

    private final ServiceInstances serviceInstances;

    private final ServiceKeys serviceKeys;

    private final ServicePlanVisibilities servicePlanVisibilities;

    private final ServicePlans servicePlans;

    private final ServiceUsageEvents serviceUsageEvents;

    private final Services services;

    private final SharedDomains sharedDomains;

    private final SpaceQuotaDefinitions spaceQuotaDefinitions;

    private final Spaces spaces;

    private final Stacks stacks;

    private final Tasks tasks;

    private final OAuth2TokenProvider tokenProvider;

    private final UserProvidedServiceInstances userProvidedServiceInstances;

    private final Users users;

    @Builder
    SpringCloudFoundryClient(@NonNull String host,
                             Integer port,
                             Boolean skipSslValidation,
                             String clientId,
                             String clientSecret,
                             String proxyHost,
                             String proxyPassword,
                             Integer proxyPort,
                             String proxyUsername,
                             @NonNull String username,
                             @NonNull String password,
                             @Singular List<DeserializationProblemHandler> problemHandlers) {

        this(getConnectionContext(host, port, skipSslValidation, clientId, clientSecret, username, password), host, port, proxyHost, proxyPassword, proxyPort, proxyUsername, skipSslValidation,
            problemHandlers);
        new CloudFoundryClientCompatibilityChecker(this.info).check();
    }

    SpringCloudFoundryClient(String host, Integer port, String proxyHost, String proxyPassword, Integer proxyPort, String proxyUsername, Boolean skipSslValidation,
                             OAuth2TokenProvider tokenProvider, List<DeserializationProblemHandler> problemHandlers) {

        this.tokenProvider = tokenProvider;

        ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(NON_NULL)
            .disable(FAIL_ON_UNKNOWN_PROPERTIES);
        problemHandlers.forEach(objectMapper::addHandler);

        this.connectionContext = DefaultConnectionContext.builder()
            .authorizationProvider(outbound -> tokenProvider.getToken()
                .map(token -> String.format("bearer %s", token))
                .map(token -> outbound.addHeader("Authorization", token)))
            .host(host)
            .objectMapper(objectMapper)
            .port(port)
            .proxyHost(proxyHost)
            .proxyPassword(proxyPassword)
            .proxyPort(proxyPort)
            .proxyUsername(proxyUsername)
            .trustCertificates(skipSslValidation)
            .build();

        AuthorizationProvider authorizationProvider = this.connectionContext.getAuthorizationProvider();
        HttpClient httpClient = this.connectionContext.getHttpClient();
        Mono<String> root2 = this.connectionContext.getRoot();  // TODO: Change name once Spring is gone

        this.applicationsV2 = new ReactorApplicationsV2(authorizationProvider, httpClient, objectMapper, root2);
        this.applicationsV3 = new ReactorApplicationsV3(authorizationProvider, httpClient, objectMapper, root2);
        this.applicationUsageEvents = new ReactorApplicationUsageEvents(authorizationProvider, httpClient, objectMapper, root2);
        this.buildpacks = new ReactorBuildpacks(authorizationProvider, httpClient, objectMapper, root2);
        this.domains = new ReactorDomains(authorizationProvider, httpClient, objectMapper, root2);
        this.droplets = new ReactorDroplets(authorizationProvider, httpClient, objectMapper, root2);
        this.environmentVariableGroups = new ReactorEnvironmentVariableGroups(authorizationProvider, httpClient, objectMapper, root2);
        this.featureFlags = new ReactorFeatureFlags(authorizationProvider, httpClient, objectMapper, root2);
        this.events = new ReactorEvents(authorizationProvider, httpClient, objectMapper, root2);
        this.info = new ReactorInfo(authorizationProvider, httpClient, objectMapper, root2);
        this.jobs = new ReactorJobs(authorizationProvider, httpClient, objectMapper, root2);
        this.organizationQuotaDefinitions = new ReactorOrganizationQuotaDefinitions(authorizationProvider, httpClient, objectMapper, root2);
        this.organizations = new ReactorOrganizations(authorizationProvider, httpClient, objectMapper, root2);
        this.packages = new ReactorPackages(authorizationProvider, httpClient, objectMapper, root2);
        this.privateDomains = new ReactorPrivateDomains(authorizationProvider, httpClient, objectMapper, root2);
        this.processes = new ReactorProcesses(authorizationProvider, httpClient, objectMapper, root2);
        this.routeMappings = new ReactorRouteMappings(authorizationProvider, httpClient, objectMapper, root2);
        this.routes = new ReactorRoutes(authorizationProvider, httpClient, objectMapper, root2);
        this.securityGroups = new ReactorSecurityGroups(authorizationProvider, httpClient, objectMapper, root2);
        this.serviceBindingsV2 = new ReactorServiceBindingsV2(authorizationProvider, httpClient, objectMapper, root2);
        this.serviceBindingsV3 = new ReactorServiceBindingsV3(authorizationProvider, httpClient, objectMapper, root2);
        this.serviceBrokers = new ReactorServiceBrokers(authorizationProvider, httpClient, objectMapper, root2);
        this.serviceInstances = new ReactorServiceInstances(authorizationProvider, httpClient, objectMapper, root2);
        this.serviceKeys = new ReactorServiceKeys(authorizationProvider, httpClient, objectMapper, root2);
        this.servicePlans = new ReactorServicePlans(authorizationProvider, httpClient, objectMapper, root2);
        this.servicePlanVisibilities = new ReactorServicePlanVisibilities(authorizationProvider, httpClient, objectMapper, root2);
        this.services = new ReactorServices(authorizationProvider, httpClient, objectMapper, root2);
        this.serviceUsageEvents = new ReactorServiceUsageEvents(authorizationProvider, httpClient, objectMapper, root2);
        this.sharedDomains = new ReactorSharedDomains(authorizationProvider, httpClient, objectMapper, root2);
        this.spaceQuotaDefinitions = new ReactorSpaceQuotaDefinitions(authorizationProvider, httpClient, objectMapper, root2);
        this.spaces = new ReactorSpaces(authorizationProvider, httpClient, objectMapper, root2);
        this.stacks = new ReactorStacks(authorizationProvider, httpClient, objectMapper, root2);
        this.tasks = new ReactorTasks(authorizationProvider, httpClient, objectMapper, root2);
        this.users = new ReactorUsers(authorizationProvider, httpClient, objectMapper, root2);
        this.userProvidedServiceInstances = new ReactorUserProvidedServiceInstances(authorizationProvider, httpClient, objectMapper, root2);
    }

    // Let's take a moment to reflect on the fact that this bridge constructor is needed to counter a useless compiler constraint
    private SpringCloudFoundryClient(ConnectionContext connectionContext, String host, Integer port, String proxyHost, String proxyPassword, Integer proxyPort, String proxyUsername,
                                     Boolean skipSslValidation, List<DeserializationProblemHandler> problemHandlers) {
        this(host, port, proxyHost, proxyPassword, proxyPort, proxyUsername, skipSslValidation, getRestOperations(connectionContext, problemHandlers), problemHandlers);
    }

    // Let's take a moment to reflect on the fact that this bridge constructor is needed to counter a useless compiler constraint
    private SpringCloudFoundryClient(String host, Integer port, String proxyHost, String proxyPassword, Integer proxyPort, String proxyUsername, Boolean skipSslValidation,
                                     OAuth2RestOperations restOperations, List<DeserializationProblemHandler> problemHandlers) {
        this(host, port, proxyHost, proxyPassword, proxyPort, proxyUsername, skipSslValidation, new OAuth2RestOperationsOAuth2TokenProvider(restOperations), problemHandlers);
    }

    @Override
    public ApplicationUsageEvents applicationUsageEvents() {
        return this.applicationUsageEvents;
    }

    @Override
    public ApplicationsV2 applicationsV2() {
        return this.applicationsV2;
    }

    @Override
    public ApplicationsV3 applicationsV3() {
        return this.applicationsV3;
    }

    @Override
    public Buildpacks buildpacks() {
        return this.buildpacks;
    }

    @Override
    public Domains domains() {
        return this.domains;
    }

    @Override
    public Droplets droplets() {
        return this.droplets;
    }

    @Override
    public EnvironmentVariableGroups environmentVariableGroups() {
        return this.environmentVariableGroups;
    }

    @Override
    public Events events() {
        return this.events;
    }

    @Override
    public FeatureFlags featureFlags() {
        return this.featureFlags;
    }

    @Override
    public Mono<String> getAccessToken() {
        return this.tokenProvider.getToken();
    }

    @Override
    public org.cloudfoundry.reactor.util.ConnectionContext getConnectionContext() {
        return this.connectionContext;
    }

    @Override
    public Info info() {
        return this.info;
    }

    @Override
    public Jobs jobs() {
        return this.jobs;
    }

    @Override
    public OrganizationQuotaDefinitions organizationQuotaDefinitions() {
        return this.organizationQuotaDefinitions;
    }

    @Override
    public Organizations organizations() {
        return this.organizations;
    }

    @Override
    public Packages packages() {
        return this.packages;
    }

    @Override
    public PrivateDomains privateDomains() {
        return this.privateDomains;
    }

    @Override
    public Processes processes() {
        return this.processes;
    }

    @Override
    public RouteMappings routeMappings() {
        return this.routeMappings;
    }

    @Override
    public Routes routes() {
        return this.routes;
    }

    @Override
    public SecurityGroups securityGroups() {
        return this.securityGroups;
    }

    @Override
    public ServiceBindingsV2 serviceBindingsV2() {
        return this.serviceBindingsV2;
    }

    @Override
    public ServiceBindingsV3 serviceBindingsV3() {
        return this.serviceBindingsV3;
    }

    @Override
    public ServiceBrokers serviceBrokers() {
        return this.serviceBrokers;
    }

    @Override
    public ServiceInstances serviceInstances() {
        return this.serviceInstances;
    }

    @Override
    public ServiceKeys serviceKeys() {
        return this.serviceKeys;
    }

    @Override
    public ServicePlanVisibilities servicePlanVisibilities() {
        return this.servicePlanVisibilities;
    }

    @Override
    public ServicePlans servicePlans() {
        return this.servicePlans;
    }

    @Override
    public ServiceUsageEvents serviceUsageEvents() {
        return this.serviceUsageEvents;
    }

    @Override
    public Services services() {
        return this.services;
    }

    @Override
    public SharedDomains sharedDomains() {
        return this.sharedDomains;
    }

    @Override
    public SpaceQuotaDefinitions spaceQuotaDefinitions() {
        return this.spaceQuotaDefinitions;
    }

    @Override
    public Spaces spaces() {
        return this.spaces;
    }

    @Override
    public Stacks stacks() {
        return this.stacks;
    }

    @Override
    public Tasks tasks() {
        return this.tasks;
    }

    @Override
    public UserProvidedServiceInstances userProvidedServiceInstances() {
        return this.userProvidedServiceInstances;
    }

    @Override
    public Users users() {
        return this.users;
    }

    private static ConnectionContext getConnectionContext(String host, Integer port, Boolean skipSslValidation, String clientId, String clientSecret, String username, String password) {
        return new ConnectionContextFactory()
            .trustCertificates(skipSslValidation)
            .host(host)
            .port(port)
            .clientId(clientId)
            .clientSecret(clientSecret)
            .username(username)
            .password(password)
            .build();
    }

    private static OAuth2RestOperations getRestOperations(ConnectionContext connectionContext, List<DeserializationProblemHandler> problemHandlers) {
        return new OAuth2RestTemplateBuilder()
            .clientContext(connectionContext.getClientContext())
            .protectedResourceDetails(connectionContext.getProtectedResourceDetails())
            .hostnameVerifier(connectionContext.getHostnameVerifier())
            .sslContext(connectionContext.getSslContext())
            .messageConverter(new FallbackHttpMessageConverter())
            .problemHandlers(problemHandlers)
            .build();
    }

}
