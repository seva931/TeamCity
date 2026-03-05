package ui.pages;


import api.configs.Config;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;



import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {
    private final SelenideElement usernameInput = $(Selectors.byId("username"));
    private final SelenideElement passwordInput = $(Selectors.byId("password"));
    private final SelenideElement checkBoxRemember = $(Selectors.byId("remember"));
    private final SelenideElement buttonLogin = $(".btn.loginButton");

    @Override
    public String url() {
        return "/login.html";
    }

    public LoginPage loginByUser (String username, String password){
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        checkBoxRemember.click();
        buttonLogin.click();
        return this;

    }
    public LoginPage loginByAdmin (){
        usernameInput.shouldBe(Condition.visible, Condition.enabled)
                .setValue(Config.getProperty("admin.login"));
        passwordInput.shouldBe(Condition.visible, Condition.enabled)
                .setValue(Config.getProperty("admin.password"));

        if (!checkBoxRemember.isSelected()) {
            checkBoxRemember.shouldBe(Condition.visible, Condition.enabled).click();
        }

        buttonLogin.shouldBe(Condition.visible, Condition.enabled).click();
        return this;

    }
}
