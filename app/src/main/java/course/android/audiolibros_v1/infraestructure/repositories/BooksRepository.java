package course.android.audiolibros_v1.infraestructure.repositories;

import course.android.audiolibros_v1.Libro;
import course.android.audiolibros_v1.infraestructure.storages.LibroStorage;

/**
 * Created by Casa on 05/02/2017.
 */

public class BooksRepository {
    private final LibroStorage libroStorage;

    private BooksRepository(LibroStorage libroStorage) {
        this.libroStorage = libroStorage;
    }

    public int getLastBook() {
        return libroStorage.getLastBook();
    }

    public boolean hasLastBook() {
        return libroStorage.hasLastBook();
    }

    public void saveLastBook(int bookId) {
        libroStorage.saveLastBook(bookId);
    }

    public Libro getById(int bookId){
        return libroStorage.getById(bookId);
    }

    public static BooksRepository create(LibroStorage libroStorage){
        return new BooksRepository(libroStorage);
    }

}
