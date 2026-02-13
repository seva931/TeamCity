package api.sample;

import api.models.CreateBuildTypeResponse;
import api.models.CreateUserResponse;
import jupiter.annotation.*;
import jupiter.extension.UsersQueueExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(UsersQueueExtension.class)
public class SampleBuildTest {
    @WithBuild(
            project = @WithProject(projectId = "MyProj"),
            vcs = @WithVcs(url = "https://git/repo.git", branch = "main")
    )
    @Test
    void test(@Build CreateBuildTypeResponse build, CreateUserResponse user) {
        System.out.println("Build Test ===============");
        System.out.println(build);
        System.out.println(user);
    }
}
