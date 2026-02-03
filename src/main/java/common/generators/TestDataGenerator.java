package common.generators;

import java.util.UUID;

public class TestDataGenerator {
    public static String generateUsername() {
        return "user_" + UUID.randomUUID();
    }

    public static String generatePassword() {
        return "pass_" + UUID.randomUUID();
    }

    public static String generateUsername(String prefix) {
        return prefix + "_" + UUID.randomUUID();
    }
}
