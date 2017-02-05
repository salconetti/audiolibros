package course.android.audiolibros_v1.adaptadores;

import android.content.Context;

import java.util.List;

import course.android.audiolibros_v1.Libro;

/**
 * Created by Casa on 05/02/2017.
 */
public class LibrosSingleton {
    private static LibrosSingleton instance;
    private AdaptadorLibrosFiltro adaptadorLibrosFiltro;
    private static List<Libro> books;
    public static LibrosSingleton getInstance() {
        if(instance == null){
            instance = new LibrosSingleton();
        }
        return instance;
    }

    private LibrosSingleton() {
    }

    public List<Libro> getBooks(){
        if(books == null){
            books = Libro.ejemploLibros();
        }
        return books;
    }

    public AdaptadorLibrosFiltro getBookAdapter(Context context){
        if(adaptadorLibrosFiltro == null){
            adaptadorLibrosFiltro = new AdaptadorLibrosFiltro(context);
        }
        return adaptadorLibrosFiltro;
    }
}
