package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class ProjectPage extends BasePage<ProjectPage>{

    private SelenideElement createBuildTypeButton = $(Selectors.byAttribute("data-test", "ring-icon"));
    private SelenideElement newBuildConfigurationButton = $(Selectors.byAttribute("data-test", "ring-list-item-label"));

    @Override
    public String url() {
        return "/project/MyProjectId1?mode=builds";
    }
    public String url(String projectId) {
        return String.format("/project/%s?mode=builds", projectId);
    }

    public ProjectPage open(String projectId) {
        return Selenide.open(url(projectId), ProjectPage.class);
    }

    public ProjectPage goToCreateBuildType(){
        createBuildTypeButton.click();
        newBuildConfigurationButton.click();
        return this;
    }

}
