package ui.pages;

import api.configs.Config;
import api.models.comparison.UsersUI;
import com.codeborne.selenide.*;
import common.generators.TestDataGenerator;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;

public class AdminPage extends BasePage<AdminPage> {
    private SelenideElement buttonCreateProject = $("[aria-label='Create'] a");
    private SelenideElement projectNameInput = $("[data-test='project-name-input']");
    private SelenideElement createButton = $(Selectors.byText("Create"));
    private SelenideElement buttonWithOutRepository = $$("a").findBy(text("Proceed without repository"));
    private SelenideElement buttonSkip = $(Selectors.byText("Skip"));
    private SelenideElement vcsRootOptions = $(Selectors.byText("VCS Roots"));
    private SelenideElement buttonCreateRoot = $(Selectors.byText("Create VCS root"));
    private SelenideElement vcsType = $("#-ufd-teamcity-ui-vcsName");
    private SelenideElement gitOption = $("li[data-title='Git']");
    private SelenideElement vcsRootName = $("#vcsRootName");
    private SelenideElement vcsRootId = $("#externalId");
    private SelenideElement fetchUrl = $("#url");
    private SelenideElement defaultBranch = $("#branch");
    private SelenideElement createRoot = $("[value='Create']");
    private SelenideElement duplicateDialog = $(".duplicateVcsRootsDialog");
    private SelenideElement duplicateCancel = duplicateDialog.$(".btn.cancel");
    private SelenideElement adminButton = $("a[data-hint-container-id='header-administration-link']");
    private SelenideElement userManagement = $(Selectors.byText("Users"));
    private SelenideElement usersToShow = $("#usersToShow");
    private SelenideElement createUserButton = $(".icon_before.icon16.addNew");
    private SelenideElement setUsername = $("#input_teamcityUsername");
    private SelenideElement setPassword = $("#password1");
    private SelenideElement setConfirmPassword = $("#retypedPassword");
    private SelenideElement createUserButtonFinal = $(".btn.btn_primary.submitButton");
    private SelenideElement adminCheckbox = $("#administrator");
    private SelenideElement errorName = $(".input-wrapper.input-wrapper_username");






    @Override
    public String url() {
        return "/admin/admin.html?item=users";
    }

    public AdminPage createNewRoot(String rootName) {
        String projectName = TestDataGenerator.generateNameUI();
        buttonCreateProject.shouldBe(Condition.visible).click();
        projectNameInput.shouldBe(Condition.visible).setValue(projectName);
        createButton.click();
        buttonWithOutRepository.shouldBe(Condition.visible).click();
        buttonSkip.shouldBe(Condition.visible).click();
        vcsRootOptions.shouldBe(Condition.visible).click();
        buttonCreateRoot.click();
        vcsType.click();
        gitOption.shouldBe(Condition.visible).click();
        vcsRootName.setValue(rootName);
        fetchUrl.setValue(Config.getProperty("vcsPropertyName"));
        defaultBranch.setValue(Config.getProperty("vcsPropertyValue"));
        createRoot.click();
        return this;
    }

    public AdminPage createDuplicate(String projectName,String rootName, String value) {
        buttonCreateProject.shouldBe(Condition.visible).click();
        projectNameInput.shouldBe(Condition.visible).setValue(projectName);
        createButton.click();
        buttonWithOutRepository.shouldBe(Condition.visible).click();
        buttonSkip.shouldBe(Condition.visible).click();
        vcsRootOptions.shouldBe(Condition.visible).click();
        buttonCreateRoot.click();
        vcsType.click();
        gitOption.shouldBe(Condition.visible).click();
        vcsRootName.setValue(rootName);
        fetchUrl.setValue(Config.getProperty("vcsPropertyName"));
        defaultBranch.setValue(value);
        createRoot.click();
        vcsRootOptions.shouldBe(Condition.visible).click();
        buttonCreateRoot.click();
        vcsType.click();
        gitOption.shouldBe(Condition.visible).click();
        vcsRootName.setValue(rootName);
        fetchUrl.setValue(Config.getProperty("vcsPropertyName"));
        defaultBranch.setValue(Config.getProperty("vcsPropertyValue"));
        createRoot.click();
        duplicateDialog.shouldBe(Condition.visible);
        duplicateCancel.click();
        return this;
    }

    public AdminPage getAllUsers() {
        adminButton.shouldBe(Condition.visible, Condition.enabled).click();
        userManagement.shouldBe(Condition.visible, Condition.enabled).click();

        usersToShow.shouldBe(Condition.visible);
        executeJavaScript(
                "arguments[0].value='-1'; arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                usersToShow
        );

        $$("table.userList td.username a").shouldHave(CollectionCondition.sizeGreaterThan(100));
        return this;
    }
    public List<UsersUI> getUsersFromUi() {
        return $$("table.userList td.username a").texts().stream()
                .map(String::trim)
                .map(UsersUI::new)
                .collect(Collectors.toList());
    }

    public AdminPage createCommonUser (String username){
        String password = TestDataGenerator.generatePasswordUI();
        adminButton.shouldBe(Condition.visible, Condition.enabled).click();
        userManagement.shouldBe(Condition.visible, Condition.enabled).click();
        createUserButton.shouldBe(Condition.visible).click();
        setUsername.shouldBe(Condition.visible).setValue(username);
        setPassword.shouldBe(Condition.visible).setValue(password);
        setConfirmPassword.shouldBe(Condition.visible).setValue(password);
        createUserButtonFinal.click();
        return this;
    }
    public AdminPage createDuplicateUser (String username){
        String password = TestDataGenerator.generatePasswordUI();
        adminButton.shouldBe(Condition.visible, Condition.enabled).click();
        userManagement.shouldBe(Condition.visible, Condition.enabled).click();
        createUserButton.shouldBe(Condition.visible).click();
        setUsername.shouldBe(Condition.visible).setValue(username);
        setPassword.shouldBe(Condition.visible).setValue(password);
        setConfirmPassword.shouldBe(Condition.visible).setValue(password);
        createUserButtonFinal.click();
        adminButton.shouldBe(Condition.visible, Condition.enabled).click();
        userManagement.shouldBe(Condition.visible, Condition.enabled).click();
        createUserButton.shouldBe(Condition.visible).click();
        setUsername.shouldBe(Condition.visible).setValue(username);
        setPassword.shouldBe(Condition.visible).setValue(password);
        setConfirmPassword.shouldBe(Condition.visible).setValue(password);
        createUserButtonFinal.click();
        errorName.shouldBe(Condition.visible);
        return this;
    }
    public AdminPage createAdminUser (String username){
        String password = TestDataGenerator.generatePasswordUI();
        adminButton.shouldBe(Condition.visible, Condition.enabled).click();
        userManagement.shouldBe(Condition.visible, Condition.enabled).click();
        createUserButton.shouldBe(Condition.visible).click();
        setUsername.shouldBe(Condition.visible).setValue(username);
        setPassword.shouldBe(Condition.visible).setValue(password);
        setConfirmPassword.shouldBe(Condition.visible).setValue(password);
        adminCheckbox.click();
        createUserButtonFinal.click();
        return this;
    }
}
