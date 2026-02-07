package api.models;

import lombok.Data;

@Data
public class VcsProject {
    private String id;
    private String name;
    private String description;
    private String href;
    private String webUrl;
}