package api;

import jupiter.extension.BuildExtension;
import jupiter.extension.ProjectExtension;
import jupiter.extension.UsersQueueExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({
        UsersQueueExtension.class,
        ProjectExtension.class,
        BuildExtension.class
})
public class BuildStepsTest {
}
