package jupiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithProject {
    String parentProjectId() default "default";
    String projectName() default "default";
    String projectId() default "default";
    boolean useExisting() default false;
    boolean addToCleanup() default true;
}
