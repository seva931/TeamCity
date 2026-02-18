package api;

import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.models.Role;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.data.RoleId;
import common.generators.RandomModelGenerator;
import jupiter.annotation.User;
import jupiter.annotation.meta.ApiTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@ApiTest
public class UserManagementTest extends BaseTest {
    @Test
    void shouldCreateUserWithSystemAdminRole() {
        CreateUserRequest request = RandomModelGenerator.generate(CreateUserRequest.class);

        CreateUserRequest.Roles roles = CreateUserRequest.Roles.builder()
                .role(List.of(Role.builder().roleId(RoleId.SYSTEM_ADMIN.toString()).scope("g").build())).build();

        request.setRoles(roles);

        CreateUserResponse response = new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.USERS,
                ResponseSpecs.requestReturnsOk())
                .post(request);

        softly.assertThat(response.getUsername())
                .as("Поле username")
                .isEqualTo(request.getUsername());
        softly.assertThat(response.getRoles().getRole())
                .extracting(Role::getRoleId, Role::getScope)
                .containsExactly(tuple("SYSTEM_ADMIN", "g"));

        //TODO: добавить проверки, получать список пользователей и проверять, что созданный пользователь там есть
    }

    @Test
    void shouldReturnInfoAboutExistingUser(@User CreateUserResponse user) {
        CreateUserResponse response = new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.USERS_USERNAME,
                ResponseSpecs.requestReturnsOk())
                .get(user.getUsername());

        assertThat(response.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    void shouldDeleteExistingUser(@User CreateUserResponse user) {
        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.USERS_ID,
                ResponseSpecs.noContent()
        ).delete(user.getId());

        //TODO: добавить проверки, получать список пользователей и проверять, что удаленного пользователя там нет
    }
}
