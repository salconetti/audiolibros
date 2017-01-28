package course.android.audiolibros_v1;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.Vector;

/**
 * Created by Casa on 26/12/2016.
 */

public class AdaptadorLibros extends RecyclerView.Adapter<AdaptadorLibros.ViewHolder> {

    private LayoutInflater inflador; //Crea Layouts a partir del XML
    protected Vector<Libro> vectorLibros; //Vector con libros a visualizar
    private Context contexto;
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;

    public AdaptadorLibros(Context contexto, Vector<Libro> vectorLibros) {
        inflador = (LayoutInflater) contexto
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.vectorLibros = vectorLibros;
        this.contexto = contexto;
    }

    public void setOnClickListener(View.OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }
    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView portada;
        public TextView titulo;
        public ViewHolder(View itemView) {
            super(itemView);
            portada = (ImageView) itemView.findViewById(R.id.portada);
            portada.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            titulo = (TextView) itemView.findViewById(R.id.titulo);
        }
    }

    // Creamos el ViewHolder con las vista de un elemento sin personalizar
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
// Inflamos la vista desde el xml
        View view = inflador.inflate(R.layout.elemento_selector, null);
        view.setOnClickListener(onClickListener);
        view.setOnLongClickListener(onLongClickListener);
        return new ViewHolder(view);
    }
    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(final ViewHolder holder, int posicion) {
        final Libro libro = vectorLibros.elementAt(posicion);
        holder.titulo.setText(libro.titulo);
        holder.itemView.setScaleX(1);
        holder.itemView.setScaleY(1);
        Aplicacion aplicacion = (Aplicacion) contexto.getApplicationContext();
        aplicacion.getLectorImagenes().get(libro.urlImagen,
                new ImageLoader.ImageListener() {
                    @Override public void onResponse(ImageLoader.ImageContainer
                                                             response, boolean isImmediate) {
                        final Bitmap bitmap = response.getBitmap();
                        if (bitmap != null) {
                            holder.portada.setImageBitmap(bitmap);
                            if (!libro.isColorSet()) {
                                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                    public void onGenerated(Palette palette) {
                                        libro.colorApagado = palette.getLightMutedColor(0);
                                        libro.colorVibrante = palette.getLightVibrantColor(0);
                                        holder.itemView.setBackgroundColor(libro.colorApagado);
                                        holder.titulo.setBackgroundColor(libro.colorVibrante);
                                    }
                                });
                            }
                            else {
                                holder.itemView.setBackgroundColor(libro.colorApagado);
                                holder.titulo.setBackgroundColor(libro.colorVibrante);
                            }
                            holder.portada.invalidate();
                        }
                    }
                    @Override public void onErrorResponse(VolleyError error) {
                        holder.portada.setImageResource(R.drawable.books);
                    }
                });
    }

    // Indicamos el número de elementos de la lista
    @Override public int getItemCount() {
        return vectorLibros.size();
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener){
        this.onLongClickListener = onLongClickListener;
    }

}
