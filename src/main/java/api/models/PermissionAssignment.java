package api.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PermissionAssignment extends BaseModel{

    private boolean isGlobalScope;
    private Permission permission;
    private ProjectModel project;

}
