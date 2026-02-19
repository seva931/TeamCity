package api.requests.steps;

import api.models.Agent;
import api.models.AgentResponse;
import api.models.AgentsResponse;
import api.models.CreateUserResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import io.restassured.http.ContentType;

public class AgentSteps {

    public static AgentsResponse getAgents(CreateUserResponse user) {
        return new ValidatedCrudRequester<AgentsResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOk()
        ).get();
    }

    public static AgentsResponse getAgents() {
        return new ValidatedCrudRequester<AgentsResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOk()
        ).get();
    }

    public static AgentsResponse getAllAgents() {
        return new ValidatedCrudRequester<AgentsResponse>(
                RequestSpecs.withAdminBasicAuth()
                        .addQueryParam("locator", "authorized:any,enabled:any")
                        .setAccept(ContentType.JSON)
                        .setContentType(ContentType.JSON)
                        .build(),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOk()
        ).get();
    }

    public static Agent getAgent(CreateUserResponse user) {
        return new ValidatedCrudRequester<AgentsResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOk()
        ).get().getAgent().getFirst();
    }

    public static String disableAgent(long agentId) {
        return new CrudRequester(
                RequestSpecs.withAdminBasicAuth()
                        .setContentType(ContentType.TEXT)
                        .setAccept(ContentType.TEXT)
                        .build(),
                Endpoint.AGENTS_ID_ENABLED,
                ResponseSpecs.requestReturnsOk()
        ).put(agentId, "false").extract().asString();
    }

    public static String enableAgent(long agentId) {
        return new CrudRequester(
                RequestSpecs.withAdminBasicAuth()
                        .setContentType(ContentType.TEXT)
                        .setAccept(ContentType.TEXT)
                        .build(),
                Endpoint.AGENTS_ID_ENABLED,
                ResponseSpecs.requestReturnsOk()
        ).put(agentId, "true").extract().asString();
    }

    public static String authorizeAgent(long agentId) {
        return new CrudRequester(
                RequestSpecs.withAdminBasicAuth()
                        .setContentType(ContentType.TEXT)
                        .setAccept(ContentType.TEXT)
                        .build(),
                Endpoint.AGENTS_ID_AUTHORIZED,
                ResponseSpecs.requestReturnsOk()
        ).put(agentId, "true").extract().asString();
    }

    public static String unauthorizeAgent(long agentId) {
        return new CrudRequester(
                RequestSpecs.withAdminBasicAuth()
                        .setContentType(ContentType.TEXT)
                        .setAccept(ContentType.TEXT)
                        .build(),
                Endpoint.AGENTS_ID_AUTHORIZED,
                ResponseSpecs.requestReturnsOk()
        ).put(agentId, "false").extract().asString();
    }

    public static AgentResponse getAgentById(CreateUserResponse user, long agentId) {

        return new ValidatedCrudRequester<AgentResponse>(
                RequestSpecs.authAsUser(user.getUsername(), user.getTestData().getPassword()),
                Endpoint.AGENTS_ID,
                ResponseSpecs.requestReturnsOk()
        ).get(agentId);
    }
}
