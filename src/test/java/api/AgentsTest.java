package api;

import api.models.AgentsResponse;
import api.models.CreateUserRequest;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import org.junit.jupiter.api.Test;

public class AgentsTest extends BaseTest {
    @Test
    void shouldProvideListOfAvailableAgents() {
        CreateUserRequest adminUser = AdminSteps.createAdminUser();
        AgentsResponse response = new ValidatedCrudRequester<AgentsResponse>(
                RequestSpecs.authAsUser(adminUser.getUsername(), adminUser.getPassword()),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOk()
        ).get();

        softly.assertThat(response)
                .as("Ответ не пустой")
                .isNotNull();
        softly.assertThat(response.getAgent())
                .as("Agent не пустой")
                .isNotNull();
        softly.assertThat(response.getCount())
                .as("Поле count")
                .isGreaterThanOrEqualTo(0);
        softly.assertThat(response.getAgent())
                .as("количество agents и поле count")
                .hasSize(response.getCount());
    }
}
