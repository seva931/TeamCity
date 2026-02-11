package api.sample;

import api.BaseTest;
import api.models.CreateBuildTypeRequest;
import api.models.CreateBuildTypeResponse;
import api.models.CreateProjectRequest;
import api.models.CreateUserResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.generators.TestDataGenerator;
import jupiter.annotation.WithProject;
import jupiter.annotation.WithUsersQueue;
import jupiter.extension.ProjectExtension;
import jupiter.extension.UsersQueueExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({UsersQueueExtension.class, ProjectExtension.class})
public class SampleTest extends BaseTest {

    @WithUsersQueue
    @WithProject
    @Test
    public void userCreateBuildConfigurationTest(CreateUserResponse user, CreateProjectRequest project) {
        String buildName = TestDataGenerator.generateBuildName();

        String buildId = project.getId() + "_" + buildName;

        CreateBuildTypeRequest createBuildTypeRequest = new CreateBuildTypeRequest(buildId, buildName, project.getId());

        CreateBuildTypeResponse createBuildTypeResponse = new ValidatedCrudRequester<CreateBuildTypeResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk())
                .post(createBuildTypeRequest);

        assertThat(createBuildTypeRequest)
                .usingRecursiveComparison()
                .comparingOnlyFields("id", "name", "ProjectId");
    }
}
