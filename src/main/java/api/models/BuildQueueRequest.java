package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class BuildQueueRequest extends BaseModel {
    private BuildTypeRef buildType;

    public static BuildQueueRequest of(String buildTypeId) {
        return new BuildQueueRequest(new BuildTypeRef(buildTypeId));
    }
}
