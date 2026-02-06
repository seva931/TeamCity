package api.projects;

import api.BaseTest;
import api.models.CreateProjectRequest;
import api.models.ProjectResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.generators.TestDataGenerator;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProjectGetListTest extends BaseTest {

    private RequestSpecification userSpec;

    @BeforeEach
    void preconditions() {
        var adminUser = AdminSteps.createUserWithRole(
                TestDataGenerator.generateUsername("admin"),
                TestDataGenerator.generatePassword(),
                api.models.RoleId.SYSTEM_ADMIN
        );
        this.userSpec = RequestSpecs.authAsUser(adminUser.getUsername(), adminUser.getTestData().getPassword());
    }

    @DisplayName("Позитивный тест: получение списка проектов")
    @Test
    void shouldGetProjectsListSuccessfully() {
        String projectId = TestDataGenerator.generateUsername("PRJ").replace("-", "").toUpperCase();
        String projectName = "Test project " + projectId;
        String parentProjectId = "_Root";

        new ValidatedCrudRequester<ProjectResponse>(
                userSpec,
                Endpoint.PROJECTS,
                ResponseSpecs.requestReturnsOk()
        ).post(new CreateProjectRequest(projectId, projectName, parentProjectId));

        String projectsJson = new CrudRequester(
                userSpec,
                Endpoint.PROJECTS,
                ResponseSpecs.requestReturnsOk()
        ).get().extract().asString();
        
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
}