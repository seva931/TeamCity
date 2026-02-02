package api;

import api.models.CreateProjectRequest;
import api.requests.steps.AdminSteps;
import org.junit.jupiter.api.Test;

public class CreateBuildConfigurationTest extends BaseTest {

    @Test
    public void CreateBuildConfigurationTest() {
        //TODO: сделать рандомную генерацию id и имени проекта
        //Создать проект
        CreateProjectRequest createProjectRequest = AdminSteps.createProject("MyProjectId14", "My Project Name14", "_Root");
        //TODO: сделать рандомную генерацию билд конфигурации
        //создать билд конфигурацию
        AdminSteps.createBuildConfiguration(createProjectRequest, "Build12");

    }

}
