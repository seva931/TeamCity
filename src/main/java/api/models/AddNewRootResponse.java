package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddNewRootResponse extends BaseModel{
    private String id;
    private String name;
    private String vcsName;
    private String href;
    private VcsProject project;
    private VcsProperties properties;
    private VcsRootInstances vcsRootInstances;
}
