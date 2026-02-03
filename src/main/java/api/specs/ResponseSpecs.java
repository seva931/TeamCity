package api.specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;

public class ResponseSpecs {

    private ResponseSpecs() {
    }

    public static ResponseSpecification requestReturnsOk() {
        return ok();
    }

    public static ResponseSpecification ok() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    public static ResponseSpecification noContent() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_NO_CONTENT)
                .build();
    }
    public static ResponseSpecification created() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_CREATED)
                .build();
    }
}