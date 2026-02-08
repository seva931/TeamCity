package api.requests.steps;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class ProjectManagementSteps {


    public static ProjectListResponse getAllProjects(CreateUserResponse user) {
        return new CrudRequester(RequestSpecs.authAsUser(user), Endpoint.PROJECTS, ResponseSpecs.ok())
                .get()
                .extract()
                .as(ProjectListResponse.class);
    }

    public static ProjectResponse createProject(String id, String name, ParentProject parentProject, CreateUserResponse user) {
        CreateProjectRequest request = CreateProjectRequest.builder()
                .id(id)
                .name(name)
                .parentProject(parentProject)
                .build();

        return new ValidatedCrudRequester<ProjectResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECTS,
                ResponseSpecs.requestReturnsOk())
                .post(request);
    }

    public static ProjectResponse getProjectById(String projectId, CreateUserResponse user) {
        return new ValidatedCrudRequester<ProjectResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECT_ID,
                ResponseSpecs.requestReturnsOk())
                .get(projectId);
    }

    public static void deleteProjectByIdQuietly(String projectId, CreateUserResponse user) {
        new CrudRequester(RequestSpecs.authAsUser(user),
                Endpoint.PROJECT_ID,
                ResponseSpecs.deletesQuietly())
                .delete(projectId);
    }

    public static String updateProjectName(String projectId, String newName, CreateUserResponse user) {
        RequestSpecification textSpec = new RequestSpecBuilder()
                .addRequestSpecification(RequestSpecs.authAsUser(user))
                .setContentType(ContentType.TEXT)
                .setAccept(ContentType.TEXT)
                .build();

        return new CrudRequester(
                textSpec,
                Endpoint.PROJECT_NAME,
                ResponseSpecs.requestReturnsOk()
        ).put(projectId, newName)
                .extract()
                .asString();
    }

    public static void updateProjectNameWithWrongContentType(String projectId, String newName, CreateUserResponse user) {
        RequestSpecification wrongSpec = new RequestSpecBuilder()
                .addRequestSpecification(RequestSpecs.authAsUser(user))
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.TEXT)
                .build();

        new CrudRequester(
                wrongSpec,
                Endpoint.PROJECT_NAME,
                ResponseSpecs.notAcceptable()
        ).put(projectId, newName);
    }

    public static String getProjectNameParam(String projectId, CreateUserResponse user) {
        return new CrudRequester(
                RequestSpecs.authAsUser(user, ContentType.TEXT),
                Endpoint.PROJECT_NAME,
                ResponseSpecs.requestReturnsOk()
        ).get(projectId)
                .extract()
                .asString();
    }
}