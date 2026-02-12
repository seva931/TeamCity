package jupiter.extension;

import api.models.Agent;
import api.requests.steps.AgentSteps;
import jupiter.annotation.AgentParam;
import jupiter.annotation.Agents;
import jupiter.annotation.WithAgent;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.opentest4j.TestAbortedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AgentExtension implements ExecutionCondition, ParameterResolver {

    ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(AgentExtension.class);

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        if (context.getTestMethod().isEmpty()) {
            return ConditionEvaluationResult.enabled("Not a test method context");
        }

        WithAgent withAgent = AnnotationSupport
                .findAnnotation(context.getRequiredTestMethod(), WithAgent.class)
                .orElse(null);

        long countFromAgents = Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(p -> p.isAnnotationPresent(Agents.class))
                .map(p -> p.getAnnotation(Agents.class))
                .mapToLong(a -> a.agents().length)
                .sum();

        // 1) приоритет: если есть @Agents — берём его количество
        int required = (countFromAgents > 0)
                ? (int) countFromAgents
                : (withAgent != null ? withAgent.count() : 0);

        if (required == 0) {
            return ConditionEvaluationResult.enabled("No agents required");
        }

        // 2) если агентов меньше — просто пропускаем тест
        int available = AgentSteps.getAllAgents().getCount();
        return available < required
                ? ConditionEvaluationResult.disabled("Недостаточно агентов: нужно " + required + ", доступно " + available)
                : ConditionEvaluationResult.enabled("Требуемые агенты доступны");
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {

        Agents agentsAnno = AnnotationSupport.findAnnotation(parameterContext.getParameter(), Agents.class).get();

        int cfgCount = agentsAnno.agents().length;
        if (cfgCount == 0) {
            throw new ExtensionConfigurationException("добавьте @AgentParam в аннотацию");
        }

        List<Agent> allAgents = AgentSteps.getAllAgents().getAgent();
        if (cfgCount > allAgents.size()) {
            throw new TestAbortedException("Не хватает агентов для запуска теста");
        }
        List<Agent> requiredAgents = new ArrayList<>(allAgents.subList(0, cfgCount));

        for (int i = 0; i < cfgCount; i++) {
            AgentParam cfg = agentsAnno.agents()[i];
            long agentId = requiredAgents.get(i).getId();
            AgentSteps.enableDisableAgent(agentId, cfg.isEnabled());
            AgentSteps.authorizeUnauthorizeAgent(agentId, cfg.isAuthorized());
        }

        extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), requiredAgents);

        return requiredAgents;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.isAnnotated(Agents.class)
                && parameterContext.getParameter().getType().equals(List.class);
    }
}
