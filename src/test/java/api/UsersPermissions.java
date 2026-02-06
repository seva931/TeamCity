package api;

import api.requests.steps.VCSSteps;
import org.junit.jupiter.api.Test;
import api.requests.steps.AdminSteps;



public class UsersPermissions extends BaseTest {
    @Test
    public void getUser (){
        AdminSteps.createAdminUser();
        AdminSteps.getAllUsers();
    }

    @Test
    public void createAdminUser (){
        AdminSteps.createAdminUser();
    }

    @Test
    public void createNewRoot (){
        VCSSteps.createNewRoot();
    }

    @Test
    public void getAllRoots (){
        VCSSteps.createNewRoot();
        VCSSteps.getAllRoots();
    }

}
