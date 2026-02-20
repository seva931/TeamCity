package jupiter.extension;

import api.models.CreateBuildTypeRequest;
import api.models.CreateBuildTypeResponse;
import api.models.ProjectResponse;
import api.requests.steps.AdminSteps;
import common.generators.RandomModelGenerator;
import jupiter.annotation.Build;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class BuildExtension implements AfterEachCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(BuildExtension.class);

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {

        Build anno = AnnotationSupport.findAnnotation(parameterContext.getParameter(), Build.class).orElseThrow(
                () -> new ParameterResolutionException("Необходима аннотация @Build"));

        ProjectResponse project = extensionContext.getStore(
                ProjectExtension.NAMESPACE).get(extensionContext.getUniqueId(),
                ProjectResponse.class);

        //setup request
        CreateBuildTypeRequest request = RandomModelGenerator.generate(CreateBuildTypeRequest.class);

        if (!Build.UNDEFINED.equals(anno.buildId())) {
            request.setId(anno.buildId());
        }

        if (!Build.UNDEFINED.equals(anno.buildName())) {
            request.setName(anno.buildName());
        }

        if (Build.UNDEFINED.equals(anno.projectId())) {
            if (project == null) {
                throw new ExtensionConfigurationException("Аннотация @Project должна стоять перед аннотацией @Build или в параметрах @Build должен быть указан проект");
            }
            request.setProjectId(project.getId());
        } else {
            request.setProjectId(anno.projectId());
        }

        //send request
        CreateBuildTypeResponse response = AdminSteps.createBuildType(request);

        extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), response);

        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CreateBuildTypeResponse.class);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        CreateBuildTypeResponse build = context.getStore(NAMESPACE).get(context.getUniqueId(), CreateBuildTypeResponse.class);
        if (build != null) {
            AdminSteps.deleteBuildTypeQuietly(build.getId());
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(CreateBuildTypeResponse.class)
                && parameterContext.isAnnotated(Build.class);
    }
}
