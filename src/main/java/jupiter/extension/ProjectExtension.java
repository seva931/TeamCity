package jupiter.extension;

import api.models.CreateProjectRequest;
import api.models.ParentProject;
import api.models.ProjectResponse;
import api.requests.steps.AdminSteps;
import common.generators.RandomModelGenerator;
import jupiter.annotation.Project;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class ProjectExtension implements AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(ProjectExtension.class);

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {

        Project anno = AnnotationSupport.findAnnotation(parameterContext.getParameter(), Project.class).orElseThrow(
                () -> new ParameterResolutionException("@Project annotation is required"));

        //setup request
        CreateProjectRequest request = RandomModelGenerator.generate(CreateProjectRequest.class);

        if (!Project.UNDEFINED.equals(anno.projectName())) {
            request.setName(anno.projectName());
        }

        if (!Project.UNDEFINED.equals(anno.projectId())) {
            request.setId(anno.projectId());
        }

        if (!Project.PARENT_PROJECT_ID.equals(anno.projectId())) {
            request.setParentProject(ParentProject.builder().id(anno.parentProjectId()).build());
        }

        //send request
        ProjectResponse project = AdminSteps.createProject(request);

        extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), project);
        return project;
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        ProjectResponse project = context.getStore(NAMESPACE).get(context.getUniqueId(), ProjectResponse.class);
        if (project != null) {
            AdminSteps.deleteProjectByIdQuietly(project.getId());
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(ProjectResponse.class)
                && parameterContext.isAnnotated(Project.class);
    }
}
