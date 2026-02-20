package api.requests.steps;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.data.RoleId;

import java.util.List;

public class AdminSteps {

    public static CreateUserRequest createAdminUser() {
        CreateUserRequest createUserRequest = CreateUserRequest.systemAdmin();

        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.USERS,
                ResponseSpecs.requestReturnsOk())
                .post(createUserRequest);

        return createUserRequest;
    }

    public static CreateUserResponse createUser(CreateUserRequest request) {
        CreateUserResponse response = new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.USERS,
                ResponseSpecs.requestReturnsOk()
        ).post(request);

        response.setTestData(CreateUserResponse.TestData.builder().password(request.getPassword()).build());

        return response;
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

    public static CreateUserResponse createUserWithRole(RoleId role) {
        CreateUserRequest request = CreateUserRequest.withRole(role);

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

    public static CreateUserRequest createUserByAdmin(String username, String password) {
        CreateUserRequest createUserRequest = new CreateUserRequest(username, password);

        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.USERS,
                ResponseSpecs.requestReturnsOk())
                .post(createUserRequest);

        return createUserRequest;
    }

    public static void deleteUser(long id) {
        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.USERS_ID,
                ResponseSpecs.deletesQuietly()
        ).delete(id);
    }

    public static GetUsersResponse getAllUsers() {
        return new CrudRequester(RequestSpecs.adminSpec(), Endpoint.USERS, ResponseSpecs.ok())
                .get()
                .extract()
                .as(GetUsersResponse.class);
    }

    public static PermissionsResponse getPermissionsForUser(int id) {
        return new CrudRequester(RequestSpecs.adminSpec(), Endpoint.USERS_ID_PERMISSIONS, ResponseSpecs.ok())
                .get(id)
                .extract()
                .as(PermissionsResponse.class);
    }

    public static CreateUserResponse getUserInfoByUsername(String username) {
        return new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.USERS_USERNAME,
                ResponseSpecs.requestReturnsOk())
                .get(username);
    }

    public static long getDefaultAgentId() {
        return new ValidatedCrudRequester<AgentsResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOk()
        ).get().getAgent().getFirst().getId();
    }

    public static void deleteBuildTypeQuietly(String buildId) {
        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.deletesQuietly())
                .delete(buildId);
    }

    public static CreateBuildTypeResponse createBuildType(CreateBuildTypeRequest request) {

        return new ValidatedCrudRequester<CreateBuildTypeResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk())
                .post(request);
    }

    public static void deleteProjectByIdQuietly(String projectId) {
        new CrudRequester(RequestSpecs.adminSpec(),
                Endpoint.PROJECT_ID,
                ResponseSpecs.deletesQuietly())
                .delete(projectId);
    }

    public static ProjectResponse createProject(CreateProjectRequest request) {
        return new ValidatedCrudRequester<ProjectResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PROJECTS,
                ResponseSpecs.requestReturnsOk())
                .post(request);
    }
}
