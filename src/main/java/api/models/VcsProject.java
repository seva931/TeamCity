package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class VcsProject {
    private String id;
    private String name;
    private String description;
    private String href;
    private String webUrl;
}