package ui;

import api.configs.Config;
import api.models.ProjectModel;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.steps.VCSSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.generators.TestDataGenerator;
import common.storage.ProjectStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.pages.AdminPage;
import common.annotations.AdminSession;

import static org.assertj.core.api.Assertions.assertThat;


public class VCSRootTestUi extends BaseUITest {

    //Positive tests
    @Test
    @AdminSession
    @DisplayName("Создание нового рута UI")
    public void getAllRoots() {
        String rootName = Config.getProperty("vcsRootName");
        new AdminPage()
                .getPage(AdminPage.class)
                .createNewRoot(rootName);

        softly.assertThat(VCSSteps.getRootByName(rootName)).isNotNull();

    }

    //Negative tests
    @Test
    @AdminSession
    @DisplayName("Создание одинакового рута UI")
    public void createDuplicateVcsRoot() {
        String projectName = TestDataGenerator.generateNameUI();
        String rootName = Config.getProperty("vcsRootName");
        String value = Config.getProperty("vcsPropertyValue");
        new AdminPage()
                .getPage(AdminPage.class)
                .createDuplicate(projectName, rootName, value);
        ProjectModel project = new CrudRequester(RequestSpecs.adminSpec(), Endpoint.PROJECT_NAME_ID, ResponseSpecs.ok())
                .get(projectName)
                .extract()
                .as(ProjectModel.class);

        ProjectStorage.getStorage().addProject(project);

        assertThat(project.getVcsRoots().getCount()).isEqualTo(1);
    }
}
