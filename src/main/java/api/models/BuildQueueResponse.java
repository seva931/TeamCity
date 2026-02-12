package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BuildQueueResponse extends BaseModel {
    private long id;
    private String buildTypeId;
    private String state;
}