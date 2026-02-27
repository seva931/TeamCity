package ui;

import api.models.Agent;
import api.models.CreateUserResponse;
import api.requests.steps.AgentSteps;
import jupiter.annotation.User;
import jupiter.annotation.WithAgent;
import jupiter.annotation.meta.WebTest;
import jupiter.extension.AgentExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.pages.AgentsOverviewPage;

@WebTest
@ExtendWith(AgentExtension.class)
public class AgentsTest extends BaseUITest {

    @WithAgent(configKeys = {"teamcity.agent.1.name"})
    @Test
    void userCanDisableAgent(@User CreateUserResponse user, Agent[] agents) {
        String agentName = agents[0].getName();

        new AgentsOverviewPage()
                .open()
                .disableAgent(agentName, "Disable for maintenance");

        boolean isEnabled = AgentSteps.getAgentByName(user, agentName).isEnabled();
        softly.assertThat(isEnabled)
                .as("Поле enabled")
                .isFalse();
    }

    @WithAgent(configKeys = {"teamcity.agent.1.name"})
    @Test
    void userCanUnauthorizeAgent(
            @User CreateUserResponse user,
            Agent[] agent
    ) {
        String agentName = agent[0].getName();
        String agentPool = agent[0].getPool().getName();
        new AgentsOverviewPage()
                .open()
                .openAgentFromSidebar(agentPool, agentName)
                .unauthorizeAgent("Unauthorized agent comment");

        boolean isEnabled = AgentSteps.getAgentByName(user, agentName).isAuthorized();
        softly.assertThat(isEnabled)
                .as("Поле authorized")
                .isFalse();
    }
}
