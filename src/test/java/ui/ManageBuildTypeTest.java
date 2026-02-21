package ui;

import api.models.CreateBuildTypeRequest;
import api.models.CreateUserResponse;
import api.models.ProjectResponse;
import api.requests.steps.BuildManageSteps;
import common.generators.TestDataGenerator;
import jdk.jfr.Description;
import jupiter.annotation.Project;
import jupiter.annotation.User;
import jupiter.annotation.meta.WithProject;
import jupiter.annotation.meta.WebTest;
import org.junit.jupiter.api.Test;
import ui.pages.BuildTypePage;
import ui.pages.CreateBuildTypePage;
import ui.pages.ProjectPage;

import static org.junit.jupiter.api.Assertions.*;

@WithProject
@WebTest
public class ManageBuildTypeTest extends BaseUITest{

    @Description("Позитивный тест. Создание билд конфигурации с корректными данными")
    @Test
    public void userCanCreateBuildTypeTest(@User CreateUserResponse user,
                                           @Project ProjectResponse project) {

        String buildName = TestDataGenerator.generateBuildName();
        new ProjectPage().open(project.getId()).goToCreateBuildType().getPage(CreateBuildTypePage.class).createBuildTypePage(buildName);
        new BuildTypePage().open(project.getId(), buildName).checkCreatedBuildType(buildName);
        boolean isFind = BuildManageSteps.getAllBuildTypes().stream()
                .anyMatch(build -> build.getName().equals(buildName));

        assertTrue(isFind);
    }

    @Description("Негативный тест. Создание билд конфигурации именем уже созданной конфигурации")
    @Test
    public void userCanNotCreateBuildTypeWithSameNameTest(@User CreateUserResponse user,
                                                          @Project ProjectResponse project) {
        CreateBuildTypeRequest createFirstBuildTypeRequest = BuildManageSteps.createBuildType(project.getId()).request();
        new ProjectPage().open(project.getId()).goToCreateBuildType().getPage(CreateBuildTypePage.class).createBuildTypePage(createFirstBuildTypeRequest.getName()).checkAlert(createFirstBuildTypeRequest.getName(), project.getName());
        new ProjectPage().open(project.getId());
        long count = BuildManageSteps.getAllBuildTypes().stream()
                .filter(build -> build.getName().equals(createFirstBuildTypeRequest.getName())).count();

        assertEquals(1, count);
    }

    @Description("Негативный тест. Невозможно создать конфигурацию с пустым именем (кнопка создания задизейблена)")
    @Test
    public void userCanNotCreateBuildTypeWithEmptyNameTest(@User CreateUserResponse user,
                                           @Project ProjectResponse project) {
        new ProjectPage().open(project.getId()).goToCreateBuildType().getPage(CreateBuildTypePage.class).checkDisableButtonCreate();
    }
}
