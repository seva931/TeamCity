package jupiter.extension;

import api.models.CreateUserResponse;
import api.requests.steps.AdminSteps;
import jupiter.annotation.User;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class UserExtension implements ParameterResolver, AfterEachCallback {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {

        User user = AnnotationSupport.findAnnotation(parameterContext.getParameter(), User.class).get();

        CreateUserResponse userWithRole = AdminSteps.createUserWithRole(user.role());

        extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), userWithRole);

        return userWithRole;
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        CreateUserResponse response = context.getStore(NAMESPACE).get(context.getUniqueId(), CreateUserResponse.class);
        if (response != null) {
            AdminSteps.deleteUser(response.getId());
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(CreateUserResponse.class)
                && parameterContext.isAnnotated(User.class);
    }
}
