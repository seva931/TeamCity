package ui.sample;

import com.codeborne.selenide.Selenide;
import jupiter.annotation.WithUsersQueue;
import jupiter.annotation.meta.WebTest;
import org.junit.jupiter.api.Test;
import ui.BaseUITest;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@WebTest
public class SampleUiTest extends BaseUITest {
    @WithUsersQueue
    @Test
    void sampleUiTest() {
        Selenide.open("/changes");
        Selenide.sleep(3000);
        $("[data-test-title='TeamCity']").should(visible);
    }

    @Test
    void noLogin() {
        Selenide.open("/changes");
        Selenide.sleep(3000);
        $("[data-test-title='TeamCity']").shouldNot(visible);
    }
}
