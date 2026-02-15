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
import jupiter.annotation.WithProject;
import jupiter.annotation.WithUsersQueue;
import jupiter.extension.BuildExtension;
import jupiter.extension.ProjectExtension;
import jupiter.extension.UsersQueueExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith({
        UsersQueueExtension.class,
        BuildExtension.class,
        ProjectExtension.class
})
public class BuildStepsTest extends BaseTest {

    @WithProject
    @WithUsersQueue
    @Test
    void shouldReturnZeroBuildStepsInNewlyCreatedBuild(@Build CreateBuildTypeResponse build, CreateUserResponse user) {
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

    @WithUsersQueue
    @Test
    void shouldCreateStepForExistingBuild(@Build CreateBuildTypeResponse build, CreateUserResponse user) {

        BuildStepsResponse allStepsBefore = BuildStepsSteps.getAllSteps(user, build);

        String buildId = build.getId();
        AddBuildStepRequest request = RandomModelGenerator.generate(AddBuildStepRequest.class);
        request.setType(BuildStepTypeData.SIMPLE_RUNNER.getType());
        BuildStepProperties properties = BuildStepProperties.builder().property(
                List.of(
                        BuildStepProperty.builder()
                                .name(BuildStepPropertyData.SCRIPT_CONTENT.getName())
                                .value("echo Hello")
                                .build())
        ).build();
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

    @WithProject
    @WithUsersQueue
    @Test
    void shouldDeleteExistingStepById(@Build CreateBuildTypeResponse build, CreateUserResponse user) {

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
