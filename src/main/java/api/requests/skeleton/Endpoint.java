package api.requests.skeleton;

import api.models.BaseModel;
import api.models.CreateBuildConfigurationRequest;
import api.models.CreateProjectRequest;
import api.models.CreateUserRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {
    SERVER("/app/rest/server", BaseModel.class, BaseModel.class),
    USERS("/app/rest/users", CreateUserRequest.class, BaseModel.class),
    PROJECTS("/app/rest/projects", CreateProjectRequest.class, BaseModel.class),
    BUILD_TYPES("/app/rest/buildTypes", CreateBuildConfigurationRequest.class, BaseModel.class);

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}
