package api.sample;

import api.BaseTest;
import api.models.CreateBuildConfigurationRequest;
import api.models.CreateBuildConfigurationResponse;
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

@ExtendWith({UsersQueueExtension.class, ProjectExtension.class})
@WithUsersQueue
public class SampleTest extends BaseTest {

    @WithProject
    @Test
    public void userCreateBuildConfigurationTest(CreateUserResponse user, CreateProjectRequest project) {
        String buildName = TestDataGenerator.generateBuildName();

        String buildId = project.getId() + "_" + buildName;

        CreateBuildConfigurationRequest createBuildConfigurationRequest = new CreateBuildConfigurationRequest(buildId, buildName, project.getId());

        CreateBuildConfigurationResponse createBuildConfigurationResponse = new ValidatedCrudRequester<CreateBuildConfigurationResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk())
                .post(createBuildConfigurationRequest);

        softly.assertThat(createBuildConfigurationResponse.getId())
                .as("Поле id")
                .isEqualTo(buildId);

        softly.assertThat(createBuildConfigurationResponse.getName())
                .as("Поле name")
                .isEqualTo(buildName);

        softly.assertThat(createBuildConfigurationResponse.getProjectId())
                .as("Поле ProjectId")
                .isEqualTo(project.getId());
    }
}
