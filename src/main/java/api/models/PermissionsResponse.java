package api.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class PermissionsResponse extends BaseModel{

    private int count;
    private List<PermissionAssignment> permissionAssignment;

}
