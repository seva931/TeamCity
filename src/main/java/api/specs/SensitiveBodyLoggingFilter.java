package api.specs;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.util.regex.Pattern;

public class SensitiveBodyLoggingFilter implements Filter {
    private static final Pattern JSON_SECRETS = Pattern.compile(
            "(?i)\"(password|token|secret|apiKey|accessToken|refreshToken)\"\\s*:\\s*\"[^\"]*\""
    );

    @Override
    public Response filter(FilterableRequestSpecification req,
                           FilterableResponseSpecification res,
                           FilterContext ctx) {
        Object bodyObj = req.getBody();
        String body = bodyObj == null ? "" : String.valueOf(bodyObj);
        if (!body.isBlank()) {
            String masked = JSON_SECRETS.matcher(body).replaceAll("\"$1\":\"***\"");
            System.out.println("REQUEST BODY (masked)");
            System.out.println(masked);
        }
        return ctx.next(req, res);
    }
}
