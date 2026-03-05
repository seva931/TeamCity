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
    private final SelenideElement buttonCreateProject = $("[aria-label='Create'] a");
    private final SelenideElement projectNameInput = $("[data-test='project-name-input']");
    private final SelenideElement createButton = $(Selectors.byText("Create"));
    private final SelenideElement buttonWithOutRepository = $$("a").findBy(text("Proceed without repository"));
    private final SelenideElement buttonSkip = $(Selectors.byText("Skip"));
    private final SelenideElement vcsRootOptions = $(Selectors.byText("VCS Roots"));
    private final SelenideElement buttonCreateRoot = $(Selectors.byText("Create VCS root"));
    private final SelenideElement vcsType = $("#-ufd-teamcity-ui-vcsName");
    private final SelenideElement gitOption = $("li[data-title='Git']");
    private final SelenideElement vcsRootName = $("#vcsRootName");
    private final SelenideElement vcsRootId = $("#externalId");
    private final SelenideElement fetchUrl = $("#url");
    private final SelenideElement defaultBranch = $("#branch");
    private final SelenideElement createRoot = $("[value='Create']");
    private final SelenideElement duplicateDialog = $(".duplicateVcsRootsDialog");
    private final SelenideElement duplicateCancel = duplicateDialog.$(".btn.cancel");
    private final SelenideElement adminButton = $("a[data-hint-container-id='header-administration-link']");
    private final SelenideElement userManagement = $(Selectors.byText("Users"));
    private final SelenideElement usersToShow = $("#usersToShow");
    private final SelenideElement createUserButton = $(".icon_before.icon16.addNew");
    private final SelenideElement setUsername = $("#input_teamcityUsername");
    private final SelenideElement setPassword = $("#password1");
    private final SelenideElement setConfirmPassword = $("#retypedPassword");
    private final SelenideElement createUserButtonFinal = $(".btn.btn_primary.submitButton");
    private final SelenideElement adminCheckbox = $("#administrator");
    private final SelenideElement errorName = $(".input-wrapper.input-wrapper_username");






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
