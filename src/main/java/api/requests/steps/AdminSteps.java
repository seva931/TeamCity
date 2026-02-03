package api.requests.steps;

import api.models.CreateBuildConfigurationRequest;
import api.models.CreateProjectRequest;
import api.models.CreateUserRequest;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.generators.TestDataGenerator;

public class AdminSteps {

    public static CreateUserRequest createAdminUser() {
        CreateUserRequest createUserRequest = CreateUserRequest.systemAdmin(
                TestDataGenerator.generateUsername(),
                TestDataGenerator.generatePassword());

        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.USERS,
                ResponseSpecs.requestReturnsOk())
                .post(createUserRequest);

        return createUserRequest;
    }

    public static CreateProjectRequest createProject(String projectId, String projectName, String parentProjectId) {
        CreateProjectRequest createProjectRequest = new CreateProjectRequest(projectId, projectName, parentProjectId);

        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.PROJECTS,
                ResponseSpecs.requestReturnsOk())
                .post(createProjectRequest);
        return createProjectRequest;
    }


    public static void createBuildConfiguration(CreateProjectRequest createProjectRequest, String buildName) {
        CreateBuildConfigurationRequest createBuildConfigurationRequest = new CreateBuildConfigurationRequest(createProjectRequest.getId() + "_" + buildName, buildName, createProjectRequest.getId());

        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk())
                .post(createBuildConfigurationRequest);
    }

}
