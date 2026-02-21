package ui.component;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Selenide.$x;

public class LeftNavigationMenu {

    private final SelenideElement adminButton = $x("//span[@data-test-title='Administration']");
    private final SelenideElement queueButton = $x("//span[contains(@data-test-title,'Queue')]");
    private final SelenideElement agentsButton = $x("//a[@href='/agents']/parent::span");
    private final SelenideElement changesButton = $x("//span[@data-test-title='Changes']");
    private final SelenideElement projectsButton = $x("//span[@data-test-title='Projects']");

    public SelenideElement adminButton() {
        return adminButton;
    }

    public SelenideElement queueButton() {
        return queueButton;
    }

    public SelenideElement agentsButton() {
        return agentsButton;
    }

    public SelenideElement changesButton() {
        return changesButton;
    }

    public SelenideElement projectsButton() {
        return projectsButton;
    }
}