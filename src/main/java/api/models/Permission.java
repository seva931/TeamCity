package api.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Permission extends BaseModel{

    private String id;
    private String name;
    private boolean global;

}
