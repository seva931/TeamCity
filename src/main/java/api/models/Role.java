package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Role {
    private String roleId;
    private String scope;

    public static Role projectAdmin() {
        return Role.builder()
                .roleId("PROJECT_ADMIN")
                .scope("g")
                .build();
    }

    public static Role systemAdmin() {
        return Role.builder()
                .roleId("SYSTEM_ADMIN")
                .scope("g")
                .build();
    }
}