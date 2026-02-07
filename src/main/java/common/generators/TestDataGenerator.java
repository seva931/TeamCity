package common.generators;

import org.apache.commons.lang3.RandomStringUtils;

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

    public static String generateVCSName(){
        return RandomStringUtils.randomAlphabetic(5) + " " +
                RandomStringUtils.randomAlphabetic(4);
    }
}
