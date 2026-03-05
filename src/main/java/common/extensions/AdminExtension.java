package common.extensions;

import common.annotations.AdminSession;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ui.pages.LoginPage;

public class AdminExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        boolean annotated =
                context.getRequiredTestMethod().isAnnotationPresent(AdminSession.class) ||
                        context.getRequiredTestClass().isAnnotationPresent(AdminSession.class);

        if (annotated) {
            new LoginPage()
                    .open()
                    .loginByAdmin();
        }
    }
}