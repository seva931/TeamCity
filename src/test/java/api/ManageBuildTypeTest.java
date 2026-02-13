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
import common.generators.RandomModelGenerator;
import common.generators.TestDataGenerator;
import jupiter.annotation.WithProject;
import jupiter.annotation.WithUsersQueue;
import jupiter.extension.ProjectExtension;
import jupiter.extension.UsersQueueExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({UsersQueueExtension.class, ProjectExtension.class})
    public class ManageBuildTypeTest extends BaseTest {

    @DisplayName("Позитивный тест: создание билд конфигурации")
    @WithUsersQueue
    @WithProject
    @Test
    public void userCreateBuildTypeTest(CreateUserResponse user, CreateProjectRequest project) {
        CreateBuildTypeRequest createBuildTypeRequest = RandomModelGenerator.builder(CreateBuildTypeRequest.class).withProjectId(project.getId()).build();

        CreateBuildTypeResponse createBuildTypeResponse = new ValidatedCrudRequester<CreateBuildTypeResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk())
                .post(createBuildTypeRequest);

        ModelAssertions.assertThatModels(createBuildTypeRequest, createBuildTypeResponse).match();

        CreateBuildTypeResponse createdBuild = BuildManageSteps.getAllBuildTypes().stream()
                .filter(build -> build.getId().equals(createBuildTypeResponse.getId())).findFirst().get();

        ModelAssertions.assertThatModels(createBuildTypeRequest, createdBuild).match();
    }

    @DisplayName("Негативный тест: создание билд конфигурации с именем уже созданной конфигурации")
    @WithUsersQueue
    @WithProject
    @Test
    public void userCanNotCreateBuildTypeWithSameNameTest(CreateUserResponse user, CreateProjectRequest project) {
        CreateBuildTypeRequest createFirstBuildTypeRequest = BuildManageSteps.createBuildType(project.getId()).request();

        CreateBuildTypeRequest createSecondBuildTypeRequest = RandomModelGenerator.builder(CreateBuildTypeRequest.class).withName(createFirstBuildTypeRequest.getName()).withProjectId(project.getId()).build();
        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.badRequestWithErrorText(ApiAtributesOfResponse.BUILD_CONFIGURATION_WITH_SUCH_NAME_ALREADY_EXISTS_ERROR.getFormatedText(createFirstBuildTypeRequest.getName(), project.getName())))
                .post(createSecondBuildTypeRequest);

        boolean isFind = BuildManageSteps.getAllBuildTypes().stream()
                .anyMatch(build -> build.getId().equals(createSecondBuildTypeRequest.getId()));

        assertFalse(isFind);
    }

    @DisplayName("Позитивный тест: получение информации о созданной билд конфигурации")
    @WithUsersQueue
    @WithProject
    @Test
    public void userGetInfoBuildTypeTest(CreateUserResponse user, CreateProjectRequest project) {
        CreateBuildTypeRequest createBuildTypeRequest = BuildManageSteps.createBuildType(project.getId()).request();

        GetInfoBuildTypeResponse getInfoBuildTypeResponse = new ValidatedCrudRequester<GetInfoBuildTypeResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.requestReturnsOk())
                .get(createBuildTypeRequest.getId());

        ModelAssertions.assertThatModels(createBuildTypeRequest, getInfoBuildTypeResponse).match();
    }

    @DisplayName("Негативный тест: получение информации о не существующей билд конфигурации")
    @WithUsersQueue
    @Test
    public void userGetInfoAboutNotExistBuildTypeTest(CreateUserResponse user) {
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
    public void userGetInfoBuildTypeListTest(CreateUserResponse user, CreateProjectRequest project) {
        CreateBuildTypeRequest createBuildTypeRequest = BuildManageSteps.createBuildType(project.getId()).request();

        GetBuildListInfoResponse getBuildListInfoResponse = new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk())
                .get().extract().as(GetBuildListInfoResponse.class);

        softly.assertThat(getBuildListInfoResponse.getCount())
                .as("Поле count")
                .isNotNull();

        boolean isFind = BuildManageSteps.getAllBuildTypes().stream()
                .anyMatch(build -> build.getId().equals(createBuildTypeRequest.getId()));

        assertTrue(isFind);
    }

    @DisplayName("Позитивный тест: удаление билд конфигурации")
    @WithUsersQueue
    @WithProject
    @Test
    public void userDeleteBuildTypeTest(CreateUserResponse user, CreateProjectRequest project) {
        CreateBuildTypeRequest createBuildTypeRequest = BuildManageSteps.createBuildType(project.getId()).request();

        boolean isFindCreatedBuildType = BuildManageSteps.getAllBuildTypes().stream()
                .anyMatch(build -> build.getId().equals(createBuildTypeRequest.getId()));

        assertTrue(isFindCreatedBuildType);

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.noContent())
                .delete(createBuildTypeRequest.getId());

        boolean isFindDeletedBuildType = BuildManageSteps.getAllBuildTypes().stream()
                .anyMatch(build -> build.getId().equals(createBuildTypeRequest.getId()));

        assertFalse(isFindDeletedBuildType);
    }

    @DisplayName("Негативный тест: удаление несуществующей билд конфигурации")
    @WithUsersQueue
    @Test
    public void userDeleteNotExistBuildTypeTest(CreateUserResponse user) {
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
    public void userDeleteBuildTypeWithoutRulesTest(CreateUserResponse user, CreateProjectRequest project) {
        CreateBuildTypeRequest createBuildTypeRequest = BuildManageSteps.createBuildType(project.getId()).request();

        boolean isFindCreatedBuildType = BuildManageSteps.getAllBuildTypes().stream()
                .anyMatch(build -> build.getId().equals(createBuildTypeRequest.getId()));

        assertTrue(isFindCreatedBuildType);

        CreateUserRequest usualUser = AdminSteps.createUserByAdmin(TestDataGenerator.generateUsername(), TestDataGenerator.generatePassword());

        new CrudRequester(
                RequestSpecs.authAsUser(usualUser.getUsername(), usualUser.getPassword()),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.forbiddenWithErrorText(ApiAtributesOfResponse.YOU_DONT_HAVE_ENOUGH_PERMISSIONS_ERROR.getFormatedText(project.getId())))
                .delete(createBuildTypeRequest.getId());

        boolean isFindDeletedBuildType = BuildManageSteps.getAllBuildTypes().stream()
                .anyMatch(build -> build.getId().equals(createBuildTypeRequest.getId()));

        assertTrue(isFindDeletedBuildType);
    }
}
