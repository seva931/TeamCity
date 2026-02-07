package api.models;

import lombok.Data;

import java.util.List;

@Data
public class ErrorResponse {
    private List<ErrorDetail> errors;

    @Data
    public static class ErrorDetail {
        private String message;
        private String additionalMessage;
        private String stackTrace;
        private String statusText;
    }
}
