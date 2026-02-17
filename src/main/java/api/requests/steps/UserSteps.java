package api.requests.steps;

import api.models.CreateUserResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.data.Cookies;

public class UserSteps {
    public static String getUserCookie(CreateUserResponse user) {
        return new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.SERVER,
                ResponseSpecs.requestReturnsOk()
        ).get().extract().response().getCookies().get(Cookies.TCSESSIONID.getName());
    }
}
