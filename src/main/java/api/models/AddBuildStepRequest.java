package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import common.generators.GeneratingRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddBuildStepRequest extends BaseModel {
    @GeneratingRule(regex = "^[A-Za-z]{8,12}$")
    private String name;
    private String type;
    private BuildStepProperties properties;
}
