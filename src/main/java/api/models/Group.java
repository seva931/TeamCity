package api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class Group extends BaseModel {

    private String key;
    private String name;
    private String href;
    private String description;

    @JsonProperty("parent-groups")
    private String parentGroups;

    @JsonProperty("child-groups")
    private String childGroups;

    private Users users;
    private List<Role> roles;
    private Properties properties;
}
