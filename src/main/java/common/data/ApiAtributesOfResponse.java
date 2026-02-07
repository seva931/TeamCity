package common.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiAtributesOfResponse {
    NO_BUILD_TYPE_ERROR("No build type nor template is found by id '%s'."),
    YOU_DONT_HAVE_ENOUGH_PERMISSIONS_ERROR("You do not have enough permissions to edit project with id: %s."),
    BUILD_CONFIGURATION_WITH_SUCH_NAME_ALREADY_EXISTS_ERROR("Build configuration with name \"%s\" already exists in project: \"%s\".");

    private final String message;
}
