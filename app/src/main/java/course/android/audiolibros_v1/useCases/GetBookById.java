package course.android.audiolibros_v1.useCases;

import course.android.audiolibros_v1.Libro;
import course.android.audiolibros_v1.infraestructure.repositories.BooksRepository;

/**
 * Created by Casa on 05/02/2017.
 */

public class GetBookById {
    private final BooksRepository booksRepository;

    private GetBookById(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }
    public Libro execute(int bookId) {
        return booksRepository.getById(bookId);
    }

    public static GetBookById create(BooksRepository booksRepository){
        return new GetBookById(booksRepository);
    }
}
