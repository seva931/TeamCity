package api;

import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import common.data.RoleId;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.generators.TestDataGenerator;
import jupiter.annotation.WithUsersQueue;
import jupiter.extension.UsersQueueExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith({
        UsersQueueExtension.class
})
public class UserManagementTest extends BaseTest {
    @Disabled
    @Test
    void shouldCreateUserWithSystemAdminRole() {
        CreateUserRequest request = CreateUserRequest.systemAdmin();

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

    @WithUsersQueue
    @Test
    void shouldReturnInfoAboutExistingUser(CreateUserResponse user) {
        CreateUserResponse response = new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.USERS_USERNAME,
                ResponseSpecs.requestReturnsOk())
                .get(user.getUsername());

        assertThat(response.getUsername()).isEqualTo(user.getUsername());
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
