package api.requests.steps;

import api.models.BuildQueueListResponse;
import api.models.BuildQueueRequest;
import api.models.BuildQueueResponse;
import api.models.CreateBuildTypeResponse;
import api.models.CreateUserResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;

public class BuildQueueSteps {

    @Step("Поставить билд в очередь")
    public static BuildQueueResponse queueBuild(CreateBuildTypeResponse build, CreateUserResponse user) {
        BuildQueueRequest body = BuildQueueRequest.of(build.getId());

        return new CrudRequester(
                RequestSpecs.authAsUser(user, ContentType.JSON),
                Endpoint.BUILD_QUEUE,
                ResponseSpecs.requestReturnsOk()
        ).post(body)
                .extract()
                .as(BuildQueueResponse.class);
    }

    @Step("Удалить билд из очереди по queueId: {queueId}")
    public static void deleteQueuedBuild(long queueId, CreateUserResponse user) {
        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_QUEUE_ID,
                ResponseSpecs.noContent()
        ).delete(queueId);
    }

    @Step("Тихо удалить билд из очереди по queueId: {queueId}")
    public static void deleteQueuedBuildQuietly(long queueId, CreateUserResponse user) {
        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_QUEUE_ID,
                ResponseSpecs.deletesQuietly()
        ).delete(queueId);
    }

    @Step("Удалить все билды из очереди")
    public static void deleteAllQueuedBuilds(CreateUserResponse user) {
        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_QUEUE,
                ResponseSpecs.deletesQuietly()
        ).delete();
    }

    @Step("Получить все билды в очереди")
    public static BuildQueueListResponse getAllQueuedBuilds(CreateUserResponse user) {
        return new CrudRequester(
                RequestSpecs.authAsUser(user, ContentType.JSON),
                Endpoint.BUILD_QUEUE,
                ResponseSpecs.requestReturnsOk()
        ).get().extract().as(BuildQueueListResponse.class);
    }

    @Step("Поставить endless build в очередь")
    public static BuildQueueResponse queueEndlessBuild(CreateBuildTypeResponse build, CreateUserResponse user) {
        BuildStepsSteps.addEndlessStep(user, build);
        return BuildQueueSteps.queueBuild(build, user);
    }
}