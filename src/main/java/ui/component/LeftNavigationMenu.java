package ui.component;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class LeftNavigationMenu {
    private final SelenideElement adminButton = $x("//span[@data-test-title='Administration']");
    private final SelenideElement queueButton = $x("//span[contains(@data-test-title,'Queue')]");
    private final SelenideElement agentsButton = $x("//a[@href='/agents']/parent::span");
    private final SelenideElement changesButton = $x("//span[@data-test-title='Changes']");
    private final SelenideElement projectsButton = $x("//span[@data-test-title='Projects']");

    public void clickAdminButton() {
        adminButton.click();
    }

    public boolean isAdminButtonPresent() {
        adminButton.shouldBe(visible);
        return adminButton.isDisplayed();
    }

    public boolean isQueueButtonPresent() {
        queueButton.shouldBe(visible);
        return queueButton.isDisplayed();
    }

    public boolean isAgentsButtonPresent() {
        agentsButton.shouldBe(visible);
        return agentsButton.isDisplayed();
    }

    public boolean isChangesButtonPresent() {
        changesButton.shouldBe(visible);
        return changesButton.isDisplayed();
    }

    public boolean isProjectsButtonPresent() {
        projectsButton.shouldBe(visible);
        return projectsButton.isDisplayed();
    }
}
