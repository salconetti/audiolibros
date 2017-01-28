package course.android.audiolibros_v1;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.Vector;

/**
 * Created by Casa on 21/12/2016.
 */

public class Aplicacion extends Application {
    private Vector<Libro> vectorLibros;
    private AdaptadorLibrosFiltro adaptador;
    private static RequestQueue colaPeticiones;
    private static ImageLoader lectorImagenes;

    @Override
    public void onCreate() {
        vectorLibros = Libro.ejemploLibros();
        adaptador = new AdaptadorLibrosFiltro(this, vectorLibros);

        colaPeticiones = Volley.newRequestQueue(this);
        lectorImagenes = new ImageLoader(colaPeticiones,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache =
                            new LruCache<String, Bitmap>(10);
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }
                });
    }

    public AdaptadorLibrosFiltro getAdaptador() {
        return adaptador;
    }
    public Vector<Libro> getVectorLibros() {
        return vectorLibros;
    }

    public static RequestQueue getColaPeticiones() {
        return colaPeticiones;
    }

    public static ImageLoader getLectorImagenes() {
        return lectorImagenes;
    }
}
