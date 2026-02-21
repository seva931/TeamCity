package api.models;

import common.generators.GeneratingRule;
import groovy.transform.builder.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CancelBuildRequest extends BaseModel {
    @GeneratingRule(regex = "^[A-Za-z]{8,12}$")
    private String comment;
    private boolean readdIntoQueue;
}
