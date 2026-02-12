package api;

import api.models.BuildQueueResponse;
import api.models.CreateBuildConfigurationResponse;
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
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({
        UsersQueueExtension.class,
        ProjectExtension.class,
        BuildExtension.class
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BuildQueueRunCancelTest extends BaseTest {

    private static long queueId;

    @WithUsersQueue
    @WithProject(addToCleanup = false)
    @Test
    @Order(1)
    @DisplayName("Запуск билда")
    void triggerBuild(
            CreateUserResponse admin,
            @Build(addToCleanup = false) CreateBuildConfigurationResponse build
    ) {
        BuildQueueResponse queued = BuildQueueSteps.queueBuild(build.getId(), admin);

        softly.assertThat(queued.getId())
                .as("Queue item id")
                .isGreaterThan(0);

        softly.assertThat(queued.getBuildTypeId())
                .as("buildTypeId")
                .isEqualTo(build.getId());

        softly.assertThat(queued.getState())
                .as("state")
                .isEqualTo("queued");

        queueId = queued.getId();
    }

    @WithUsersQueue
    @Test
    @Order(2)
    @DisplayName("Отмена билда")
    void cancelQueuedBuild(CreateUserResponse admin) {
        softly.assertThat(queueId)
                .as("queueId получен из теста запуска")
                .isGreaterThan(0);

        BuildQueueSteps.cancelQueuedBuild(queueId, admin);

        String queueJson = new CrudRequester(
                RequestSpecs.authAsUser(admin),
                Endpoint.BUILD_QUEUE,
                ResponseSpecs.requestReturnsOk()
        ).get()
                .extract()
                .asString();

        softly.assertThat(queueJson)
                .as("Очередь билдов не содержит отменённый queueId")
                .doesNotContain("id:" + queueId);
    }
}