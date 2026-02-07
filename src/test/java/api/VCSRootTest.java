package api;

import api.models.*;
import api.requests.steps.VCSSteps;
import common.generators.TestDataGenerator;
import org.junit.jupiter.api.Test;


public class VCSRootTest extends BaseTest {

    //Positive tests

    @Test
    public void getAllRoots (){
        AllVcsRootsResponse allRootsResponse = VCSSteps.getAllRoots();
        softly.assertThat(allRootsResponse.getCount())
                .isGreaterThan(0);
        
    }
    @Test
    public void createNewRoot(){
        VCSSteps.createNewRoot();
        AddNewRootResponse createdRoot = VCSSteps.createNewRoot();
        softly.assertThat(createdRoot.getId()).isNotEmpty();
        softly.assertThat(createdRoot.getName()).isNotEmpty();
        softly.assertThat(createdRoot.getProperties().getProperty())
                .extracting("name")
                .containsExactlyInAnyOrder("url", "branch");
    }

    //Negative tests
    @Test
    public void createDuplicateVcsRoot() {
        String rootName = TestDataGenerator.getName();
        VCSSteps.createNewRoot(rootName);
        ErrorResponse errorResponse = VCSSteps.createNewRootWithError(rootName);
        softly.assertThat(errorResponse.getErrors().get(0).getMessage())
                .isEqualTo("VCS root with name \"" + rootName + "\" already exists in project \"<Root project>\"");
    }

}
