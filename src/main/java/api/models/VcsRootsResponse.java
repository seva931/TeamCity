package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VcsRootsResponse extends BaseModel{
    private int count;

    @JsonProperty("vcs-root")
    private List<VcsRoot> vcsRoots;
}
