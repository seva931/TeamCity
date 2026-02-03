package api.requests.skeleton;

import api.models.BaseModel;
import api.models.CreateProjectRequest;
import api.models.ProjectResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {
    SERVER("/app/rest/server", BaseModel.class, BaseModel.class),
    PROJECTS("/app/rest/projects", CreateProjectRequest.class, ProjectResponse.class),
    PROJECT_BY_ID("/app/rest/projects/id:%s", BaseModel.class, ProjectResponse.class);

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;

    public String getUrlByID() {
        return url + "/{id}";
    }
}