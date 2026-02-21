package ui;

import api.models.CreateUserResponse;
import com.codeborne.selenide.Condition;
import jupiter.annotation.User;
import jupiter.annotation.meta.WebTest;
import jupiter.annotation.meta.WithBuild;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.pages.ProjectsPage;

import static com.codeborne.selenide.Selenide.page;

@WebTest
@WithBuild
public class LeftNavigationMenuTest extends BaseUITest {

    @DisplayName("Позитивный тест: проверка навигационного меню")
    @Test
    public void leftNavigationMenuItemsShouldBeVisible(@User CreateUserResponse user) {
        ProjectsPage projectsPage = page(ProjectsPage.class);
        projectsPage.open();

        softly.assertThat(projectsPage.leftMenu().adminButton().shouldBe(Condition.visible).isDisplayed()).isTrue();
        softly.assertThat(projectsPage.leftMenu().queueButton().shouldBe(Condition.visible).isDisplayed()).isTrue();
        softly.assertThat(projectsPage.leftMenu().agentsButton().shouldBe(Condition.visible).isDisplayed()).isTrue();
        softly.assertThat(projectsPage.leftMenu().changesButton().shouldBe(Condition.visible).isDisplayed()).isTrue();
        softly.assertThat(projectsPage.leftMenu().projectsButton().shouldBe(Condition.visible).isDisplayed()).isTrue();
    }
}