package jupiter.extension;

import api.models.CreateProjectRequest;
import api.models.CreateUserResponse;
import api.models.ParentProject;
import api.requests.steps.ProjectManagementSteps;
import common.data.ProjectData;
import common.generators.TestDataGenerator;
import jupiter.annotation.WithProject;
import org.junit.jupiter.api.extension.*;

public class ProjectExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(ProjectExtension.class);
    private CreateUserResponse user;

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        WithProject annotation = context.getRequiredTestMethod().getAnnotation(WithProject.class);

        if (annotation != null) {
            user = context.getStore(UsersQueueExtension.NAMESPACE)
                    .get(context.getUniqueId(), CreateUserResponse.class);

            if (user == null) {
                throw new ExtensionConfigurationException("Добавьте аннотацию @WithUsersQueue");
            }

            String parentProjectId = annotation.parentProjectId().equals("default") ? ProjectData.PARENT_PROJECT.value : annotation.parentProjectId();
            String projectId = annotation.projectId().equals("default") ? TestDataGenerator.generateProjectID() : annotation.projectId();
            String projectName = annotation.projectName().equals("default") ? TestDataGenerator.generateProjectName() : annotation.projectName();

            CreateProjectRequest project = CreateProjectRequest.builder()
                    .id(projectId)
                    .name(projectName)
                    .parentProject(new ParentProject(parentProjectId))
                    .build();

            if(!annotation.useExisting()) {
                ProjectManagementSteps.createProject(
                        project.getId(),
                        project.getName(),
                        project.getParentProject(),
                        user
                );
            }

            context.getStore(NAMESPACE).put(context.getUniqueId(), project);

            if(annotation.addToCleanup() && !annotation.useExisting()) {
                context.getStore(NAMESPACE).put("project_cleanup", (ExtensionContext.Store.CloseableResource) () -> {
                    ProjectManagementSteps.deleteProjectByIdQuietly(project.getId(), user);
                });
            }
        }
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
