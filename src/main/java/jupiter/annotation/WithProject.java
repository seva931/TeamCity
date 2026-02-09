package jupiter.annotation;

import common.data.ProjectData;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithProject {
    ProjectData parentProjectId() default ProjectData.PARENT_PROJECT;
    WithBuild[] value() default {};
}
