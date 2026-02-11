package api.requests.steps;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;

public class BuildManageSteps {

    public record CreateBuildTypeResult(CreateBuildTypeRequest request, CreateBuildTypeResponse response) {}

    public static CreateBuildTypeResult createBuildType(String projectId, String buildId, String buildName) {
        CreateBuildTypeRequest createBuildTypeRequest = new CreateBuildTypeRequest(buildId, buildName, projectId);

        CreateBuildTypeResponse createBuildTypeResponse = new ValidatedCrudRequester<CreateBuildTypeResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk())
                .post(createBuildTypeRequest);

        return new CreateBuildTypeResult(createBuildTypeRequest, createBuildTypeResponse);
    }

    public static void deleteBuildConfiguration(String buildId, CreateUserResponse user) {
        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.noContent())
                .delete(buildId);
    }

    public static GetInfoBuildTypeResponse getInfoBuildType(String buildId, CreateUserResponse user) {
        return new ValidatedCrudRequester<GetInfoBuildTypeResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.requestReturnsOk())
                .get(buildId);
    }

    public static List<CreateBuildTypeResponse> getAllBuilds() {
        return new ValidatedCrudRequester<CreateBuildTypeResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk()).getAllBuilds(CreateBuildTypeResponse[].class);
    }
}
