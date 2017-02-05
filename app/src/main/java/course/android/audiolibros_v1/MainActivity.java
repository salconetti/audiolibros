package course.android.audiolibros_v1;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import course.android.audiolibros_v1.adaptadores.AdaptadorLibrosFiltro;
import course.android.audiolibros_v1.adaptadores.LibrosSingleton;
import course.android.audiolibros_v1.fragments.DetalleFragment;
import course.android.audiolibros_v1.fragments.PreferenciasFragment;
import course.android.audiolibros_v1.fragments.SelectorFragment;
import course.android.audiolibros_v1.infraestructure.storages.LibroSharedPreferencesStorage;
import course.android.audiolibros_v1.infraestructure.storages.LibroStorage;
import course.android.audiolibros_v1.infraestructure.repositories.BooksRepository;
import course.android.audiolibros_v1.presenters.DetallePresenter;
import course.android.audiolibros_v1.presenters.MainPresenter;
import course.android.audiolibros_v1.useCases.GetLastBook;
import course.android.audiolibros_v1.useCases.HasLastBook;
import course.android.audiolibros_v1.useCases.SaveLastBook;

import static course.android.audiolibros_v1.widget.WidgetProvider.ACCION_REPRODUCTOR;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MainPresenter.View {

    private AdaptadorLibrosFiltro adaptador;
    private AppBarLayout appBarLayout;
    private TabLayout tabs;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private AudioLibrosReceiver receiver;
    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        LibroStorage libroStorage = LibroSharedPreferencesStorage.getInstance(getApplicationContext());
        BooksRepository booksRepository = BooksRepository.create(libroStorage);
        SaveLastBook saveLastBook = SaveLastBook.create(booksRepository);
        HasLastBook hasLastBook = HasLastBook.create(booksRepository);
        GetLastBook getLastBook = GetLastBook.create(booksRepository);
        presenter = MainPresenter.create(saveLastBook, hasLastBook, getLastBook, this);

        adaptador = LibrosSingleton.getInstance().getBookAdapter(getApplicationContext());

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
                presenter.clickFavoriteButton();
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

    public void irUltimoVisitado() {
        presenter.clickFavoriteButton();
    }

    public void mostrarDetalle(int bookPosition) {
        presenter.openDetalle(bookPosition);
    }

    @Override
    public void mostrarNoUltimaVisita() {
        Toast.makeText(this, "Sin última vista",Toast.LENGTH_LONG).show();
    }

    @Override
    public void showDetailFragment(int bookPosition) {
        DetalleFragment detalleFragment = (DetalleFragment)
                getFragmentManager().findFragmentById(R.id.detalle_fragment);
        //Primer caso, Tableta. Segundo móvil
        if (detalleFragment != null) {
            detalleFragment.ponInfoLibro(bookPosition);
        } else {
            detalleFragment = new DetalleFragment();
            Bundle args = new Bundle();
            args.putInt(DetallePresenter.ARG_ID_LIBRO, bookPosition);
            detalleFragment.setArguments(args);
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.contenedor_pequeno, detalleFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            setDrawerState(false);
        }


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
