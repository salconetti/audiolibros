package course.android.audiolibros_v1;

import java.util.Vector;

/**
 * Created by Casa on 21/12/2016.
 */

public class Libro {
    public String titulo;
    public String autor;
    public String urlImagen;
    public String urlAudio;
    public String genero;   // Género literario
    public Boolean novedad; // Es una novedad
    public Boolean leido;   // Leído por el usuario
    public final static String G_TODOS = "Todos los géneros";
    public final static String G_EPICO = "Poema épico";
    public final static String G_S_XIX = "Literatura siglo XIX";
    public final static String G_SUSPENSE = "Suspense";
    public final static String[] G_ARRAY = new String[]{G_TODOS, G_EPICO, G_S_XIX, G_SUSPENSE};
    public int colorVibrante = -1;
    public int colorApagado = -1;

    public Libro(String titulo, String autor, String urlImagen, String urlAudio,
                 String genero, Boolean novedad, Boolean leido) {
        this.titulo = titulo;
        this.autor = autor;
        this.urlImagen = urlImagen;
        this.urlAudio = urlAudio;
        this.genero = genero;
        this.novedad = novedad;
        this.leido = leido;
    }

    public static Vector<Libro> ejemploLibros() {
        final String SERVIDOR = "http://www.dcomg.upv.es/~jtomas/android/audiolibros/";
        Vector<Libro> libros = new Vector<Libro>();
        libros.add(new Libro("Kappa", "Akutagawa", SERVIDOR + "kappa.jpg", SERVIDOR + "kappa.mp3", Libro.G_S_XIX, false, false));
        libros.add(new Libro("Avecilla", "Alas Clarín, Leopoldo", SERVIDOR + "avecilla.jpg", SERVIDOR + "avecilla.mp3", Libro.G_S_XIX, true, false));
        libros.add(new Libro("Divina Comedia", "Dante", SERVIDOR + "divina_comedia.jpg", SERVIDOR + "divina_comedia.mp3", Libro.G_EPICO, true, false));
        libros.add(new Libro("Viejo Pancho, El", "Alonso y Trelles, José", SERVIDOR + "viejo_pancho.jpg", SERVIDOR + "viejo_pancho.mp3", Libro.G_S_XIX, true, true));
        libros.add(new Libro("Canción de Rolando", "Anónimo", SERVIDOR + "cancion_rolando.jpg", SERVIDOR + "cancion_rolando.mp3", Libro.G_EPICO, false, true));
        libros.add(new Libro("Matrimonio de sabuesos", "Agata Christie", SERVIDOR + "matrim_sabuesos.jpg", SERVIDOR + "matrim_sabuesos.mp3", Libro.G_SUSPENSE, false, true));
        libros.add(new Libro("La iliada", "Homero", SERVIDOR + "la_iliada.jpg", SERVIDOR + "la_iliada.mp3", Libro.G_EPICO, true, false));
        return libros;
    }

    public boolean isColorSet(){
        return colorVibrante != -1 && colorApagado != -1;
    }
}
