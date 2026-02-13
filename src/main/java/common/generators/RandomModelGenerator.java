package common.generators;

import com.github.curiousoddman.rgxgen.RgxGen;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class RandomModelGenerator {

    private static final Random random = new Random();

    public static <T> T generate(Class<T> clazz) {
        return generate(clazz, Collections.emptyMap());
    }

    public static  <T> T generate(Class<T> clazz, Map<String, Object> overrides) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            for (Field field : getAllFields(clazz)) {
                field.setAccessible(true);

                if (overrides.containsKey(field.getName())) {
                    field.set(instance, overrides.get(field.getName()));
                    continue;
                }

                Object value;
                GeneratingRule rule = field.getAnnotation(GeneratingRule.class);
                if (rule != null) {
                    value = generateFromRegex(rule.regex(), field.getType());
                } else {
                    value = generateRandomValue(field);
                }
                field.set(instance, value);
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate entity", e);
        }
    }

    public static <T> ModelBuilder<T> builder(Class<T> clazz) {
        return new ModelBuilder<>(clazz);
    }

    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private static Object generateRandomValue(Field field) {
        Class<?> type = field.getType();
        if (type.equals(String.class)) {
            return UUID.randomUUID().toString().substring(0, 8);
        } else if (type.equals(Integer.class) || type.equals(int.class)) {
            return random.nextInt(1000);
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return random.nextLong();
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            return random.nextDouble() * 100;
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return random.nextBoolean();
        } else if (type.equals(List.class)) {
            return generateRandomList(field);
        } else if (type.equals(Date.class)) {
            return new Date(System.currentTimeMillis() - random.nextInt(1000000000));
        } else {
            // Вложенный объект
            return generate(type);
        }
    }

    private static Object generateFromRegex(String regex, Class<?> type) {
        RgxGen rgxGen = new RgxGen(regex);
        String result = rgxGen.generate();
        if (type.equals(Integer.class) || type.equals(int.class)) {
            return Integer.parseInt(result);
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return Long.parseLong(result);
        } else {
            return result;
        }
    }

    private static List<String> generateRandomList(Field field) {
        // Пытаемся определить generic-параметр списка
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            Type actualType = pt.getActualTypeArguments()[0];
            if (actualType == String.class) {
                return List.of(UUID.randomUUID().toString().substring(0, 5),
                        UUID.randomUUID().toString().substring(0, 5));
            }
        }
        return Collections.emptyList();
    }

    public static class ModelBuilder<T> {
        private final Class<T> clazz;
        private final Map<String, Object> overrides = new HashMap<>();

        private ModelBuilder(Class<T> clazz) {
            this.clazz = clazz;
        }

        public ModelBuilder<T> with(String fieldName, Object value) {
            overrides.put(fieldName, value);
            return this;
        }

        public ModelBuilder<T> withProjectId(String projectId) {
            return with("projectId", projectId);
        }

        public T build() {
            return RandomModelGenerator.generate(clazz, overrides);
        }
    }
}
