package api;

import api.models.AgentResponse;
import api.models.AgentsResponse;
import api.models.CreateUserResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.steps.AgentSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import jupiter.annotation.WithUsersQueue;
import jupiter.extension.UsersQueueExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@ExtendWith({UsersQueueExtension.class})
@WithUsersQueue
public class AgentsTest extends BaseTest {

    private static final long NON_EXISTENT_AGENT_ID = 999_999L;

    @Test
    void shouldProvideListOfAvailableAgents(CreateUserResponse user) {
        AgentsResponse response = new ValidatedCrudRequester<AgentsResponse>(
                RequestSpecs.authAsUser(user),
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

    @ParameterizedTest
    @CsvSource({"false,false", "true,true"})
    void shouldDisableAgentByLocator(String bodyMessage, String responseText, CreateUserResponse user) {
        AgentsResponse agents = AgentSteps.getAgents(user.getUsername(), user.getTestData().getPassword());
        long agentId = agents.getAgent().getFirst().getId();

        String response = new CrudRequester(
                RequestSpecs.authAsUser(user.getUsername(), user.getTestData().getPassword(), ContentType.TEXT),
                Endpoint.AGENTS_ID_ENABLED,
                ResponseSpecs.requestReturnsOk()
        ).put(agentId, bodyMessage)
                .extract().asString();

        softly.assertThat(response)
                .as("текст в запросе и текст в ответе совпадают")
                .isEqualTo(responseText);

        AgentResponse agentById = AgentSteps.getAgentById(user.getUsername(), user.getTestData().getPassword(), agentId);
        softly.assertThat(agentById.isEnabled())
                .as("Поле enabled")
                .isEqualTo(Boolean.parseBoolean(responseText));
    }

    @Test
    void shouldProvideInfoAboutAgentById(CreateUserResponse user) {
        AgentsResponse agents = AgentSteps.getAgents(user);
        long agentId = agents.getAgent().getFirst().getId();

        AgentResponse response = new ValidatedCrudRequester<AgentResponse>(
                RequestSpecs.authAsUser(user),
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

    @Test
    void shouldReturnListOfUnauthorizedAgents(CreateUserResponse user) {

        RequestSpecBuilder requestSpecBuilder = RequestSpecs
                .builder()
                .addQueryParam("locator", "authorized:false")
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON);

        new CrudRequester(
                RequestSpecs.authAsUserWithBuilder(user, requestSpecBuilder),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOk()
        ).get();
    }

    @Test
    void shouldReturnNotFoundForNonexistentAgent(CreateUserResponse user) {

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.AGENTS_ID,
                ResponseSpecs.requestReturnsNotFound()
        ).get(NON_EXISTENT_AGENT_ID);
    }
}
