package common.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BuildQueueStateData {
    QUEUED("queued");
    private final String name;
}
