package jupiter.extension;

import api.models.CreateUserResponse;
import api.requests.steps.UserSteps;
import com.codeborne.selenide.WebDriverRunner;
import jupiter.annotation.User;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.openqa.selenium.Cookie;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Optional;

import static com.codeborne.selenide.Selenide.clearBrowserCookies;
import static com.codeborne.selenide.Selenide.open;

public class UiAuthExtension implements BeforeEachCallback, InvocationInterceptor {

    @Override
    public void beforeEach(ExtensionContext context) {
        if (WebDriverRunner.hasWebDriverStarted()) {
            clearBrowserCookies();
        }
    }

    @Override
    public void interceptTestMethod(
            Invocation<Void> invocation,
            ReflectiveInvocationContext<Method> invocationContext,
            ExtensionContext extensionContext
    ) throws Throwable {
        findUser(invocationContext).ifPresent(this::loginViaApi);
        invocation.proceed();
    }

    @Override
    public void interceptTestTemplateMethod(
            Invocation<Void> invocation,
            ReflectiveInvocationContext<Method> invocationContext,
            ExtensionContext extensionContext
    ) throws Throwable {
        findUser(invocationContext).ifPresent(this::loginViaApi);
        invocation.proceed();
    }

    private Optional<CreateUserResponse> findUser(ReflectiveInvocationContext<Method> invocationContext) {
        List<Object> args = invocationContext.getArguments();
        Parameter[] parameters = invocationContext.getExecutable().getParameters();

        for (int i = 0; i < parameters.length; i++) {
            if (!parameters[i].isAnnotationPresent(User.class)) {
                continue;
            }
            if (args.get(i) instanceof CreateUserResponse user) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    private void loginViaApi(CreateUserResponse user) {
        String tcSessionId = UserSteps.getUserCookie(user);
        if (tcSessionId == null || tcSessionId.isBlank()) {
            throw new IllegalStateException("TCSESSIONID не получен");
        }

        clearBrowserCookies();
        open("/");
        WebDriverRunner.getWebDriver()
                .manage()
                .addCookie(new Cookie("TCSESSIONID", tcSessionId));
    }
}
