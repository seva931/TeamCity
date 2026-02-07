package common.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiAtributesOfResponse {

    NO_BUILD_TYPE_ERROR("No build type nor template is found by id"),
    BUILD_CONFIGURATION_WITH_SUCH_NAME_ALREADY_EXISTS_ERROR("Build configuration with name \"%s\" already exists in project: \"%s\"."),
    NO_PROJECT_FOUND_BY_ID_ERROR("No project found by locator 'count:1,id:%s'. Project cannot be found by external id '%s'.");

    private final String message;

    public String getFormatedUrl(Object... args) {
        return String.format(message, args);
    }
}
