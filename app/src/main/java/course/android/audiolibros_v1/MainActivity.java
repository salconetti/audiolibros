package course.android.audiolibros_v1;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import course.android.audiolibros_v1.fragments.DetalleFragment;
import course.android.audiolibros_v1.fragments.PreferenciasFragment;
import course.android.audiolibros_v1.fragments.SelectorFragment;

import static course.android.audiolibros_v1.widget.WidgetProvider.ACCION_REPRODUCTOR;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AdaptadorLibrosFiltro adaptador;
    private AppBarLayout appBarLayout;
    private TabLayout tabs;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private AudioLibrosReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adaptador = ((Aplicacion)getApplicationContext()).getAdaptador();

        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);

        //Fragment selector
        int idContenedor = (findViewById(R.id.contenedor_pequeno) != null) ?
                R.id.contenedor_pequeno : R.id.contenedor_izquierdo;
        SelectorFragment primerFragment = new SelectorFragment();
        getFragmentManager().beginTransaction().add(idContenedor, primerFragment)
                .commit();

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FloatingButton
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irUltimoVisitado();
                Snackbar.make(view, "Escuchando último libro", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Tabs
        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("Todos"));
        tabs.addTab(tabs.newTab().setText("Nuevos"));
        tabs.addTab(tabs.newTab().setText("Leidos"));
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: //Todos
                        adaptador.setNovedad(false);
                        adaptador.setLeido(false);
                        break;
                    case 1: //Nuevos
                        adaptador.setNovedad(true);
                        adaptador.setLeido(false);
                        break;
                    case 2: //Leidos
                        adaptador.setNovedad(false);
                        adaptador.setLeido(true);
                        break;
                }
                adaptador.notifyDataSetChanged();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Navigation Drawer
        drawer = (DrawerLayout) findViewById(
                R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.drawer_open, R.string. drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(
                R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_preferencias) {
            mostrarPreferencias();
            return true;
        } else if (id == R.id.menu_acerca) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Mensaje de Acerca De");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.create().show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void irUltimoVisitado() {
        SharedPreferences pref = getSharedPreferences(
                "course.android.audiolibros_v1_internal", MODE_PRIVATE);
        int id = pref.getInt("ultimo", -1);
        if (id >= 0) {
            mostrarDetalle(id);
        } else {
            Toast.makeText(this,"Sin última vista",Toast.LENGTH_LONG).show();
        }
    }

    public void mostrarPreferencias(){
        DetalleFragment detalleFragment = (DetalleFragment)
                getFragmentManager().findFragmentById(R.id.detalle_fragment);
        //Primer caso, Tableta. Segundo móvil

        if(detalleFragment != null){
            int viewToChange = R.id.contenedor_izquierdo;

            PreferenciasFragment preferenciasFragment = new PreferenciasFragment();
            getFragmentManager().beginTransaction()
                    .replace(viewToChange, preferenciasFragment)
                    .addToBackStack(null)
                    .commit();
        }
        else{
            Intent i = new Intent(this, PreferenciasActivity.class);
            startActivity(i);
        }

    }

    public void mostrarDetalle(int id) {
        DetalleFragment detalleFragment = (DetalleFragment)
                getFragmentManager().findFragmentById(R.id.detalle_fragment);
        //Primer caso, Tableta. Segundo móvil
        if (detalleFragment != null) {
            detalleFragment.ponInfoLibro(id);
        } else {
            detalleFragment = new DetalleFragment();
            Bundle args = new Bundle();
            args.putInt(DetalleFragment.ARG_ID_LIBRO, id);
            detalleFragment.setArguments(args);
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.contenedor_pequeno, detalleFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            setDrawerState(false);
        }
        SharedPreferences pref = getSharedPreferences(
                "course.android.audiolibros_v1_internal", MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("ultimo", id);
        editor.commit();

        IntentFilter filtro = new IntentFilter(ACCION_REPRODUCTOR);

        if(receiver != null){
            unregisterReceiver(receiver);
        }

        receiver = new AudioLibrosReceiver(detalleFragment);
        registerReceiver(receiver, filtro);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_todos:
                adaptador.setGenero("");
                adaptador.notifyDataSetChanged();
                break;
            case R.id.nav_epico:
                adaptador.setGenero(Libro.G_EPICO);
                adaptador.notifyDataSetChanged();
                break;
            case R.id.nav_XIX:
                adaptador.setGenero(Libro.G_S_XIX);
                adaptador.notifyDataSetChanged();
                break;
            case R.id.nav_suspense:
                adaptador.setGenero(Libro.G_SUSPENSE);
                adaptador.notifyDataSetChanged();
                break;
            case R.id.nav_preferencias:
                mostrarPreferencias();
                break;
            case R.id.nav_compartir:
                Toast.makeText(this, "Sharing...", Toast.LENGTH_LONG).show();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(
                R.id.drawer_layout);
        drawer.closeDrawers();

        return true;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(
                R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        if(receiver != null){
            unregisterReceiver(receiver);
            receiver = null;
        }

        setDrawerState(true);
    }

    public void mostrarElementos(boolean mostrar) {
        appBarLayout.setExpanded(mostrar);
        toggle.setDrawerIndicatorEnabled(mostrar);
        if (mostrar) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            tabs.setVisibility(View.VISIBLE);
        } else {
            tabs.setVisibility(View.GONE);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    public void setDrawerState(boolean isEnabled) {
        if ( isEnabled ) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.syncState();

        }
        else {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.setDrawerIndicatorEnabled(false);
            toggle.syncState();
        }
    }
}
