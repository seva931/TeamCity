package api;

import api.models.BuildQueueResponse;
import api.models.CreateBuildTypeResponse;
import api.models.CreateUserResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.steps.BuildQueueSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import jupiter.annotation.Build;
import jupiter.annotation.WithProject;
import jupiter.annotation.WithUsersQueue;
import jupiter.extension.BuildExtension;
import jupiter.extension.ProjectExtension;
import jupiter.extension.UsersQueueExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({
        UsersQueueExtension.class,
        ProjectExtension.class,
        BuildExtension.class
})
public class BuildQueueRunCancelTest extends BaseTest {

    @WithUsersQueue
    @WithProject(addToCleanup = false)
    @Test
    @DisplayName("Запуск и отмена билда")
    void triggerAndCancelBuild(
            CreateUserResponse admin,
            @Build(addToCleanup = false) CreateBuildTypeResponse build
    ) {
        BuildQueueResponse queued = BuildQueueSteps.queueBuild(build, admin);

        softly.assertThat(queued.getId())
                .as("Queue item id")
                .isGreaterThan(0);

        softly.assertThat(queued.getBuildTypeId())
                .as("buildTypeId")
                .isEqualTo(build.getId());

        softly.assertThat(queued.getState())
                .as("state")
                .isEqualTo("queued");

        long queueId = queued.getId();

        BuildQueueSteps.cancelQueuedBuild(queueId, admin);

        new CrudRequester(
                RequestSpecs.authAsUser(admin),
                Endpoint.BUILD_QUEUE_ID,
                ResponseSpecs.notFound()
        ).get(queueId);
    }

    @WithUsersQueue
    @WithProject(addToCleanup = false)
    @Test
    @DisplayName("Повторная отмена уже отменённого queueId")
    void cancelAlreadyCancelledQueuedBuildReturns404(
            CreateUserResponse admin,
            @Build(addToCleanup = false) CreateBuildTypeResponse build
    ) {
        BuildQueueResponse queued = BuildQueueSteps.queueBuild(build, admin);
        long queueId = queued.getId();

        BuildQueueSteps.cancelQueuedBuild(queueId, admin);

        new CrudRequester(
                RequestSpecs.authAsUser(admin),
                Endpoint.BUILD_QUEUE_ID,
                ResponseSpecs.notFound()
        ).delete(queueId);
    }
}