package common.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BuildStepTypeData {
    SIMPLE_RUNNER("simpleRunner");
    private final String type;
}
