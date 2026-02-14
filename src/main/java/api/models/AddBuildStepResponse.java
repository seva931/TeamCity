package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddBuildStepResponse extends BaseModel {
    private String id;
    private String name;
    private String type;
    private BuildStepProperties properties;
}
