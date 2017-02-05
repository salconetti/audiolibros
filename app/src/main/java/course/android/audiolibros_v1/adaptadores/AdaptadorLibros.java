package course.android.audiolibros_v1.adaptadores;

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

import java.util.List;
import java.util.Vector;

import course.android.audiolibros_v1.Libro;
import course.android.audiolibros_v1.R;
import course.android.audiolibros_v1.VolleySingleton;
import course.android.audiolibros_v1.commands.ClickAction;
import course.android.audiolibros_v1.commands.EmptyClickAction;
import course.android.audiolibros_v1.commands.EmptyLongClickAction;
import course.android.audiolibros_v1.commands.LongClickAction;

/**
 * Created by Casa on 26/12/2016.
 */

public class AdaptadorLibros extends RecyclerView.Adapter<AdaptadorLibros.ViewHolder> {

    private LayoutInflater inflador; //Crea Layouts a partir del XML
    private List<Libro> vectorLibros = new Vector<Libro>(); //Vector con libros a visualizar
    private Context contexto;

    private ClickAction clickAction = new EmptyClickAction();
    private LongClickAction longClickAction = new EmptyLongClickAction();

    protected AdaptadorLibros(Context context) {
        this.contexto = context;
        inflador = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.vectorLibros = LibrosSingleton.getInstance().getBooks();
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
        return new ViewHolder(view);
    }
    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int posicion) {
        final Libro libro = vectorLibros.get(posicion);
        holder.titulo.setText(libro.titulo);
        holder.itemView.setScaleX(1);
        holder.itemView.setScaleY(1);

        VolleySingleton.getInstance(contexto).getLectorImagenes().get(libro.urlImagen,
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAction.execute(posicion);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View view) {

                longClickAction.execute(view, posicion);
                return true;
            }
        });
    }

    // Indicamos el número de elementos de la lista
    @Override public int getItemCount() {
        return vectorLibros.size();
    }

    public void setClickAction(ClickAction clickAction){
        this.clickAction = clickAction;
    }

    public void setLongClickAction(LongClickAction longClickAction) {
        this.longClickAction = longClickAction;
    }

    protected List<Libro> getLibros(){
        return this.vectorLibros;
    }
}
