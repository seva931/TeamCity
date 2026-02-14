package jupiter.extension;

import api.models.CreateProjectRequest;
import api.models.CreateUserResponse;
import api.models.ParentProject;
import api.models.ProjectResponse;
import api.requests.steps.ProjectManagementSteps;
import common.data.ProjectData;
import common.generators.TestDataGenerator;
import jupiter.annotation.WithBuild;
import jupiter.annotation.WithProject;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class ProjectExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(ProjectExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        WithProject anno = AnnotationSupport.findAnnotation(
                context.getRequiredTestMethod(),
                WithProject.class
        ).orElse(null);

        if (anno == null) {
            WithBuild build = AnnotationSupport.findAnnotation(
                    context.getRequiredTestMethod(), WithBuild.class
            ).orElse(null);
            if (build != null) {
                anno = build.project();
            }
        }

        if (anno != null) {
            CreateUserResponse user = context.getStore(UsersQueueExtension.NAMESPACE)
                    .get(context.getUniqueId(), CreateUserResponse.class);

            if (user == null) {
                throw new ExtensionConfigurationException("Добавьте аннотацию @WithUsersQueue или подключите UserQueueExtension в @ExtendsWith");
            }

            String parentProjectId = anno.parentProjectId().equals("default") ? ProjectData.PARENT_PROJECT.value : anno.parentProjectId();
            String projectId = anno.projectId().equals("default") ? TestDataGenerator.generateProjectID() : anno.projectId();
            String projectName = anno.projectName().equals("default") ? TestDataGenerator.generateProjectName() : anno.projectName();

            CreateProjectRequest project;

            if (anno.useExisting()) {
                ProjectResponse projectById = ProjectManagementSteps.getProjectById(projectId, user);
                project = CreateProjectRequest.builder()
                        .parentProject(new ParentProject(projectById.getParentProject().getId()))
                        .id(projectById.getId())
                        .name(projectById.getName())
                        .build();
            } else {
                ProjectResponse projectById = ProjectManagementSteps.createProject(
                        projectId,
                        projectName,
                        new ParentProject(parentProjectId),
                        user
                );

                project = CreateProjectRequest.builder()
                        .parentProject(new ParentProject(projectById.getParentProject().getId()))
                        .id(projectById.getId())
                        .name(projectById.getName())
                        .build();
            }

            context.getStore(NAMESPACE).put(context.getUniqueId(), project);

            if (anno.addToCleanup() && !anno.useExisting()) {
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
