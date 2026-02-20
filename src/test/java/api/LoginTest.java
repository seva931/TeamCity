package api;

import api.models.CreateUserResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.data.Cookies;
import io.restassured.response.Response;
import jupiter.annotation.User;
import jupiter.annotation.meta.ApiTest;
import org.junit.jupiter.api.Test;

@ApiTest
public class LoginTest extends BaseTest {
    @Test
    void userCanAccessServerInfoUsingBaseAuth(@User CreateUserResponse user) {
        Response response = new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.SERVER,
                ResponseSpecs.requestReturnsOk()
        ).get().extract().response();

        softly.assertThat(response.getDetailedCookie(Cookies.TCSESSIONID.getName()))
                .as("TCSESSIONID cookie should be present")
                .isNotNull();
        softly.assertThat(response.getCookies().containsKey(Cookies.TCSESSIONID.getName()))
                .as("TCSESSIONID cookie should exist")
                .isTrue();
    }
}
