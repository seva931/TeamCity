package api;

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
        AdminSteps.createNewRoot();
    }

    @Test
    public void getAllRoots (){
        AdminSteps.createNewRoot();
        AdminSteps.getAllRoots();
    }

}
