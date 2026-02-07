package api.requests.steps;

import api.models.CreateProjectRequest;
import api.models.CreateUserResponse;
import api.models.ProjectResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class ProjectManagementSteps {

    private final RequestSpecification spec;

    public ProjectManagementSteps(RequestSpecification spec) {
        this.spec = spec;
    }

    public String getAllProjectsRaw() {
        return new CrudRequester(
                spec,
                Endpoint.PROJECTS,
                ResponseSpecs.requestReturnsOk()
        ).get().extract().asString();
    }

    public ProjectResponse createProject(CreateProjectRequest body) {
        return new ValidatedCrudRequester<ProjectResponse>(
                spec,
                Endpoint.PROJECTS,
                ResponseSpecs.requestReturnsOk()
        ).post(body);
    }

    public ProjectResponse getProjectById(String projectId) {
        return new ValidatedCrudRequester<ProjectResponse>(
                spec,
                Endpoint.PROJECT_ID,
                ResponseSpecs.requestReturnsOk()
        ).get(projectId);
    }

    public void deleteProjectById(String projectId) {
        new CrudRequester(
                spec,
                Endpoint.PROJECT_ID,
                ResponseSpecs.noContent()
        ).delete(projectId);
    }

    public String updateProjectName(String projectId, String newName) {
        RequestSpecification textSpec = new RequestSpecBuilder()
                .addRequestSpecification(spec)
                .setContentType(ContentType.TEXT)
                .setAccept(ContentType.TEXT)
                .build();

        return new CrudRequester(
                textSpec,
                Endpoint.PROJECT_NAME,
                ResponseSpecs.requestReturnsOk()
        ).put(projectId, newName)
                .extract().asString();
    }

    public void updateProjectNameWithWrongContentType(String projectId, String newName) {
        RequestSpecification wrongSpec = new RequestSpecBuilder()
                .addRequestSpecification(spec)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.TEXT)
                .build();

        new CrudRequester(
                wrongSpec,
                Endpoint.PROJECT_NAME,
                ResponseSpecs.notAcceptable()
        ).put(projectId, newName);
    }

    public static CreateProjectRequest createProject(String projectId,
                                                     String projectName,
                                                     String parentProjectId,
                                                     CreateUserResponse user) {
        CreateProjectRequest request = new CreateProjectRequest(projectId, projectName, parentProjectId);
        new ProjectManagementSteps(RequestSpecs.authAsUser(user)).createProject(request);
        return request;
    }

    public static void deleteProjectById(String projectId, CreateUserResponse user) {
        new ProjectManagementSteps(RequestSpecs.authAsUser(user)).deleteProjectById(projectId);
    }
}