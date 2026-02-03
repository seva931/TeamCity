package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateUserResponse extends BaseModel {
    private String username;
    private Integer id;
    private String href;
    private Roles roles;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Roles {
        private List<Role> role;
    }
}
