package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class QueueBuildRequest extends BaseModel {
    private BuildTypeRef buildType;

    public static QueueBuildRequest of(String buildTypeId) {
        return new QueueBuildRequest(new BuildTypeRef(buildTypeId));
    }
}
