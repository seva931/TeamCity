package api.models;

import common.generators.TestDataGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBuildTypeRequest extends BaseModel {
    private String id;
    private String name;
    private String projectId;

    public static CreateBuildConfigurationRequest createBuildConfig(String projectId) {
        return CreateBuildConfigurationRequest.builder()
                .id(TestDataGenerator.generateBuildId())
                .name(TestDataGenerator.generateBuildName())
                .projectId(projectId)
                .build();
    }
}
