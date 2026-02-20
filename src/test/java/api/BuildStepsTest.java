package api;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.steps.BuildStepsSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.data.BuildStepPropertyData;
import common.data.BuildStepTypeData;
import common.generators.RandomModelGenerator;
import jupiter.annotation.Build;
import jupiter.annotation.Project;
import jupiter.annotation.User;
import jupiter.annotation.meta.ApiTest;
import jupiter.annotation.meta.WithBuild;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ApiTest
@WithBuild
public class BuildStepsTest extends BaseTest {

    @Test
    void shouldReturnZeroBuildStepsInNewlyCreatedBuild(
            @User CreateUserResponse user,
            @Project ProjectResponse project,
            @Build CreateBuildTypeResponse build) {

        String buildId = build.getId();

        BuildStepsResponse response = new ValidatedCrudRequester<BuildStepsResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID_STEPS,
                ResponseSpecs.ok()
        ).get(buildId);

        assertThat(response.getCount())
                .as("Поле count")
                .isEqualTo(0);
    }

    @Test
    void shouldCreateStepForExistingBuild(
            @User CreateUserResponse user,
            @Project ProjectResponse project,
            @Build CreateBuildTypeResponse build
    ) {

        BuildStepsResponse allStepsBefore = BuildStepsSteps.getAllSteps(user, build);

        String buildId = build.getId();

        //build step properties
        BuildStepProperties properties = BuildStepProperties.builder().property(
                List.of(
                        BuildStepProperty.builder()
                                .name(BuildStepPropertyData.SCRIPT_CONTENT.getName())
                                .value("echo Hello")
                                .build())
        ).build();

        AddBuildStepRequest request = RandomModelGenerator.generate(AddBuildStepRequest.class);
        request.setType(BuildStepTypeData.SIMPLE_RUNNER.getType());
        request.setProperties(properties);

        AddBuildStepResponse response = new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID_STEPS,
                ResponseSpecs.ok()
        ).post(buildId, request)
                .extract().as(AddBuildStepResponse.class);

        BuildStepsResponse allStepsAfter = BuildStepsSteps.getAllSteps(user, build);

        softly.assertThat(allStepsAfter.getCount())
                .as("new count = old count + 1")
                .isEqualTo(allStepsBefore.getCount() + 1);
        softly.assertThat(response.getId())
                .as("поле id")
                .isNotNull()
                .isNotEmpty();
        softly.assertThat(response)
                .usingRecursiveComparison()
                .as("Поля name, type, properties.property")
                .comparingOnlyFields("name", "type", "properties.property")
                .isEqualTo(request);
    }

    @Test
    void shouldDeleteExistingStepById(
            @User CreateUserResponse user,
            @Project ProjectResponse project,
            @Build CreateBuildTypeResponse build) {

        String stepId = BuildStepsSteps.createStep(user, build).getId();
        String buildId = build.getId();

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID_STEPS_ID,
                ResponseSpecs.noContent()
        ).delete(buildId, stepId);

        BuildStepsResponse allSteps = BuildStepsSteps.getAllSteps(user, build);

        softly.assertThat(allSteps.getStep()).isNull();
    }
}
