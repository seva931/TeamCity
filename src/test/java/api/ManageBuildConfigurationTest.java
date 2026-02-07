package api;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.requests.steps.BuildManageSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.data.ApiAtributesOfResponse;
import common.generators.TestDataGenerator;
import jupiter.annotation.WithProject;
import jupiter.annotation.WithUsersQueue;
import jupiter.extension.ProjectExtension;
import jupiter.extension.UsersQueueExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({UsersQueueExtension.class, ProjectExtension.class})
    public class ManageBuildConfigurationTest extends BaseTest {

    @DisplayName("Позитивный тест: создание билд конфигурации")
    @WithUsersQueue
    @WithProject
    @Test
    public void userCreateBuildConfigurationTest(CreateUserResponse user, CreateProjectRequest project) {
        String buildName = TestDataGenerator.generateBuildName();
        String buildId = TestDataGenerator.generateBuildId(project.getId(), buildName);

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

    @DisplayName("Негативный тест: создание билд конфигурации с именем уже созданной конфигурации")
    @WithUsersQueue
    @WithProject
    @Test
    public void userCanNotCreateBuildConfigurationWithSameNameTest(CreateUserResponse user, CreateProjectRequest project) {
        String buildName = TestDataGenerator.generateBuildName();
        String buildId = TestDataGenerator.generateBuildId(project.getId(), buildName);
        BuildManageSteps.createBuildConfiguration(project.getId(), buildId, buildName);

        CreateBuildConfigurationRequest createBuildConfigurationRequest = new CreateBuildConfigurationRequest(buildId + "1", buildName, project.getId());

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.badRequestWithErrorText(ApiAtributesOfResponse.BUILD_CONFIGURATION_WITH_SUCH_NAME_ALREADY_EXISTS_ERROR.getFormatedText(buildName, project.getName())))
                .post(createBuildConfigurationRequest);
    }

    @DisplayName("Позитивный тест: получение информации о созданной билд конфигурации")
    @WithUsersQueue
    @WithProject
    @Test
    public void userGetInfoBuildConfigurationTest(CreateUserResponse user, CreateProjectRequest project) {
        String buildName = TestDataGenerator.generateBuildName();
        String buildId = TestDataGenerator.generateBuildId(project.getId(), buildName);

        BuildManageSteps.createBuildConfiguration(project.getId(), buildId, buildName);

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
                .isEqualTo(project.getId());
    }

    @DisplayName("Негативный тест: получение информации о не существующей билд конфигурации")
    @WithUsersQueue
    @Test
    public void userGetInfoAboutNotExistBuildConfigurationTest(CreateUserResponse user) {
        String buildId = TestDataGenerator.generateBuildId();

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.notFoundWithErrorText(ApiAtributesOfResponse.NO_BUILD_TYPE_ERROR.getFormatedText(buildId)))
                .get(buildId);
    }

    @DisplayName("Позитивный тест: получение информации о списке созданных билд конфигураций")
    @WithUsersQueue
    @WithProject
    @Test
    public void userGetInfoBuildConfigurationsListTest(CreateUserResponse user, CreateProjectRequest project) {
        String buildName = TestDataGenerator.generateBuildName();
        String buildId = TestDataGenerator.generateBuildId(project.getId(), buildName);

        BuildManageSteps.createBuildConfiguration(project.getId(), buildId, buildName);

        GetBuldListInfoResponse getBuldListInfoResponse = new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk())
                .get().extract().as(GetBuldListInfoResponse.class);

        softly.assertThat(getBuldListInfoResponse.getCount())
                .as("Поле count")
                .isNotNull();
        softly.assertThat(getBuldListInfoResponse.getBuildType())
                .as("Список билд конфигураций существующих")
                .isNotEmpty();
    }

    @DisplayName("Позитивный тест: удаление билд конфигурации")
    @WithUsersQueue
    @WithProject
    @Test
    public void userDeleteBuildConfigurationTest(CreateUserResponse user, CreateProjectRequest project) {
        String buildName = TestDataGenerator.generateBuildName();
        String buildId = TestDataGenerator.generateBuildId(project.getId(), buildName);

        BuildManageSteps.createBuildConfiguration(project.getId(), buildId, buildName);

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
                ResponseSpecs.notFoundWithErrorText(ApiAtributesOfResponse.NO_BUILD_TYPE_ERROR.getFormatedText(buildId)))
                .get(buildId);
    }

    @DisplayName("Негативный тест: удаление несуществующей билд конфигурации")
    @WithUsersQueue
    @Test
    public void userDeleteNotExistBuildConfigurationTest(CreateUserResponse user) {
        String buildId = TestDataGenerator.generateBuildId();

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.notFoundWithErrorText(ApiAtributesOfResponse.NO_BUILD_TYPE_ERROR.getFormatedText(buildId)))
                .delete(buildId);
    }

    @DisplayName("Негативный тест: удаление билд конфигурации без прав админа")
    @WithUsersQueue
    @WithProject
    @Test
    public void userDeleteBuildConfigurationWithoutRulesTest(CreateUserResponse user, CreateProjectRequest project) {
        String buildName = TestDataGenerator.generateBuildName();
        String buildId = TestDataGenerator.generateBuildId(project.getId(), buildName);

        BuildManageSteps.createBuildConfiguration(project.getId(), buildId, buildName);

        //проверка, что конфигурация создалась
        GetInfoBuildConfigurationResponse getInfoBuildConfigurationResponse = BuildManageSteps.getInfoBuildConfiguration(buildId, user);
        softly.assertThat(getInfoBuildConfigurationResponse.getId())
                .as("Поле id")
                .isEqualTo(buildId);

        CreateUserRequest usualUser = AdminSteps.createUserByAdmin(TestDataGenerator.generateUsername(), TestDataGenerator.generatePassword());

        new CrudRequester(
                RequestSpecs.authAsUser(usualUser.getUsername(), usualUser.getPassword()),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.forbiddenWithErrorText(ApiAtributesOfResponse.YOU_DONT_HAVE_ENOUGH_PERMISSIONS_ERROR.getFormatedText(project.getId())))
                .delete(buildId);
    }
}
