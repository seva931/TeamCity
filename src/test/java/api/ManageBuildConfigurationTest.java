package api;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.requests.steps.BuildManageSteps;
import api.requests.steps.ProjectManagementSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.data.ApiAtributesOfResponse;
import common.generators.TestDataGenerator;
import jupiter.annotation.WithUsersQueue;
import jupiter.extension.UsersQueueExtension;
import org.junit.jupiter.api.*;
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

        if (!testInfo.getTags().contains("noCleanupProject")) {
            ProjectManagementSteps.deleteProjectById(projectId, user);
        }
    }

    @DisplayName("Позитивный тест: создание билд конфигурации")
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

    @DisplayName("Негативный тест: создание билд конфигурации с именем уже созданной конфигурации")
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

    @DisplayName("Позитивный тест: получение информации о созданной билд конфигурации")
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

    @DisplayName("Негативный тест: получение информации о не существующей билд конфигурации")
    @Tag("noCleanupBuild")
    @Tag("noCleanupProject")
    @Test
    public void userGetInfoAboutNotExistBuildConfigurationTest(CreateUserResponse user) {
        String buildId = TestDataGenerator.generateBuildId();

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.notFoundWithErrorText(String.format(ApiAtributesOfResponse.NO_BUILD_TYPE_ERROR.getMessage(), buildId)))
                .get(buildId);
    }

    @DisplayName("Позитивный тест: получение информации о списке созданных билд конфигураций")
    @Test
    public void userGetInfoBuildConfigurationsListTest(CreateUserResponse user) {
        projectId = TestDataGenerator.generateProjectID();
        createProjectRequest = ProjectManagementSteps.createProject(projectId, TestDataGenerator.generateProjectName(), "_Root", user);

        String buildName = TestDataGenerator.generateBuildName();
        buildId = createProjectRequest.getId() + "_" + buildName;
        BuildManageSteps.createBuildConfiguration(createProjectRequest.getId(), buildId, buildName);

        GetBuldListInfoResponse getBuldListInfoResponse = new ValidatedCrudRequester<GetBuldListInfoResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_GET,
                ResponseSpecs.requestReturnsOk())
                .get();

        softly.assertThat(getBuldListInfoResponse.getCount())
                .as("Поле count")
                .isNotNull();
        softly.assertThat(getBuldListInfoResponse.getBuildType())
                .as("Список билд конфигураций существующих")
                .isNotEmpty();
    }

    @DisplayName("Позитивный тест: удаление билд конфигурации")
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
                ResponseSpecs.notFoundWithErrorText(String.format(ApiAtributesOfResponse.NO_BUILD_TYPE_ERROR.getMessage(), buildId)))
                .get(buildId);
    }

    @DisplayName("Негативный тест: удаление несуществующей билд конфигурации")
    @Tag("noCleanupBuild")
    @Tag("noCleanupProject")
    @Test
    public void userDeleteNotExistBuildConfigurationTest(CreateUserResponse user) {
        String buildId = TestDataGenerator.generateBuildId();

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.notFoundWithErrorText(String.format(ApiAtributesOfResponse.NO_BUILD_TYPE_ERROR.getMessage(), buildId)))
                .delete(buildId);
    }

    @DisplayName("Негативный тест: удаление билд конфигурации без прав админа")
    @Tag("noCleanupBuild")
    @Test
    public void userDeleteBuildConfigurationWithoutRulesTest(CreateUserResponse user) {
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

        CreateUserRequest usualUser = AdminSteps.createUserByAdmin(TestDataGenerator.generateUsername(), TestDataGenerator.generatePassword());

        new CrudRequester(
                RequestSpecs.authAsUser(usualUser.getUsername(), usualUser.getPassword()),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.forbiddenWithErrorText(String.format(ApiAtributesOfResponse.YOU_DONT_HAVE_ENOUGH_PERMISSIONS_ERROR.getMessage(), projectId)))
                .delete(buildId);
    }
}
