package api.requests.steps;

import api.models.CreateProjectRequest;
import api.models.ProjectResponse;
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

    public void updateProjectName(String projectId, String newName) {
        new CrudRequester(
                RequestSpecs.adminSpec(ContentType.TEXT),
                Endpoint.PROJECT_NAME,
                ResponseSpecs.requestReturnsOk()
        ).put(projectId, newName);
    }
}