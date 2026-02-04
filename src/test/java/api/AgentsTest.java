package api;

import api.models.AgentResponse;
import api.models.AgentsResponse;
import api.models.CreateUserRequest;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.requests.steps.AgentSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import io.restassured.http.ContentType;
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

    @Test
    void shouldDisableAgentByLocator() {
        CreateUserRequest adminUser = AdminSteps.createAdminUser();
        AgentsResponse agents = AgentSteps.getAgents(adminUser.getUsername(), adminUser.getPassword());
        long agentId = agents.getAgent().getFirst().getId();
        String bodyMessage = "true";

        String response = new CrudRequester(
                RequestSpecs.authAsUser(adminUser.getUsername(), adminUser.getPassword(), ContentType.TEXT),
                Endpoint.AGENTS_ID_ENABLED,
                ResponseSpecs.requestReturnsOk()
        ).put(agentId, bodyMessage)
                .extract().asString();

        softly.assertThat(response)
                .as("текст в запросе и текст в ответе совпадают")
                .isEqualTo(bodyMessage);

        AgentResponse agentById = AgentSteps.getAgentById(adminUser.getUsername(), adminUser.getPassword(), agentId);
        softly.assertThat(agentById.isEnabled())
                .as("Поле enabled")
                .isEqualTo(Boolean.parseBoolean(bodyMessage));
    }

    @Test
    void shouldProvideInfoAboutAgentById() {
        CreateUserRequest adminUser = AdminSteps.createAdminUser();
        AgentsResponse agents = AgentSteps.getAgents(adminUser.getUsername(), adminUser.getPassword());
        long agentId = agents.getAgent().getFirst().getId();

        AgentResponse response = new ValidatedCrudRequester<AgentResponse>(
                RequestSpecs.authAsUser(adminUser.getUsername(), adminUser.getPassword()),
                Endpoint.AGENTS_ID,
                ResponseSpecs.requestReturnsOk()
        ).get(agentId);

        softly.assertThat(response.getId())
                .as("Поле id")
                .isEqualTo(agentId);
        softly.assertThat(response.getName())
                .as("Поле name")
                .isEqualTo(agents.getAgent().getFirst().getName());
    }
}
