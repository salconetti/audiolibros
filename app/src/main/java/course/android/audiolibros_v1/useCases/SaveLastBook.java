package course.android.audiolibros_v1.useCases;

import course.android.audiolibros_v1.infraestructure.repositories.BooksRepository;

/**
 * Created by Casa on 05/02/2017.
 */

public class SaveLastBook {
    private final BooksRepository booksRepository;

    private SaveLastBook(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }
    public void execute(int id) {
        booksRepository.saveLastBook(id);
    }

    public static SaveLastBook create(BooksRepository booksRepository){
        return new SaveLastBook(booksRepository);
    }
}
