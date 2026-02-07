package jupiter.extension;

import api.models.CreateProjectRequest;
import api.models.CreateUserResponse;
import api.requests.steps.ProjectManagementSteps;
import common.generators.TestDataGenerator;
import jupiter.annotation.WithProject;
import org.junit.jupiter.api.extension.*;

public class ProjectExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(ProjectExtension.class);
    private CreateUserResponse user;

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        WithProject annotation = context.getRequiredTestMethod().getAnnotation(WithProject.class);

        if(annotation != null) {
            user = context.getStore(
                    UsersQueueExtension.NAMESPACE).get(context.getUniqueId(),
                    CreateUserResponse.class);

            if(user == null) {
                throw new ExtensionConfigurationException("Добавьте аннотацию @WithUsersQueue");
            }
                CreateProjectRequest project = ProjectManagementSteps.createProject(
                        TestDataGenerator.generateProjectID(),
                        TestDataGenerator.generateProjectName(),
                        annotation.parentProjectId().value,
                        user);

                context.getStore(NAMESPACE).put(context.getUniqueId(), project);
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        CreateProjectRequest project = context.getStore(NAMESPACE).get(context.getUniqueId(), CreateProjectRequest.class);
        ProjectManagementSteps.deleteProjectByIdQuietly(project.getId(), user);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(CreateProjectRequest.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CreateProjectRequest.class);
    }
}
