package api.models;

import lombok.Data;

@Data
public class CreateProjectRequest extends BaseModel {
    private ParentProjectRef parentProject;
    private String id;
    private String name;
}
