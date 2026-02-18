package jupiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Project {
    String UNDEFINED = "UNDEFINED";
    String PARENT_PROJECT_ID = "_Root";

    String parentProjectId() default PARENT_PROJECT_ID;
    String projectName() default UNDEFINED;
    String projectId() default UNDEFINED;
}
