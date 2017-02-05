package course.android.audiolibros_v1.useCases;

import course.android.audiolibros_v1.infraestructure.repositories.BooksRepository;
import course.android.audiolibros_v1.infraestructure.repositories.UserConfigRepository;

/**
 * Created by Casa on 05/02/2017.
 */

public class IsAutoplaySelected {
    private UserConfigRepository userConfigRepository;

    private IsAutoplaySelected(UserConfigRepository userConfigRepository) {
        this.userConfigRepository = userConfigRepository;
    }
    public Boolean execute() {
        return userConfigRepository.isAutoplaySelected();
    }

    public static IsAutoplaySelected create(UserConfigRepository booksRepository){
        return new IsAutoplaySelected(booksRepository);
    }
}
