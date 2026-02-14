package jupiter.extension;

import api.models.CreateUserResponse;
import api.requests.steps.AdminSteps;
import common.generators.TestDataGenerator;
import jupiter.annotation.User;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class UserExtension implements ParameterResolver, AfterEachCallback {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {

        User anno = AnnotationSupport.findAnnotation(parameterContext.getParameter(), User.class).get();

        String username = anno.username().equals("default") ? TestDataGenerator.generateUsername() : anno.username();
        String password = anno.password().equals("default") ? TestDataGenerator.generatePassword() : anno.password();

        CreateUserResponse user;

        if (anno.useExisting()) {
            user = AdminSteps.getUserInfoByUsername(username);
            user.setTestData(CreateUserResponse.TestData.builder().password(password).build());
        } else {
            user = AdminSteps.createUserWithRole(username, password, anno.role());
        }

        extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), user);
        extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId() + ":cleanup", anno.addToCleanup() && !anno.useExisting()
        );
        return user;
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        CreateUserResponse response = context.getStore(NAMESPACE).get(context.getUniqueId(), CreateUserResponse.class);
        Boolean cleanup = context.getStore(NAMESPACE).get(context.getUniqueId() + ":cleanup", Boolean.class);

        if (Boolean.TRUE.equals(cleanup) && response != null) {
            AdminSteps.deleteUser(response.getId());
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(CreateUserResponse.class)
                && parameterContext.isAnnotated(User.class);
    }
}
