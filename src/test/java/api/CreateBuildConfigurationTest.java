package api;

import api.models.CreateBuildConfigurationRequest;
import api.models.CreateProjectRequest;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import org.junit.jupiter.api.Test;

public class CreateBuildConfigurationTest extends BaseTest {

    @Test
    public void CreateBuildConfigurationTest() {
        //TODO: сделать рандомную генерацию id и имени проекта
        //Создать проект
        CreateProjectRequest createProjectRequest = AdminSteps.createProject("MyProjectId19", "My Project Name19", "_Root");
        //TODO: сделать рандомную генерацию билд конфигурации
        //создать билд конфигурацию
        //AdminSteps.createBuildConfiguration(createProjectRequest, "Build16");
        CreateBuildConfigurationRequest createBuildConfigurationRequest = new CreateBuildConfigurationRequest(createProjectRequest.getId() + "_" + "Build19", "Build19", createProjectRequest.getId());

        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk())
                .post(createBuildConfigurationRequest);

    }

}
