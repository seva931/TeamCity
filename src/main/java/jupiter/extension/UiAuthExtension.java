package jupiter.extension;

import api.models.CreateUserResponse;
import api.requests.steps.UserSteps;
import com.codeborne.selenide.WebDriverRunner;
import jupiter.annotation.WithUsersQueue;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.codeborne.selenide.Selenide.clearBrowserCookies;
import static com.codeborne.selenide.Selenide.open;

import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.*;

public class UiAuthExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        CreateUserResponse user = context.getStore(UsersQueueExtension.NAMESPACE).get(context.getUniqueId(), CreateUserResponse.class);

        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), WithUsersQueue.class)
                .ifPresent(
                    anno ->
                            loginViaApi(user)
                );
    }

    private void loginViaApi(CreateUserResponse user) {
        String tcSessionId = UserSteps.getUserCookie(user);
        if (tcSessionId == null || tcSessionId.isBlank()) {
            throw new IllegalStateException("TCSESSIONID не получен");
        }

        clearBrowserCookies();
        open("/"); // чтобы браузер уже был на нужном домене
        WebDriverRunner.getWebDriver().manage().addCookie(new Cookie("TCSESSIONID", tcSessionId));
    }
}
