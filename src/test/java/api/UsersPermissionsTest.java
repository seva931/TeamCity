package api;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.generators.TestDataGenerator;
import org.junit.jupiter.api.Test;
import api.requests.steps.AdminSteps;


public class UsersPermissionsTest extends BaseTest {
    // Positive tests
    @Test
    public void getListOfUsers (){
        AdminSteps.createAdminUser();
        int totalUsers = AdminSteps.getAllUsers().getCount();
        softly.assertThat(totalUsers).isGreaterThanOrEqualTo(1);
    }

    @Test
    public void createAdminUser (){
        CreateUserResponse createdUser = AdminSteps.createUserWithRole(
                TestDataGenerator.generateUsername(),
                TestDataGenerator.generatePassword(),
                RoleId.SYSTEM_ADMIN
        );
        int createdUserId = createdUser.getId();
        int fetchedUserId = new CrudRequester(RequestSpecs.adminSpec(), Endpoint.USERS_ID, ResponseSpecs.ok())
                .get(createdUserId)
                .extract()
                .as(User.class)
                .getId();
        softly.assertThat(createdUserId).isEqualTo(fetchedUserId);
    }

    @Test
    public void checkRoleForUser(){
        CreateUserResponse user = AdminSteps.createUserWithRole(
                TestDataGenerator.generateUsername(),
                TestDataGenerator.generatePassword(),
                RoleId.SYSTEM_ADMIN
        );
        int createdUserId = user.getId();
        CreateUserResponse response = new CrudRequester(RequestSpecs.adminSpec(), Endpoint.USERS_ID, ResponseSpecs.ok())
                .get(createdUserId)
                .extract()
                .as(CreateUserResponse.class);
        softly.assertThat(user.getRoles()).isEqualTo(response.getRoles());
    }

    @Test 
    public void getPermissionsForUser (){
        CreateUserResponse user = AdminSteps.createUserWithRole(
                TestDataGenerator.generateUsername(),
                TestDataGenerator.generatePassword(),
                RoleId.SYSTEM_ADMIN
        );
        int createdUserId = user.getId();
        PermissionsResponse userPermissions = AdminSteps.getPermissionsForUser(createdUserId);
        softly.assertThat(userPermissions).isNotNull();

    }

    // Negative tests
    @Test
    public void getPermissionsWithInvalidId() {
        ErrorResponse notFoundResponse = new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.USERS_ID_PERMISSIONS,
                ResponseSpecs.notFound()
        ).get(-1)
                .extract()
                .as(ErrorResponse.class);

        softly.assertThat(notFoundResponse.getErrors())
                .isNotEmpty();

        softly.assertThat(notFoundResponse.getErrors().get(0).getMessage())
                .isEqualTo("User not found");
    }
    @Test
    public void getRoleWithInvalidId(){
        ErrorResponse errorResponse = new CrudRequester(RequestSpecs.adminSpec(), Endpoint.USERS_ID, ResponseSpecs.notFound())
                .get(-1)
                .extract()
                .as(ErrorResponse.class);

        softly.assertThat(errorResponse.getErrors())
                .isNotEmpty();

        softly.assertThat(errorResponse.getErrors().get(0).getMessage())
                .isEqualTo("User not found");
    }
}
