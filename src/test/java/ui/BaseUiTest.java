package ui;

import api.BaseTest;
import api.configs.Config;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;

import java.util.Map;

public class BaseUiTest extends BaseTest {
    @BeforeAll
    public static void setupSelenoid(){
        Configuration.remote = Config.getProperty("uiRemote");
        Configuration.baseUrl = Config.getProperty("uiBaseUrl");
        Configuration.browser = Config.getProperty("browser");
        Configuration.browserSize = Config.getProperty("browserSize");

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enebleLog", true)
        );
    }
}
