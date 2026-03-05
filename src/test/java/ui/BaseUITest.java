package ui;

import api.BaseTest;
import com.codeborne.selenide.Configuration;
import common.storage.ProjectStorage;
import common.storage.UsersStorage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(common.extensions.AdminExtension.class)
public class BaseUITest extends BaseTest {
    @BeforeAll
    public static void setupSelenide() {
        Configuration.baseUrl = api.configs.Config.getProperty("ui.baseUrl");
        Configuration.browser = api.configs.Config.getProperty("browser");
        Configuration.browserSize = api.configs.Config.getProperty("browser.size");
        Configuration.headless = true;
    }
    @AfterAll
    public static void clearStorage (){
        UsersStorage.getStorage().clear();
        ProjectStorage.getStorage().clear();
    }
}
