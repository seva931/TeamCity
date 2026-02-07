package api.models;

import lombok.Data;

@Data
public class AddNewRootResponse extends BaseModel{
    private String id;
    private String name;
    private String vcsName;
    private String href;
    private VcsProject project;
    private VcsProperties properties;
    private VcsRootInstances vcsRootInstances;
}
