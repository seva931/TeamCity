package api.sample;

import api.BaseTest;
import api.models.CreateBuildTypeResponse;
import api.models.CreateProjectRequest;
import api.models.CreateUserResponse;
import common.data.RoleId;
import jupiter.annotation.Build;
import jupiter.annotation.User;
import jupiter.annotation.WithProject;
import jupiter.annotation.WithUsersQueue;
import jupiter.extension.BuildExtension;
import jupiter.extension.ProjectExtension;
import jupiter.extension.UserExtension;
import jupiter.extension.UsersQueueExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({
        UsersQueueExtension.class,
        ProjectExtension.class,
        BuildExtension.class,
        UserExtension.class
})
public class SampleTest extends BaseTest {

    @WithUsersQueue
    @WithProject
    @Test
    public void buildSampleTest(
            CreateUserResponse admin,
            CreateProjectRequest project,
            @Build CreateBuildTypeResponse build,
            @User(role = RoleId.PROJECT_VIEWER) CreateUserResponse user
    ) {
        System.out.println("Building Sample Test");
        System.out.println(build);

        System.out.println("Project Sample Test");
        System.out.println(project);

        System.out.println("User Sample Test");
        System.out.println(user);

        System.out.println("Admin Sample Test");
        System.out.println(admin);
    }
}
