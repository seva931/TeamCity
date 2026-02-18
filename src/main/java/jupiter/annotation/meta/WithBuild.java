package jupiter.annotation.meta;

import jupiter.extension.BuildExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@WithProject
@ExtendWith({BuildExtension.class})
public @interface WithBuild {
}
