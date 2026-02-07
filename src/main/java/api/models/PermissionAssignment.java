package api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@Getter
@EqualsAndHashCode(callSuper = false)
public class PermissionAssignment extends BaseModel{

    @JsonProperty("isGlobalScope")
    private boolean globalScope;

    private Permission permission;
    private ProjectModel project;

}
