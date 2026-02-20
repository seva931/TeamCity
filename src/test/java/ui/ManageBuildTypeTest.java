package ui;

import api.models.CreateProjectRequest;
import api.models.CreateUserResponse;
import common.generators.TestDataGenerator;
import jupiter.annotation.WithProject;
import jupiter.annotation.WithUsersQueue;
import jupiter.annotation.meta.WebTest;
import jupiter.extension.ProjectExtension;
import jupiter.extension.UsersQueueExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.pages.BuildTypePage;
import ui.pages.CreateBuildTypePage;
import ui.pages.ProjectPage;

@ExtendWith({UsersQueueExtension.class, ProjectExtension.class})
@WebTest
public class ManageBuildTypeTest extends BaseUITest{

    @WithUsersQueue
    @WithProject
    @Test
    public void userCanCreateBuildTypeTest(CreateUserResponse user, CreateProjectRequest project) {


        //ЛОГИН
        // переход к созданию билда и создание
        String buildName = TestDataGenerator.generateBuildName();
        new ProjectPage().open(project.getId()).goToCreateBuildType().getPage(CreateBuildTypePage.class).createBuildTypePage(buildName);
        new BuildTypePage().open(project.getId(), buildName);
        //Проверяем, что билд создался на ui(в левом меню отображается имя билда)
        //Проверяем, что билд создался в api(гет инфо эбаут список конфигураций и найти нужную по имени)

    }

    @WithUsersQueue
    @WithProject
    @Test
    public void userCanNotCreateBuildTypeWithSameNameTest(CreateUserResponse user, CreateProjectRequest project) {
        //ЛОГИН
        String buildName = TestDataGenerator.generateBuildName();
        new ProjectPage().open(project.getId()).goToCreateBuildType().getPage(CreateBuildTypePage.class).createBuildTypePage(buildName);
        new BuildTypePage().open(project.getId(), buildName);
        //Проверяем, что билд создался на ui(в левом меню отображается имя билда)
        //Проверяем, что билд создался в api(гет инфо эбаут список конфигураций и найти нужную по имени)

    }

    @Test
    public void userCanNotCreateBuildTypeWithEmptyNameTest(CreateUserResponse user, CreateProjectRequest project) {
        //ЛОГИН
        String buildName = TestDataGenerator.generateBuildName();
        new ProjectPage().open(project.getId()).goToCreateBuildType().getPage(CreateBuildTypePage.class).createBuildTypePage(buildName);
        new BuildTypePage().open(project.getId(), buildName);
        //Проверяем, что билд создался на ui(в левом меню отображается имя билда)
        //Проверяем, что билд создался в api(гет инфо эбаут список конфигураций и найти нужную по имени)

    }
}
