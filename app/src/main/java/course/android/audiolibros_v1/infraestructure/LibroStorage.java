package course.android.audiolibros_v1.infraestructure;

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
    void setLastBook(int bookId);
}
