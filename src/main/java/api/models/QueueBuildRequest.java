package api.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QueueBuildRequest extends BaseModel {
    private BuildTypeRef buildType;

    public static QueueBuildRequest of(String buildTypeId) {
        return new QueueBuildRequest(new BuildTypeRef(buildTypeId));
    }
}
