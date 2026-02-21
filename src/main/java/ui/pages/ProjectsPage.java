package ui.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class ProjectsPage extends BasePage<ProjectsPage>{

    private SelenideElement createBuildTypeButton = $(By.xpath("(//*[@data-test='ring-dropdown']//*/button[@title='add'])[last()]"));
    private SelenideElement newBuildConfigurationButton = $(By.xpath("//*/span[contains(text(),'New build configuration')]/../.."));

    @Override
    public String url() {
        return "/project/MyProjectId1?mode=builds";
    }
    public String url(String projectId) {
        return String.format("/project/%s?mode=builds", projectId);
    }

    public ProjectsPage open(String projectId) {
        return Selenide.open(url(projectId), ProjectsPage.class);
    }

    public ProjectsPage goToCreateBuildType(){
        createBuildTypeButton.click();
        newBuildConfigurationButton.click();
        return this;
    }

}
