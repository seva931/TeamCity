package api.models;

import lombok.Data;
import java.util.List;

@Data
public class Groups extends BaseModel {
    private int count;
    private List<Group> group;
}
