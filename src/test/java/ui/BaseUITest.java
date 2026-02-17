package ui;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;

public class BaseUITest {
    @BeforeAll
    public static void setupSelenide() {
        Configuration.baseUrl = api.configs.Config.getProperty("ui.baseUrl");
        Configuration.browser = api.configs.Config.getProperty("browser");
        Configuration.browserSize = api.configs.Config.getProperty("browser.size");
    }
}
