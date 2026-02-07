package api.models;

import lombok.Data;
import java.util.List;

@Data
public class Users extends BaseModel {
    private int count;
    private List<User> user;
}
