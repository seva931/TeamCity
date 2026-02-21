package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueuedBuild {
    private long id;
    private String buildTypeId;
    private String state;
    private String href;
    private String webUrl;
}
