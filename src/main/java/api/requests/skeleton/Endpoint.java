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
    CREATE_NEW_ROOT("/vcs-roots?fields=string",CreateNewRootRequest.class,CreateNewRootResponse.class),
    AGENTS("/agents", BaseModel.class, AgentsResponse.class),
    AGENTS_ID("/agents/id:%s", BaseModel.class, AgentResponse.class),
    AGENTS_ID_ENABLED("/agents/id:%s/enabled", BaseModel.class, BaseModel.class);


    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;

    public String getFormatedUrl(Object... args) {
        return String.format(url, args);
    }
}
