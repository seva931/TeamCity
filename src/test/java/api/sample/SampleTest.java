package api.sample;

import api.BaseTest;
import api.models.CreateBuildConfigurationResponse;
import api.models.CreateProjectRequest;
import api.models.CreateUserResponse;
import api.models.GetBuldListInfoResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import jupiter.annotation.WithBuild;
import jupiter.annotation.WithProject;
import jupiter.annotation.WithUsersQueue;
import jupiter.extension.BuildExtension;
import jupiter.extension.ProjectExtension;
import jupiter.extension.UsersQueueExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({
        UsersQueueExtension.class,
        ProjectExtension.class,
        BuildExtension.class
})
public class SampleTest extends BaseTest {

    @WithUsersQueue
    @WithProject(
            @WithBuild
    )
    @Test
    public void buildSampleTest(
            CreateUserResponse user,
            CreateProjectRequest project,
            CreateBuildConfigurationResponse build) {

        GetBuldListInfoResponse getBuldListInfoResponse = new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk())
                .get().extract().as(GetBuldListInfoResponse.class);
    }
}
