package course.android.audiolibros_v1.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


import java.util.Vector;

import course.android.audiolibros_v1.AdaptadorLibrosFiltro;
import course.android.audiolibros_v1.Aplicacion;
import course.android.audiolibros_v1.Libro;
import course.android.audiolibros_v1.MainActivity;
import course.android.audiolibros_v1.R;

/**
 * Created by Casa on 26/12/2016.
 */

public class SelectorFragment extends Fragment implements Animator.AnimatorListener {
    private Activity actividad;
    private RecyclerView recyclerView;
    private AdaptadorLibrosFiltro adaptador;
    private Vector<Libro> vectorLibros;

    @Override public void onAttach(Context contexto) {
        super.onAttach(contexto);
        if (contexto instanceof Activity) {
            hacerOnAttach((Activity) contexto);
        }
    }

    @Override public void onAttach(Activity actividad) {
        super.onAttach(actividad);
        hacerOnAttach(actividad);
    }

    private void hacerOnAttach(Activity actividad) {
        this.actividad = actividad;
        Aplicacion app = (Aplicacion) actividad.getApplication();
        adaptador = app.getAdaptador();
        vectorLibros = app.getVectorLibros();
    }

    @Override public View onCreateView(LayoutInflater inflador, ViewGroup
            contenedor, Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.fragment_selector,
                contenedor, false);
        recyclerView = (RecyclerView) vista.findViewById(
                R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(actividad,2));
        recyclerView.setAdapter(adaptador);

        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(2000);
        animator.setMoveDuration(2000);
        recyclerView.setItemAnimator(animator);

        setHasOptionsMenu(true);

        adaptador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) actividad).mostrarDetalle(
                        (int) adaptador.getItemId(
                                recyclerView.getChildAdapterPosition(v)));
            }
        });

        adaptador.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(final View v) {
                final int id = recyclerView.getChildAdapterPosition(v);
                AlertDialog.Builder menu = new AlertDialog.Builder(actividad);
                CharSequence[] opciones = { "Compartir", "Borrar ", "Insertar" };
                menu.setItems(opciones, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int opcion) {
                        switch (opcion) {
                            case 0: //Compartir
                                Animator anim = AnimatorInflater.loadAnimator(actividad,
                                        R.animator.crecer);
                                anim.addListener(SelectorFragment.this);
                                anim.setTarget(v);
                                anim.start();

                                Libro libro = vectorLibros.elementAt(id);
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("text/plain");
                                i.putExtra(Intent.EXTRA_SUBJECT, libro.titulo);
                                i.putExtra(Intent.EXTRA_TEXT, libro.urlAudio);
                                startActivity(Intent.createChooser(i, "Compartir"));
                                break;
                            case 1: //Borrar
                                Snackbar.make(v,"¿Estás seguro?", Snackbar.LENGTH_LONG)
                                        .setAction("SI", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Animator anim = AnimatorInflater.loadAnimator(actividad,
                                                        R.animator.menguar_property);
                                                anim.addListener(SelectorFragment.this);
                                                anim.setTarget(v);
                                                anim.start();
                                                adaptador.borrar(id);
                                            }
                                        })
                                        .show();
                                break;
                            case 2: //Insertar
                                int posicion = recyclerView.getChildLayoutPosition(v);
                                adaptador.insertar(adaptador.getItem(posicion));
                                adaptador.notifyItemInserted(0);
                                Snackbar.make(v,"Libro insertado", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("OK", new View.OnClickListener() {
                                            @Override public void onClick(View view) { }
                                        })
                                        .show();
                                break;
                        }
                    }
                });
                menu.create().show();
                return true;
            }
        });
        return vista;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_selector, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_buscar);
        SearchView searchView= (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextChange(String query) {
                        adaptador.setBusqueda(query);
                        adaptador.notifyDataSetChanged();
                        return false;
                    }
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }
                });
        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        adaptador.setBusqueda("");
                        adaptador.notifyDataSetChanged();
                        return true; // Para permitir cierre
                    }
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true; // Para permitir expansión
                    }
                });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_ultimo) {
            ((MainActivity) actividad).irUltimoVisitado();
            return true;
        } else if (id == R.id.menu_buscar) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onResume(){
        ((MainActivity) getActivity()).mostrarElementos(true);
        super.onResume();
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        adaptador.notifyDataSetChanged();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

}
