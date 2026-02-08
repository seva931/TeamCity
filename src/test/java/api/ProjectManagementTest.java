package api;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.steps.ProjectManagementSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.data.ApiAtributesOfResponse;
import common.generators.TestDataGenerator;
import jupiter.annotation.WithUsersQueue;
import jupiter.extension.UsersQueueExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

import static common.generators.TestDataGenerator.generateProjectID;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith({UsersQueueExtension.class})
public class ProjectManagementTest extends BaseTest {

    private static final String NOT_EXISTS_ID = "PRJ_NOT_EXISTS_404";

    private final List<String> projectsToCleanup = new ArrayList<>();

    @AfterEach
    void cleanup() {
        for (String projectId : projectsToCleanup) {
            new CrudRequester(
                    RequestSpecs.adminSpec(),
                    Endpoint.PROJECT_ID,
                    ResponseSpecs.deletesQuietly()
            ).delete(projectId);
        }
        projectsToCleanup.clear();
    }

    @DisplayName("Позитивный тест: создание проекта")
    @WithUsersQueue
    @Test
    public void userCreateProjectTest(CreateUserResponse user) {
        String projectId = generateProjectID();
        String projectName = TestDataGenerator.generateProjectName();

        ProjectResponse created = ProjectManagementSteps.createProject(projectId, projectName, ParentProject.root(), user);
        projectsToCleanup.add(projectId);

        softly.assertThat(created.getId()).as("Поле id").isEqualTo(projectId);
        softly.assertThat(created.getName()).as("Поле name").isEqualTo(projectName);

        ProjectListResponse list = ProjectManagementSteps.getAllProjects(user);
        boolean existsInList = list.getProject() != null && list.getProject().stream()
                .anyMatch(p -> projectId.equals(p.getId()));

        softly.assertThat(existsInList)
                .as("Проект присутствует в списке проектов по id")
                .isTrue();

        ProjectResponse byId = ProjectManagementSteps.getProjectById(projectId, user);
        softly.assertThat(byId.getId()).as("GET by id: поле id").isEqualTo(projectId);
    }

    @DisplayName("Негативный тест: создание проекта с тем же id")
    @WithUsersQueue
    @Test
    public void userCanNotCreateProjectWithSameIdTest(CreateUserResponse user) {
        String projectId = generateProjectID();
        String firstName = TestDataGenerator.generateProjectName();
        String secondName = (firstName + "_second").toLowerCase();

        ProjectManagementSteps.createProject(projectId, firstName, ParentProject.root(), user);
        projectsToCleanup.add(projectId);

        CreateProjectRequest duplicateRequest = CreateProjectRequest.builder()
                .id(projectId)
                .name(secondName)
                .parentProject(ParentProject.root())
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECTS,
                ResponseSpecs.badRequest()
        ).post(duplicateRequest);

        ProjectResponse byId = ProjectManagementSteps.getProjectById(projectId, user);
        softly.assertThat(byId.getName()).as("Имя проекта не изменилось").isEqualTo(firstName);
    }

    @DisplayName("Позитивный тест: получение списка проектов")
    @WithUsersQueue
    @Test
    public void userGetProjectsListTest(CreateUserResponse user) {
        String projectId = generateProjectID();
        String projectName = TestDataGenerator.generateProjectName();

        ProjectManagementSteps.createProject(projectId, projectName, ParentProject.root(), user);
        projectsToCleanup.add(projectId);

        ProjectListResponse list = ProjectManagementSteps.getAllProjects(user);

        softly.assertThat(list.getCount()).as("Поле count").isNotNull().isGreaterThan(0);
        softly.assertThat(list.getProject()).as("Поле project").isNotNull().isNotEmpty();

        boolean exists = list.getProject().stream().anyMatch(p -> projectId.equals(p.getId()));

        softly.assertThat(exists)
                .as("Список содержит созданный проект по id")
                .isTrue();
    }

    @DisplayName("Позитивный тест: получение информации о проекте по id")
    @WithUsersQueue
    @Test
    public void userGetProjectByIdTest(CreateUserResponse user) {
        String projectId = generateProjectID();
        String projectName = TestDataGenerator.generateProjectName();

        ProjectManagementSteps.createProject(projectId, projectName, ParentProject.root(), user);
        projectsToCleanup.add(projectId);

        ProjectResponse byId = ProjectManagementSteps.getProjectById(projectId, user);

        softly.assertThat(byId.getId()).as("Поле id").isEqualTo(projectId);
        softly.assertThat(byId.getName()).as("Поле name").isEqualTo(projectName);
    }

    @DisplayName("Негативный тест: получение информации о проекте по несуществующему id")
    @WithUsersQueue
    @Test
    public void userGetProjectByNotExistIdTest(CreateUserResponse user) {
        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECT_ID,
                ResponseSpecs.notFound()
        ).get(NOT_EXISTS_ID);
    }

    @DisplayName("Позитивный тест: обновление имени проекта")
    @WithUsersQueue
    @Test
    public void userUpdateProjectNameTest(CreateUserResponse user) {
        String projectId = generateProjectID();
        String initialName = TestDataGenerator.generateProjectName();

        ProjectManagementSteps.createProject(projectId, initialName, ParentProject.root(), user);
        projectsToCleanup.add(projectId);

        String updatedName = (initialName + "_updated").toLowerCase();

        String responseBody = ProjectManagementSteps.updateProjectName(projectId, updatedName, user);
        softly.assertThat(responseBody)
                .as("PUT /parameters/name: тело ответа")
                .isEqualTo(updatedName);

        softly.assertThat(ProjectManagementSteps.getProjectNameParam(projectId, user))
                .as("GET /parameters/name возвращает обновлённое значение")
                .isEqualTo(updatedName);
    }

    @DisplayName("Позитивный тест: удаление проекта")
    @WithUsersQueue
    @Test
    public void userDeleteProjectTest(CreateUserResponse user) {
        String projectId = generateProjectID();
        String projectName = TestDataGenerator.generateProjectName();

        ProjectManagementSteps.createProject(projectId, projectName, ParentProject.root(), user);
        projectsToCleanup.add(projectId);

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECT_ID,
                ResponseSpecs.noContent()
        ).delete(projectId);

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECT_ID,
                ResponseSpecs.notFound()
        ).get(projectId);

        projectsToCleanup.remove(projectId);
    }

    @DisplayName("Негативный тест: удаление несуществующего проекта")
    @WithUsersQueue
    @Test
    public void userDeleteNotExistProjectTest(CreateUserResponse user) {
        ErrorResponse errorResponse = new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECT_ID,
                ResponseSpecs.notFound()
        ).delete(NOT_EXISTS_ID)
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrors())
                .hasSize(1)
                .filteredOn(e -> e.getMessage().equals(
                        ApiAtributesOfResponse.NO_PROJECT_FOUND_BY_ID_ERROR.getFormatedText(
                                NOT_EXISTS_ID,
                                NOT_EXISTS_ID
                        )))
                .hasSize(1);
    }
}