package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateVcsRootRequest extends BaseModel {
    private String name;
    private String vcsName;
    private ProjectModel project;
    private VcsProperties properties;
}
