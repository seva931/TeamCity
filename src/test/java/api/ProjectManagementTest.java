package api;

import api.models.CreateProjectRequest;
import api.models.CreateUserResponse;
import api.models.ProjectResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.steps.ProjectManagementSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.generators.TestDataGenerator;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jupiter.annotation.WithUsersQueue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

@WithUsersQueue
public class ProjectManagementTest extends BaseTest {

    private static final String PARENT_PROJECT_ID = "_Root";
    private static final String NOT_EXISTS_ID = "PRJ_NOT_EXISTS_404";

    private RequestSpecification userSpec;
    private ProjectManagementSteps projectSteps;

    private final List<String> projectsToCleanup = new ArrayList<>();

    @BeforeEach
    void preconditions(CreateUserResponse user) {
        this.userSpec = RequestSpecs.authAsUser(user);
        this.projectSteps = new ProjectManagementSteps(userSpec);
    }

    @AfterEach
    void cleanup() {
        for (String projectId : projectsToCleanup) {
            deleteProjectSilently(projectId);
        }
        projectsToCleanup.clear();
    }

    @DisplayName("Успешное создание проекта")
    @Test
    void shouldCreateProjectSuccessfully() {
        String projectId = generateProjectId();
        String projectName = TestDataGenerator.generateProjectName();

        ProjectResponse response = createProject(projectId, projectName);

        softly.assertThat(response.getId()).as("Поле id").isEqualTo(projectId);
        softly.assertThat(response.getName()).as("Поле name").isEqualTo(projectName);
    }

    @DisplayName("Негативный тест: создание проекта с тем же id")
    @Test
    void shouldNotCreateProjectWithExistingId() {
        String duplicateId = generateProjectId();

        String firstName = TestDataGenerator.generateProjectName();
        String secondName = firstName + "_second";

        createProject(duplicateId, firstName);

        new CrudRequester(
                userSpec,
                Endpoint.PROJECTS,
                ResponseSpecs.badRequest()
        ).post(new CreateProjectRequest(duplicateId, secondName, PARENT_PROJECT_ID));

        ProjectResponse project = projectSteps.getProjectById(duplicateId);
        softly.assertThat(project.getName()).as("Имя проекта не изменилось").isEqualTo(firstName);
    }

    @DisplayName("Позитивный тест: получение списка проектов")
    @Test
    void shouldGetProjectsListSuccessfully() {
        String projectId = generateProjectId();
        String projectName = TestDataGenerator.generateProjectName();

        createProject(projectId, projectName);

        String projectsJson = projectSteps.getAllProjectsRaw();

        softly.assertThat(projectsJson).as("Список проектов содержит id").contains(projectId);
        softly.assertThat(projectsJson).as("Список проектов содержит name").contains(projectName);
    }

    @DisplayName("Негативный тест: получение списка без авторизации")
    @Test
    void shouldReturn401WithoutAuth() {
        var response = new CrudRequester(
                RequestSpecs.builder().build(),
                Endpoint.PROJECTS,
                ResponseSpecs.unauthorized()
        ).get();

        softly.assertThat(response.extract().statusCode())
                .as("HTTP статус ответа")
                .isEqualTo(401);
    }

    @DisplayName("Получение информации о проекте по id")
    @Test
    void shouldGetProjectByIdSuccessfully() {
        String projectId = generateProjectId();
        String projectName = TestDataGenerator.generateProjectName();

        createProject(projectId, projectName);

        ProjectResponse response = projectSteps.getProjectById(projectId);

        softly.assertThat(response.getId()).as("Поле id").isEqualTo(projectId);
        softly.assertThat(response.getName()).as("Поле name").isEqualTo(projectName);
    }

    @DisplayName("Получение информации о проекте по несуществующему id")
    @Test
    void shouldReturn404WhenGetProjectByNonExistingId() {
        var response = new CrudRequester(
                userSpec,
                Endpoint.PROJECT_ID,
                ResponseSpecs.notFound()
        ).get(NOT_EXISTS_ID);

        String body = response.extract().asString();

        softly.assertThat(body).as("Тело ответа содержит ошибки").contains("\"errors\"");
        softly.assertThat(body).as("Тело ответа не содержит полей проекта")
                .doesNotContain("\"webUrl\"")
                .doesNotContain("\"href\"")
                .doesNotContain("\"parentProjectId\"");
    }

    @DisplayName("Обновление имени проекта")
    @Test
    void shouldUpdateProjectNameSuccessfully() {
        String projectId = generateProjectId();
        String initialName = TestDataGenerator.generateProjectName();

        createProject(projectId, initialName);

        String updatedName = (initialName + "_updated").toLowerCase();

        String responseBody = projectSteps.updateProjectName(projectId, updatedName);
        softly.assertThat(responseBody).as("Тело ответа").isEqualTo(updatedName);

        RequestSpecification textSpec = new RequestSpecBuilder()
                .addRequestSpecification(userSpec)
                .setAccept(ContentType.TEXT)
                .setContentType(ContentType.TEXT)
                .build();

        String actualNameParam = new CrudRequester(
                textSpec,
                Endpoint.PROJECT_NAME,
                ResponseSpecs.requestReturnsOk()
        ).get(projectId).extract().asString();

        softly.assertThat(actualNameParam).as("Параметр name").isEqualTo(updatedName);
    }

    @DisplayName("Негативный тест: обновление имени проекта с неверным Content-Type")
    @Test
    void shouldNotUpdateProjectNameWithWrongContentType() {
        String projectId = generateProjectId();
        String initialName = TestDataGenerator.generateProjectName();

        createProject(projectId, initialName);

        String updatedName = (initialName + "_updated").toLowerCase();

        int statusCode = projectSteps.updateProjectNameWithWrongContentType(projectId, updatedName);

        softly.assertThat(statusCode).as("HTTP статус ответа").isNotEqualTo(200);
        softly.assertThat(projectSteps.getProjectById(projectId).getName())
                .as("Имя проекта не изменилось")
                .isEqualTo(initialName);
    }

    @DisplayName("Удаление проекта по id")
    @Test
    void shouldDeleteProjectByIdSuccessfully() {
        String projectId = generateProjectId();
        String projectName = TestDataGenerator.generateProjectName();

        createProject(projectId, projectName);

        projectSteps.deleteProjectById(projectId);

        var getAfterDeleteResponse = new CrudRequester(
                userSpec,
                Endpoint.PROJECT_ID,
                ResponseSpecs.notFound()
        ).get(projectId);

        softly.assertThat(getAfterDeleteResponse.extract().statusCode())
                .as("HTTP статус получения удалённого проекта")
                .isEqualTo(404);

        projectsToCleanup.remove(projectId);
    }

    @DisplayName("Удаление несуществующего проекта")
    @Test
    void shouldReturn404WhenDeleteNonExistingProject() {
        var response = new CrudRequester(
                userSpec,
                Endpoint.PROJECT_ID,
                ResponseSpecs.notFound()
        ).delete(NOT_EXISTS_ID);

        softly.assertThat(response.extract().statusCode())
                .as("HTTP статус ответа")
                .isEqualTo(404);
    }

    private ProjectResponse createProject(String projectId, String projectName) {
        CreateProjectRequest request = new CreateProjectRequest(projectId, projectName, PARENT_PROJECT_ID);
        ProjectResponse response = projectSteps.createProject(request);
        projectsToCleanup.add(projectId);
        return response;
    }

    private void deleteProjectSilently(String projectId) {
        given()
                .spec(userSpec)
                .delete(Endpoint.PROJECT_ID.getFormatedUrl(projectId));
    }

    private String generateProjectId() {
        return TestDataGenerator.generateUsername("PRJ").replace("-", "").toUpperCase();
    }
}