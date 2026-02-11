package api.models.comparison;

import org.assertj.core.api.AbstractAssert;

import java.util.List;

public class ModelAssertions extends AbstractAssert<ModelAssertions, Object> {

    private final Object request;
    private final Object response;

    private ModelAssertions(Object request, Object response) {
        super(request, ModelAssertions.class);
        this.request = request;
        this.response = response;
    }

    public static ModelAssertions assertThatModels(Object request, Object response) {
        return new ModelAssertions(request, response);
    }

    public ModelAssertions match() {
        ModelComparisonConfigLoader configLoader = new ModelComparisonConfigLoader("model-comparison.properties");
        List<ModelComparisonConfigLoader.ComparisonRule> rules = configLoader.getRulesFor(request.getClass());

        if (rules.isEmpty()) {
            failWithMessage("No comparison rules found for class %s", request.getClass().getSimpleName());
        }

        boolean foundMatchingRule = false;

        for (ModelComparisonConfigLoader.ComparisonRule rule : rules) {
            // Проверяем, подходит ли это правило для текущего response
            if (rule.getResponseClassSimpleName().equals(response.getClass().getSimpleName())) {
                foundMatchingRule = true;

                ModelComparator.ComparisonResult result = ModelComparator.compareFields(
                        request,
                        response,
                        rule.getFieldMappings()
                );

                if (!result.isSuccess()) {
                    failWithMessage("Model comparison failed for response %s with mismatched fields:\n%s",
                            response.getClass().getSimpleName(), result);
                }

                break; // Нашли подходящее правило и успешно сравнили
            }
        }

        if (!foundMatchingRule) {
            failWithMessage("No matching comparison rule found for request %s and response %s",
                    request.getClass().getSimpleName(), response.getClass().getSimpleName());
        }

        return this;
    }
}