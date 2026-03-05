package api.models.comparison;

import api.models.BaseModel;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@Getter
public class UsersUI extends BaseModel {
        private final String username;

}
