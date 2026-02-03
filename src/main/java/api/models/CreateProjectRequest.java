package api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateProjectRequest extends BaseModel {
    private ParentProject parentProject = new ParentProject("id: Root");
    private String id;
    private String name;
}