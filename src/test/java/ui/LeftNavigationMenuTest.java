package ui;

import api.models.CreateUserResponse;
import jupiter.annotation.User;
import jupiter.annotation.meta.WebTest;
import jupiter.annotation.meta.WithBuild;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.pages.ProjectsPage;

import static com.codeborne.selenide.Selenide.page;

@WebTest
@WithBuild
public class LeftNavigationMenuTest extends BaseUITest{

    @DisplayName("Позитивный тест: проверка на элемент админ")
    @Test
    public void adminTest(@User CreateUserResponse user) {
        ProjectsPage page = page(ProjectsPage.class);
        page.open();
        softly.assertThat(page.leftMenu().isAdminButtonPresent()).isTrue();
        softly.assertThat(page.leftMenu().isQueueButtonPresent()).isTrue();
        softly.assertThat(page.leftMenu().isAgentsButtonPresent()).isTrue();
        softly.assertThat(page.leftMenu().isChangesButtonPresent()).isTrue();
        softly.assertThat(page.leftMenu().isProjectsButtonPresent()).isTrue();
    }
}
