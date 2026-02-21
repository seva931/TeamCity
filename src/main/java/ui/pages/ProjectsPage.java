package ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class ProjectsPage extends BasePage<ProjectsPage> {

    private SelenideElement createBuildTypeButton = $(By.xpath("(//*[@data-test='ring-dropdown']//*/button[@title='add'])[last()]"));
    private SelenideElement newBuildConfigurationButton = $(By.xpath("//*/span[contains(text(),'New build configuration')]/../.."));

    @Override
    public String url() {
        return "/favorite/projects?mode=builds";
    }

    private ElementsCollection projects() {
        return $$x("//div[@data-test='subproject']");
    }

        public String url (String projectId){
            return String.format("/project/%s?mode=builds", projectId);
        }

        public ProjectsPage open (String projectId) {
            return Selenide.open(url(projectId), ProjectsPage.class);
        }
            private SelenideElement projectById (String projectId){
                return $x(String.format("//div[@data-test='subproject' and @data-project-id='%s']", projectId));
            }

            public ProjectsPage goToCreateBuildType () {
                createBuildTypeButton.click();
                newBuildConfigurationButton.click();
                return this;
            }

                public ProjectsPage shouldContainProjectId (String projectId){
                    projectById(projectId).shouldBe(visible, Duration.ofSeconds(15));
                    return this;
                }

                public List<String> visibleProjectIds () {
                    return projects().asFixedIterable().stream()
                            .map(e -> e.getAttribute("data-project-id"))
                            .filter(Objects::nonNull)
                            .toList();
                }
            }


