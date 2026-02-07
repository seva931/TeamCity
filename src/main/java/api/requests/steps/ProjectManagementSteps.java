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

import static java.lang.reflect.Array.get;

public class ProjectManagementSteps {


    public static ProjectResponse getAllProjectsRaw() {
        return new CrudRequester(RequestSpecs.adminSpec(), Endpoint.PROJECTS, ResponseSpecs.ok());
                .get()
                .extract()
                .as(ProjectResponse.class);

    }

    public static ProjectResponse createProject(String id, String name, ParentProject parentProject, CreateUserResponse user) {
        ProjectResponse projectResponse = new ProjectResponse(id, name, parentProject);
        return new ValidatedCrudRequester<ProjectResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECTS,
                ResponseSpecs.requestReturnsOk())
                .post(projectResponse);
    }

    public static ProjectResponse getProjectById(String projectId,CreateUserResponse user) {
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
     public static String updateProjectName(String projectId, String newName,CreateUserResponse user) {
        RequestSpecification textSpec = new RequestSpecBuilder();
         new CrudRequester(RequestSpecs.authAsUser(user),
                 Endpoint.PROJECT_ID,
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

    public static void updateProjectNameWithWrongContentType(String projectId, String newName,CreateUserResponse user) {
        RequestSpecification wrongSpec = new RequestSpecBuilder();
        new CrudRequester(RequestSpecs.authAsUser(user),
                Endpoint.PROJECT_ID,
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.TEXT)
                .build();

        new CrudRequester(
                wrongSpec,
                Endpoint.PROJECT_NAME,
                ResponseSpecs.notAcceptable()
        ).put(projectId, newName);
    }

   /* public static CreateProjectRequest createProject(String projectId,
                                                     String projectName,
                                                     String parentProjectId,
                                                     CreateUserResponse user) {
        CreateProjectRequest request = new CreateProjectRequest(projectId, projectName, parentProjectId);
        new ProjectManagementSteps(RequestSpecs.authAsUser(user)).createProject(request, user);
        return request;
    }

    public static void deleteProjectById(String projectId, CreateUserResponse user) {
        new ProjectManagementSteps(RequestSpecs.authAsUser(user)).deleteProjectById(projectId);
    }*/


   /* public void deleteProjectById(String projectId) {
        new CrudRequester(
                spec,
                Endpoint.PROJECT_ID,
                ResponseSpecs.noContent()
        ).delete(projectId);
    }*/

  /*public  void updateProjectName(String projectId, String newName) {
      new CrudRequester(
             spec,
             Endpoint.PROJECT_ID,
              ResponseSpecs.noContent()
    ).delete(projectId);
    }*/

   /* public static String updateProjectName(String projectId, String newName) {
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
    }*/
}