package jupiter.extension;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.generators.TestDataGenerator;
import jupiter.annotation.WithBuild;
import jupiter.annotation.WithVcs;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.List;

public class VcsExtension implements ParameterResolver, AfterEachCallback, BeforeEachCallback {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(VcsExtension.class);

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

        WithVcs anno = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), WithVcs.class).orElse(null);

        if (anno == null) {
            WithBuild build = AnnotationSupport.findAnnotation(
                    context.getRequiredTestMethod(), WithBuild.class
            ).orElse(null);
            if (build != null) {
                anno = build.vcs();
            }
        }

        if(anno !=null) {
            String aName = WithVcs.UNDEFINED_NAME.equals(anno.name())
                    ? TestDataGenerator.generateVCSName()
                    : anno.name();
            String projectId = WithVcs.UNDEFINED_PROJECT_ID.equals(anno.projectId())
                    ? createProjectRequest.getId()
                    : anno.projectId();

            CreateVcsRootRequest request = CreateVcsRootRequest.builder()
                    .vcsName(anno.vcsName())
                    .project(ProjectModel.builder().id(projectId).build())
                    .name(aName)
                    .properties(
                            VcsProperties.builder()
                                    .property(
                                            List.of(
                                                    VcsProperty.builder().name("authMethod").value(anno.authMethod()).build(),
                                                    VcsProperty.builder().name("url").value(anno.url()).build(),
                                                    VcsProperty.builder().name("branch").value(anno.branch()).build()
                                            )).build()
                    ).build();

            AddNewRootResponse addNewRootResponse = new CrudRequester(
                    RequestSpecs.authAsUser(createUserResponse),
                    Endpoint.VCS_ROOTS,
                    ResponseSpecs.ok()
            ).post(request)
                    .extract().as(AddNewRootResponse.class);

            context.getStore(NAMESPACE).put(context.getUniqueId(), addNewRootResponse);
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        AddNewRootResponse addNewRootResponse = context.getStore(NAMESPACE).get(context.getUniqueId(), AddNewRootResponse.class);
        CreateUserResponse createUserResponse = context.getStore(
                UsersQueueExtension.NAMESPACE).get(context.getUniqueId(),
                CreateUserResponse.class
        );
        if (addNewRootResponse != null && createUserResponse != null) {
            String vcsLocator = addNewRootResponse.getId();

            new CrudRequester(
                    RequestSpecs.authAsUser(createUserResponse),
                    Endpoint.VCS_ROOTS_ID,
                    ResponseSpecs.deletesQuietly()
            ).delete(vcsLocator);
        }
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
