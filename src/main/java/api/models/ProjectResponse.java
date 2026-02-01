package api.models;

import lombok.Data;

@Data
public class ProjectResponse extends BaseModel {
    private String id;
    private String name;
    private ParentProjectRef parentProject;
}
