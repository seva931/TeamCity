package ui;

import api.models.CreateBuildTypeRequest;
import api.models.CreateProjectRequest;
import api.models.CreateUserResponse;
import api.models.ProjectResponse;
import api.requests.steps.BuildManageSteps;
import common.generators.TestDataGenerator;
import jupiter.annotation.Project;
import jupiter.annotation.User;
import jupiter.annotation.meta.WithProject;
import jupiter.annotation.meta.WebTest;
import org.junit.jupiter.api.Test;
import ui.pages.BuildTypePage;
import ui.pages.CreateBuildTypePage;
import ui.pages.ProjectPage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WithProject
@WebTest
public class ManageBuildTypeTest extends BaseUITest{

    @Test
    public void userCanCreateBuildTypeTest(@User CreateUserResponse user,
                                           @Project ProjectResponse project) {

        //ЛОГИН
        // переход к созданию билда и создание
        String buildName = TestDataGenerator.generateBuildName();
        new ProjectPage().open(project.getId()).goToCreateBuildType().getPage(CreateBuildTypePage.class).createBuildTypePage(buildName);
        new BuildTypePage().open(project.getId(), buildName);
        //Проверяем, что билд создался на ui(в левом меню отображается имя билда)
        //Проверяем, что билд создался в api(гет инфо эбаут список конфигураций и найти нужную по имени)
        boolean isFind = BuildManageSteps.getAllBuildTypes().stream()
                .anyMatch(build -> build.getName().equals(buildName));

        assertTrue(isFind);

    }

    @Test
    public void userCanNotCreateBuildTypeWithSameNameTest(CreateUserResponse user, CreateProjectRequest project) {
        CreateBuildTypeRequest createFirstBuildTypeRequest = BuildManageSteps.createBuildType(project.getId()).request();
        //ЛОГИН
        String buildName = TestDataGenerator.generateBuildName();
        new ProjectPage().open(project.getId()).goToCreateBuildType().getPage(CreateBuildTypePage.class).createBuildTypePage(createFirstBuildTypeRequest.getName()).checkAlert();
        //Проверяем, что билд создался на ui(в левом меню отображается имя билда)
        //Проверяем, что билд НЕ создался в api(гет инфо эбаут список конфигураций и НЕ найти нужную по имени)
        boolean isFind = BuildManageSteps.getAllBuildTypes().stream()
                .anyMatch(build -> build.getName().equals(buildName));

        assertFalse(isFind);

    }

    @Test
    public void userCanNotCreateBuildTypeWithEmptyNameTest(CreateUserResponse user, CreateProjectRequest project) {
        //ЛОГИН
        String buildName = TestDataGenerator.generateBuildName();
        new ProjectPage().open(project.getId()).goToCreateBuildType().getPage(CreateBuildTypePage.class).createBuildTypePage("").checkDisableButtonCreate();
    }
}
