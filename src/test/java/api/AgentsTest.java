package api;

import api.models.AgentResponse;
import api.models.AgentsResponse;
import api.models.CreateUserResponse;
import api.models.ErrorsResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.steps.AgentSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.data.ApiAtributesOfResponse;
import common.data.UsersTestData;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import jupiter.annotation.WithUsersQueue;
import jupiter.extension.UsersQueueExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith({UsersQueueExtension.class})
public class AgentsTest extends BaseTest {

    private static final long NON_EXISTENT_AGENT_ID = 999_999L;

    @WithUsersQueue
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

    @WithUsersQueue
    @ParameterizedTest
    @CsvSource({"false,false", "true,true"})
    void shouldDisableOrEnableAgentByLocator(String bodyMessage, String responseText, CreateUserResponse user) {
        AgentsResponse agents = AgentSteps.getAgents(user);
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

    @WithUsersQueue
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

    @WithUsersQueue
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

    @WithUsersQueue
    @Test
    void shouldReturnNotFoundForNonexistentAgent(CreateUserResponse user) {
        ErrorsResponse response = new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.AGENTS_ID,
                ResponseSpecs.notFound()
        ).get(NON_EXISTENT_AGENT_ID)
                .extract().as(ErrorsResponse.class);

        assertThat(response.getErrors())
                .hasSize(1)
                .filteredOn(e ->
                        e.getMessage().equals(ApiAtributesOfResponse.NO_AGENT_CAN_BE_FOUND_BY_ID.getFormatedUrl(NON_EXISTENT_AGENT_ID)))
                .hasSize(1);
    }

    @WithUsersQueue
    @Test
    void shouldNotBeAbleToEnableAgentWithoutPermissions(CreateUserResponse user) {
        CreateUserResponse projectViewerUser = UsersTestData.projectViewerUser;
        long agentId = AgentSteps.getAgent(user).getId();


        ErrorsResponse response = new CrudRequester(
                RequestSpecs.authAsUser(projectViewerUser.getUsername(), projectViewerUser.getTestData().getPassword(), ContentType.TEXT),
                Endpoint.AGENTS_ID_ENABLED,
                ResponseSpecs.forbidden()
        ).put(agentId, "true").extract().as(ErrorsResponse.class);

        assertThat(response.getErrors())
                .hasSize(1)
                .filteredOn(e ->
                        e.getMessage().equals(ApiAtributesOfResponse.YOU_DO_NOT_HAVE_ENABLE_DISABLE_AGENTS_ASSOCIATED_WITH_PROJECT_PERMISSION_FOR_POOL_DEFAULT.getMessage()))
                .hasSize(1);
    }
}
