package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import common.generators.TestDataGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddBuildStepRequest extends BaseModel {
    private String name;
    private String type;
    private BuildStepProperties properties;

    public static AddBuildStepRequest sample() {
        return AddBuildStepRequest.builder()
                .name("step_" + TestDataGenerator.generateBuildName())
                .type("simpleRunner")
                .properties(BuildStepProperties.builder()
                        .property(List.of(
                                BuildStepProperty.builder()
                                        .name("script.content")
                                        .value("echo Hello")
                                        .build()
                        )).build()
                ).build();
    }

    public static AddBuildStepRequest sample(String type, List<BuildStepProperty> properties) {
        return AddBuildStepRequest.builder()
                .name("step_" + TestDataGenerator.generateBuildName())
                .type(type)
                .properties(BuildStepProperties.builder()
                        .property(properties).build()
                ).build();
    }
}
