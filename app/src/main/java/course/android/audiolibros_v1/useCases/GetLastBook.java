package course.android.audiolibros_v1.useCases;

import course.android.audiolibros_v1.infraestructure.repositories.BooksRepository;

/**
 * Created by Casa on 05/02/2017.
 */

public class GetLastBook {
    private final BooksRepository booksRepository;

    private GetLastBook(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }
    public int execute() {
        return booksRepository.getLastBook();
    }

    public static GetLastBook create(BooksRepository booksRepository){
        return new GetLastBook(booksRepository);
    }
}
