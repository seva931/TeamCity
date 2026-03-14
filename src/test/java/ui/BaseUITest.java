package ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import configs.Config;
import io.qameta.allure.selenide.AllureSelenide;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.MutableCapabilities;

import java.util.Map;

public class BaseUITest {
    protected SoftAssertions softly;

    @BeforeAll
    public static void setupSelenide() {
        Configuration.baseUrl = Config.getProperty("ui.baseUrl");
        Configuration.browser = Config.getProperty("browser");
        Configuration.browserSize = Config.getProperty("browser.size");

        String remote = Config.getProperty("uiRemote");
        boolean isRemote = remote != null && !remote.isBlank();
        Configuration.remote = isRemote ? remote : null;

        if (isRemote) {
            MutableCapabilities caps = new MutableCapabilities();
            caps.setCapability("selenoid:options", Map.of("enableVNC", true, "enableLog", true));
            Configuration.browserCapabilities = caps;
        } else {
            Configuration.browserCapabilities = new MutableCapabilities();
        }

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
//                .screenshots(true)
                .savePageSource(true));
    }

    @BeforeEach
    public void setupTest() {
        this.softly = new SoftAssertions();
    }

    @AfterEach
    public void afterTest() {
        this.softly.assertAll();
    }
}