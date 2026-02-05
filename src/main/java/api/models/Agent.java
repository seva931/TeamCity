package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Agent {
    private long id;
    private String name;
    private long typeId;
    private String href;
    private String webUrl;
}
