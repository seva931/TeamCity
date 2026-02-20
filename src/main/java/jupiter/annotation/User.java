package jupiter.annotation;

import common.data.RoleId;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface User {
    String UNDEFINED = "UNDEFINED UNDEFINED";

    RoleId role() default RoleId.SYSTEM_ADMIN;
    String username() default UNDEFINED;
    String password() default UNDEFINED;
}
