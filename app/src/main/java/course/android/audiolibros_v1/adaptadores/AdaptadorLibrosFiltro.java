package course.android.audiolibros_v1.adaptadores;

import android.content.Context;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import course.android.audiolibros_v1.Libro;

/**
 * Created by Casa on 29/12/2016.
 */

public class AdaptadorLibrosFiltro extends AdaptadorLibros implements Observer {
    private List<Libro> vectorSinFiltro = new Vector<Libro>();// Vector con todos los libros
    private List<Integer> indiceFiltro = new Vector<Integer>(); // Índice en vectorSinFiltro de
    // Cada elemento de vectorLibros
    private String busqueda = ""; // Búsqueda sobre autor o título
    private String genero = ""; // Género seleccionado
    private boolean novedad = false; // Si queremos ver solo novedades
    private boolean leido = false; // Si queremos ver solo leidos
    AdaptadorLibrosFiltro(Context context) {
        super(context);
        vectorSinFiltro.addAll(getLibros());
        recalculaFiltro();
    }
    public void setBusqueda(String busqueda) {
        this.busqueda = busqueda.toLowerCase();
        recalculaFiltro();
    }
    public void setGenero(String genero) {
        this.genero = genero;
        recalculaFiltro();
    }
    public void setNovedad(boolean novedad) {
        this.novedad = novedad;
        recalculaFiltro();
    }
    public void setLeido(boolean leido) {
        this.leido = leido;
        recalculaFiltro();
    }
    public void recalculaFiltro() {
        getLibros().clear();
        indiceFiltro.clear();
        for (int i = 0; i < vectorSinFiltro.size(); i++) {
            Libro libro = vectorSinFiltro.get(i);
            if ((libro.titulo.toLowerCase().contains(busqueda) ||
                    libro.autor.toLowerCase().contains(busqueda))
                    && (libro.genero.startsWith(genero))
                    && (!novedad || (novedad && libro.novedad))
                    && (!leido || (leido && libro.leido))) {
                getLibros().add(libro);
                indiceFiltro.add(i);
            }
        }
    }
    public Libro getItem(int posicion) {

        Libro book= vectorSinFiltro.get(indiceFiltro.get(posicion));

        if(book == Libro.LIBRO_EMPTY)
        {
            return Libro.LIBRO_EMPTY;
        }

        return book;
    }
    public long getItemId(int posicion) {
        return indiceFiltro.get(posicion);
    }
    public void borrar(int posicion){
        vectorSinFiltro.remove((int)getItemId(posicion));
        recalculaFiltro();
    }
    public void insertar(Libro libro){
        vectorSinFiltro.add(0,libro);
        recalculaFiltro();
    }

    @Override
    public void update(Observable o, Object arg) {
        setBusqueda((String) arg);
        notifyDataSetChanged();
    }
}
