package api;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.steps.AgentSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.data.ApiAtributesOfResponse;
import common.data.RoleId;
import io.restassured.http.ContentType;
import jupiter.annotation.*;
import jupiter.annotation.User;
import jupiter.extension.AgentExtension;
import jupiter.extension.UserExtension;
import jupiter.extension.UsersQueueExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Isolated;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Isolated
@ExtendWith({
        UsersQueueExtension.class,
        UserExtension.class,
        AgentExtension.class
})
public class AgentsTest extends BaseTest {

    private static final long NON_EXISTENT_AGENT_ID = 999_999L;

    @WithUsersQueue
    @WithAgent
    @Test
    void shouldProvideListOfAvailableAgents(CreateUserResponse user) {
        AgentsResponse response = new ValidatedCrudRequester<AgentsResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOk()
        ).get();

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
    void shouldDisableOrEnableAgentById(
            String bodyMessage,
            String responseText,
            CreateUserResponse user,
            @Agents(agents = {@AgentParam}) List<Agent> agents) {
        long agentId = agents.getFirst().getId();

        String response = new CrudRequester(
                RequestSpecs.withBasicAuth(user)
                        .setContentType(ContentType.TEXT)
                        .setAccept(ContentType.TEXT)
                        .build(),
                Endpoint.AGENTS_ID_ENABLED,
                ResponseSpecs.requestReturnsOk()
        ).put(agentId, bodyMessage)
                .extract().asString();

        softly.assertThat(response)
                .as("текст в запросе и текст в ответе совпадают")
                .isEqualTo(responseText);

        AgentResponse agentById = AgentSteps.getAgentById(user, agentId);
        softly.assertThat(agentById.isEnabled())
                .as("Поле enabled")
                .isEqualTo(Boolean.parseBoolean(responseText));
    }

    @WithUsersQueue
    @ParameterizedTest
    @CsvSource({"false,false", "true,true"})
    void shouldAuthorizeOrUnauthorizeAgentById(
            String bodyMessage,
            String responseText,
            CreateUserResponse user,
            @Agents(agents = {@AgentParam}) List<Agent> agents) {
        long agentId = agents.getFirst().getId();

        String response = new CrudRequester(
                RequestSpecs.withBasicAuth(user)
                        .setContentType(ContentType.TEXT)
                        .setAccept(ContentType.TEXT)
                        .build(),
                Endpoint.AGENTS_ID_AUTHORIZED,
                ResponseSpecs.requestReturnsOk()
        ).put(agentId, bodyMessage)
                .extract().asString();

        softly.assertThat(response)
                .as("текст в запросе и текст в ответе совпадают")
                .isEqualTo(responseText);

        AgentResponse agentById = AgentSteps.getAgentById(user, agentId);

        softly.assertThat(agentById.isAuthorized())
                .as("Поле enabled")
                .isEqualTo(Boolean.parseBoolean(responseText));
    }

    @WithUsersQueue
    @Test
    void shouldProvideInfoAboutAgentById(
            CreateUserResponse user,
            @Agents(agents = {@AgentParam}) List<Agent> agents) {
        long agentId = agents.getFirst().getId();

        AgentResponse response = new ValidatedCrudRequester<AgentResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.AGENTS_ID,
                ResponseSpecs.requestReturnsOk()
        ).get(agentId);

        assertThat(response)
                .as("Поля id и name")
                .usingRecursiveComparison()
                .comparingOnlyFields("id", "name")
                .isEqualTo(agents.getFirst());
    }

    @WithUsersQueue
    @Test
    void shouldReturnListOfUnauthorizedAgents(
            CreateUserResponse user,
            @Agents(agents = {
                    @AgentParam(isAuthorized = "false"),
                    @AgentParam
            }) List<Agent> agents) {
        Agent unauthAgent = agents.getFirst();
        Agent authAgent = agents.get(1);

        AgentsResponse response = new CrudRequester(
                RequestSpecs
                        .withBasicAuth(user)
                        .addQueryParam("locator", "authorized:false")
                        .setAccept(ContentType.JSON)
                        .setContentType(ContentType.JSON)
                        .build(),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOk()
        ).get().extract().as(AgentsResponse.class);

        softly.assertThat(response.getAgent())
                .as("Содержит unauthorized агента")
                .contains(unauthAgent);
        softly.assertThat(response.getAgent())
                .as("Не содержит authorized агента")
                .doesNotContain(authAgent);
    }

    @WithUsersQueue
    @Test
    void shouldReturnListOfAuthorizedAgents(
            CreateUserResponse user,
            @Agents(agents = {
                    @AgentParam(isAuthorized = "false"),
                    @AgentParam
            }) List<Agent> agents) {
        Agent unauthAgent = agents.getFirst();
        Agent authAgent = agents.get(1);

        AgentsResponse response = new CrudRequester(
                RequestSpecs
                        .withBasicAuth(user)
                        .addQueryParam("locator", "authorized:true")
                        .setAccept(ContentType.JSON)
                        .setContentType(ContentType.JSON)
                        .build(),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOk()
        ).get().extract().as(AgentsResponse.class);

        softly.assertThat(response.getAgent())
                .as("Содержит unauthorized агента")
                .contains(authAgent);
        softly.assertThat(response.getAgent())
                .as("Не содержит authorized агента")
                .doesNotContain(unauthAgent);
    }

    @WithUsersQueue
    @Test
    void shouldReturnListOfEnabledAgents(
            CreateUserResponse user,
            @Agents(agents = {
                    @AgentParam(isEnabled = "false"),
                    @AgentParam
            }) List<Agent> agents) {
        Agent disabledAgent = agents.getFirst();
        Agent enabledAgent = agents.get(1);

        AgentsResponse response = new CrudRequester(
                RequestSpecs
                        .withBasicAuth(user)
                        .addQueryParam("locator", "enabled:true")
                        .setAccept(ContentType.JSON)
                        .setContentType(ContentType.JSON)
                        .build(),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOk()
        ).get().extract().as(AgentsResponse.class);

        softly.assertThat(response.getAgent())
                .as("Содержит enabled агента")
                .contains(enabledAgent);
        softly.assertThat(response.getAgent())
                .as("Не содержит disabled агента")
                .doesNotContain(disabledAgent);
    }

    @WithUsersQueue
    @Test
    void shouldReturnListOfDisabledAgents(
            CreateUserResponse user,
            @Agents(agents = {
                    @AgentParam(isEnabled = "false"),
                    @AgentParam
            }) List<Agent> agents) {
        Agent disabledAgent = agents.getFirst();
        Agent enabledAgent = agents.get(1);

        AgentsResponse response = new CrudRequester(
                RequestSpecs
                        .withBasicAuth(user)
                        .addQueryParam("locator", "enabled:false")
                        .setAccept(ContentType.JSON)
                        .setContentType(ContentType.JSON)
                        .build(),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOk()
        ).get().extract().as(AgentsResponse.class);

        softly.assertThat(response.getAgent())
                .as("Содержит disabled агента")
                .contains(disabledAgent);
        softly.assertThat(response.getAgent())
                .as("Не содержит enabled агента")
                .doesNotContain(enabledAgent);
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
                        e.getMessage().equals(ApiAtributesOfResponse.NO_AGENT_CAN_BE_FOUND_BY_ID.getFormatedText(NON_EXISTENT_AGENT_ID)))
                .hasSize(1);
    }

    @WithUsersQueue
    @Test
    void shouldNotBeAbleToEnableAgentWithoutPermissions(@User(role = RoleId.PROJECT_VIEWER) CreateUserResponse user, CreateUserResponse admin) {
        long agentId = AgentSteps.getAgent(admin).getId();

        ErrorsResponse response = new CrudRequester(
                RequestSpecs
                        .withBasicAuth(user)
                        .setContentType(ContentType.TEXT)
                        .setAccept(ContentType.TEXT)
                        .build(),
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
