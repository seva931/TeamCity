package api.requests.steps;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.generators.TestDataGenerator;

import java.util.List;

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

    public static CreateUserResponse createUserWithRole(String username, String password, RoleId role) {
        CreateUserRequest request = CreateUserRequest.builder()
                .username(username)
                .password(password)
                .roles(CreateUserRequest.Roles.builder()
                        .role(List.of(
                                Role.builder()
                                        .roleId(role.name())
                                        .scope("g")
                                        .build()
                        ))
                        .build())
                .build();

        CreateUserResponse response = new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.USERS,
                ResponseSpecs.requestReturnsOk()
        ).post(request);

        return CreateUserResponse.builder()
                .username(response.getUsername())
                .id(response.getId())
                .roles(response.getRoles())
                .testData(CreateUserResponse.TestData.builder()
                        .password(request.getPassword()).build())
                .build();
    }

    public static void deleteUser(long id) {
        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.USERS_ID,
                ResponseSpecs.noContent()
        ).delete(id);
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
   
    public static void getAllRoots (){
        new CrudRequester(RequestSpecs.adminSpec(), Endpoint.GET_ALL_ROOTS, ResponseSpecs.ok()).get();
    }

    public static void createNewRoot (){
        CreateNewRootRequest newRoot = new CreateNewRootRequest();
        new CrudRequester(RequestSpecs.adminSpec(), Endpoint.CREATE_NEW_ROOT, ResponseSpecs.created())
        .post(newRoot);
    }
}
