package api.requests.steps;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.generators.TestDataGenerator;

import java.util.List;

public class VCSSteps {
    public static AllVcsRootsResponse getAllRoots (){
       return new CrudRequester(RequestSpecs.adminSpec(), Endpoint.VCS_ROOTS, ResponseSpecs.ok()).get().extract().as(AllVcsRootsResponse.class);
    }

    public static AddNewRootResponse createNewRoot() {
        AddNewRootRequest request = new AddNewRootRequest();
        request.setName(TestDataGenerator.generateVCSName());
        request.setVcsName("jetbrains.git");
        request.setProject(new VcsProject() {{ setId("_Root"); }});
        request.setProperties(new VcsProperties() {{
            setProperty(List.of(
                    new VcsProperty() {{ setName("url"); setValue("https://github.com/org/repo.git"); }},
                    new VcsProperty() {{ setName("branch"); setValue("refs/heads/main"); }}
            ));
        }});

        return new CrudRequester(RequestSpecs.adminSpec(), Endpoint.CREATE_NEW_ROOT, ResponseSpecs.ok())
                .post(request).extract().as(AddNewRootResponse.class);
    }

    public static void createNewRoot(String name) {
        AddNewRootRequest request = new AddNewRootRequest();
        request.setName(name);
        request.setVcsName("jetbrains.git");
        request.setProject(new VcsProject() {{ setId("_Root"); }});
        request.setProperties(new VcsProperties() {{
            setProperty(List.of(
                    new VcsProperty() {{ setName("url"); setValue("https://github.com/org/repo.git"); }},
                    new VcsProperty() {{ setName("branch"); setValue("refs/heads/main"); }}
            ));
        }});

        new CrudRequester(RequestSpecs.adminSpec(), Endpoint.CREATE_NEW_ROOT, ResponseSpecs.ok())
                .post(request);
    }
    public static ErrorResponse createNewRootWithError(String name) {
        AddNewRootRequest request = new AddNewRootRequest();
        request.setName(name);
        request.setVcsName("jetbrains.git");
        request.setProject(new VcsProject() {{ setId("_Root"); }});
        request.setProperties(new VcsProperties() {{
            setProperty(List.of(
                    new VcsProperty() {{ setName("url"); setValue("https://github.com/org/repo.git"); }},
                    new VcsProperty() {{ setName("branch"); setValue("refs/heads/main"); }}
            ));
        }});

        return new CrudRequester(RequestSpecs.adminSpec(), Endpoint.CREATE_NEW_ROOT, ResponseSpecs.InternalServerError())
                .post(request).extract().as(ErrorResponse.class);
    }

}
