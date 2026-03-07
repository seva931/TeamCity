package api.requests.steps;

import api.models.CreateBuildTypeRequest;
import api.models.CreateBuildTypeResponse;
import api.models.CreateUserResponse;
import api.models.GetInfoBuildTypeResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.generators.RandomModelGenerator;
import io.qameta.allure.Step;

import java.util.List;

public class BuildManageSteps {

    public record CreateBuildTypeResult(CreateBuildTypeRequest request, CreateBuildTypeResponse response) {
    }

    @Step("Создать build type '{buildName}' с id '{buildId}' в проекте '{projectId}'")
    public static CreateBuildTypeResult createBuildType(String projectId, String buildId, String buildName) {
        CreateBuildTypeRequest createBuildTypeRequest = new CreateBuildTypeRequest(buildId, buildName, projectId);

        CreateBuildTypeResponse createBuildTypeResponse = new ValidatedCrudRequester<CreateBuildTypeResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk())
                .post(createBuildTypeRequest);

        return new CreateBuildTypeResult(createBuildTypeRequest, createBuildTypeResponse);
    }

    @Step("Создать случайный build type в проекте '{projectId}'")
    public static CreateBuildTypeResult createBuildType(String projectId) {
        CreateBuildTypeRequest createBuildTypeRequest = RandomModelGenerator.builder(CreateBuildTypeRequest.class)
                .withProjectId(projectId)
                .build();

        CreateBuildTypeResponse createBuildTypeResponse = new ValidatedCrudRequester<CreateBuildTypeResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk())
                .post(createBuildTypeRequest);

        return new CreateBuildTypeResult(createBuildTypeRequest, createBuildTypeResponse);
    }

    @Step("Создать build type в проекте '{projectId}' от имени пользователя '{user.username}'")
    public static CreateBuildTypeResponse createBuildType(String projectId, CreateUserResponse user) {
        return new ValidatedCrudRequester<CreateBuildTypeResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk())
                .post(CreateBuildTypeRequest.createBuildConfig(projectId));
    }

    @Step("Удалить build type '{buildId}' от имени пользователя '{user.username}'")
    public static void deleteBuildType(String buildId, CreateUserResponse user) {
        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.noContent())
                .delete(buildId);
    }

    @Step("Получить информацию по build type '{buildId}' от имени пользователя '{user.username}'")
    public static GetInfoBuildTypeResponse getInfoBuildType(String buildId, CreateUserResponse user) {
        return new ValidatedCrudRequester<GetInfoBuildTypeResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.requestReturnsOk())
                .get(buildId);
    }

    @Step("Получить список всех build types")
    public static List<CreateBuildTypeResponse> getAllBuildTypes() {
        return new ValidatedCrudRequester<CreateBuildTypeResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk())
                .getAllBuildTypes(CreateBuildTypeResponse[].class);
    }
}