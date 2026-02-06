package api;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.steps.BuildManageSteps;
import api.requests.steps.ProjectManagementSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.generators.TestDataGenerator;
import jupiter.annotation.WithUsersQueue;
import jupiter.extension.UsersQueueExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({UsersQueueExtension.class})
@WithUsersQueue
public class ManageBuildConfigurationTest extends BaseTest {
    private String buildId;
    private String projectId;
    private static CreateProjectRequest createProjectRequest;

    @AfterEach
    public void afterEach(CreateUserResponse user) {
        BuildManageSteps.deleteBuildConfiguration(this.buildId, user);
        ProjectManagementSteps.deleteProjectById(this.projectId, user);
    }

    @Test
    public void userCreateBuildConfigurationTest(CreateUserResponse user) {
        this.projectId = TestDataGenerator.generateProjectID();
        createProjectRequest = ProjectManagementSteps.createProject(this.projectId, TestDataGenerator.generateProjectName(), "_Root", user);
        String buildName = TestDataGenerator.generateBuildName();
        this.buildId = createProjectRequest.getId() + "_" + buildName;

        CreateBuildConfigurationRequest createBuildConfigurationRequest = new CreateBuildConfigurationRequest(this.buildId, buildName, createProjectRequest.getId());

        CreateBuildConfigurationResponse createBuildConfigurationResponse = new ValidatedCrudRequester<CreateBuildConfigurationResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk())
                .post(createBuildConfigurationRequest);

        softly.assertThat(createBuildConfigurationResponse.getId())
                .as("Поле id")
                .isEqualTo(this.buildId);

        softly.assertThat(createBuildConfigurationResponse.getName())
                .as("Поле name")
                .isEqualTo(buildName);

        softly.assertThat(createBuildConfigurationResponse.getProjectId())
                .as("Поле ProjectId")
                .isEqualTo(createProjectRequest.getId());
    }

    @Test
    public void userCanNotCreateBuildConfigurationWithSameNameTest(CreateUserResponse user) {
        this.projectId = TestDataGenerator.generateProjectID();
        createProjectRequest = ProjectManagementSteps.createProject(this.projectId, TestDataGenerator.generateProjectName(), "_Root", user);
        String buildName = TestDataGenerator.generateBuildName();
        this.buildId = createProjectRequest.getId() + "_" + buildName;
        BuildManageSteps.createBuildConfiguration(createProjectRequest.getId(), this.buildId, buildName);

        CreateBuildConfigurationRequest createBuildConfigurationRequest = new CreateBuildConfigurationRequest(this.buildId + "1", buildName, createProjectRequest.getId());

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.badRequestWithErrorText(String.format(ApiAtributesOfResponse.BUILD_CONFIGURATION_WITH_SUCH_NAME_ALREADY_EXISTS_ERROR, buildName, createProjectRequest.getName())))
                .post(createBuildConfigurationRequest);
    }

    @Test
    public void userGetInfoBuildConfigurationTest(CreateUserResponse user) {
        this.projectId = TestDataGenerator.generateProjectID();
        createProjectRequest = ProjectManagementSteps.createProject(this.projectId, TestDataGenerator.generateProjectName(), "_Root", user);

        String buildName = TestDataGenerator.generateBuildName();
        this.buildId = createProjectRequest.getId() + "_" + buildName;

        BuildManageSteps.createBuildConfiguration(createProjectRequest.getId(), this.buildId, buildName);

        GetInfoBuildConfigurationResponse getInfoBuildConfigurationResponse = new ValidatedCrudRequester<GetInfoBuildConfigurationResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.requestReturnsOk())
                .get(this.buildId);

        softly.assertThat(getInfoBuildConfigurationResponse.getId())
                .as("Поле id")
                .isEqualTo(this.buildId);

        softly.assertThat(getInfoBuildConfigurationResponse.getName())
                .as("Поле name")
                .isEqualTo(buildName);

        softly.assertThat(getInfoBuildConfigurationResponse.getProjectId())
                .as("Поле ProjectId")
                .isEqualTo(createProjectRequest.getId());
    }


    //TODO: написать кастомную аннотацию для пропуска аннотации AfterEach
    @Test
    public void userDeleteBuildConfigurationTest(CreateUserResponse user) {
        this.projectId = TestDataGenerator.generateProjectID();
        createProjectRequest = ProjectManagementSteps.createProject(this.projectId, TestDataGenerator.generateProjectName(), "_Root", user);

        String buildName = TestDataGenerator.generateBuildName();
        this.buildId = createProjectRequest.getId() + "_" + buildName;

        BuildManageSteps.createBuildConfiguration(createProjectRequest.getId(), this.buildId, buildName);

        //проверка, что конфигурация создалась
        GetInfoBuildConfigurationResponse getInfoBuildConfigurationResponse = BuildManageSteps.getInfoBuildConfiguration(this.buildId, user);
        softly.assertThat(getInfoBuildConfigurationResponse.getId())
                .as("Поле id")
                .isEqualTo(this.buildId);

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.noContent())
                .delete(this.buildId);

        //проверка, что теперь getInfo возвращает ошибку
        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.notFoundWithErrorText(ApiAtributesOfResponse.NO_BUILD_TYPE_ERROR))
                .get(this.buildId);
    }
}
