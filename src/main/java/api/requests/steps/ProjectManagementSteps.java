package api.requests.steps;

import api.models.CreateProjectRequest;
import api.models.ProjectResponse;
import api.requests.skeleton.Endpoint;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import static io.restassured.RestAssured.given;

public class ProjectManagementSteps {

    public String getProjectsRaw() {
        return given()
                .spec(RequestSpecs.base())
                .when()
                .get(Endpoint.PROJECTS.getUrl())
                .then()
                .spec(ResponseSpecs.ok())
                .extract()
                .asString();
    }

    public ProjectResponse createProject(CreateProjectRequest body) {
        return given()
                .spec(RequestSpecs.base())
                .body(body)
                .when()
                .post(Endpoint.PROJECTS.getUrl())
                .then()
                .spec(ResponseSpecs.ok())
                .extract()
                .as(ProjectResponse.class);
    }

    public ProjectResponse getProjectById(String projectId) {
        String url = String.format(Endpoint.PROJECT_BY_ID.getUrl(), projectId);
        return given()
                .spec(RequestSpecs.base())
                .when()
                .get(url)
                .then()
                .spec(ResponseSpecs.ok())
                .extract()
                .as(ProjectResponse.class);
    }

    public void deleteProjectById(String projectId) {
        String url = String.format(Endpoint.PROJECT_BY_ID.getUrl(), projectId);
        given()
                .spec(RequestSpecs.base())
                .when()
                .delete(url)
                .then()
                .spec(ResponseSpecs.noContent());
    }
}