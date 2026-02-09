package jupiter.extension;

import api.models.CreateBuildConfigurationRequest;
import api.models.CreateBuildConfigurationResponse;
import api.models.CreateProjectRequest;
import api.models.CreateUserResponse;
import api.requests.steps.BuildManageSteps;
import common.data.ProjectData;
import jupiter.annotation.WithBuild;
import jupiter.annotation.WithProject;
import org.junit.jupiter.api.extension.*;

public class BuildExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(BuildExtension.class);
    private CreateUserResponse user;
    private CreateProjectRequest project;

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        //проверить что есть аннотация, иначе return
        WithProject projectAnnotation = context.getRequiredTestMethod().getAnnotation(WithProject.class);
        if (projectAnnotation == null || projectAnnotation.value().length == 0) {
            return;
        }
        WithBuild buildAnnotation = projectAnnotation.value()[0]; // первый билд
        //взять проект и юзера из хранилища extension
        user = context.getStore(UsersQueueExtension.NAMESPACE).get(context.getUniqueId(), CreateUserResponse.class);
        project = context.getStore(ProjectExtension.NAMESPACE).get(context.getUniqueId(), CreateProjectRequest.class);
        //создать билд
        CreateBuildConfigurationResponse build = BuildManageSteps.createBuildConfiguration(project.getId(), user);
        //сохранить билд в хранилище
        context.getStore(NAMESPACE).put(context.getUniqueId(), build);

    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        CreateBuildConfigurationResponse build = context.getStore(NAMESPACE).get(context.getUniqueId(), CreateBuildConfigurationResponse.class);
        if(build != null) {
            BuildManageSteps.deleteBuildConfigurationQuietly(build.getId(), user);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(CreateBuildConfigurationResponse.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CreateBuildConfigurationResponse.class);
    }
}
