package common.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Cookies {
    TCSESSIONID("TCSESSIONID");
    private final String name;
}
