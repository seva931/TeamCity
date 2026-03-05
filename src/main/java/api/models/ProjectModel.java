package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectModel extends BaseModel {
    private String id;
    private String name;
    private VcsRoots vcsRoots;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VcsRoots {
        private Integer count;
    }
}