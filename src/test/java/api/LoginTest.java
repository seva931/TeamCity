package api;

import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
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
}
