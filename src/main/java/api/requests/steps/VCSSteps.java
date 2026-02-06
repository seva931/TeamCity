package api.requests.steps;

import api.models.CreateNewRootRequest;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

public class VCSSteps {
    public static void getAllRoots (){
        new CrudRequester(RequestSpecs.adminSpec(), Endpoint.GET_ALL_ROOTS, ResponseSpecs.ok()).get();
    }

    public static void createNewRoot (){
        CreateNewRootRequest newRoot = new CreateNewRootRequest();
        new CrudRequester(RequestSpecs.adminSpec(), Endpoint.CREATE_NEW_ROOT, ResponseSpecs.created())
        .post(newRoot);
    }

    
}
