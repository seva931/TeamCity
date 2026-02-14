package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.apache.commons.lang3.builder.HashCodeExclude;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentResponse extends BaseModel {
    private long id;
    private String name;
    private boolean connected;
    private boolean enabled;
    private boolean authorized;
}
