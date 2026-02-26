package ui;

import api.models.CreateUserResponse;
import api.models.ProjectResponse;
import jupiter.annotation.Project;
import jupiter.annotation.User;
import jupiter.annotation.meta.WebTest;
import jupiter.annotation.meta.WithBuild;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.pages.ProjectsPage;

import static com.codeborne.selenide.logevents.SelenideLogger.step;

@WebTest
@WithBuild
public class ProjectsUITest extends BaseUITest {

    @DisplayName("Позитивный тест: созданный по API проект отображается на UI")
    @Test
    public void createdProjectShouldBeVisibleOnUiTest(
            @User CreateUserResponse user,
            @Project ProjectResponse project
    ) {
        step("Открыть страницу проектов и проверить, что проект отображается в списке", () -> {
            new ProjectsPage()
                    .open()
                    .shouldContainProjectId(project.getId());
        });
    }
}