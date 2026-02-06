package api;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.steps.BuildManageSteps;
import api.requests.steps.ProjectManagementSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.data.ApiAtributesOfResponse;
import common.generators.TestDataGenerator;
import jupiter.annotation.WithUsersQueue;
import jupiter.extension.UsersQueueExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({UsersQueueExtension.class})
@WithUsersQueue
public class ManageBuildConfigurationTest extends BaseTest {
    private static String buildId;
    private static String projectId;
    private static CreateProjectRequest createProjectRequest;

    @AfterEach
    public void afterEach(CreateUserResponse user, TestInfo testInfo){
        if (!testInfo.getTags().contains("noCleanupBuild")) {
            BuildManageSteps.deleteBuildConfiguration(buildId, user);
        }

        if (projectId != null) {
            ProjectManagementSteps.deleteProjectById(projectId, user);
        }
    }

    @Test
    public void userCreateBuildConfigurationTest(CreateUserResponse user) {
        projectId = TestDataGenerator.generateProjectID();
        createProjectRequest = ProjectManagementSteps.createProject(projectId, TestDataGenerator.generateProjectName(), "_Root", user);
        String buildName = TestDataGenerator.generateBuildName();
        buildId = createProjectRequest.getId() + "_" + buildName;

        CreateBuildConfigurationRequest createBuildConfigurationRequest = new CreateBuildConfigurationRequest(buildId, buildName, createProjectRequest.getId());

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
                .isEqualTo(createProjectRequest.getId());
    }

    @Test
    public void userCanNotCreateBuildConfigurationWithSameNameTest(CreateUserResponse user) {
        projectId = TestDataGenerator.generateProjectID();
        createProjectRequest = ProjectManagementSteps.createProject(projectId, TestDataGenerator.generateProjectName(), "_Root", user);
        String buildName = TestDataGenerator.generateBuildName();
        buildId = createProjectRequest.getId() + "_" + buildName;
        BuildManageSteps.createBuildConfiguration(createProjectRequest.getId(), buildId, buildName);

        CreateBuildConfigurationRequest createBuildConfigurationRequest = new CreateBuildConfigurationRequest(buildId + "1", buildName, createProjectRequest.getId());

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.badRequestWithErrorText(String.format(ApiAtributesOfResponse.BUILD_CONFIGURATION_WITH_SUCH_NAME_ALREADY_EXISTS_ERROR.getMessage(), buildName, createProjectRequest.getName())))
                .post(createBuildConfigurationRequest);
    }

    @Test
    public void userGetInfoBuildConfigurationTest(CreateUserResponse user) {
        projectId = TestDataGenerator.generateProjectID();
        createProjectRequest = ProjectManagementSteps.createProject(projectId, TestDataGenerator.generateProjectName(), "_Root", user);

        String buildName = TestDataGenerator.generateBuildName();
        buildId = createProjectRequest.getId() + "_" + buildName;

        BuildManageSteps.createBuildConfiguration(createProjectRequest.getId(), buildId, buildName);

        GetInfoBuildConfigurationResponse getInfoBuildConfigurationResponse = new ValidatedCrudRequester<GetInfoBuildConfigurationResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.requestReturnsOk())
                .get(buildId);

        softly.assertThat(getInfoBuildConfigurationResponse.getId())
                .as("Поле id")
                .isEqualTo(buildId);

        softly.assertThat(getInfoBuildConfigurationResponse.getName())
                .as("Поле name")
                .isEqualTo(buildName);

        softly.assertThat(getInfoBuildConfigurationResponse.getProjectId())
                .as("Поле ProjectId")
                .isEqualTo(createProjectRequest.getId());
    }


    @Tag("noCleanupBuild")
    @Test
    public void userDeleteBuildConfigurationTest(CreateUserResponse user) {
        projectId = TestDataGenerator.generateProjectID();
        createProjectRequest = ProjectManagementSteps.createProject(projectId, TestDataGenerator.generateProjectName(), "_Root", user);

        String buildName = TestDataGenerator.generateBuildName();
        buildId = createProjectRequest.getId() + "_" + buildName;

        BuildManageSteps.createBuildConfiguration(createProjectRequest.getId(), buildId, buildName);

        //проверка, что конфигурация создалась
        GetInfoBuildConfigurationResponse getInfoBuildConfigurationResponse = BuildManageSteps.getInfoBuildConfiguration(buildId, user);
        softly.assertThat(getInfoBuildConfigurationResponse.getId())
                .as("Поле id")
                .isEqualTo(buildId);

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.noContent())
                .delete(buildId);

        //проверка, что теперь getInfo возвращает ошибку
        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.notFoundWithErrorText(ApiAtributesOfResponse.NO_BUILD_TYPE_ERROR.getMessage()))
                .get(buildId);
    }
}
