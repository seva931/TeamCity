package api;

import api.requests.steps.VCSSteps;
import org.junit.jupiter.api.Test;

public class VCSRootTest extends BaseTest {

    //Positive tests

    @Test
    public void getAllRoots (){
        VCSSteps.getAllRoots();
        
    }

    @Test
    public void createNewRoot (){
        VCSSteps.createNewRoot();
    }

    //Negative tests

    

}
