package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentsResponse extends BaseModel{
    private int count;
    private String href;
    private List<Agent> agent;
}
