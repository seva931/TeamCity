package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserRequest extends BaseModel {
    private String username;
    private String password;
    private int id;
    private String name;
    private String email;
    private Roles roles;

    public CreateUserRequest(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Roles {
        private List<Role> role;
    }

    public static CreateUserRequest systemAdmin(String username, String password) {
        return CreateUserRequest.builder()
                .username(username)
                .password(password)
                .roles(Roles.builder()
                        .role(List.of(
                                Role.builder()
                                        .roleId("SYSTEM_ADMIN")
                                        .scope("g")
                                        .build()
                        ))
                        .build())
                .build();
    }
}
