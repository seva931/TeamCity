package api.models;

import lombok.Data;

@Data
public class AddNewRootRequest extends BaseModel{
    private String name;
    private String vcsName;
    private VcsProject project;
    private VcsProperties properties;
}

