package api.models;

import common.generators.GeneratingRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProjectRequest extends BaseModel{
    @GeneratingRule(regex = "^[A-Za-z]{8,12}$")
    private String id;
    @GeneratingRule(regex = "^[A-Za-z]{8,12}$")
    private String name;
    private ParentProject parentProject;
}
