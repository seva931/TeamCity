package api.requests.steps;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.data.BuildStepPropertyData;
import common.data.BuildStepTypeData;
import common.generators.RandomModelGenerator;

import java.util.List;

public class BuildStepsSteps {

    public static BuildStepsResponse getAllSteps(CreateUserResponse user, CreateBuildTypeResponse build) {

        String buildId = build.getId();

        return new ValidatedCrudRequester<BuildStepsResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID_STEPS,
                ResponseSpecs.ok()
        ).get(buildId);
    }

    public static AddBuildStepResponse createStep(
            CreateUserResponse user,
            CreateBuildTypeResponse build,
            String stepType,
            List<BuildStepProperty> props) {

        String buildId = build.getId();
        AddBuildStepRequest request = RandomModelGenerator.generate(AddBuildStepRequest.class);
        request.setType(stepType);
        BuildStepProperties properties = BuildStepProperties.builder().property(props).build();
        request.setProperties(properties);

        return new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID_STEPS,
                ResponseSpecs.ok()
        ).post(buildId, request)
                .extract().as(AddBuildStepResponse.class);
    }

    public static AddBuildStepResponse createStep(
            CreateUserResponse user,
            CreateBuildTypeResponse build) {

        AddBuildStepRequest request = RandomModelGenerator.generate(AddBuildStepRequest.class);
        String buildId = build.getId();
        request.setType(BuildStepTypeData.SIMPLE_RUNNER.getType());
        BuildStepProperties properties = BuildStepProperties.builder().property(
                List.of(
                        BuildStepProperty.builder().name(BuildStepPropertyData.SCRIPT_CONTENT.getName()).value("echo Hello").build())
        ).build();
        request.setProperties(properties);

        return new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID_STEPS,
                ResponseSpecs.ok()
        ).post(buildId, request)
                .extract().as(AddBuildStepResponse.class);
    }

    public static AddBuildStepResponse createStep(
            CreateUserResponse user,
            CreateBuildTypeResponse build,
            AddBuildStepRequest request) {

        return new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID_STEPS,
                ResponseSpecs.ok()
        ).post(build.getId(), request)
                .extract().as(AddBuildStepResponse.class);
    }

    public static AddBuildStepResponse createStep(
            CreateUserResponse user,
            AddBuildStepRequest request,
            String buildId) {

        return new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID_STEPS,
                ResponseSpecs.ok()
        ).post(buildId, request)
                .extract().as(AddBuildStepResponse.class);
    }

    public static void deleteStepQuietly(String stepId, CreateUserResponse user, CreateBuildTypeResponse build) {
        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID_STEPS_ID,
                ResponseSpecs.deletesQuietly()
        ).delete(build.getId(), stepId);
    }

    public static AddBuildStepResponse addEndlessStep(
            CreateUserResponse user,
            CreateBuildTypeResponse build
    ) {
        AddBuildStepRequest request = RandomModelGenerator.generate(AddBuildStepRequest.class);
        request.setType(BuildStepTypeData.SIMPLE_RUNNER.getType());
        BuildStepProperties properties = BuildStepProperties.builder().property(
                List.of(
                        BuildStepProperty.builder()
                                .name(BuildStepPropertyData.SCRIPT_CONTENT.getName())
                                .value("while true; do sleep 60; done")
                                .build(),
                        BuildStepProperty.builder()
                                .name(BuildStepPropertyData.USE_CUSTOM_SCRIPT.getName())
                                .value("true")
                                .build())
        ).build();
        request.setProperties(properties);

        return BuildStepsSteps.createStep(user, build, request);
    }
}
