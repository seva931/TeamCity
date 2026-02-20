package jupiter.extension;

import api.models.Agent;
import api.models.AgentsResponse;
import api.requests.steps.AgentSteps;
import jupiter.annotation.WithAgent;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class AgentExtension implements ExecutionCondition, ParameterResolver, BeforeEachCallback, AfterEachCallback {
    private static final ReentrantLock AGENT_LOCK = new ReentrantLock(true);
    ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(AgentExtension.class);

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        if (context.getTestMethod().isEmpty()) {
            return ConditionEvaluationResult.enabled("Not a test method context");
        }

        WithAgent anno = AnnotationSupport
                .findAnnotation(context.getRequiredTestMethod(), WithAgent.class)
                .orElse(null);

        if (anno == null) {
            return ConditionEvaluationResult.enabled("@WithAgent not present");
        }

        int available = AgentSteps.getAllAgents().getCount();
        return available < anno.count()
                ? ConditionEvaluationResult.disabled("Недостаточно агентов: нужно " + anno.count() + ", доступно " + available)
                : ConditionEvaluationResult.enabled("Требуемые агенты доступны");
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {

        WithAgent anno = AnnotationSupport
                .findAnnotation(extensionContext.getRequiredTestMethod(), WithAgent.class)
                .orElseThrow(() -> new ParameterResolutionException("@WithAgent is required"));

        int agentsCount = anno.count();
        AgentsResponse response = AgentSteps.getAllAgents();

        if (response.getCount() < agentsCount) {
            throw new ExtensionConfigurationException("Недостаточно агентов");
        }

        List<Agent> agents = response.getAgent();
        Agent[] agent = new Agent[agentsCount];

        for (int i = 0; i < agentsCount; i++) {
            agent[i] = agents.get(i);
        }

        extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), agent);

        return agent;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), WithAgent.class).isPresent()
                && parameterContext.getParameter().getType().equals(Agent[].class);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        if(AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), WithAgent.class).isEmpty()) {
            return;
        }
        AGENT_LOCK.lock();
        context.getStore(NAMESPACE).put(context.getUniqueId() + ":locked", true);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        Agent[] agents = context.getStore(NAMESPACE).get(context.getUniqueId(), Agent[].class);

        if(agents != null) {
            for(Agent a : agents) {
                long agentId = a.getId();

                AgentSteps.enableAgent(agentId);
                AgentSteps.authorizeAgent(agentId);
            }
        }
        Boolean isLocked = context.getStore(NAMESPACE).get(context.getUniqueId() + ":locked", Boolean.class);
        if(Boolean.TRUE.equals(isLocked)) {
            AGENT_LOCK.unlock();
        }
    }

}
