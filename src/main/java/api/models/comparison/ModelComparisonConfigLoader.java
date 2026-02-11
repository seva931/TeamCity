package api.models.comparison;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ModelComparisonConfigLoader {

    private final Map<String, List<ComparisonRule>> rules = new HashMap<>();

    public ModelComparisonConfigLoader(String configFile) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (input == null) {
                throw new IllegalArgumentException("Config file not found: " + configFile);
            }
            Properties props = new Properties();
            props.load(input);

            for (String key : props.stringPropertyNames()) {
                // Поддерживаем формат: RequestClass.ResponseClass=field1=field1Response,field2=field2Response
                String[] keyParts = key.split("\\.");
                if (keyParts.length != 2) {
                    // Fallback к старому формату
                    processOldFormat(key, props.getProperty(key));
                    continue;
                }

                String requestClass = keyParts[0].trim();
                String responseClass = keyParts[1].trim();
                List<String> fields = Arrays.asList(props.getProperty(key).split(","));

                ComparisonRule rule = new ComparisonRule(responseClass, fields);
                rules.computeIfAbsent(requestClass, k -> new ArrayList<>()).add(rule);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load DTO comparison config", e);
        }
    }

    private void processOldFormat(String key, String value) {
        String[] target = value.split(":");
        if (target.length != 2) return;

        String responseClassName = target[0].trim();
        List<String> fields = Arrays.asList(target[1].split(","));

        ComparisonRule rule = new ComparisonRule(responseClassName, fields);
        rules.computeIfAbsent(key.trim(), k -> new ArrayList<>()).add(rule);
    }

    public List<ComparisonRule> getRulesFor(Class<?> requestClass) {
        return rules.getOrDefault(requestClass.getSimpleName(), Collections.emptyList());
    }

    public static class ComparisonRule {
        private final String responseClassSimpleName;
        private final Map<String, String> fieldMappings;

        public ComparisonRule(String responseClassSimpleName, List<String> fieldPairs) {
            this.responseClassSimpleName = responseClassSimpleName;
            this.fieldMappings = new HashMap<>();

            for (String pair : fieldPairs) {
                String[] parts = pair.split("=");
                if (parts.length == 2) {
                    fieldMappings.put(parts[0].trim(), parts[1].trim());
                } else {
                    // fallback: same field name if mapping not explicitly given
                    fieldMappings.put(pair.trim(), pair.trim());
                }
            }
        }

        public String getResponseClassSimpleName() {
            return responseClassSimpleName;
        }

        public Map<String, String> getFieldMappings() {
            return fieldMappings;
        }
    }
}