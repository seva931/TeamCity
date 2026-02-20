package jupiter.extension;

import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.models.Role;
import api.requests.steps.AdminSteps;
import common.generators.RandomModelGenerator;
import jupiter.annotation.User;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.List;

public class UserExtension implements ParameterResolver, AfterEachCallback {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {

        User anno = AnnotationSupport.findAnnotation(parameterContext.getParameter(), User.class).orElseThrow(
                () -> new ParameterResolutionException("@User annotation is required"));

        //setup request
        CreateUserRequest request = RandomModelGenerator.generate(CreateUserRequest.class);

        if (!User.UNDEFINED.equals(anno.username())) {
            request.setName(anno.username());
        }

        if (!User.UNDEFINED.equals(anno.password())) {
            request.setPassword(anno.password());
        }

        List<Role> userRoles = List.of(Role.builder().roleId(anno.role().toString()).scope("g").build());
        request.setRoles(CreateUserRequest.Roles.builder().role(userRoles).build());

        //send request
        CreateUserResponse user = AdminSteps.createUser(request);

        extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), user);

        return user;
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
