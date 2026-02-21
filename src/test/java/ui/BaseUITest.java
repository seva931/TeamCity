package ui;

import com.codeborne.selenide.Configuration;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class BaseUITest {
    protected SoftAssertions softly;

    @BeforeAll
    public static void setupSelenide() {
        Configuration.baseUrl = api.configs.Config.getProperty("ui.baseUrl");
        Configuration.browser = api.configs.Config.getProperty("browser");
        Configuration.browserSize = api.configs.Config.getProperty("browser.size");
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
