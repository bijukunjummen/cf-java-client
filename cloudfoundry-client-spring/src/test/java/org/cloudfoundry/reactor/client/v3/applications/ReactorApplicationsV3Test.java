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

package org.cloudfoundry.reactor.client.v3.applications;

import org.cloudfoundry.client.v3.Id;
import org.cloudfoundry.client.v3.Lifecycle;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.PaginatedResponse;
import org.cloudfoundry.client.v3.applications.AssignApplicationDropletRequest;
import org.cloudfoundry.client.v3.applications.AssignApplicationDropletResponse;
import org.cloudfoundry.client.v3.applications.CancelApplicationTaskRequest;
import org.cloudfoundry.client.v3.applications.CancelApplicationTaskResponse;
import org.cloudfoundry.client.v3.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v3.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v3.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessStatisticsRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessStatisticsResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationTaskRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationTaskResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationDropletsRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationDropletsResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationPackagesRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationPackagesResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationProcessesRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationProcessesResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationTasksRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationTasksResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v3.applications.ScaleApplicationRequest;
import org.cloudfoundry.client.v3.applications.ScaleApplicationResponse;
import org.cloudfoundry.client.v3.applications.StartApplicationRequest;
import org.cloudfoundry.client.v3.applications.StartApplicationResponse;
import org.cloudfoundry.client.v3.applications.StopApplicationRequest;
import org.cloudfoundry.client.v3.applications.StopApplicationResponse;
import org.cloudfoundry.client.v3.applications.TerminateApplicationInstanceRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationResponse;
import org.cloudfoundry.client.v3.processes.AbstractProcessStatistics.PortMapping;
import org.cloudfoundry.client.v3.processes.HealthCheck;
import org.cloudfoundry.client.v3.processes.HealthCheck.Type;
import org.cloudfoundry.client.v3.processes.ProcessUsage;
import org.cloudfoundry.client.v3.tasks.Task;
import org.cloudfoundry.client.v3.tasks.TaskResource;
import org.cloudfoundry.reactor.InteractionContext;
import org.cloudfoundry.reactor.TestRequest;
import org.cloudfoundry.reactor.TestResponse;
import org.cloudfoundry.reactor.client.AbstractClientApiTest;
import org.cloudfoundry.util.StringMap;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static io.netty.handler.codec.http.HttpMethod.DELETE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.PATCH;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpMethod.PUT;
import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static io.netty.handler.codec.http.HttpResponseStatus.CREATED;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.cloudfoundry.client.v3.PaginatedResponse.Pagination;
import static org.cloudfoundry.client.v3.applications.ListApplicationPackagesResponse.Resource;

public final class ReactorApplicationsV3Test {

    public static final class AssignDroplet extends AbstractClientApiTest<AssignApplicationDropletRequest, AssignApplicationDropletResponse> {

        private final ReactorApplicationsV3 applications = new ReactorApplicationsV3(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v3/apps/test-application-id/current_droplet")
                    .payload("fixtures/client/v3/apps/PUT_{id}_current_droplet_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v3/apps/PUT_{id}_current_droplet_response.json")
                    .build())
                .build();
        }

        @Override
        protected AssignApplicationDropletRequest getInvalidRequest() {
            return AssignApplicationDropletRequest.builder()
                .build();
        }

        @Override
        protected AssignApplicationDropletResponse getResponse() {
            return AssignApplicationDropletResponse.builder()
                .id("guid-a08fd981-137f-4a8f-9c32-6be10007edde")
                .name("name1")
                .desiredState("STOPPED")
                .totalDesiredInstances(1)
                .createdAt("2016-01-26T22:20:35Z")
                .updatedAt("2016-01-26T22:20:35Z")
                .lifecycle(Lifecycle.builder()
                    .type("buildpack")
                    .data("buildpack", "name-2420")
                    .data("stack", "name-2421")
                    .build())
                .link("self", Link.builder()
                    .href("/v3/apps/guid-a08fd981-137f-4a8f-9c32-6be10007edde")
                    .build())
                .link("space", Link.builder()
                    .href("/v2/spaces/5e6004e5-b78e-46c3-9a8e-c97e656bbbda")
                    .build())
                .link("processes", Link.builder()
                    .href("/v3/apps/guid-a08fd981-137f-4a8f-9c32-6be10007edde/processes")
                    .build())
                .link("routes", Link.builder()
                    .href("/v3/apps/guid-a08fd981-137f-4a8f-9c32-6be10007edde/routes")
                    .build())
                .link("packages", Link.builder()
                    .href("/v3/apps/guid-a08fd981-137f-4a8f-9c32-6be10007edde/packages")
                    .build())
                .link("droplet", Link.builder()
                    .href("/v3/droplets/guid-3e946a46-3e4a-4572-9510-cb5556837da1")
                    .build())
                .link("droplets", Link.builder()
                    .href("/v3/apps/guid-a08fd981-137f-4a8f-9c32-6be10007edde/droplets")
                    .build())
                .link("start", Link.builder()
                    .href("/v3/apps/guid-a08fd981-137f-4a8f-9c32-6be10007edde/start")
                    .method("PUT")
                    .build())
                .link("stop", Link.builder()
                    .href("/v3/apps/guid-a08fd981-137f-4a8f-9c32-6be10007edde/stop")
                    .method("PUT")
                    .build())
                .link("assign_current_droplet", Link.builder()
                    .href("/v3/apps/guid-a08fd981-137f-4a8f-9c32-6be10007edde/current_droplet")
                    .method("PUT")
                    .build())
                .build();
        }

        @Override
        protected AssignApplicationDropletRequest getValidRequest() throws Exception {
            return AssignApplicationDropletRequest.builder()
                .dropletId("guid-3b5793e7-f6c8-40cb-a8d8-07080280da83")
                .applicationId("test-application-id")
                .build();
        }

        @Override
        protected Mono<AssignApplicationDropletResponse> invoke(AssignApplicationDropletRequest request) {
            return this.applications.assignDroplet(request);
        }

    }

    public static final class CancelTask extends AbstractClientApiTest<CancelApplicationTaskRequest, CancelApplicationTaskResponse> {

        private final ReactorApplicationsV3 applications = new ReactorApplicationsV3(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v3/apps/test-application-id/tasks/test-task-id/cancel")
                    .build())
                .response(TestResponse.builder()
                    .status(ACCEPTED)
                    .payload("fixtures/client/v3/apps/PUT_{id}_tasks_{id}_cancel_response.json")
                    .build())
                .build();
        }

        @Override
        protected CancelApplicationTaskRequest getInvalidRequest() {
            return CancelApplicationTaskRequest.builder()
                .build();
        }

        @Override
        protected CancelApplicationTaskResponse getResponse() {
            return CancelApplicationTaskResponse.builder()
                .id("d5cc22ec-99a3-4e6a-af91-a44b4ab7b6fa")
                .name("migrate")
                .command("rake db:migrate")
                .state(Task.CANCELING_STATE)
                .memoryInMb(512)
                .results(Collections.singletonMap("failure_reason", null))
                .link("self", Link.builder()
                    .href("/v3/tasks/d5cc22ec-99a3-4e6a-af91-a44b4ab7b6fa")
                    .build())
                .link("app", Link.builder()
                    .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                    .build())
                .link("droplet", Link.builder()
                    .href("/v3/droplets/740ebd2b-162b-469a-bd72-3edb96fabd9a")
                    .build())
                .build();
        }

        @Override
        protected CancelApplicationTaskRequest getValidRequest() throws Exception {
            return CancelApplicationTaskRequest.builder()
                .applicationId("test-application-id")
                .taskId("test-task-id")
                .build();
        }

        @Override
        protected Mono<CancelApplicationTaskResponse> invoke(CancelApplicationTaskRequest request) {
            return this.applications.cancelTask(request);
        }

    }

    public static final class Create extends AbstractClientApiTest<CreateApplicationRequest, CreateApplicationResponse> {

        private final ReactorApplicationsV3 applications = new ReactorApplicationsV3(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(POST).path("/v3/apps")
                    .payload("fixtures/client/v3/apps/POST_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(CREATED)
                    .payload("fixtures/client/v3/apps/POST_response.json")
                    .build())
                .build();
        }

        @Override
        protected CreateApplicationRequest getInvalidRequest() {
            return CreateApplicationRequest
                .builder().build();
        }

        @Override
        protected CreateApplicationResponse getResponse() {
            return CreateApplicationResponse.builder()
                .id("a1aef47a-600b-4b2b-a2f6-f5dc5344f886")
                .name("my_app")
                .desiredState("STOPPED")
                .totalDesiredInstances(0)
                .createdAt("2016-01-26T22:20:35Z")
                .lifecycle(Lifecycle.builder()
                    .type("buildpack")
                    .data("buildpack", "name-2443")
                    .data("stack", "default-stack-name")
                    .build())
                .environmentVariable("open", "source")
                .link("self", Link.builder()
                    .href("/v3/apps/a1aef47a-600b-4b2b-a2f6-f5dc5344f886")
                    .build())
                .link("space", Link.builder()
                    .href("/v2/spaces/48989e6d-bb23-480d-94da-dae7c20e7af3")
                    .build())
                .link("processes", Link.builder()
                    .href("/v3/apps/a1aef47a-600b-4b2b-a2f6-f5dc5344f886/processes")
                    .build())
                .link("routes", Link.builder()
                    .href("/v3/apps/a1aef47a-600b-4b2b-a2f6-f5dc5344f886/routes")
                    .build())
                .link("packages", Link.builder()
                    .href("/v3/apps/a1aef47a-600b-4b2b-a2f6-f5dc5344f886/packages")
                    .build())
                .link("droplets", Link.builder()
                    .href("/v3/apps/a1aef47a-600b-4b2b-a2f6-f5dc5344f886/droplets")
                    .build())
                .link("start", Link.builder()
                    .href("/v3/apps/a1aef47a-600b-4b2b-a2f6-f5dc5344f886/start")
                    .method("PUT")
                    .build())
                .link("stop", Link.builder()
                    .href("/v3/apps/a1aef47a-600b-4b2b-a2f6-f5dc5344f886/stop")
                    .method("PUT")
                    .build())
                .link("assign_current_droplet", Link.builder()
                    .href("/v3/apps/a1aef47a-600b-4b2b-a2f6-f5dc5344f886/current_droplet")
                    .method("PUT")
                    .build())
                .build();
        }

        @Override
        protected CreateApplicationRequest getValidRequest() throws Exception {
            return CreateApplicationRequest.builder()
                .name("my_app")
                .environmentVariable("open", "source")
                .lifecycle(Lifecycle.builder()
                    .type("buildpack")
                    .data("buildpack", "name-2443")
                    .build())
                .relationship("space", Id.of("48989e6d-bb23-480d-94da-dae7c20e7af3"))
                .build();
        }

        @Override
        protected Mono<CreateApplicationResponse> invoke(CreateApplicationRequest request) {
            return this.applications.create(request);
        }

    }

    public static final class Delete extends AbstractClientApiTest<DeleteApplicationRequest, Void> {

        private final ReactorApplicationsV3 applications = new ReactorApplicationsV3(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v3/apps/test-application-id")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected DeleteApplicationRequest getInvalidRequest() {
            return DeleteApplicationRequest.builder()
                .build();
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected DeleteApplicationRequest getValidRequest() throws Exception {
            return DeleteApplicationRequest.builder()
                .applicationId("test-application-id")
                .build();
        }

        @Override
        protected Mono<Void> invoke(DeleteApplicationRequest request) {
            return this.applications.delete(request);
        }

    }

    public static final class DeleteProcess extends AbstractClientApiTest<TerminateApplicationInstanceRequest, Void> {

        private final ReactorApplicationsV3 applications = new ReactorApplicationsV3(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(DELETE).path("/v3/apps/test-application-id/processes/test-type/instances/test-index")
                    .build())
                .response(TestResponse.builder()
                    .status(NO_CONTENT)
                    .build())
                .build();
        }

        @Override
        protected TerminateApplicationInstanceRequest getInvalidRequest() {
            return TerminateApplicationInstanceRequest.builder()
                .build();
        }

        @Override
        protected Void getResponse() {
            return null;
        }

        @Override
        protected TerminateApplicationInstanceRequest getValidRequest() throws Exception {
            return TerminateApplicationInstanceRequest.builder()
                .applicationId("test-application-id")
                .index("test-index")
                .type("test-type")
                .build();
        }

        @Override
        protected Mono<Void> invoke(TerminateApplicationInstanceRequest request) {
            return this.applications.terminateInstance(request);
        }

    }

    public static final class Get extends AbstractClientApiTest<GetApplicationRequest, GetApplicationResponse> {

        private final ReactorApplicationsV3 applications = new ReactorApplicationsV3(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v3/apps/test-application-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v3/apps/GET_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected GetApplicationRequest getInvalidRequest() {
            return GetApplicationRequest.builder()
                .build();
        }

        @Override
        protected GetApplicationResponse getResponse() {
            return GetApplicationResponse.builder()
                .id("guid-cad6a8b6-a0dc-4bb1-a5a8-095dd29cb6d6")
                .name("my_app")
                .desiredState("STOPPED")
                .totalDesiredInstances(3)
                .createdAt("2016-01-26T22:20:35Z")
                .lifecycle(Lifecycle.builder()
                    .type("buildpack")
                    .data("buildpack", "name-2431")
                    .data("stack", "name-2432")
                    .build())
                .environmentVariable("unicorn", "horn")
                .link("self", Link.builder()
                    .href("/v3/apps/guid-cad6a8b6-a0dc-4bb1-a5a8-095dd29cb6d6")
                    .build())
                .link("space", Link.builder()
                    .href("/v2/spaces/d6a87ffd-2655-4655-b5f1-e64627a419fc")
                    .build())
                .link("processes", Link.builder()
                    .href("/v3/apps/guid-cad6a8b6-a0dc-4bb1-a5a8-095dd29cb6d6/processes")
                    .build())
                .link("routes", Link.builder()
                    .href("/v3/apps/guid-cad6a8b6-a0dc-4bb1-a5a8-095dd29cb6d6/routes")
                    .build())
                .link("packages", Link.builder()
                    .href("/v3/apps/guid-cad6a8b6-a0dc-4bb1-a5a8-095dd29cb6d6/packages")
                    .build())
                .link("droplet", Link.builder()
                    .href("/v3/droplets/a-droplet-guid")
                    .build())
                .link("droplets", Link.builder()
                    .href("/v3/apps/guid-cad6a8b6-a0dc-4bb1-a5a8-095dd29cb6d6/droplets")
                    .build())
                .link("start", Link.builder()
                    .href("/v3/apps/guid-cad6a8b6-a0dc-4bb1-a5a8-095dd29cb6d6/start")
                    .method("PUT")
                    .build())
                .link("stop", Link.builder()
                    .href("/v3/apps/guid-cad6a8b6-a0dc-4bb1-a5a8-095dd29cb6d6/stop")
                    .method("PUT")
                    .build())
                .link("assign_current_droplet", Link.builder()
                    .href("/v3/apps/guid-cad6a8b6-a0dc-4bb1-a5a8-095dd29cb6d6/current_droplet")
                    .method("PUT")
                    .build())
                .build();
        }

        @Override
        protected GetApplicationRequest getValidRequest() throws Exception {
            return GetApplicationRequest.builder()
                .applicationId("test-application-id")
                .build();
        }

        @Override
        protected Mono<GetApplicationResponse> invoke(GetApplicationRequest request) {
            return this.applications.get(request);
        }

    }

    public static final class GetApplicationProcessStatistics extends AbstractClientApiTest<GetApplicationProcessStatisticsRequest, GetApplicationProcessStatisticsResponse> {

        private final ReactorApplicationsV3 applications = new ReactorApplicationsV3(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v3/apps/test-id/processes/test-type/stats")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v3/apps/GET_{id}_processes_{type}_stats_response.json")
                    .build())
                .build();
        }

        @Override
        protected GetApplicationProcessStatisticsRequest getInvalidRequest() {
            return GetApplicationProcessStatisticsRequest.builder()
                .build();
        }

        @Override
        protected GetApplicationProcessStatisticsResponse getResponse() {
            return GetApplicationProcessStatisticsResponse.builder()
                .resource(GetApplicationProcessStatisticsResponse.Resource.builder()
                    .type("web")
                    .index(0)
                    .state("RUNNING")
                    .usage(ProcessUsage.builder()
                        .time("2016-03-23T23:17:30.476314154Z")
                        .cpu(0.00038711029163348665)
                        .memory(19177472L)
                        .disk(69705728L)
                        .build())
                    .host("10.244.16.10")
                    .instancePort(PortMapping.builder()
                        .external(64546)
                        .internal(8080)
                        .build())
                    .uptime(9042L)
                    .memoryQuota(268435456L)
                    .diskQuota(1073741824L)
                    .fdsQuota(16384)
                    .build())
                .build();
        }

        @Override
        protected GetApplicationProcessStatisticsRequest getValidRequest() throws Exception {
            return GetApplicationProcessStatisticsRequest.builder()
                .applicationId("test-id")
                .type("test-type")
                .build();
        }

        @Override
        protected Mono<GetApplicationProcessStatisticsResponse> invoke(GetApplicationProcessStatisticsRequest request) {
            return this.applications.getProcessStatistics(request);
        }

    }

    public static final class GetEnvironment extends AbstractClientApiTest<GetApplicationEnvironmentRequest, GetApplicationEnvironmentResponse> {

        private final ReactorApplicationsV3 applications = new ReactorApplicationsV3(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v3/apps/test-application-id/env")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v3/apps/GET_{id}_env_response.json")
                    .build())
                .build();
        }

        @Override
        protected GetApplicationEnvironmentRequest getInvalidRequest() {
            return GetApplicationEnvironmentRequest.builder()
                .build();
        }

        @Override
        protected GetApplicationEnvironmentResponse getResponse() {
            return GetApplicationEnvironmentResponse.builder()
                .environmentVariable("SOME_KEY", "some_val")
                .stagingEnvironmentVariable("STAGING_ENV", "staging_value")
                .runningEnvironmentVariable("RUNNING_ENV", "running_value")
                .applicationEnvironmentVariable("VCAP_APPLICATION", StringMap.builder()
                    .entry("limits", StringMap.builder()
                        .entry("fds", 16384)
                        .build())
                    .entry("application_name", "app_name")
                    .entry("application_uris", Collections.emptyList())
                    .entry("name", "app_name")
                    .entry("space_name", "some_space")
                    .entry("space_id", "c595c2ee-df01-4769-a61f-df5bd5e4cbc1")
                    .entry("uris", Collections.emptyList())
                    .entry("users", null)
                    .build())
                .build();
        }

        @Override
        protected GetApplicationEnvironmentRequest getValidRequest() throws Exception {
            return GetApplicationEnvironmentRequest.builder()
                .applicationId("test-application-id")
                .build();
        }

        @Override
        protected Mono<GetApplicationEnvironmentResponse> invoke(GetApplicationEnvironmentRequest request) {
            return this.applications.getEnvironment(request);
        }

    }

    public static final class GetProcess extends AbstractClientApiTest<GetApplicationProcessRequest, GetApplicationProcessResponse> {

        private final ReactorApplicationsV3 applications = new ReactorApplicationsV3(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v3/apps/test-application-id/processes/web")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v3/apps/GET_{id}_processes_{type}_response.json")
                    .build())
                .build();
        }

        @Override
        protected GetApplicationProcessRequest getInvalidRequest() {
            return GetApplicationProcessRequest.builder()
                .build();
        }

        @Override
        protected GetApplicationProcessResponse getResponse() {
            return GetApplicationProcessResponse.builder()
                .id("6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                .type("web")
                .command("rackup")
                .instances(5)
                .memoryInMb(256)
                .diskInMb(1_024)
                .port(8080)
                .healthCheck(HealthCheck.builder()
                    .type(Type.PORT)
                    .data("timeout", null)
                    .build())
                .createdAt("2016-03-23T18:48:22Z")
                .updatedAt("2016-03-23T18:48:42Z")
                .link("self", Link.builder()
                    .href("/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                    .build())
                .link("scale", Link.builder()
                    .href("/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82/scale")
                    .method("PUT")
                    .build())
                .link("app", Link.builder()
                    .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                    .build())
                .link("space", Link.builder()
                    .href("/v2/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                    .build())
                .build();
        }

        @Override
        protected GetApplicationProcessRequest getValidRequest() throws Exception {
            return GetApplicationProcessRequest.builder()
                .applicationId("test-application-id")
                .type("web")
                .build();
        }

        @Override
        protected Mono<GetApplicationProcessResponse> invoke(GetApplicationProcessRequest request) {
            return this.applications.getProcess(request);
        }

    }

    public static final class GetTask extends AbstractClientApiTest<GetApplicationTaskRequest, GetApplicationTaskResponse> {

        private final ReactorApplicationsV3 applications = new ReactorApplicationsV3(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v3/apps/test-application-id/tasks/test-task-id")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v3/apps/GET_{id}_tasks_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected GetApplicationTaskRequest getInvalidRequest() {
            return GetApplicationTaskRequest.builder()
                .build();
        }

        @Override
        protected GetApplicationTaskResponse getResponse() {
            return GetApplicationTaskResponse.builder()
                .id("d5cc22ec-99a3-4e6a-af91-a44b4ab7b6fa")
                .name("migrate")
                .command("rake db:migrate")
                .state(Task.RUNNING_STATE)
                .memoryInMb(512)
                .results(Collections.singletonMap("failure_reason", null))
                .link("self", Link.builder()
                    .href("/v3/tasks/d5cc22ec-99a3-4e6a-af91-a44b4ab7b6fa")
                    .build())
                .link("app", Link.builder()
                    .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                    .build())
                .link("droplet", Link.builder()
                    .href("/v3/droplets/740ebd2b-162b-469a-bd72-3edb96fabd9a")
                    .build())
                .build();
        }

        @Override
        protected GetApplicationTaskRequest getValidRequest() throws Exception {
            return GetApplicationTaskRequest.builder()
                .applicationId("test-application-id")
                .taskId("test-task-id")
                .build();
        }

        @Override
        protected Mono<GetApplicationTaskResponse> invoke(GetApplicationTaskRequest request) {
            return this.applications.getTask(request);
        }

    }

    public static final class List extends AbstractClientApiTest<ListApplicationsRequest, ListApplicationsResponse> {

        private final ReactorApplicationsV3 applications = new ReactorApplicationsV3(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v3/apps?names=test-name&order_by=%2Bcreated_at&page=1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v3/apps/GET_response.json")
                    .build())
                .build();
        }

        @Override
        protected ListApplicationsRequest getInvalidRequest() {
            return ListApplicationsRequest.builder()
                .page(-1)
                .build();
        }

        @Override
        protected ListApplicationsResponse getResponse() {
            return ListApplicationsResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(3)
                    .first(Link.builder()
                        .href("/v3/apps?order_by=-created_at&page=1&per_page=2")
                        .build())
                    .last(Link.builder()
                        .href("/v3/apps?order_by=-created_at&page=2&per_page=2")
                        .build())
                    .next(Link.builder()
                        .href("/v3/apps?order_by=-created_at&page=2&per_page=2")
                        .build())
                    .build())
                .resource(ListApplicationsResponse.Resource.builder()
                    .id("guid-fde0d401-0615-4ebf-9585-57ab0fe0d2fa")
                    .name("my_app3")
                    .desiredState("STOPPED")
                    .totalDesiredInstances(0)
                    .createdAt("1970-01-01T00:00:03Z")
                    .lifecycle(Lifecycle.builder()
                        .type("buildpack")
                        .data("buildpack", "name-2374")
                        .data("stack", "name-2375")
                        .build())
                    .environmentVariable("magic", "beautiful")
                    .link("self", Link.builder()
                        .href("/v3/apps/guid-fde0d401-0615-4ebf-9585-57ab0fe0d2fa")
                        .build())
                    .link("space", Link.builder()
                        .href("/v2/spaces/801a008f-dfda-464f-88ed-b9abd4bf3b1b")
                        .build())
                    .link("processes", Link.builder()
                        .href("/v3/apps/guid-fde0d401-0615-4ebf-9585-57ab0fe0d2fa/processes")
                        .build())
                    .link("routes", Link.builder()
                        .href("/v3/apps/guid-fde0d401-0615-4ebf-9585-57ab0fe0d2fa/routes")
                        .build())
                    .link("packages", Link.builder()
                        .href("/v3/apps/guid-fde0d401-0615-4ebf-9585-57ab0fe0d2fa/packages")
                        .build())
                    .link("droplets", Link.builder()
                        .href("/v3/apps/guid-fde0d401-0615-4ebf-9585-57ab0fe0d2fa/droplets")
                        .build())
                    .link("start", Link.builder()
                        .href("/v3/apps/guid-fde0d401-0615-4ebf-9585-57ab0fe0d2fa/start")
                        .method("PUT")
                        .build())
                    .link("stop", Link.builder()
                        .href("/v3/apps/guid-fde0d401-0615-4ebf-9585-57ab0fe0d2fa/stop")
                        .method("PUT")
                        .build())
                    .link("assign_current_droplet", Link.builder()
                        .href("/v3/apps/guid-fde0d401-0615-4ebf-9585-57ab0fe0d2fa/current_droplet")
                        .method("PUT")
                        .build())
                    .build())
                .resource(ListApplicationsResponse.Resource.builder()
                    .id("guid-5b9fc319-1483-40f4-b868-18240a6c6e5f")
                    .name("my_app2")
                    .desiredState("STOPPED")
                    .totalDesiredInstances(0)
                    .createdAt("1970-01-01T00:00:02Z")
                    .lifecycle(Lifecycle.builder()
                        .type("buildpack")
                        .data("buildpack", "name-2372")
                        .data("stack", "name-2373")
                        .build())
                    .link("self", Link.builder()
                        .href("/v3/apps/guid-5b9fc319-1483-40f4-b868-18240a6c6e5f")
                        .build())
                    .link("space", Link.builder()
                        .href("/v2/spaces/801a008f-dfda-464f-88ed-b9abd4bf3b1b")
                        .build())
                    .link("processes", Link.builder()
                        .href("/v3/apps/guid-5b9fc319-1483-40f4-b868-18240a6c6e5f/processes")
                        .build())
                    .link("routes", Link.builder()
                        .href("/v3/apps/guid-5b9fc319-1483-40f4-b868-18240a6c6e5f/routes")
                        .build())
                    .link("packages", Link.builder()
                        .href("/v3/apps/guid-5b9fc319-1483-40f4-b868-18240a6c6e5f/packages")
                        .build())
                    .link("droplets", Link.builder()
                        .href("/v3/apps/guid-5b9fc319-1483-40f4-b868-18240a6c6e5f/droplets")
                        .build())
                    .link("start", Link.builder()
                        .href("/v3/apps/guid-5b9fc319-1483-40f4-b868-18240a6c6e5f/start")
                        .method("PUT")
                        .build())
                    .link("stop", Link.builder()
                        .href("/v3/apps/guid-5b9fc319-1483-40f4-b868-18240a6c6e5f/stop")
                        .method("PUT")
                        .build())
                    .link("assign_current_droplet", Link.builder()
                        .href("/v3/apps/guid-5b9fc319-1483-40f4-b868-18240a6c6e5f/current_droplet")
                        .method("PUT")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListApplicationsRequest getValidRequest() throws Exception {
            return ListApplicationsRequest.builder()
                .page(1)
                .orderBy("+created_at")
                .name("test-name")
                .build();
        }

        @Override
        protected Mono<ListApplicationsResponse> invoke(ListApplicationsRequest request) {
            return this.applications.list(request);
        }

    }

    public static final class ListDroplets extends AbstractClientApiTest<ListApplicationDropletsRequest, ListApplicationDropletsResponse> {

        private final ReactorApplicationsV3 applications = new ReactorApplicationsV3(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v3/apps/test-application-id/droplets?order_by=-created_at&page=1&per_page=2")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v3/apps/GET_{id}_droplets_response.json")
                    .build())
                .build();
        }

        @Override
        protected ListApplicationDropletsRequest getInvalidRequest() {
            return ListApplicationDropletsRequest.builder()
                .build();
        }

        @Override
        protected ListApplicationDropletsResponse getResponse() {
            return ListApplicationDropletsResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(2)
                    .first(Link.builder()
                        .href("/v3/apps/guid-7098082f-0843-4a44-a224-da72d8626349/droplets?order_by=-created_at&page=1&per_page=2")
                        .build())
                    .last(Link.builder()
                        .href("/v3/apps/guid-7098082f-0843-4a44-a224-da72d8626349/droplets?order_by=-created_at&page=1&per_page=2")
                        .build())
                    .build())
                .resource(ListApplicationDropletsResponse.Resource.builder()
                    .id("guid-03f27a7a-9121-4dd4-833b-4a36eabfc801")
                    .state("STAGED")
                    .error(null)
                    .lifecycle(Lifecycle.builder()
                        .type("buildpack")
                        .data("buildpack", "name-2303")
                        .data("stack", "name-2304")
                        .build())
                    .result("execution_metadata", null)
                    .result("process_types", "{\"web\":\"started\"}")
                    .result("hash", StringMap.builder()
                        .entry("type", "sha1")
                        .entry("value", "my-hash")
                        .build())
                    .result("buildpack", "https://github.com/cloudfoundry/my-buildpack.git")
                    .result("stack", null)
                    .createdAt("1970-01-01T00:00:02Z")
                    .updatedAt("2016-01-26T22:20:33Z")
                    .link("self", Link.builder()
                        .href("/v3/droplets/guid-03f27a7a-9121-4dd4-833b-4a36eabfc801")
                        .build())
                    .link("package", Link.builder()
                        .href("/v3/packages/guid-a4c16d72-13e7-4578-a2be-1684b4c484d9")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/guid-7098082f-0843-4a44-a224-da72d8626349")
                        .build())
                    .link("assign_current_droplet", Link.builder()
                        .href("/v3/apps/guid-7098082f-0843-4a44-a224-da72d8626349/current_droplet")
                        .method("PUT")
                        .build())
                    .build())
                .resource(ListApplicationDropletsResponse.Resource.builder()
                    .id("guid-87d3fe05-1d32-4063-99c5-cdfbf2f66eae")
                    .state("STAGING")
                    .lifecycle(Lifecycle.builder()
                        .type("buildpack")
                        .data("buildpack", "name-2301")
                        .data("stack", "name-2302")
                        .build())
                    .environmentVariable("yuu", "huuu")
                    .createdAt("1970-01-01T00:00:01Z")
                    .updatedAt("2016-01-26T22:20:33Z")
                    .link("self", Link.builder()
                        .href("/v3/droplets/guid-87d3fe05-1d32-4063-99c5-cdfbf2f66eae")
                        .build())
                    .link("package", Link.builder()
                        .href("/v3/packages/guid-a4c16d72-13e7-4578-a2be-1684b4c484d9")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/guid-7098082f-0843-4a44-a224-da72d8626349")
                        .build())
                    .link("assign_current_droplet", Link.builder()
                        .href("/v3/apps/guid-7098082f-0843-4a44-a224-da72d8626349/current_droplet")
                        .method("PUT")
                        .build())
                    .link("buildpack", Link.builder()
                        .href("/v2/buildpacks/11f0ea63-2116-42ea-93e8-275ab6d912b6")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListApplicationDropletsRequest getValidRequest() throws Exception {
            return ListApplicationDropletsRequest.builder()
                .page(1)
                .perPage(2)
                .orderBy("-created_at")
                .applicationId("test-application-id")
                .build();
        }

        @Override
        protected Mono<ListApplicationDropletsResponse> invoke(ListApplicationDropletsRequest request) {
            return this.applications.listDroplets(request);
        }

    }

    public static final class ListPackages extends AbstractClientApiTest<ListApplicationPackagesRequest, ListApplicationPackagesResponse> {

        private final ReactorApplicationsV3 applications = new ReactorApplicationsV3(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v3/apps/test-application-id/packages?page=1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v3/apps/GET_{id}_packages_response.json")
                    .build())
                .build();
        }

        @Override
        protected ListApplicationPackagesRequest getInvalidRequest() {
            return ListApplicationPackagesRequest.builder()
                .build();
        }

        @Override
        protected ListApplicationPackagesResponse getResponse() {
            return ListApplicationPackagesResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(1)
                    .first(Link.builder()
                        .href("/v3/apps/guid-3c5a1cae-3ee6-4dd6-b374-50c21393d353/packages?page=1&per_page=50")
                        .build())
                    .last(Link.builder()
                        .href("/v3/apps/guid-3c5a1cae-3ee6-4dd6-b374-50c21393d353/packages?page=1&per_page=50")
                        .build())
                    .build())
                .resource(Resource.builder()
                    .id("guid-874e0dd0-2b8e-4f07-8173-f7df02094e38")
                    .type("bits")
                    .data("error", null)
                    .data("hash", StringMap.builder()
                        .entry("type", "sha1")
                        .entry("value", null)
                        .build())
                    .state("AWAITING_UPLOAD")
                    .createdAt("2016-01-26T22:20:16Z")
                    .link("self", Link.builder()
                        .href("/v3/packages/guid-874e0dd0-2b8e-4f07-8173-f7df02094e38")
                        .build())
                    .link("upload", Link.builder()
                        .href("/v3/packages/guid-874e0dd0-2b8e-4f07-8173-f7df02094e38/upload")
                        .method("POST")
                        .build())
                    .link("download", Link.builder()
                        .href("/v3/packages/guid-874e0dd0-2b8e-4f07-8173-f7df02094e38/download")
                        .method("GET")
                        .build())
                    .link("stage", Link.builder()
                        .href("/v3/packages/guid-874e0dd0-2b8e-4f07-8173-f7df02094e38/droplets")
                        .method("POST")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/guid-3c5a1cae-3ee6-4dd6-b374-50c21393d353")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListApplicationPackagesRequest getValidRequest() throws Exception {
            return ListApplicationPackagesRequest.builder()
                .page(1)
                .applicationId("test-application-id")
                .build();
        }

        @Override
        protected Mono<ListApplicationPackagesResponse> invoke(ListApplicationPackagesRequest request) {
            return this.applications.listPackages(request);
        }

    }

    public static final class ListProcesses extends AbstractClientApiTest<ListApplicationProcessesRequest, ListApplicationProcessesResponse> {

        private final ReactorApplicationsV3 applications = new ReactorApplicationsV3(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v3/apps/test-application-id/processes?page=1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v3/apps/GET_{id}_processes_response.json")
                    .build())
                .build();
        }

        @Override
        protected ListApplicationProcessesRequest getInvalidRequest() {
            return ListApplicationProcessesRequest.builder()
                .build();
        }

        @Override
        protected ListApplicationProcessesResponse getResponse() {
            return ListApplicationProcessesResponse.builder()
                .pagination(Pagination.builder()
                    .totalResults(3)
                    .first(Link.builder()
                        .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5/processes?page=1&per_page=2")
                        .build())
                    .last(Link.builder()
                        .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5/processes?page=2&per_page=2")
                        .build())
                    .next(Link.builder()
                        .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5/processes?page=2&per_page=2")
                        .build())
                    .build())
                .resource(ListApplicationProcessesResponse.Resource.builder()
                    .id("6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                    .type("web")
                    .command("rackup")
                    .instances(5)
                    .memoryInMb(256)
                    .diskInMb(1_024)
                    .port(8080)
                    .healthCheck(HealthCheck.builder()
                        .type(Type.PORT)
                        .data("timeout", null)
                        .build())
                    .createdAt("2016-03-23T18:48:22Z")
                    .updatedAt("2016-03-23T18:48:42Z")
                    .link("self", Link.builder()
                        .href("/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                        .build())
                    .link("scale", Link.builder()
                        .href("/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82/scale")
                        .method("PUT")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                        .build())
                    .link("space", Link.builder()
                        .href("/v2/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                        .build())
                    .build())
                .resource(ListApplicationProcessesResponse.Resource.builder()
                    .id("3fccacd9-4b02-4b96-8d02-8e865865e9eb")
                    .type("worker")
                    .command("bundle exec rake worker")
                    .instances(1)
                    .memoryInMb(256)
                    .diskInMb(1_024)
                    .healthCheck(HealthCheck.builder()
                        .type(Type.PROCESS)
                        .data("timeout", null)
                        .build())
                    .createdAt("2016-03-23T18:48:22Z")
                    .updatedAt("2016-03-23T18:48:42Z")
                    .link("self", Link.builder()
                        .href("/v3/processes/3fccacd9-4b02-4b96-8d02-8e865865e9eb")
                        .build())
                    .link("scale", Link.builder()
                        .href("/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82/scale")
                        .method("PUT")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                        .build())
                    .link("space", Link.builder()
                        .href("/v2/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListApplicationProcessesRequest getValidRequest() throws Exception {
            return ListApplicationProcessesRequest.builder()
                .page(1)
                .applicationId("test-application-id")
                .build();
        }

        @Override
        protected Mono<ListApplicationProcessesResponse> invoke(ListApplicationProcessesRequest request) {
            return this.applications.listProcesses(request);
        }

    }

    public static final class ListTasks extends AbstractClientApiTest<ListApplicationTasksRequest, ListApplicationTasksResponse> {

        private final ReactorApplicationsV3 applications = new ReactorApplicationsV3(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(GET).path("/v3/apps/test-application-id/tasks?page=1")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v3/apps/GET_{id}_tasks_response.json")
                    .build())
                .build();
        }

        @Override
        protected ListApplicationTasksRequest getInvalidRequest() {
            return ListApplicationTasksRequest.builder()
                .build();
        }

        @Override
        protected ListApplicationTasksResponse getResponse() {
            return ListApplicationTasksResponse.builder()
                .pagination(PaginatedResponse.Pagination.builder()
                    .totalResults(3)
                    .first(Link.builder()
                        .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5/tasks?page=1&per_page=2")
                        .build())
                    .last(Link.builder()
                        .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5/tasks?page=2&per_page=2")
                        .build())
                    .next(Link.builder()
                        .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5/tasks?page=2&per_page=2")
                        .build())
                    .build())
                .resource(TaskResource.builder()
                    .id("d5cc22ec-99a3-4e6a-af91-a44b4ab7b6fa")
                    .name("hello")
                    .command("echo \"hello world\"")
                    .state(Task.SUCCEEDED_STATE)
                    .memoryInMb(512)
                    .results(Collections.singletonMap("failure_reason", null))
                    .link("self", Link.builder()
                        .href("/v3/tasks/d5cc22ec-99a3-4e6a-af91-a44b4ab7b6fa")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                        .build())
                    .link("droplet", Link.builder()
                        .href("/v3/droplets/740ebd2b-162b-469a-bd72-3edb96fabd9a")
                        .build())
                    .build())
                .resource(TaskResource.builder()
                    .id("63b4cd89-fd8b-4bf1-a311-7174fcc907d6")
                    .name("migrate")
                    .command("rake db:migrate")
                    .state(Task.FAILED_STATE)
                    .memoryInMb(512)
                    .results(Collections.singletonMap("failure_reason", "Exited with status 1"))
                    .link("self", Link.builder()
                        .href("/v3/tasks/63b4cd89-fd8b-4bf1-a311-7174fcc907d6")
                        .build())
                    .link("app", Link.builder()
                        .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                        .build())
                    .link("droplet", Link.builder()
                        .href("/v3/droplets/740ebd2b-162b-469a-bd72-3edb96fabd9a")
                        .build())
                    .build())
                .build();
        }

        @Override
        protected ListApplicationTasksRequest getValidRequest() throws Exception {
            return ListApplicationTasksRequest.builder()
                .page(1)
                .applicationId("test-application-id")
                .build();
        }

        @Override
        protected Mono<ListApplicationTasksResponse> invoke(ListApplicationTasksRequest request) {
            return this.applications.listTasks(request);
        }

    }

    public static final class Scale extends AbstractClientApiTest<ScaleApplicationRequest, ScaleApplicationResponse> {

        private final ReactorApplicationsV3 applications = new ReactorApplicationsV3(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v3/apps/test-application-id/processes/web/scale")
                    .payload("fixtures/client/v3/apps/PUT_{id}_processes_{type}_scale_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v3/apps/PUT_{id}_processes_{type}_scale_response.json")
                    .build())
                .build();
        }

        @Override
        protected ScaleApplicationRequest getInvalidRequest() {
            return ScaleApplicationRequest.builder()
                .build();
        }

        @Override
        protected ScaleApplicationResponse getResponse() {
            return ScaleApplicationResponse.builder()
                .id("6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                .type("web")
                .command("rackup")
                .instances(5)
                .memoryInMb(256)
                .diskInMb(1_024)
                .port(8080)
                .healthCheck(HealthCheck.builder()
                    .type(Type.PORT)
                    .data("timeout", null)
                    .build())
                .createdAt("2016-03-23T18:48:22Z")
                .updatedAt("2016-03-23T18:48:42Z")
                .link("self", Link.builder()
                    .href("/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82")
                    .build())
                .link("scale", Link.builder()
                    .href("/v3/processes/6a901b7c-9417-4dc1-8189-d3234aa0ab82/scale")
                    .method("PUT")
                    .build())
                .link("app", Link.builder()
                    .href("/v3/apps/ccc25a0f-c8f4-4b39-9f1b-de9f328d0ee5")
                    .build())
                .link("space", Link.builder()
                    .href("/v2/spaces/2f35885d-0c9d-4423-83ad-fd05066f8576")
                    .build())
                .build();
        }

        @Override
        protected ScaleApplicationRequest getValidRequest() throws Exception {
            return ScaleApplicationRequest.builder()
                .applicationId("test-application-id")
                .type("web")
                .instances(5)
                .memoryInMb(256)
                .diskInMb(1_024)
                .build();
        }

        @Override
        protected Mono<ScaleApplicationResponse> invoke(ScaleApplicationRequest request) {
            return this.applications.scale(request);
        }

    }

    public static final class Start extends AbstractClientApiTest<StartApplicationRequest, StartApplicationResponse> {

        private final ReactorApplicationsV3 applications = new ReactorApplicationsV3(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v3/apps/test-application-id/start")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v3/apps/PUT_{id}_start_response.json")
                    .build())
                .build();
        }

        @Override
        protected StartApplicationRequest getInvalidRequest() {
            return StartApplicationRequest.builder()
                .build();
        }

        @Override
        protected StartApplicationResponse getResponse() {
            return StartApplicationResponse.builder()
                .id("guid-76956e3e-9905-42f9-b2c1-f9b53ef24ac0")
                .name("original_name")
                .desiredState("STARTED")
                .totalDesiredInstances(0)
                .createdAt("2016-01-26T22:20:35Z")
                .updatedAt("2016-01-26T22:20:35Z")
                .lifecycle(Lifecycle.builder()
                    .type("buildpack")
                    .data("buildpack", "name-2438")
                    .data("stack", "name-2439")
                    .build())
                .link("self", Link.builder()
                    .href("/v3/apps/guid-76956e3e-9905-42f9-b2c1-f9b53ef24ac0")
                    .build())
                .link("space", Link.builder()
                    .href("/v2/spaces/a3585834-1d6e-41ad-8b25-2ca17a3e5dca")
                    .build())
                .link("processes", Link.builder()
                    .href("/v3/apps/guid-76956e3e-9905-42f9-b2c1-f9b53ef24ac0/processes")
                    .build())
                .link("routes", Link.builder()
                    .href("/v3/apps/guid-76956e3e-9905-42f9-b2c1-f9b53ef24ac0/routes")
                    .build())
                .link("packages", Link.builder()
                    .href("/v3/apps/guid-76956e3e-9905-42f9-b2c1-f9b53ef24ac0/packages")
                    .build())
                .link("droplet", Link.builder()
                    .href("/v3/droplets/guid-352f8e31-2ef0-412b-90f1-3404cd8e929d")
                    .build())
                .link("droplets", Link.builder()
                    .href("/v3/apps/guid-76956e3e-9905-42f9-b2c1-f9b53ef24ac0/droplets")
                    .build())
                .link("start", Link.builder()
                    .href("/v3/apps/guid-76956e3e-9905-42f9-b2c1-f9b53ef24ac0/start")
                    .method("PUT")
                    .build())
                .link("stop", Link.builder()
                    .href("/v3/apps/guid-76956e3e-9905-42f9-b2c1-f9b53ef24ac0/stop")
                    .method("PUT")
                    .build())
                .link("assign_current_droplet", Link.builder()
                    .href("/v3/apps/guid-76956e3e-9905-42f9-b2c1-f9b53ef24ac0/current_droplet")
                    .method("PUT")
                    .build())
                .build();
        }

        @Override
        protected StartApplicationRequest getValidRequest() throws Exception {
            return StartApplicationRequest.builder()
                .applicationId("test-application-id")
                .build();
        }

        @Override
        protected Mono<StartApplicationResponse> invoke(StartApplicationRequest request) {
            return this.applications.start(request);
        }

    }

    public static final class Stop extends AbstractClientApiTest<StopApplicationRequest, StopApplicationResponse> {

        private final ReactorApplicationsV3 applications = new ReactorApplicationsV3(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PUT).path("/v3/apps/test-application-id/stop")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v3/apps/PUT_{id}_stop_response.json")
                    .build())
                .build();
        }

        @Override
        protected StopApplicationRequest getInvalidRequest() {
            return StopApplicationRequest.builder()
                .build();
        }

        @Override
        protected StopApplicationResponse getResponse() {
            return StopApplicationResponse.builder()
                .id("guid-11e8e36d-71f5-4be8-9487-c6d1a187e439")
                .name("original_name")
                .desiredState("STOPPED")
                .totalDesiredInstances(0)
                .createdAt("2016-01-26T22:20:34Z")
                .updatedAt("2016-01-26T22:20:34Z")
                .lifecycle(Lifecycle.builder()
                    .type("buildpack")
                    .data("buildpack", "name-2406")
                    .data("stack", "name-2407")
                    .build())
                .link("self", Link.builder()
                    .href("/v3/apps/guid-11e8e36d-71f5-4be8-9487-c6d1a187e439")
                    .build())
                .link("space", Link.builder()
                    .href("/v2/spaces/fba26de0-301f-499b-9967-885a6b8ab533")
                    .build())
                .link("processes", Link.builder()
                    .href("/v3/apps/guid-11e8e36d-71f5-4be8-9487-c6d1a187e439/processes")
                    .build())
                .link("routes", Link.builder()
                    .href("/v3/apps/guid-11e8e36d-71f5-4be8-9487-c6d1a187e439/routes")
                    .build())
                .link("packages", Link.builder()
                    .href("/v3/apps/guid-11e8e36d-71f5-4be8-9487-c6d1a187e439/packages")
                    .build())
                .link("droplet", Link.builder()
                    .href("/v3/droplets/guid-5123a382-2538-4c21-8a7b-d06e5f9a1f0c")
                    .build())
                .link("droplets", Link.builder()
                    .href("/v3/apps/guid-11e8e36d-71f5-4be8-9487-c6d1a187e439/droplets")
                    .build())
                .link("start", Link.builder()
                    .href("/v3/apps/guid-11e8e36d-71f5-4be8-9487-c6d1a187e439/start")
                    .method("PUT")
                    .build())
                .link("stop", Link.builder()
                    .href("/v3/apps/guid-11e8e36d-71f5-4be8-9487-c6d1a187e439/stop")
                    .method("PUT")
                    .build())
                .link("assign_current_droplet", Link.builder()
                    .href("/v3/apps/guid-11e8e36d-71f5-4be8-9487-c6d1a187e439/current_droplet")
                    .method("PUT")
                    .build())
                .build();
        }

        @Override
        protected StopApplicationRequest getValidRequest() throws Exception {
            return StopApplicationRequest.builder()
                .applicationId("test-application-id")
                .build();
        }

        @Override
        protected Mono<StopApplicationResponse> invoke(StopApplicationRequest request) {
            return this.applications.stop(request);
        }

    }

    public static final class Update extends AbstractClientApiTest<UpdateApplicationRequest, UpdateApplicationResponse> {

        private final ReactorApplicationsV3 applications = new ReactorApplicationsV3(AUTHORIZATION_PROVIDER, HTTP_CLIENT, OBJECT_MAPPER, this.root);

        @Override
        protected InteractionContext getInteractionContext() {
            return InteractionContext.builder()
                .request(TestRequest.builder()
                    .method(PATCH).path("/v3/apps/test-application-id")
                    .payload("fixtures/client/v3/apps/PATCH_{id}_request.json")
                    .build())
                .response(TestResponse.builder()
                    .status(OK)
                    .payload("fixtures/client/v3/apps/PATCH_{id}_response.json")
                    .build())
                .build();
        }

        @Override
        protected UpdateApplicationRequest getInvalidRequest() {
            return UpdateApplicationRequest.builder()
                .build();
        }

        @Override
        protected UpdateApplicationResponse getResponse() {
            return UpdateApplicationResponse.builder()
                .id("guid-003fabe4-542f-487d-b9b7-8b1bffdd0ca9")
                .name("new_name")
                .desiredState("STOPPED")
                .totalDesiredInstances(0)
                .createdAt("2016-01-26T22:20:34Z")
                .updatedAt("2016-01-26T22:20:35Z")
                .lifecycle(Lifecycle.builder()
                    .type("buildpack")
                    .data("buildpack", "http://gitwheel.org/my-app")
                    .data("stack", "redhat")
                    .build())
                .environmentVariable("MY_ENV_VAR", "foobar")
                .environmentVariable("FOOBAR", "MY_ENV_VAR")
                .link("self", Link.builder()
                    .href("/v3/apps/guid-003fabe4-542f-487d-b9b7-8b1bffdd0ca9")
                    .build())
                .link("space", Link.builder()
                    .href("/v2/spaces/5441b385-8398-49fd-afb0-cdb5d69a89ce")
                    .build())
                .link("processes", Link.builder()
                    .href("/v3/apps/guid-003fabe4-542f-487d-b9b7-8b1bffdd0ca9/processes")
                    .build())
                .link("routes", Link.builder()
                    .href("/v3/apps/guid-003fabe4-542f-487d-b9b7-8b1bffdd0ca9/routes")
                    .build())
                .link("packages", Link.builder()
                    .href("/v3/apps/guid-003fabe4-542f-487d-b9b7-8b1bffdd0ca9/packages")
                    .build())
                .link("droplets", Link.builder()
                    .href("/v3/apps/guid-003fabe4-542f-487d-b9b7-8b1bffdd0ca9/droplets")
                    .build())
                .link("start", Link.builder()
                    .href("/v3/apps/guid-003fabe4-542f-487d-b9b7-8b1bffdd0ca9/start")
                    .method("PUT")
                    .build())
                .link("stop", Link.builder()
                    .href("/v3/apps/guid-003fabe4-542f-487d-b9b7-8b1bffdd0ca9/stop")
                    .method("PUT")
                    .build())
                .link("assign_current_droplet", Link.builder()
                    .href("/v3/apps/guid-003fabe4-542f-487d-b9b7-8b1bffdd0ca9/current_droplet")
                    .method("PUT")
                    .build())
                .build();
        }

        @Override
        protected UpdateApplicationRequest getValidRequest() throws Exception {
            return UpdateApplicationRequest.builder()
                .applicationId("test-application-id")
                .name("new_name")
                .environmentVariable("MY_ENV_VAR", "foobar")
                .environmentVariable("FOOBAR", "MY_ENV_VAR")
                .lifecycle(Lifecycle.builder()
                    .type("buildpack")
                    .data("buildpack", "http://gitwheel.org/my-app")
                    .data("stack", "redhat")
                    .build())
                .build();
        }

        @Override
        protected Mono<UpdateApplicationResponse> invoke(UpdateApplicationRequest request) {
            return this.applications.update(request);
        }

    }

}
