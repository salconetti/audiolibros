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

    public final static Libro LIBRO_EMPTY = new Libro("", "anónimo",
            "http://www.dcomg.upv.es/~jtomas/android/audiolibros/sin_portada.jpg", "",
            G_TODOS, true, false);

    private Libro(String titulo, String autor, String urlImagen, String urlAudio,
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
        libros.add(new LibroBuilder()
                .withTitulo("Kappa")
                .withAutor("Akutagawa")
                .withUrlImagen(SERVIDOR + "kappa.jpg")
                .withUrlAudio(SERVIDOR + "kappa.mp3")
                .withGenero(Libro.G_S_XIX)
                .withNuevo(false)
                .withLeido(false)
                .build());

        libros.add(new LibroBuilder()
                .withTitulo("Avecilla")
                .withAutor("Alas Clarín, Leopoldo")
                .withUrlImagen(SERVIDOR + "avecilla.jpg")
                .withUrlAudio(SERVIDOR + "avecilla.mp3")
                .withGenero(Libro.G_S_XIX)
                .withNuevo(true)
                .withLeido(false)
                .build());

        libros.add(new LibroBuilder()
                .withTitulo("Divina Comedia")
                .withAutor("Dante")
                .withUrlImagen(SERVIDOR + "divina_comedia.jpg")
                .withUrlAudio(SERVIDOR + "divina_comedia.mp3")
                .withGenero(Libro.G_EPICO)
                .withNuevo(true)
                .withLeido(false)
                .build());

        libros.add(new LibroBuilder()
                .withTitulo("Viejo Pancho, El")
                .withAutor("Alonso y Trelles, José")
                .withUrlImagen(SERVIDOR + "viejo_pancho.jpg")
                .withUrlAudio(SERVIDOR + "viejo_pancho.mp3")
                .withGenero(Libro.G_S_XIX)
                .withNuevo(true)
                .withLeido(false)
                .build());

        libros.add(new LibroBuilder()
                .withTitulo("Canción de Rolando")
                .withAutor("Anónimo")
                .withUrlImagen(SERVIDOR + "cancion_rolando.jpg")
                .withUrlAudio(SERVIDOR + "cancion_rolando.mp3")
                .withGenero(Libro.G_EPICO)
                .withNuevo(false)
                .withLeido(true)
                .build());

        libros.add(new LibroBuilder()
                .withTitulo("Matrimonio de sabuesos")
                .withAutor("Agata Christie")
                .withUrlImagen(SERVIDOR + "matrim_sabuesos.jpg")
                .withUrlAudio(SERVIDOR + "matrim_sabuesos.mp3")
                .withGenero(Libro.G_SUSPENSE)
                .withNuevo(false)
                .withLeido(true)
                .build());

        libros.add(new LibroBuilder()
                .withTitulo("La iliada")
                .withAutor("Homero")
                .withUrlImagen(SERVIDOR + "la_iliada.jpg")
                .withUrlAudio(SERVIDOR + "la_iliada.mp3")
                .withGenero(Libro.G_EPICO)
                .withNuevo(true)
                .withLeido(false)
                .build());

        return libros;
    }

    public boolean isColorSet(){
        return colorVibrante != -1 && colorApagado != -1;
    }

    public static class LibroBuilder{
        private String titulo = "";
        private String autor = "anónimo";
        private String urlImagen =
                "http://www.dcomg.upv.es/~jtomas/android/audiolibros/sin_portada.jpg";
        private String urlAudio = "";
        private String genero = G_TODOS;
        private boolean nuevo = true;
        private boolean leido = false;

        public LibroBuilder withTitulo(String titulo) {
            this.titulo = titulo;
            return this;
        }
        public LibroBuilder withAutor(String autor) {
            this.autor = autor;
            return this;
        }
        public LibroBuilder withUrlImagen(String urlImagen) {
            this.urlImagen = urlImagen;
            return this;
        }

        public LibroBuilder withUrlAudio(String urlAudio) {
            this.urlAudio = urlAudio;
            return this;
        }
        public LibroBuilder withGenero(String genero) {
            this.genero = genero;
            return this;
        }
        public LibroBuilder withNuevo(Boolean nuevo) {
            this.nuevo = nuevo;
            return this;
        }
        public LibroBuilder withLeido(Boolean leido) {
            this.leido = leido;
            return this;
        }

        public Libro build() {
            return new Libro(titulo, autor, urlImagen, urlAudio, genero,
                    nuevo, leido);
        }
    }
}
