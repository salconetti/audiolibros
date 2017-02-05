package course.android.audiolibros_v1.infraestructure.storages;

import java.util.List;
import java.util.Vector;

import course.android.audiolibros_v1.Libro;

/**
 * Created by Casa on 31/01/2017.
 */

public interface LibroStorage  {
    List<Libro> getAll();
    boolean hasLastBook();
    int getLastBook();
    void saveLastBook(int bookId);
    Libro getById(int id);

}
