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

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        WithProject annotation = context.getRequiredTestMethod().getAnnotation(WithProject.class);

        if(annotation != null) {
            CreateUserResponse user = context.getStore(
                    UsersQueueExtension.NAMESPACE).get(context.getUniqueId(),
                    CreateUserResponse.class);

            if(user == null) {
                throw new ExtensionConfigurationException("Добавьте аннотацию @WithUsersQueue");
            }
                CreateProjectRequest project = ProjectManagementSteps.createProject(
                        TestDataGenerator.generateProjectID(),
                        TestDataGenerator.generateProjectName(),
                        "_Root",
                        user);

                context.getStore(NAMESPACE).put("project", project);
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(CreateProjectRequest.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get("project", CreateProjectRequest.class);
    }
}
