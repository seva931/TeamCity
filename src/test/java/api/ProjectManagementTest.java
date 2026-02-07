package api;

import api.models.CreateProjectRequest;
import api.models.CreateUserResponse;
import api.models.ErrorResponse;
import api.models.ProjectResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.steps.ProjectManagementSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.data.ApiAtributesOfResponse;
import common.generators.TestDataGenerator;
import io.restassured.http.ContentType;
import jupiter.annotation.WithUsersQueue;
import jupiter.extension.UsersQueueExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

import static common.generators.TestDataGenerator.generateProjectID;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith({UsersQueueExtension.class})
public class ProjectManagementTest extends BaseTest {

    private static final String PARENT_PROJECT_ID = "_Root";
    private static final String NOT_EXISTS_ID = "PRJ_NOT_EXISTS_404";

    private ProjectManagementSteps ;

    private final List<String> projectsToCleanup = new ArrayList<>();

    @BeforeEach
    void preconditions() {
        /*this. = new ProjectManagementSteps(userSpec);*/
    }

    /*@AfterEach
    void cleanup() {
        for (String projectId : projectsToCleanup) {
            deleteProjectSilently(projectId);
        }
        projectsToCleanup.clear();
    }*/

    @WithUsersQueue
    @DisplayName("Успешное создание проекта")
    @Test
    void shouldCreateProjectSuccessfully(CreateUserResponse user) {
        String projectId = generateProjectID();
        String projectName = TestDataGenerator.generateProjectName();
        CreateProjectRequest request = new CreateProjectRequest(projectId, projectName, PARENT_PROJECT_ID);
        new ValidatedCrudRequester<ProjectResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECTS,
                ResponseSpecs.requestReturnsOk()
        ).post(request);

        ProjectResponse response = createProject(projectId, projectName,user);

        softly.assertThat(response.getId()).as("Поле id").isEqualTo(projectId);
        softly.assertThat(response.getName()).as("Поле name").isEqualTo(projectName);
    }

    @WithUsersQueue
    @DisplayName("Негативный тест: создание проекта с тем же id")
    @Test
    void shouldNotCreateProjectWithExistingId(CreateUserResponse user) {
        String duplicateId = generateProjectID();

        String firstName = TestDataGenerator.generateProjectName();
        String secondName = (firstName + "_second").toLowerCase();

        createProject(duplicateId, firstName,user);

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECTS,
                ResponseSpecs.badRequest()
        ).post(new CreateProjectRequest(duplicateId, secondName, PARENT_PROJECT_ID));

        ProjectResponse project = .getProjectById(duplicateId);

        softly.assertThat(project.getName())
                .as("Имя проекта не изменилось")
                .isEqualTo(firstName);
    }

    @WithUsersQueue
    @DisplayName("Позитивный тест: получение списка проектов")
    @Test
    void shouldGetProjectsListSuccessfully(CreateUserResponse user) {
        String projectId = generateProjectID();
        String projectName = TestDataGenerator.generateProjectName();

        createProject(projectId, projectName,user);

        String projectsJson = .getAllProjectsRaw();

        softly.assertThat(projectsJson).as("Список проектов содержит id").contains(projectId);
        softly.assertThat(projectsJson).as("Список проектов содержит name").contains(projectName);
    }

    @WithUsersQueue
    @DisplayName("Негативный тест: получение списка без авторизации")
    @Test
    void shouldReturn401WithoutAuth(CreateUserResponse user) {
        String body = new CrudRequester(
                RequestSpecs.builder().build(),
                Endpoint.PROJECTS,
                ResponseSpecs.unauthorized()
        ).get().extract().asString();

        softly.assertThat(body)
                .as("Тело ответа содержит сообщение об авторизации")
                .contains("Authentication required");

        softly.assertThat(body)
                .as("Тело ответа не содержит данных проектов")
                .doesNotContain("\"project\"")
                .doesNotContain("\"webUrl\"")
                .doesNotContain("\"href\"");
    }

    @WithUsersQueue
    @DisplayName("Получение информации о проекте по id")
    @Test
    void shouldGetProjectByIdSuccessfully(CreateUserResponse user) {
        String projectId = generateProjectID();
        String projectName = TestDataGenerator.generateProjectName();

        createProject(projectId, projectName,user);

        ProjectResponse response = .getProjectById(projectId);

        softly.assertThat(response.getId()).as("Поле id").isEqualTo(projectId);
        softly.assertThat(response.getName()).as("Поле name").isEqualTo(projectName);
    }

    @WithUsersQueue
    @DisplayName("Получение информации о проекте по несуществующему id")
    @Test
    void shouldReturn404WhenGetProjectByNonExistingId(CreateUserResponse user) {
        var response = new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECT_ID,
                ResponseSpecs.notFound()
        ).get(NOT_EXISTS_ID);

        String message = response.extract().path("errors[0].message");

        softly.assertThat(message)
                .as("errors[0].message содержит запрошенный id")
                .contains(NOT_EXISTS_ID);
    }

    @WithUsersQueue
    @DisplayName("Обновление имени проекта")
    @Test
    void shouldUpdateProjectNameSuccessfully(CreateUserResponse user) {
        String projectId = generateProjectID();
        String initialName = TestDataGenerator.generateProjectName();

        createProject(projectId, initialName,user);

        String updatedName = (initialName + "_updated").toLowerCase();

        String responseBody = new CrudRequester(
                RequestSpecs.authAsUser(user, ContentType.TEXT),
                Endpoint.PROJECT_NAME,
                ResponseSpecs.requestReturnsOk()
        ).put(projectId, updatedName)
                .extract().asString();

        softly.assertThat(responseBody).as("Тело ответа").isEqualTo(updatedName);

        softly.assertThat(getProjectNameParam(projectId, user))
                .as("Параметр name")
                .isEqualTo(updatedName);
    }

 /*   @Disabled
    @WithUsersQueue
    @DisplayName("Негативный тест: обновление имени проекта с неверным Content-Type")
    @Test
    void shouldNotUpdateProjectNameWithWrongContentType(CreateUserResponse user) {
        String projectId = generateProjectID();
        String initialName = TestDataGenerator.generateProjectName();
        createProject(projectId, initialName);

        .updateProjectName(projectId, initialName);

        String updatedName = (initialName + "_updated").toLowerCase();

        .updateProjectNameWithWrongContentType(projectId, updatedName);

        softly.assertThat(getProjectNameParam(projectId))
                .as("Параметр name не изменился")
                .isEqualTo(initialName);
    }*/

    @WithUsersQueue
    @DisplayName("Удаление проекта по id")
    @Test
    void shouldDeleteProjectByIdSuccessfully(CreateUserResponse user) {
        String projectId = generateProjectID();
        String projectName = TestDataGenerator.generateProjectName();

        createProject(projectId, projectName,user);

        .deleteProjectById(projectId);

        String projectsJson = .getAllProjectsRaw();

        softly.assertThat(projectsJson)
                .as("Список проектов не содержит id удалённого проекта")
                .doesNotContain(projectId);

        projectsToCleanup.remove(projectId);
    }

    @WithUsersQueue
    @DisplayName("Удаление несуществующего проекта")
    @Test
    void shouldReturn404WhenDeleteNonExistingProject(CreateUserResponse user) {
        ErrorResponse errorResponse = new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECT_ID,
                ResponseSpecs.notFound()
        ).delete(NOT_EXISTS_ID)
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrors())
                .hasSize(1)
                .filteredOn(e ->
                        e.getMessage().equals(
                                ApiAtributesOfResponse.NO_PROJECT_FOUND_BY_ID_ERROR.getFormatedText(
                                        NOT_EXISTS_ID,
                                        NOT_EXISTS_ID
                                )))
                .hasSize(1);
    }

    private ProjectResponse createProject(String projectId, String projectName,CreateUserResponse user) {
        CreateProjectRequest request = new CreateProjectRequest(projectId, projectName, PARENT_PROJECT_ID);
        ProjectResponse response = .createProject(request,user);
        projectsToCleanup.add(projectId);
        return response;
    }

    private String getProjectNameParam(String projectId, CreateUserResponse user) {

        return new CrudRequester(
                RequestSpecs.authAsUser(user, ContentType.TEXT),
                Endpoint.PROJECT_NAME,
                ResponseSpecs.requestReturnsOk()
        ).get(projectId).extract().asString();
    }
}