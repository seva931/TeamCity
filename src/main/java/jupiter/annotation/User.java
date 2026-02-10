package jupiter.annotation;

import common.data.RoleId;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface User {
    RoleId role() default RoleId.SYSTEM_ADMIN;
    String username() default "default";
    String password() default "default";
    boolean useExisting() default false;
    boolean addToCleanup() default true;
}
