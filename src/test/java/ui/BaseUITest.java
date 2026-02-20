package ui;

import api.configs.Config;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;

import java.util.Map;

public class BaseUITest {
    @BeforeAll
    public static void setupSelenide() {
        Configuration.remote = Config.getProperty("uiRemote");
        Configuration.baseUrl = api.configs.Config.getProperty("ui.baseUrl");
        Configuration.browser = api.configs.Config.getProperty("browser");
        Configuration.browserSize = api.configs.Config.getProperty("browser.size");

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enebleLog", true)
        );
    }
}
