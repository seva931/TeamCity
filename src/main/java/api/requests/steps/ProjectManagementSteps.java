package api.requests.steps;

import api.models.CreateProjectRequest;
import api.models.CreateUserResponse;
import api.models.ParentProject;
import api.models.ProjectListResponse;
import api.models.ProjectResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class ProjectManagementSteps {

    @Step("Получить список всех проектов от имени пользователя '{user.username}'")
    public static ProjectListResponse getAllProjects(CreateUserResponse user) {
        return new CrudRequester(RequestSpecs.authAsUser(user), Endpoint.PROJECTS, ResponseSpecs.ok())
                .get()
                .extract()
                .as(ProjectListResponse.class);
    }

    @Step("Создать проект '{name}' с id '{id}' от имени пользователя '{user.username}'")
    public static ProjectResponse createProject(String id, String name, ParentProject parentProject, CreateUserResponse user) {
        CreateProjectRequest request = CreateProjectRequest.builder()
                .id(id)
                .name(name)
                .parentProject(parentProject)
                .build();

        return new ValidatedCrudRequester<ProjectResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECTS,
                ResponseSpecs.requestReturnsOk())
                .post(request);
    }

    @Step("Создать проект '{request.name}' от имени пользователя '{user.username}'")
    public static ProjectResponse createProject(CreateProjectRequest request, CreateUserResponse user) {
        return new ValidatedCrudRequester<ProjectResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECTS,
                ResponseSpecs.requestReturnsOk())
                .post(request);
    }

    @Step("Получить проект по id '{projectId}' от имени пользователя '{user.username}'")
    public static ProjectResponse getProjectById(String projectId, CreateUserResponse user) {
        return new ValidatedCrudRequester<ProjectResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECT_ID,
                ResponseSpecs.requestReturnsOk())
                .get(projectId);
    }

    @Step("Тихо удалить проект по id '{projectId}' от имени пользователя '{user.username}'")
    public static void deleteProjectByIdQuietly(String projectId, CreateUserResponse user) {
        new CrudRequester(RequestSpecs.authAsUser(user),
                Endpoint.PROJECT_ID,
                ResponseSpecs.deletesQuietly())
                .delete(projectId);
    }

    @Step("Обновить имя проекта '{projectId}' на '{newName}' от имени пользователя '{user.username}'")
    public static String updateProjectName(String projectId, String newName, CreateUserResponse user) {
        RequestSpecification textSpec = new RequestSpecBuilder()
                .addRequestSpecification(RequestSpecs.authAsUser(user))
                .setContentType(ContentType.TEXT)
                .setAccept(ContentType.TEXT)
                .build();

        return new CrudRequester(
                textSpec,
                Endpoint.PROJECT_NAME,
                ResponseSpecs.requestReturnsOk()
        ).put(projectId, newName)
                .extract()
                .asString();
    }

    @Step("Обновить имя проекта '{projectId}' с неверным Content-Type от имени пользователя '{user.username}'")
    public static void updateProjectNameWithWrongContentType(String projectId, String newName, CreateUserResponse user) {
        RequestSpecification wrongSpec = new RequestSpecBuilder()
                .addRequestSpecification(RequestSpecs.authAsUser(user))
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.TEXT)
                .build();

        new CrudRequester(
                wrongSpec,
                Endpoint.PROJECT_NAME,
                ResponseSpecs.notAcceptable()
        ).put(projectId, newName);
    }

    @Step("Получить параметр имени проекта '{projectId}' от имени пользователя '{user.username}'")
    public static String getProjectNameParam(String projectId, CreateUserResponse user) {
        return new CrudRequester(
                RequestSpecs.authAsUser(user, ContentType.TEXT),
                Endpoint.PROJECT_NAME,
                ResponseSpecs.requestReturnsOk()
        ).get(projectId)
                .extract()
                .asString();
    }
}