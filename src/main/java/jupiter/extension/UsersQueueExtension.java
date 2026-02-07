package jupiter.extension;

import api.configs.Config;
import api.models.CreateUserResponse;
import api.models.RoleId;
import api.requests.steps.AdminSteps;
import common.generators.TestDataGenerator;
import jupiter.annotation.WithUsersQueue;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UsersQueueExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver {
    private static final String USER_POOL_PROPERTY = Config.getProperty("users.pool.size");
    private static final int POOL_SIZE =
            USER_POOL_PROPERTY == null
                    ? 5
                    : Math.max(0, Integer.parseInt(USER_POOL_PROPERTY));
    private static final Queue<CreateUserResponse> USER_POOL_QUEUE = new ConcurrentLinkedQueue<>();
    private static final List<CreateUserResponse> USER_POOL_LIST = new ArrayList<>();

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (!USER_POOL_QUEUE.isEmpty()) {
            return;
        }

        for (int i = 0; i < POOL_SIZE; i++) {

            CreateUserResponse user = AdminSteps.createUserWithRole(
                    TestDataGenerator.generateUsername(),
                    TestDataGenerator.generatePassword(),
                    RoleId.SYSTEM_ADMIN);

            USER_POOL_QUEUE.add(user);
            USER_POOL_LIST.add(user);
        }
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        for (CreateUserResponse user : USER_POOL_LIST) {
            AdminSteps.deleteUser(user.getId());
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        WithUsersQueue anno = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), WithUsersQueue.class)
                .orElse(null);

        if (anno == null) {
            throw new ExtensionConfigurationException("Не задана аннотация @WithUsersQueue над методом");
        }

        if (USER_POOL_QUEUE.isEmpty()) {
            throw new ExtensionConfigurationException("Очередь пуста: увеличь users.pool.size или уменьшай параллелизм");
        }

        context.getStore(NAMESPACE).put(context.getUniqueId(), USER_POOL_QUEUE.poll());
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        USER_POOL_QUEUE.add(context.getStore(NAMESPACE).get(context.getUniqueId(), CreateUserResponse.class));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(CreateUserResponse.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CreateUserResponse.class);
    }
}
