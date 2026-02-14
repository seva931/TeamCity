package api.models;

import common.generators.GeneratingRule;
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
    @GeneratingRule(regex = "^[A-Za-z]{8,12}$")
    private String id;
    @GeneratingRule(regex = "^[A-Za-z]{8,12}$")
    private String name;
    private String projectId;

    public static CreateBuildTypeRequest createBuildConfig(String projectId) {
        return CreateBuildTypeRequest.builder()
                .id(TestDataGenerator.generateBuildId())
                .name(TestDataGenerator.generateBuildName())
                .projectId(projectId)
                .build();
    }
}
