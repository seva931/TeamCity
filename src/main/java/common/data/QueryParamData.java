package common.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum QueryParamData {
    LOCATOR("locator");
    private final String name;
}
