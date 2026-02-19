package api.requests.steps;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.data.BuildStepPropertyData;
import common.data.BuildStepTypeData;
import common.generators.RandomModelGenerator;
import io.restassured.http.ContentType;

import java.util.List;

public class BuildQueueSteps {

    public static BuildQueueResponse queueBuild(CreateBuildTypeResponse build, CreateUserResponse user) {
        QueueBuildRequest body = QueueBuildRequest.of(build.getId());

        return new CrudRequester(
                RequestSpecs.authAsUser(user, ContentType.JSON),
                Endpoint.BUILD_QUEUE,
                ResponseSpecs.requestReturnsOk()
        ).post(body)
                .extract()
                .as(BuildQueueResponse.class);
    }

    public static void cancelQueuedBuild(long queueId, CreateUserResponse user) {
        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_QUEUE_ID,
                ResponseSpecs.noContent()
        ).delete(queueId);
    }

    public static BuildQueueResponse getAllQueuedBuilds(CreateUserResponse user) {
        return new ValidatedCrudRequester<BuildQueueResponse>(
                RequestSpecs.authAsUser(user, ContentType.JSON),
                Endpoint.BUILD_QUEUE,
                ResponseSpecs.requestReturnsOk()
        ).get();
    }

    public static BuildQueueResponse queueEndlessBuild(CreateBuildTypeResponse build, CreateUserResponse user) {

        BuildStepsSteps.addEndlessStep(user, build);

        QueueBuildRequest request = QueueBuildRequest.builder().buildType(
                BuildTypeRef.builder()
                        .id(build.getId()).build()
        ).build();

        return new CrudRequester(
                RequestSpecs.authAsUser(user, ContentType.JSON),
                Endpoint.BUILD_QUEUE,
                ResponseSpecs.requestReturnsOk()
        ).post(request)
                .extract()
                .as(BuildQueueResponse.class);
    }
}