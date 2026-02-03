package api.requests.skeleton;

import api.models.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {
    SERVER("/server", BaseModel.class, BaseModel.class),
    USERS("/users", CreateUserRequest.class, CreateUserResponse.class),
    PROJECTS("/projects", CreateProjectRequest.class, ProjectResponse.class),
    PROJECT_BY_ID("/projects/id:{id}", BaseModel.class, ProjectResponse.class),
    BUILD_TYPES("/buildTypes", CreateBuildConfigurationRequest.class, BaseModel.class),
    GET_ALL_ROOTS("/vcs-roots?locator=string&fields=string",GetAllRootsRequest.class,GetAllRootResponse.class),
    CREATE_NEW_ROOT("/vcs-roots?fields=string",CreateNewRootRequest.class,CreateNewRootResponse.class);


    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;

    public String getUrlByID() {
        return url + "{id}";
    }
}
