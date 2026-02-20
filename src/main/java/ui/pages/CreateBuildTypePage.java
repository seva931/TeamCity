package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class CreateBuildTypePage extends BasePage <CreateBuildTypePage>{

    private SelenideElement newBuildConfigurationNameInput = $(Selectors.byAttribute("data-test", "ring-input"));
    private SelenideElement saveChangesButton = $(Selectors.byText("Create"));

    @Override
    public String url() {
        return "/projects/create?projectId=MyProjectId1&setup=build";
    }

    public String url(String projectId) {
        return String.format("/projects/create?%s=MyProjectId1&setup=build", projectId);
    }

    public CreateBuildTypePage createBuildTypePage(String buildName){
        newBuildConfigurationNameInput.append(buildName);
        saveChangesButton.click();
        return this;
    }
}
