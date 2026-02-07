package common.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.security.cert.CertificateRevokedException;

@Getter
@AllArgsConstructor
public enum ApiAtributesOfResponse {
    NO_BUILD_TYPE_ERROR("No build type nor template is found by id"),
    BUILD_CONFIGURATION_WITH_SUCH_NAME_ALREADY_EXISTS_ERROR("Build configuration with name \"%s\" already exists in project: \"%s\"."),
    NO_AGENT_CAN_BE_FOUND_BY_ID("No agent can be found by id '%s'."),
    YOU_DO_NOT_HAVE_ENABLE_DISABLE_AGENTS_ASSOCIATED_WITH_PROJECT_PERMISSION_FOR_POOL_DEFAULT("You do not have \"Enable / disable agents associated with project\" permission for pool 'Default'");
    private final String message;
    public String getFormatedUrl(Object... args) {
        return String.format(message, args);
    }
}
