package api.specs;

import api.configs.Config;
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
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(
                        List.of(new RequestLoggingFilter(),
                                new ResponseLoggingFilter()))
                .setBaseUri(Config.getProperty("apiBaseUrl"));
    }

    public static RequestSpecification adminSpec() {
        return defaultRequestBuilder()
                .addHeader("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString(
                                (Config.getProperty("admin.login") + ":" + Config.getProperty("admin.password"))
                                        .getBytes()))
                .build();
    }

}
