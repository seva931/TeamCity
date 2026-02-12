package jupiter.extension;

import api.models.Agent;
import api.models.AgentsResponse;
import api.requests.steps.AgentSteps;
import jupiter.annotation.Agents;
import jupiter.annotation.WithAgent;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;

public class AgentExtension implements ExecutionCondition, ParameterResolver {

    ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(AgentExtension.class);

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        if (context.getTestMethod().isEmpty()) {
            return ConditionEvaluationResult.enabled("Not a test method context");
        }

        WithAgent anno = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), WithAgent.class).orElse(null);
        if (anno == null) {
            return ConditionEvaluationResult.enabled("No @WithAgent annotation");
        }

        AgentsResponse agents = AgentSteps.getAgents();
        return agents.getCount() < anno.count()
                ? ConditionEvaluationResult.disabled("Нет агентов для выполнения теста")
                : ConditionEvaluationResult.enabled("Требуемые агенты доступны");
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Agents anno = AnnotationSupport.findAnnotation(parameterContext.getParameter(), Agents.class).get();
        WithAgent withAgent = AnnotationSupport
                .findAnnotation(extensionContext.getRequiredTestMethod(), WithAgent.class)
                .orElseThrow(() -> new ExtensionConfigurationException("Добавь @WithAgent над тестом"));

        int count = withAgent.count();
        List<Agent> agents = AgentSteps.getAgents().getAgent();
        if (agents.size() < count) {
            throw new ExtensionConfigurationException("Недостаточно агентов: требуется " + count);
        }
        return new ArrayList<>(agents.subList(0, count));

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.isAnnotated(Agents.class)
                && parameterContext.getParameter().getType().equals(List.class);
    }
}
