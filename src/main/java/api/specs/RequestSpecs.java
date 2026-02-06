package api.specs;

import api.configs.Config;
import api.models.CreateUserRequest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.Base64;
import java.util.List;

public class RequestSpecs {

    private RequestSpecs() {
    }

    private static RequestSpecBuilder defaultRequestBuilder() {
        return new RequestSpecBuilder()
                .addFilters(
                        List.of(new RequestLoggingFilter(),
                                new ResponseLoggingFilter()))
                .setBaseUri(Config.getProperty("BaseUrl") + Config.getProperty("api"));
    }

    public static RequestSpecBuilder builder() {
        return defaultRequestBuilder();
    }

    public static RequestSpecification authAsUser(String username, String password, RequestSpecBuilder builder) {
        return builder
                .addHeader("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString((username + ":" + password)
                                .getBytes()))
                .build();
    }

    public static RequestSpecification authAsUser(String username, String password, ContentType type) {
        return defaultRequestBuilder()
                .setContentType(type)
                .setAccept(type)
                .addHeader("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString((username + ":" + password)
                                .getBytes()))
                .build();
    }

    public static RequestSpecification authAsUser(String username, String password) {
        return authAsUser(username, password, ContentType.JSON);
    }

    public static RequestSpecification adminSpec() {
        return authAsUser(Config.getProperty("admin.login"), Config.getProperty("admin.password"));
    }

    public static RequestSpecification authAsUserWithBuilder(CreateUserRequest user, RequestSpecBuilder requestSpecBuilder) {
        return requestSpecBuilder
                .addHeader("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString((user.getUsername() + ":" + user.getPassword())
                                .getBytes()))
                .build();
    }
}
