package jupiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithVcs {
    String UNDEFINED_NAME = "UNDEFINED NAME";
    String UNDEFINED_PROJECT_ID = "UNDEFINED-PROJECT";
    String DEFAULT_VCS_NAME = "jetbrains.git";
    String ANONYMOUS_AUTH_METHOD = "ANONYMOUS";

    String name() default UNDEFINED_NAME;
    String vcsName() default DEFAULT_VCS_NAME;
    String projectId() default UNDEFINED_PROJECT_ID;
    String url();
    String branch();
    String authMethod() default ANONYMOUS_AUTH_METHOD;
}
