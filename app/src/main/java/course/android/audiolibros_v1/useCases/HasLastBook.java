package course.android.audiolibros_v1.useCases;

import course.android.audiolibros_v1.infraestructure.repositories.BooksRepository;

/**
 * Created by Casa on 05/02/2017.
 */

public class HasLastBook {
    private final BooksRepository booksRepository;

    private HasLastBook(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }
    public Boolean execute() {
        return booksRepository.hasLastBook();
    }

    public static HasLastBook create(BooksRepository booksRepository){
        return new HasLastBook(booksRepository);
    }
}
