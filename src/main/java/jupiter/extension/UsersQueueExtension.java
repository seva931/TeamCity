package jupiter.extension;

import api.configs.Config;
import api.models.CreateUserResponse;
import api.requests.steps.AdminSteps;
import common.data.RoleId;
import jupiter.annotation.User;
import jupiter.annotation.WithBuild;
import jupiter.annotation.WithUsersQueue;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class UsersQueueExtension implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver {
    private static final String USER_POOL_PROPERTY = Config.getProperty("users.pool.size");
    private static final int POOL_SIZE =
            USER_POOL_PROPERTY == null
                    ? 5
                    : Math.max(0, Integer.parseInt(USER_POOL_PROPERTY));
    private static final BlockingQueue<CreateUserResponse> USER_POOL_QUEUE = new LinkedBlockingQueue<>();
    private static final List<CreateUserResponse> USER_POOL_LIST = new ArrayList<>();
    private static final Object INIT_LOCK = new Object();
    private static volatile boolean INITIALIZED = false;

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (INITIALIZED) {
            return;
        }
        synchronized (INIT_LOCK) {
            if (INITIALIZED) {
                return;
            }
            for (int i = 0; i < POOL_SIZE; i++) {
                CreateUserResponse user = AdminSteps.createUserWithRole(RoleId.SYSTEM_ADMIN);
                USER_POOL_QUEUE.add(user);
                USER_POOL_LIST.add(user);
            }
            INITIALIZED = true;
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        WithUsersQueue anno = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), WithUsersQueue.class)
                .orElse(null);

        if(anno == null) {
            WithBuild build = AnnotationSupport.findAnnotation(
                    context.getRequiredTestMethod(), WithBuild.class
            ).orElse(null);
            if(build != null) {
                anno = build.users();
            }
        }

        if (anno != null) {

            if (anno.addToCleanup()) {
                context.getRoot().getStore(NAMESPACE).getOrComputeIfAbsent(
                        "cleanup",
                        key -> (ExtensionContext.Store.CloseableResource) () -> {
                            for (CreateUserResponse user : USER_POOL_LIST) {
                                AdminSteps.deleteUser(user.getId());
                            }
                        }
                );
            }
            CreateUserResponse user = USER_POOL_QUEUE.poll(60, TimeUnit.SECONDS);
            if (user == null) {
                throw new ExtensionConfigurationException("Не дождались пользователя за 60s");
            }
            context.getStore(NAMESPACE).put(context.getUniqueId(), user);
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        CreateUserResponse response = context.getStore(NAMESPACE).get(context.getUniqueId(), CreateUserResponse.class);
        if (response != null) {
            USER_POOL_QUEUE.add(context.getStore(NAMESPACE).get(context.getUniqueId(), CreateUserResponse.class));
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(CreateUserResponse.class)
                && !parameterContext.isAnnotated(User.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CreateUserResponse.class);
    }
}
