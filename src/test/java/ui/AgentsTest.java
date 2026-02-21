package ui;

import api.models.CreateUserResponse;
import jupiter.annotation.User;
import jupiter.annotation.meta.WebTest;
import org.junit.jupiter.api.Test;
import ui.pages.AgentsOverviewPage;

@WebTest
public class AgentsTest extends BaseUITest {
    @Test
    void userCanDisableAgent(@User CreateUserResponse user) {
        //TODO вынести имена агентов в конфиг
        new AgentsOverviewPage()
                .open()
                .disableAgent("teamcity-agent-docker-2", "Disable for maintenance");

        //TODO добавить проверку на апи что агент disabled
        //TODO возвращать в состояние enabled (добавить @WithAgent аннотацию)
    }
}
