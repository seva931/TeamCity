package api.requests.steps;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;

public class BuildManageSteps {

    public static CreateBuildConfigurationResponse createBuildConfiguration(String projectId, String buildId, String buildName) {
        CreateBuildConfigurationRequest createBuildConfigurationRequest = new CreateBuildConfigurationRequest(buildId, buildName, projectId);

        return new ValidatedCrudRequester<CreateBuildConfigurationResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk())
                .post(createBuildConfigurationRequest);
    }

    public static void deleteBuildConfiguration(String buildId, CreateUserResponse user) {
        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.noContent())
                .delete(buildId);
    }

    public static GetInfoBuildConfigurationResponse getInfoBuildConfiguration(String buildId, CreateUserResponse user) {
        return new ValidatedCrudRequester<GetInfoBuildConfigurationResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.requestReturnsOk())
                .get(buildId);
    }

    public static List<CreateBuildConfigurationResponse> getAllBuilds() {
        return new ValidatedCrudRequester<CreateBuildConfigurationResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk()).getAllBuilds(CreateBuildConfigurationResponse[].class);
    }
}
