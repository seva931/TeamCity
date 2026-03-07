package api.requests.steps;

import api.models.AddNewRootRequest;
import api.models.AddNewRootResponse;
import api.models.AllVcsRootsResponse;
import api.models.CreateUserResponse;
import api.models.ErrorResponse;
import api.models.VcsProject;
import api.models.VcsProperties;
import api.models.VcsProperty;
import api.models.VcsRoot;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import configs.Config;
import io.qameta.allure.Step;

import java.util.List;

public class VCSSteps {

    @Step("Получить все VCS roots от имени администратора")
    public static AllVcsRootsResponse getAllRoots() {
        return new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.VCS_ROOTS,
                ResponseSpecs.ok()
        ).get().extract().as(AllVcsRootsResponse.class);
    }

    @Step("Получить все VCS roots от имени пользователя '{user.username}'")
    public static AllVcsRootsResponse getAllRoots(CreateUserResponse user) {
        return new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.VCS_ROOTS,
                ResponseSpecs.ok()
        ).get().extract().as(AllVcsRootsResponse.class);
    }

    @Step("Создать новый VCS root от имени администратора")
    public static AddNewRootResponse createNewRoot() {
        AddNewRootRequest request = AddNewRootRequest.createRoot();

        return new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.VCS_ROOTS,
                ResponseSpecs.ok()
        ).post(request).extract().as(AddNewRootResponse.class);
    }

    @Step("Создать новый VCS root от имени пользователя '{user.username}'")
    public static AddNewRootResponse createNewRoot(CreateUserResponse user) {
        AddNewRootRequest request = AddNewRootRequest.createRoot();

        return new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.VCS_ROOTS,
                ResponseSpecs.ok()
        ).post(request).extract().as(AddNewRootResponse.class);
    }

    @Step("Создать новый VCS root с именем '{name}'")
    public static AddNewRootResponse createNewRoot(String name) {
        AddNewRootRequest request = AddNewRootRequest.createRoot();
        request.setName(name);
        request.setVcsName(Config.getProperty("vcsRootName"));
        request.setProject(new VcsProject() {{
            setId(Config.getProperty("vcsId"));
        }});
        request.setProperties(new VcsProperties() {{
            setProperty(List.of(
                    new VcsProperty() {{
                        setName(Config.getProperty("vcsPropertyName"));
                        setValue(Config.getProperty("vcsPropertyValue"));
                    }}
            ));
        }});

        return new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.VCS_ROOTS,
                ResponseSpecs.ok()
        ).post(request).extract().as(AddNewRootResponse.class);
    }

    @Step("Создать новый VCS root с ошибкой для имени '{name}'")
    public static ErrorResponse createNewRootWithError(String name) {
        AddNewRootRequest request = new AddNewRootRequest();
        request.setName(name);
        request.setVcsName(Config.getProperty("vcsRootName"));
        request.setProject(new VcsProject() {{
            setId(Config.getProperty("vcsId"));
        }});
        request.setProperties(new VcsProperties() {{
            setProperty(List.of(
                    new VcsProperty() {{
                        setName(Config.getProperty("vcsPropertyName"));
                        setValue(Config.getProperty("vcsPropertyValue"));
                    }}
            ));
        }});

        return new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.VCS_ROOTS,
                ResponseSpecs.InternalServerError()
        ).post(request).extract().as(ErrorResponse.class);
    }

    @Step("Получить VCS root по имени '{name}'")
    public static VcsRoot getRootByName(String name) {
        AllVcsRootsResponse allRoots = getAllRoots();

        return allRoots.getVcsRoot().stream()
                .filter(root -> root.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("VCS root not found: " + name));
    }
}