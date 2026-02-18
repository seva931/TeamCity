package api;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.steps.VCSSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import jupiter.annotation.User;
import jupiter.annotation.meta.ApiTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@ApiTest
public class VCSRootTest extends BaseTest {
    Map<CreateUserResponse, String> collectionRoots = new HashMap<>();

    @AfterEach
    public void cleanAllRoosts() {
        for (var root : collectionRoots.entrySet()) {
            new CrudRequester(RequestSpecs.authAsUser(root.getKey()), Endpoint.VCS_ROOTS_ID, ResponseSpecs.deletesQuietly())
                    .delete(root.getValue());
        }
        collectionRoots.clear();
    }

    //Positive tests

    @Disabled
    @Test
    @DisplayName("Получение всех рутов")
    public void getAllRoots(@User CreateUserResponse user) {
        AllVcsRootsResponse allRootsResponse = VCSSteps.getAllRoots(user);
        softly.assertThat(allRootsResponse.getCount())
                .isGreaterThan(0);

    }

    @Test
    @DisplayName("Создание нового рута")
    public void createNewRoot(@User CreateUserResponse user) {
        AddNewRootResponse createdRoot = VCSSteps.createNewRoot(user);
        collectionRoots.put(user, createdRoot.getId());
        softly.assertThat(createdRoot.getId()).isNotEmpty();
        softly.assertThat(createdRoot.getName()).isNotEmpty();
        VcsRoot getVCSRoot = VCSSteps.getRootByName(createdRoot.getName());
        softly.assertThat(createdRoot.getName()).isEqualTo(getVCSRoot.getName());
    }

    //Negative tests
    @Test
    @DisplayName("Создание одинакового рута")
    public void createDuplicateVcsRoot(@User CreateUserResponse user) {
        AddNewRootResponse createdRoot = VCSSteps.createNewRoot(user);
        AllVcsRootsResponse getAllRoots = VCSSteps.getAllRoots();
        ErrorResponse errorResponse = VCSSteps.createNewRootWithError(createdRoot.getName());
        softly.assertThat(errorResponse.getErrors().get(0).getMessage())
                .isEqualTo("VCS root with name \"" + createdRoot.getName() + "\" already exists in project \"<Root project>\"");
        AllVcsRootsResponse getAllRootsAssert = VCSSteps.getAllRoots();
        collectionRoots.put(user, createdRoot.getId());
        softly.assertThat(getAllRoots.getCount()).isEqualTo(getAllRootsAssert.getCount());
    }

}
