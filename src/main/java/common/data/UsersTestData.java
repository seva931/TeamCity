package common.data;

import api.models.CreateUserResponse;
import api.requests.steps.AdminSteps;
import common.generators.TestDataGenerator;

public class UsersTestData {
    public static CreateUserResponse projectViewerUser = AdminSteps.createUserWithRole(
            TestDataGenerator.generateUsername(),
            TestDataGenerator.generatePassword(),
            RoleId.PROJECT_VIEWER
    );
}
