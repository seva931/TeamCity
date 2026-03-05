package common.storage;

import api.models.ProjectModel;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.LinkedHashSet;
import java.util.Set;

public class ProjectStorage {
    private static final ThreadLocal<ProjectStorage> INSTANCE = ThreadLocal.withInitial(ProjectStorage::new);
    private final Set<ProjectModel> projectStorage = new LinkedHashSet<>();

    private ProjectStorage() {
    }

    public void addProject (ProjectModel project){
        projectStorage.add(project);
    }
    public void deleteProject(ProjectModel project) {
        projectStorage.remove(project);
    }
    public void clear (){
        for (ProjectModel project : projectStorage){
            new CrudRequester(RequestSpecs.adminSpec(), Endpoint.PROJECT_ID, ResponseSpecs.noContent())
                    .delete(project.getId());
        }
        projectStorage.clear();
    }
    public static ProjectStorage getStorage() {
        return INSTANCE.get();
    }
}
