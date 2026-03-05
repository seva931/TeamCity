package common.storage;

import api.models.User;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.LinkedHashSet;
import java.util.Set;

public class UsersStorage {
    private static final ThreadLocal<UsersStorage> INSTANCE = ThreadLocal.withInitial(UsersStorage::new);
    private final Set<User> usersStorage = new LinkedHashSet<>();

    private UsersStorage() {
    }

    public void addUser (User user){
        usersStorage.add(user);
    }
    public void deleteUser(User user) {
        usersStorage.remove(user);
    }
    public void clear (){
        for (User user : usersStorage){
            new CrudRequester(RequestSpecs.adminSpec(), Endpoint.USERS_USER ,ResponseSpecs.noContent())
                    .delete(user.getUsername());
        }
        usersStorage.clear();
    }
    public static UsersStorage getStorage() {
        return INSTANCE.get();
    }
}
