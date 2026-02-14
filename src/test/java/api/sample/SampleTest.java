package api.sample;

import api.BaseTest;
import api.models.AddNewRootResponse;
import api.models.CreateBuildTypeResponse;
import api.models.CreateProjectRequest;
import api.models.CreateUserResponse;
import common.data.RoleId;
import jupiter.annotation.*;
import jupiter.extension.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({
        UsersQueueExtension.class,
        ProjectExtension.class,
        BuildExtension.class,
        UserExtension.class,
        VcsExtension.class
})
public class SampleTest extends BaseTest {

    @WithUsersQueue
    @WithProject
    @WithVcs(
            url = "https://github.com/metaf-x/java-app.git",
            branch = "refs/heads/main"
    )
    @Test
    public void buildSampleTest(
            CreateUserResponse admin,
            CreateProjectRequest project,
            @Build CreateBuildTypeResponse build,
            @User(role = RoleId.PROJECT_VIEWER) CreateUserResponse user,
            AddNewRootResponse vcs
    ) {
        System.out.println("Building Sample Test");
        System.out.println(build);

        System.out.println("Project Sample Test");
        System.out.println(project);

        System.out.println("User Sample Test");
        System.out.println(user);

        System.out.println("Admin Sample Test");
        System.out.println(admin);

        System.out.println("VCS Sample Test");
        System.out.println(vcs);
    }
}
