package common.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BuildStepPropertyData {
    SCRIPT_CONTENT("script.content");
    private final String name;
}
