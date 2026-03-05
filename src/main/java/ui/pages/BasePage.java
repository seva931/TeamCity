package ui.pages;

import com.codeborne.selenide.Selenide;

import static com.codeborne.selenide.Selenide.executeJavaScript;

public abstract class BasePage<T extends BasePage<?>> {
    public abstract String url();

    public T open() {
        return Selenide.open(url(), (Class<T>) this.getClass());
    }

    public <T extends BasePage<?>> T getPage(Class<T> pageClass) {
        return Selenide.page(pageClass);
    }

    public static void authAsUser(String username, String password) {
        Selenide.open("/");
        String token = java.util.Base64.getEncoder()
                .encodeToString((username + ":" + password).getBytes());
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", "Basic " + token);
    }
}
