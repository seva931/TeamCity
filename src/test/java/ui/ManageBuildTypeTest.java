package ui;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;


public class ManageBuildTypeTest extends BaseUITest{

    @Test
    public void userCanCreateBuildTypeTest() {
        //ЛОГИН
        //ПЕРЕХОД НА СТРАНИЦУ /project/MyProjectId1?mode=builds
        private SelenideElement createBuildTypeButton = $(Selectors.byAttribute("data-test", "ring-icon"));
        private SelenideElement newBuildConfigurationButton = $(Selectors.byAttribute("data-test", "ring-list-item-label"));
        //переход на страницу /projects/create?projectId=MyProjectId1&setup=build
        private SelenideElement newBuildConfigurationNameInput = $(Selectors.byAttribute("data-test", "ring-input"));
        private SelenideElement saveChangesButton = $(Selectors.byText("Create"));
        //переход на страницу с этой конфигурацией /buildConfiguration/MyProjectId1_NewNAme?buildTypeTab=
        //Проверяем, что билд создался на ui(в левом меню отображается имя билда)
        //Проверяем, что билд создался в api(гет инфо эбаут список конфигураций и найти нужную по имени)

    }

    @Test
    public void userCanNotCreateBuildTypeWithSameNameTest() {
        //ЛОГИН
        //ПЕРЕХОД НА СТРАНИЦУ /project/MyProjectId1?mode=builds
        private SelenideElement createBuildTypeButton = $(Selectors.byAttribute("data-test", "ring-icon"));
        private SelenideElement newBuildConfigurationButton = $(Selectors.byAttribute("data-test", "ring-list-item-label"));
        //переход на страницу /projects/create?projectId=MyProjectId1&setup=build
        private SelenideElement newBuildConfigurationNameInput = $(Selectors.byAttribute("data-test", "ring-input"));
        private SelenideElement saveChangesButton = $(Selectors.byText("Create"));
        //переход на страницу с этой конфигурацией /buildConfiguration/MyProjectId1_NewNAme?buildTypeTab=
        //Проверяем, что билд создался на ui(в левом меню отображается имя билда)
        //Проверяем, что билд создался в api(гет инфо эбаут список конфигураций и найти нужную по имени)

    }

    @Test
    public void userCanNotCreateBuildTypeWithEmptyNameTest() {
        //ЛОГИН
        //ПЕРЕХОД НА СТРАНИЦУ /project/MyProjectId1?mode=builds
        private SelenideElement createBuildTypeButton = $(Selectors.byAttribute("data-test", "ring-icon"));
        private SelenideElement newBuildConfigurationButton = $(Selectors.byAttribute("data-test", "ring-list-item-label"));
        //переход на страницу /projects/create?projectId=MyProjectId1&setup=build
        private SelenideElement newBuildConfigurationNameInput = $(Selectors.byAttribute("data-test", "ring-input"));
        private SelenideElement saveChangesButton = $(Selectors.byText("Create"));
        //переход на страницу с этой конфигурацией /buildConfiguration/MyProjectId1_NewNAme?buildTypeTab=
        //Проверяем, что билд создался на ui(в левом меню отображается имя билда)
        //Проверяем, что билд создался в api(гет инфо эбаут список конфигураций и найти нужную по имени)

    }
}
