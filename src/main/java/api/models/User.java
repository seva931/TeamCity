package api.models;

import lombok.Data;

@Data
public class User extends BaseModel{

    private String username;
    private String name;
    private int id;
    private String email;
    private String lastLogin;
    private String password;
    private boolean hasPassword;
    private String realm;
    private String href;

    private Properties properties;
    private Role roles;
    private Groups groups;

    private String locator;
    private Avatars avatars;
    private boolean enabled2FA;
}
