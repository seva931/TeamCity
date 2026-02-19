package ui;

import api.models.CreateBuildTypeResponse;
import api.models.CreateUserResponse;
import api.models.ProjectResponse;
import api.requests.steps.BuildQueueSteps;
import jupiter.annotation.Build;
import jupiter.annotation.Project;
import jupiter.annotation.User;
import jupiter.annotation.meta.WebTest;
import jupiter.annotation.meta.WithBuild;
import org.junit.jupiter.api.Test;

@WebTest
@WithBuild
public class QueueTest {

    @Test
    void displaysMessageWhenNoBuildAddedToQueue(
            @User CreateUserResponse user
    ) {

    }

    @Test
    void addedBuildDisplayedInQueue(
            @User CreateUserResponse user,
            @Project ProjectResponse project,
            @Build CreateBuildTypeResponse build
    ) {
        BuildQueueSteps.queueEndlessBuild(build, user);

    }
}
