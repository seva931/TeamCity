package api;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.data.RoleId;
import common.generators.TestDataGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import api.requests.steps.AdminSteps;

import java.util.ArrayList;
import java.util.List;



public class UsersPermissionsTest extends BaseTest {
    List<Integer> usersId = new ArrayList<>();
    @AfterEach
    public void cleanAllRoosts() {
        for (long root : usersId) {
            AdminSteps.deleteUser(root);
        }
        usersId.clear();
    }
    // Positive tests
    @Test
    @DisplayName("Получение всех пользователей")
    public void getListOfUsers() {
        GetUsersResponse allUsers = AdminSteps.getAllUsers();
        CreateUserRequest user = AdminSteps.createAdminUser();
        usersId.add(user.getId());
        GetUsersResponse allUsersAfterCreate = AdminSteps.getAllUsers();
        softly.assertThat(allUsers.getCount()).isEqualTo(allUsersAfterCreate.getCount()-1);
    }

    @Test
    @DisplayName("Создание пользователя админом")
    public void createAdminUser() {
        CreateUserResponse createdUser = AdminSteps.createUserWithRole(
                TestDataGenerator.generateUsername(),
                TestDataGenerator.generatePassword(),
                RoleId.SYSTEM_ADMIN
        );
        int createdUserId = createdUser.getId();
        usersId.add(createdUser.getId());
        int fetchedUserId = new CrudRequester(RequestSpecs.adminSpec(), Endpoint.USERS_ID, ResponseSpecs.ok())
                .get(createdUserId)
                .extract()
                .as(User.class)
                .getId();
        softly.assertThat(createdUserId).isEqualTo(fetchedUserId);
    }

    @Test
    @DisplayName("Проверка роли пользователя")
    public void checkRoleForUser() {
        CreateUserResponse createdUser = AdminSteps.createUserWithRole(
                TestDataGenerator.generateUsername(),
                TestDataGenerator.generatePassword(),
                RoleId.SYSTEM_ADMIN
        );
        int createdUserId = createdUser.getId();
        usersId.add(createdUser.getId());
        CreateUserResponse response = new CrudRequester(RequestSpecs.adminSpec(), Endpoint.USERS_ID, ResponseSpecs.ok())
                .get(createdUserId)
                .extract()
                .as(CreateUserResponse.class);
        softly.assertThat(createdUser.getRoles()).isEqualTo(response.getRoles());
    }

    @Test
    @DisplayName("Проверка прав пользователя")
    public void getPermissionsForUser() {
        CreateUserResponse createdUser = AdminSteps.createUserWithRole(
                TestDataGenerator.generateUsername(),
                TestDataGenerator.generatePassword(),
                RoleId.SYSTEM_ADMIN
        );
        int createdUserId = createdUser.getId();
        usersId.add(createdUser.getId());
        PermissionsResponse userPermissions = AdminSteps.getPermissionsForUser(createdUserId);
        softly.assertThat(userPermissions).isNotNull();

    }

    // Negative tests
    @Test
    @DisplayName("Негативная проверка прав пользователя")
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
    @DisplayName("Негативная проверка роли пользователя")
    public void getRoleWithInvalidId() {
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
