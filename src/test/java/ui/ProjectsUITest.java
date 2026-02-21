package ui;

import api.models.CreateUserResponse;
import api.models.ProjectListResponse;
import api.models.ProjectResponse;
import api.requests.steps.ProjectManagementSteps;
import jupiter.annotation.Project;
import jupiter.annotation.User;
import jupiter.annotation.meta.WebTest;
import jupiter.annotation.meta.WithBuild;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.pages.ProjectsPage;

import java.util.List;

@WebTest
@WithBuild
public class ProjectsUITest extends BaseUITest {

    @DisplayName("Позитивный тест: проверка проектов на UI")
    @Test
    public void userCreateProjectTest(
            @User CreateUserResponse user,
            @Project ProjectResponse project
    ) {
        ProjectListResponse list = ProjectManagementSteps.getAllProjects(user);

        boolean existsInList = list.getProject() != null && list.getProject().stream()
                .anyMatch(p -> project.getId().equals(p.getId()));

        softly.assertThat(existsInList)
                .as("Проект присутствует в списке проектов по id")
                .isTrue();
        ProjectsPage projectsPage = new ProjectsPage();
        List<String> projectsUIId = projectsPage.open().visibleProjectIds();
        softly.assertThat(projectsUIId.size()).isEqualTo(list.getCount());
    }
}