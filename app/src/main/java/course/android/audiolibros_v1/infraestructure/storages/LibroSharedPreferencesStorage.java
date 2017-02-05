package course.android.audiolibros_v1.infraestructure.storages;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

import course.android.audiolibros_v1.Libro;
import course.android.audiolibros_v1.adaptadores.LibrosSingleton;

/**
 * Created by Casa on 28/01/2017.
 */

public class LibroSharedPreferencesStorage implements LibroStorage{

    private static final String PREF_AUDIOLIBROS =
            "course.android.audiolibros_internal";
    private static final String KEY_ULTIMO_LIBRO = "ultimo";
    private final Context context;

    private static LibroSharedPreferencesStorage instance;

    private LibroSharedPreferencesStorage(Context context) {
        this.context = context;
    }

    @Override
    public List<Libro> getAll() {
        return Libro.ejemploLibros();
    }

    public boolean hasLastBook() {
        return getPreference().contains(KEY_ULTIMO_LIBRO);
    }
    private SharedPreferences getPreference() {
        return context.getSharedPreferences(
                PREF_AUDIOLIBROS, Context.MODE_PRIVATE);
    }
    public int getLastBook() {
        return getPreference().getInt(KEY_ULTIMO_LIBRO, -1);
    }

    public void saveLastBook(int bookId){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_AUDIOLIBROS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ULTIMO_LIBRO, bookId);
        editor.commit();
    }

    @Override
    public Libro getById(int id) {
        return LibrosSingleton.getInstance().getBooks().get(id);
    }

    public static LibroStorage getInstance(Context context){
        if(instance == null){
            instance = new LibroSharedPreferencesStorage(context);
        }
        return instance;
    }
}
