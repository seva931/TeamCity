package ui.pages;

import com.codeborne.selenide.Selenide;

public class BuildTypePage extends BasePage<BuildTypePage>{

    @Override
    public String url() {
        return "/buildConfiguration/MyProjectId1_NewNAme?buildTypeTab=";
    }

    public String url(String projectId, String buildName) {
        return String.format("/buildConfiguration/%s_%s?buildTypeTab=", projectId, buildName);
    }

    public ProjectPage open(String projectId, String buildName) {
        return Selenide.open(url(projectId, buildName), ProjectPage.class);
    }

}
