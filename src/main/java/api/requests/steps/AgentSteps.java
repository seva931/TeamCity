package api.requests.steps;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

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

    public static Agent getAgent(CreateUserResponse user) {
        return new ValidatedCrudRequester<AgentsResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOk()
        ).get().getAgent().getFirst();
    }

    public static AgentResponse getAgentById(CreateUserResponse user, long agentId) {

        return new ValidatedCrudRequester<AgentResponse>(
                RequestSpecs.authAsUser(user.getUsername(), user.getTestData().getPassword()),
                Endpoint.AGENTS_ID,
                ResponseSpecs.requestReturnsOk()
        ).get(agentId);
    }
}
