package api;

import org.junit.jupiter.api.Test;
import api.requests.steps.AdminSteps;


public class UsersPermissionsTest extends BaseTest {
    // Positive tests
    @Test
    public void getListOfUsers (){
        AdminSteps.createAdminUser();
        int userCountAfterCreate = AdminSteps.getAllUsers().getCount();
        softly.assertThat(userCountAfterCreate).isGreaterThanOrEqualTo(1);
    }

    @Test
    public void createAdminUser (){
        int userId = AdminSteps.createAdminUser().getId();
        int user = AdminSteps.getUserById(userId).getId();
        softly.assertThat(user).isEqualTo(userId);
    }

    @Test
    public void getRoleForUser (){
        String roleId = AdminSteps.getRoleForUser(1).getRoleId();
        softly.assertThat(roleId).isNotEmpty();
    }

    @Test 
    public void getPermissionsForUser (){
        int permissions = AdminSteps.getPermissionsForUser(1).getCount();
        // softly.assertThat(permissions).isNotEmpty();
    }

    // Negative tests

    public void getPermissionsForUserWithInvalidId (){
        Response response = AdminSteps.getPermissionsWithInvalidId(0);
        softly.assertThat(response.getStatusCode()).isEqualTo(404);
    }
}
