package api.requests.steps;

import api.models.*;
import api.configs.Config;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import io.restassured.http.ContentType;

public class ProjectManagementSteps {

    public void getAllProjects() {
        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.PROJECTS,
                ResponseSpecs.requestReturnsOk()
        ).get();
    }

    public ProjectResponse createProject(CreateProjectRequest body) {
       return new ValidatedCrudRequester<ProjectResponse>(RequestSpecs.adminSpec(),
               Endpoint.PROJECTS, ResponseSpecs.requestReturnsOk()).post(body);
    }

    public ProjectResponse getProjectById(long projectId) {
        return new ValidatedCrudRequester<ProjectResponse>(RequestSpecs.adminSpec(),
                Endpoint.PROJECTS, ResponseSpecs.requestReturnsOk()).get(projectId);
    }

    public void deleteProjectById(long projectId) {
         new CrudRequester(RequestSpecs.adminSpec(),
                Endpoint.PROJECTS, ResponseSpecs.requestReturnsOk()).delete(projectId);
    }

    public static void deleteProjectById(String projectId, CreateUserResponse user) {
         new CrudRequester(RequestSpecs.authAsUser(user),
                Endpoint.PROJECT_BY_ID,
                 ResponseSpecs.noContent())
                 .delete(projectId);
    }

    public void updateProjectName(String projectId, String newName) {
        new CrudRequester(
                RequestSpecs.authAsUser(
                        Config.getProperty("admin.login"),
                        Config.getProperty("admin.password"),
                        ContentType.TEXT
                ),
                Endpoint.PROJECT_NAME,
                ResponseSpecs.requestReturnsOk()
        ).put(projectId, newName);
    }

    public static CreateProjectRequest createProject(String projectId, String projectName, String parentProjectId, CreateUserResponse user) {
        CreateProjectRequest createProjectRequest = new CreateProjectRequest(projectId, projectName, parentProjectId);

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECTS,
                ResponseSpecs.requestReturnsOk())
                .post(createProjectRequest);
        return createProjectRequest;
    }
}