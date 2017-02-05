package course.android.audiolibros_v1.infraestructure.repositories;

import course.android.audiolibros_v1.infraestructure.storages.UserConfigStorage;

/**
 * Created by Casa on 05/02/2017.
 */

public class UserConfigRepository {
    private final UserConfigStorage userConfigStorage;

    private UserConfigRepository(UserConfigStorage userConfigStorage) {
        this.userConfigStorage = userConfigStorage;
    }

    public Boolean isAutoplaySelected(){
        return this.userConfigStorage.isAutoplaySelected();
    }

    public static UserConfigRepository create(UserConfigStorage userConfigStorage){
        return new UserConfigRepository(userConfigStorage);
    }
}
