package jupiter.extension;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.generators.TestDataGenerator;
import jupiter.annotation.WithVcs;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.List;

public class VcsExtension implements ParameterResolver, AfterEachCallback, BeforeEachCallback {
    ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(VcsExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        CreateProjectRequest createProjectRequest = context.getStore(
                ProjectExtension.NAMESPACE).get(context.getUniqueId(),
                CreateProjectRequest.class
        );

        if (createProjectRequest == null) {
            throw new ExtensionConfigurationException("Create project request is null");
        }

        CreateUserResponse createUserResponse = context.getStore(
                UsersQueueExtension.NAMESPACE).get(context.getUniqueId(),
                CreateUserResponse.class
        );

        if (createUserResponse == null) {
            throw new ExtensionConfigurationException("Create user response is null");
        }

        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), WithVcs.class).ifPresent(
                vcsAnnotation -> {
                    String aName = WithVcs.UNDEFINED_NAME.equals(vcsAnnotation.name())
                            ? TestDataGenerator.generateVCSName()
                            : vcsAnnotation.name();
                    String projectId = WithVcs.UNDEFINED_PROJECT_ID.equals(vcsAnnotation.projectId())
                            ? createProjectRequest.getId()
                            : vcsAnnotation.projectId();

                    CreateVcsRootRequest request = CreateVcsRootRequest.builder()
                            .vcsName(vcsAnnotation.vcsName())
                            .project(ProjectModel.builder().id(projectId).build())
                            .name(aName)
                            .properties(
                                    VcsProperties.builder()
                                            .property(
                                                    List.of(
                                                            VcsProperty.builder().name("authMethod").value(vcsAnnotation.authMethod()).build(),
                                                            VcsProperty.builder().name("url").value(vcsAnnotation.url()).build(),
                                                            VcsProperty.builder().name("branch").value(vcsAnnotation.branch()).build()
                                                    )).build()
                            ).build();

                    AddNewRootResponse addNewRootResponse = new CrudRequester(
                            RequestSpecs.authAsUser(createUserResponse),
                            Endpoint.CREATE_NEW_ROOT,
                            ResponseSpecs.ok()
                    ).post(request)
                            .extract().as(AddNewRootResponse.class);

                    context.getStore(NAMESPACE).put(context.getUniqueId(), addNewRootResponse);
                }
        );
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(AddNewRootResponse.class);
    }

    @Override
    public AddNewRootResponse resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext
                .getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), AddNewRootResponse.class);
    }
}
