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

@WebTest
@WithBuild
public class ProjectsUITest extends BaseUITest {

    @DisplayName("Позитивный тест: созданный по API проект отображается на UI")
    @Test
    public void createdProjectShouldBeVisibleOnUiTest(
            @User CreateUserResponse user,
            @Project ProjectResponse project
    ) {
        ProjectsPage projectsPage = new ProjectsPage();

        softly.assertThatCode(() -> projectsPage.open().shouldContainProjectId(project.getId()))
                .as("На UI отображается проект с id: " + project.getId())
                .doesNotThrowAnyException();
    }
}