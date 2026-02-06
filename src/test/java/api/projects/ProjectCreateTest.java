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

public class ProjectCreateTest extends BaseTest {

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

    @DisplayName("Успешное создание проекта")
    @Test
    void shouldCreateProjectSuccessfully() {
        String projectId = TestDataGenerator.generateUsername("PRJ").replace("-", "").toUpperCase();
        String projectName = "Test project " + projectId;
        String parentProjectId = "_Root";

        CreateProjectRequest request = new CreateProjectRequest(projectId, projectName, parentProjectId);

        ProjectResponse response = new ValidatedCrudRequester<ProjectResponse>(
                userSpec,
                Endpoint.PROJECTS,
                ResponseSpecs.requestReturnsOk()
        ).post(request);

        softly.assertThat(response.getId()).as("Поле id").isEqualTo(projectId);
        softly.assertThat(response.getName()).as("Поле name").isEqualTo(projectName);
    }

    @DisplayName("Негативный тест: создание проекта с тем же id")
    @Test
    void shouldNotCreateProjectWithExistingId() {
        String projectId = "PRJ_DUPLICATE_ID";
        String parentProjectId = "_Root";

        CreateProjectRequest first = new CreateProjectRequest(projectId, "First project", parentProjectId);
        new ValidatedCrudRequester<ProjectResponse>(
                userSpec,
                Endpoint.PROJECTS,
                ResponseSpecs.requestReturnsOk()
        ).post(first);

        CreateProjectRequest second = new CreateProjectRequest(projectId, "Second project", parentProjectId);

        new CrudRequester(
                userSpec,
                Endpoint.PROJECTS,
                ResponseSpecs.badRequest()
        ).post(second);
    }
}