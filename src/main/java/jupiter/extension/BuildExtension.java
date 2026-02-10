package jupiter.extension;

import api.models.CreateBuildConfigurationResponse;
import api.models.CreateProjectRequest;
import api.models.CreateUserResponse;
import api.requests.steps.BuildManageSteps;
import jupiter.annotation.Build;
import org.junit.jupiter.api.extension.*;

public class BuildExtension implements AfterEachCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(BuildExtension.class);


    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {

        CreateUserResponse user = extensionContext.getStore(UsersQueueExtension.NAMESPACE).get(extensionContext.getUniqueId(), CreateUserResponse.class);
        CreateProjectRequest project = extensionContext.getStore(ProjectExtension.NAMESPACE).get(extensionContext.getUniqueId(), CreateProjectRequest.class);

        if (user == null || project == null) {
            throw new ExtensionConfigurationException("User and Project annotation are mandatory");
        }

        CreateBuildConfigurationResponse buildConfiguration = BuildManageSteps.createBuildConfiguration(project.getId(), user);

        extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), buildConfiguration);

        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CreateBuildConfigurationResponse.class);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        CreateBuildConfigurationResponse build = context.getStore(NAMESPACE).get(context.getUniqueId(), CreateBuildConfigurationResponse.class);

        CreateUserResponse user = context.getStore(UsersQueueExtension.NAMESPACE).get(context.getUniqueId(), CreateUserResponse.class);
        if (build != null) {
            BuildManageSteps.deleteBuildConfigurationQuietly(build.getId(), user);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(CreateBuildConfigurationResponse.class)
                && parameterContext.isAnnotated(Build.class);
    }
}
