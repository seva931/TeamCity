package api.requests.steps;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;

public class BuildStepsSteps {

    public static BuildStepsResponse getAllSteps(CreateUserResponse user, CreateBuildTypeResponse build) {

        return new ValidatedCrudRequester<BuildStepsResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID_STEPS,
                ResponseSpecs.ok()
        ).get(build.getId());
    }

    public static AddBuildStepResponse createStep(
            CreateUserResponse user,
            CreateBuildTypeResponse build,
            String stepType,
            List<BuildStepProperty> properties) {

        AddBuildStepRequest request = AddBuildStepRequest.sample(stepType, properties);
        return new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID_STEPS,
                ResponseSpecs.ok()
        ).post(build.getId(), request)
                .extract().as(AddBuildStepResponse.class);
    }

    public static AddBuildStepResponse createStep(
            CreateUserResponse user,
            CreateBuildTypeResponse build) {

        AddBuildStepRequest request = AddBuildStepRequest.sample();

        return new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID_STEPS,
                ResponseSpecs.ok()
        ).post(build.getId(), request)
                .extract().as(AddBuildStepResponse.class);
    }

    public static void deleteStepQuietly(String stepId, CreateUserResponse user, CreateBuildTypeResponse build) {
        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID_STEPS_ID,
                ResponseSpecs.deletesQuietly()
        ).delete(build.getId(), stepId);
    }
}
