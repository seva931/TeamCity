package jupiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Build {
    String UNDEFINED = "UNDEFINED";

    String projectId() default UNDEFINED;
    String buildName() default UNDEFINED;
    String buildId() default UNDEFINED;
}
