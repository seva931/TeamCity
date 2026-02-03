package api;

import api.models.CreateUserRequest;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import org.junit.jupiter.api.Test;

public class LoginTest extends BaseTest {
    @Test
    void adminCanAccessServerInfoUsingBaseAuth() {
        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.SERVER,
                ResponseSpecs.requestReturnsOk()
        ).get();
    }

    @Test
    void userCanAccessServerInfoUsingBaseAuth() {
        CreateUserRequest user = AdminSteps.createAdminUser();

        new CrudRequester(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.SERVER,
                ResponseSpecs.requestReturnsOk()
        ).get();
    }
}
