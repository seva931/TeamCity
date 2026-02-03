package api.requests.skeleton;

import api.models.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {
    SERVER("/app/rest/server", BaseModel.class, BaseModel.class),
    USERS("/app/rest/users", CreateUserRequest.class, CreateUserResponse.class),
    PROJECTS("/app/rest/projects", CreateProjectRequest.class, ProjectResponse.class),
    PROJECT_BY_ID("/app/rest/projects/id:{id}", BaseModel.class, ProjectResponse.class),
    BUILD_TYPES("/app/rest/buildTypes", CreateBuildConfigurationRequest.class, BaseModel.class),
    GET_ALL_ROOTS("/app/rest/vcs-roots?locator=string&fields=string",GetAllRootsRequest.class,GetAllRootResponse.class),
    CREATE_NEW_ROOT("/app/rest/vcs-roots?fields=string",CreateNewRootRequest.class,CreateNewRootResponse.class);


    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;

    public String getUrlByID() {
        return url + "{id}";
    }
}
