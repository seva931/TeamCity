package api;

import api.models.CreateUserRequest;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class LoginTest extends BaseTest {
    @Test
    void userCanAccessServerInfoUsingBaseAuth() {
        CreateUserRequest adminUser = AdminSteps.createAdminUser();
        Response response = new CrudRequester(
                RequestSpecs.authAsUser(adminUser.getUsername(), adminUser.getPassword()),
                Endpoint.SERVER,
                ResponseSpecs.requestReturnsOk()
        ).get().extract().response();

        softly.assertThat(response.getDetailedCookie("TCSESSIONID"))
                .as("TCSESSIONID cookie should be present")
                .isNotNull();
        softly.assertThat(response.getCookies().containsKey("TCSESSIONID"))
                .as("TCSESSIONID cookie should exist")
                .isTrue();
    }
}
