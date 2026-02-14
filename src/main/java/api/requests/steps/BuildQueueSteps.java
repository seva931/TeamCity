package api.requests.steps;

import api.models.BuildQueueResponse;
import api.models.CreateBuildTypeResponse;
import api.models.CreateUserResponse;
import api.models.QueueBuildRequest;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import io.restassured.http.ContentType;

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
}