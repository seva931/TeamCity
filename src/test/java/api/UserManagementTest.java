package api;

import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.models.RoleId;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.generators.TestDataGenerator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class UserManagementTest extends BaseTest {
    @Disabled
    @Test
    void shouldCreateUserWithSystemAdminRole() {
        CreateUserRequest request = CreateUserRequest.systemAdmin(
                TestDataGenerator.generateUsername(),
                TestDataGenerator.generatePassword());

        CreateUserResponse response = new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.USERS,
                ResponseSpecs.requestReturnsOk())
                .post(request);

        softly.assertThat(request.getUsername())
                .as("Поле username")
                .isEqualTo(response.getUsername());
        softly.assertThat(request.getRoles().getRole())
                .as("поле role")
                .isEqualTo(response.getRoles().getRole());
    }

    @Test
    void shouldDeleteExistingUser() {
        CreateUserResponse user = AdminSteps.createUserWithRole(
                TestDataGenerator.generateUsername(),
                TestDataGenerator.generatePassword(),
                RoleId.SYSTEM_ADMIN
        );

        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.USERS_ID,
                ResponseSpecs.noContent()
        ).delete(user.getId());

        //TODO: добавить проверки, получать список пользователей и проверять, что удаленного пользователя там нет
    }
}
