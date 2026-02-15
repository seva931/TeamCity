package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddNewRootResponse extends BaseModel{
    private String id;
    private String name;
    private String vcsName;
    private String href;
    private VcsProject project;
    private VcsProperties properties;
    private VcsRootInstances vcsRootInstances;
}
