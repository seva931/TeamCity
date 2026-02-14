package jupiter.annotation;

import jupiter.extension.BuildExtension;
import jupiter.extension.ProjectExtension;
import jupiter.extension.VcsExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({
        ProjectExtension.class,
        VcsExtension.class,
        BuildExtension.class
})
public @interface WithBuild {
    WithUsersQueue users() default @WithUsersQueue;
    WithProject project() default @WithProject;
    WithVcs vcs() default @WithVcs(
            url = "default",
            branch = "default"
    );
}

