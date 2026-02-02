package api.requests.steps;

import api.models.CreateProjectRequest;
import api.models.ProjectResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import static io.restassured.RestAssured.given;

public class ProjectManagementSteps {

  /*  public String getProjectsRaw() {
        return given()
                .spec(RequestSpecs.base())
                .when()
                .get(Endpoint.PROJECTS.getUrl())
                .then()
                .spec(ResponseSpecs.ok())
                .extract()
                .asString();
    }*/

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
}