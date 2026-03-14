package api.specs;

import api.models.CreateUserResponse;
import configs.Config;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class RequestSpecs {

    private RequestSpecs() {
    }

    private static RequestSpecBuilder defaultRequestBuilder() {
        boolean loggingEnabled = Boolean.parseBoolean(
                System.getProperty("api.logging.enabled", "true")
        );
        AllureRestAssured allureFilter = new AllureRestAssured()
                .setRequestTemplate("http-request.ftl")
                .setResponseTemplate("http-response.ftl");

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder()
                .setConfig(RestAssuredConfig.config().logConfig(
                        LogConfig.logConfig()
                                .blacklistHeader("Authorization")
                                .blacklistHeader("Cookie")
                                .blacklistHeader("Set-Cookie")
                ))
                .addFilters(List.of(
                        new RequestLoggingFilter(LogDetail.METHOD),
                        new RequestLoggingFilter(LogDetail.URI),
                        new ResponseLoggingFilter(LogDetail.STATUS)
                ))
                .setBaseUri(Config.getProperty("BaseUrl") + Config.getProperty("api"));

        if (loggingEnabled) {
            requestSpecBuilder.addFilter(new SensitiveBodyLoggingFilter());
            requestSpecBuilder.addFilter(new ResponseLoggingFilter(LogDetail.BODY));
        }

        requestSpecBuilder.addFilter(allureFilter);

        return requestSpecBuilder;
    }

    public static RequestSpecBuilder withBasicAuth(CreateUserResponse user) {
        return defaultRequestBuilder()
                .addHeader(
                        "Authorization",
                        "Basic " + encode(user.getUsername(), user.getTestData().getPassword()));
    }

    public static RequestSpecBuilder withAdminBasicAuth() {
        return defaultRequestBuilder()
                .addHeader("Authorization", "Basic " +
                        encode(Config.getProperty("admin.login"), Config.getProperty("admin.password")));
    }

    public static RequestSpecification authAsUser(CreateUserResponse userResponse, ContentType type) {
        return defaultRequestBuilder()
                .setContentType(type)
                .setAccept(type)
                .addHeader("Authorization", "Basic " +
                        encode(userResponse.getUsername(), userResponse.getTestData().getPassword()))
                .build();
    }

    public static RequestSpecification authAsUser(String username, String password) {
        return defaultRequestBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("Authorization", "Basic " + encode(username, password))
                .build();
    }

    public static RequestSpecification authAsUser(CreateUserResponse user) {
        return authAsUser(user.getUsername(), user.getTestData().getPassword());
    }

    public static RequestSpecification adminSpec() {
        return authAsUser(Config.getProperty("admin.login"), Config.getProperty("admin.password"));
    }

    private static String encode(String username, String password) {
        return Base64.getEncoder()
                .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
    }
}