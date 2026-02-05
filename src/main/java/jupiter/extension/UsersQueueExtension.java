package jupiter.extension;

import api.configs.Config;
import api.models.CreateUserRequest;
import api.requests.steps.AdminSteps;
import jupiter.annotation.WithUsersQueue;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UsersQueueExtension implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver {
    private static final String USER_POOL_PROPERTY = Config.getProperty("users.pool.size");
    private static final int POOL_SIZE =
            USER_POOL_PROPERTY == null
                    ? 5
                    : Math.max(0, Integer.parseInt(USER_POOL_PROPERTY));
    private static final Queue<CreateUserRequest> USER_POOL = new ConcurrentLinkedQueue<>();

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if(!USER_POOL.isEmpty()) {
            return;
        }

        for (int i = 0; i < POOL_SIZE; i++) {
            CreateUserRequest user = AdminSteps.createAdminUser();
            USER_POOL.add(user);
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        WithUsersQueue anno = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), WithUsersQueue.class)
                .orElseGet(() -> AnnotationSupport.findAnnotation(context.getRequiredTestClass(), WithUsersQueue.class)
                        .orElse(null));

        if (anno == null) {
            throw new ExtensionConfigurationException("Не задана аннотация @WithUsersQueue над классом или методом");
        }

        if (USER_POOL.isEmpty()) {
            throw new ExtensionConfigurationException("Очередь пуста: увеличь users.pool.size или уменьшай параллелизм");
        }

        context.getStore(NAMESPACE).put(context.getUniqueId(), USER_POOL.poll());
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        USER_POOL.add(context.getStore(NAMESPACE).get(context.getUniqueId(), CreateUserRequest.class));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(CreateUserRequest.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CreateUserRequest.class);
    }
}
