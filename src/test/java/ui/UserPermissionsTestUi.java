package ui;

import api.models.User;
import api.models.comparison.UsersUI;
import api.requests.steps.AdminSteps;
import common.generators.TestDataGenerator;
import common.storage.UsersStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import ui.pages.AdminPage;
import common.annotations.AdminSession;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Execution(ExecutionMode.SAME_THREAD)
public class UserPermissionsTestUi extends BaseUITest {
    // Positive case
    @Test
    @AdminSession
    @DisplayName("Получение всех пользователей")
    public void createUserAndCheckPermission() {
        AdminPage adminPage =new AdminPage()
                .getPage(AdminPage.class);

        List<UsersUI> usersFromUi = adminPage
                .getAllUsers()
                .getUsersFromUi();

        List<String> uiNames = usersFromUi.stream()
                .map(UsersUI::getUsername)
                .sorted()
                .collect(Collectors.toList());


        List<String> apiNames = AdminSteps.getAllUsersList().stream()
                .map(u -> u.getUsername())
                .sorted()
                .collect(Collectors.toList());

        assertThat(uiNames).isEqualTo(apiNames);
    }

    @Test
    @AdminSession
    @DisplayName("Создание обычного пользователя")
    public void createCommonUser() {
        String username = TestDataGenerator.generateNameUI();
        new AdminPage()
                .getPage(AdminPage.class)
                .createCommonUser(username);
        User user = AdminSteps.getUser(username);
        UsersStorage.getStorage().addUser(user);

        assertThat(username).isEqualTo(user.getUsername());
    }

    @Test
    @AdminSession
    @DisplayName("Создание пользователя с правами админа")
    public void createAdminUser() {
        String username = TestDataGenerator.generateNameUI();
        new AdminPage()
                .getPage(AdminPage.class)
                .createAdminUser(username);
        User user = AdminSteps.getUser(username);
        UsersStorage.getStorage().addUser(user);

        assertThat(username).isEqualTo(user.getUsername());
        assertThat(
                user.getRoles().getRole().stream().anyMatch(r -> "SYSTEM_ADMIN".equals(r.getRoleId()))
        ).isTrue();
    }

    // Negative tests
    @Test
    @AdminSession
    @DisplayName("Создание пользователя существующего пользователя")
    public void createDuplicateUser() {
        String username = TestDataGenerator.generateNameUI();
        new AdminPage()
                .getPage(AdminPage.class)
                .createDuplicateUser(username);
        User user = AdminSteps.getUser(username);
        UsersStorage.getStorage().addUser(user);
        assertThat(username).isEqualTo(user.getUsername());
        assertThat(AdminSteps.getAllUsersList().stream()
                .filter(u -> username.equals(u.getUsername()))
                .count()).isEqualTo(1L);
    }
}
