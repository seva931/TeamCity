package jupiter.extension;

import api.models.AddNewRootResponse;
import api.models.CreateBuildTypeResponse;
import api.models.CreateProjectRequest;
import api.models.CreateUserResponse;
import api.requests.steps.BuildManageSteps;
import common.generators.TestDataGenerator;
import jupiter.annotation.Build;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class BuildExtension implements AfterEachCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(BuildExtension.class);

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {

        CreateUserResponse user = extensionContext.getStore(
                UsersQueueExtension.NAMESPACE).get(extensionContext.getUniqueId(),
                CreateUserResponse.class);
        CreateProjectRequest project = extensionContext.getStore(
                ProjectExtension.NAMESPACE).get(extensionContext.getUniqueId(),
                CreateProjectRequest.class);
        AddNewRootResponse addNewRootResponse = extensionContext.getStore(
                VcsExtension.NAMESPACE).get(extensionContext.getUniqueId(),
                AddNewRootResponse.class);

        if (user == null || project == null) {
            throw new ExtensionConfigurationException("User and Project annotation are mandatory");
        }

        Build anno = AnnotationSupport.findAnnotation(parameterContext.getParameter(), Build.class).get();

        String projectId = anno.projectId().equals("default") ? project.getId() : anno.projectId();
        String buildName = anno.buildName().equals("default") ? TestDataGenerator.generateBuildName() : anno.buildName();
        String buildId = anno.buildId().equals("default") ? TestDataGenerator.generateBuildId() : anno.buildId();

        CreateBuildTypeResponse buildConfiguration;
        if (anno.useExisting()) {
            buildConfiguration = CreateBuildTypeResponse.builder()
                    .id(buildId)
                    .name(buildName)
                    .projectId(projectId)
                    .build();
        } else {
            buildConfiguration = BuildManageSteps.createBuildType(projectId, buildId, buildName, user);
        }

        if(addNewRootResponse != null) {
            //todo дописать добавление vcs к билду
        }

        extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), buildConfiguration);
        extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId() + ":cleanup",
                anno.addToCleanup() && !anno.useExisting());

        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CreateBuildTypeResponse.class);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        CreateBuildTypeResponse build = context.getStore(NAMESPACE).get(context.getUniqueId(), CreateBuildTypeResponse.class);
        CreateUserResponse user = context.getStore(UsersQueueExtension.NAMESPACE).get(context.getUniqueId(), CreateUserResponse.class);
        Boolean cleanup = context.getStore(NAMESPACE).get(context.getUniqueId() + ":cleanup", Boolean.class);
        if (Boolean.TRUE.equals(cleanup) && build != null) {
            BuildManageSteps.deleteBuildTypeQuietly(build.getId(), user);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(CreateBuildTypeResponse.class)
                && parameterContext.isAnnotated(Build.class);
    }
}
