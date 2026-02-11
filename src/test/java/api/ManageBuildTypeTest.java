package api;

import api.models.*;
import api.models.comparison.ModelAssertions;
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

import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith({UsersQueueExtension.class, ProjectExtension.class})
    public class ManageBuildTypeTest extends BaseTest {

    @DisplayName("Позитивный тест: создание билд конфигурации")
    @WithUsersQueue
    @WithProject
    @Test
    public void userCreateBuildConfigurationTest(CreateUserResponse user, CreateProjectRequest project) {
        String buildName = TestDataGenerator.generateBuildName();
        String buildId = TestDataGenerator.generateBuildId(project.getId(), buildName);

        CreateBuildTypeRequest createBuildTypeRequest = new CreateBuildTypeRequest(buildId, buildName, project.getId());

        CreateBuildTypeResponse createBuildTypeResponse = new ValidatedCrudRequester<CreateBuildTypeResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk())
                .post(createBuildTypeRequest);

        ModelAssertions.assertThatModels(createBuildTypeRequest, createBuildTypeResponse).match();

        CreateBuildTypeResponse createdBuild = BuildManageSteps.getAllBuilds().stream()
                .filter(build -> build.getId().equals(createBuildTypeResponse.getId())).findFirst().get();

        ModelAssertions.assertThatModels(createBuildTypeRequest, createdBuild).match();
    }

    @DisplayName("Негативный тест: создание билд конфигурации с именем уже созданной конфигурации")
    @WithUsersQueue
    @WithProject
    @Test
    public void userCanNotCreateBuildConfigurationWithSameNameTest(CreateUserResponse user, CreateProjectRequest project) {
        String buildName = TestDataGenerator.generateBuildName();
        String buildId = TestDataGenerator.generateBuildId(project.getId(), buildName);
        BuildManageSteps.createBuildType(project.getId(), buildId, buildName);

        CreateBuildTypeRequest createBuildTypeRequest = new CreateBuildTypeRequest(buildId + "1", buildName, project.getId());

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.badRequestWithErrorText(ApiAtributesOfResponse.BUILD_CONFIGURATION_WITH_SUCH_NAME_ALREADY_EXISTS_ERROR.getFormatedText(buildName, project.getName())))
                .post(createBuildTypeRequest);

        boolean isFind = BuildManageSteps.getAllBuilds().stream()
                .anyMatch(build -> build.getId().equals(createBuildTypeRequest.getId()));

        assertFalse(isFind);
    }

    @DisplayName("Позитивный тест: получение информации о созданной билд конфигурации")
    @WithUsersQueue
    @WithProject
    @Test
    public void userGetInfoBuildConfigurationTest(CreateUserResponse user, CreateProjectRequest project) {
        String buildName = TestDataGenerator.generateBuildName();
        String buildId = TestDataGenerator.generateBuildId(project.getId(), buildName);

        CreateBuildTypeRequest createBuildTypeRequest = BuildManageSteps.createBuildType(project.getId(), buildId, buildName).request();

        GetInfoBuildTypeResponse getInfoBuildTypeResponse = new ValidatedCrudRequester<GetInfoBuildTypeResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.requestReturnsOk())
                .get(buildId);

        ModelAssertions.assertThatModels(createBuildTypeRequest, getInfoBuildTypeResponse).match();

        softly.assertThat(getInfoBuildTypeResponse.getId())
                .as("Поле id")
                .isEqualTo(buildId);

        softly.assertThat(getInfoBuildTypeResponse.getName())
                .as("Поле name")
                .isEqualTo(buildName);

        softly.assertThat(getInfoBuildTypeResponse.getProjectId())
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

        BuildManageSteps.createBuildType(project.getId(), buildId, buildName);

        GetBuildListInfoResponse getBuildListInfoResponse = new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk())
                .get().extract().as(GetBuildListInfoResponse.class);

        softly.assertThat(getBuildListInfoResponse.getCount())
                .as("Поле count")
                .isNotNull();
        softly.assertThat(getBuildListInfoResponse.getBuildType())
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

        BuildManageSteps.createBuildType(project.getId(), buildId, buildName);

        //проверка, что конфигурация создалась
        GetInfoBuildTypeResponse getInfoBuildTypeResponse = BuildManageSteps.getInfoBuildType(buildId, user);
        softly.assertThat(getInfoBuildTypeResponse.getId())
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

        BuildManageSteps.createBuildType(project.getId(), buildId, buildName);

        //проверка, что конфигурация создалась
        GetInfoBuildTypeResponse getInfoBuildTypeResponse = BuildManageSteps.getInfoBuildType(buildId, user);
        softly.assertThat(getInfoBuildTypeResponse.getId())
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
