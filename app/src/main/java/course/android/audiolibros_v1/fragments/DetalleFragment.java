package course.android.audiolibros_v1.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.io.IOException;

import course.android.audiolibros_v1.Libro;
import course.android.audiolibros_v1.MainActivity;
import course.android.audiolibros_v1.R;
import course.android.audiolibros_v1.VolleySingleton;
import course.android.audiolibros_v1.components.OnSeekBarListener;
import course.android.audiolibros_v1.components.ZoomSeekBar;
import course.android.audiolibros_v1.infraestructure.storages.LibroSharedPreferencesStorage;
import course.android.audiolibros_v1.infraestructure.storages.LibroStorage;
import course.android.audiolibros_v1.infraestructure.storages.UserConfigSharedPreferencesStorage;
import course.android.audiolibros_v1.infraestructure.storages.UserConfigStorage;
import course.android.audiolibros_v1.infraestructure.repositories.BooksRepository;
import course.android.audiolibros_v1.infraestructure.repositories.UserConfigRepository;
import course.android.audiolibros_v1.presenters.DetallePresenter;
import course.android.audiolibros_v1.useCases.GetBookById;
import course.android.audiolibros_v1.useCases.IsAutoplaySelected;
import course.android.audiolibros_v1.widget.WidgetProvider;

/**
 * Created by Casa on 26/12/2016.
 */

public class DetalleFragment extends Fragment
        implements View.OnTouchListener,
        MediaPlayer.OnPreparedListener,
        MediaController.MediaPlayerControl,
        OnSeekBarListener,
        DetallePresenter.DetailView{

    MediaPlayer mediaPlayer;
    MediaController mediaController;
    Libro libro;
    private static final int ID_NOTIFICACION = 1;
    private ZoomSeekBar zoomSeekBar;
    private Handler seekHandler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            if(!isMediaPlayerReleased) {
                changeSeek();
            }
        }
    };

    private boolean isMediaPlayerReleased = false;

    private DetallePresenter detallePresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeSeek();

    }

    @Override public View onCreateView(LayoutInflater inflador, ViewGroup
            contenedor, Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.fragment_detalle,
                contenedor, false);
        Context context = getActivity().getApplicationContext();

        LibroStorage libroStorage = LibroSharedPreferencesStorage.getInstance(context);
        BooksRepository booksRepository = BooksRepository.create(libroStorage);
        GetBookById getBookById = GetBookById.create(booksRepository);

        UserConfigStorage userConfigStorage = UserConfigSharedPreferencesStorage.getInstance(context);
        UserConfigRepository userConfigRepository = UserConfigRepository.create(userConfigStorage);
        IsAutoplaySelected isAutoplaySelected = IsAutoplaySelected.create(userConfigRepository);

        detallePresenter = DetallePresenter.create(getBookById, isAutoplaySelected,this);

        Bundle args = getArguments();
        detallePresenter.setBook(args, vista);
        return vista;
    }

    @Override public void onResume(){
        DetalleFragment detalleFragment = (DetalleFragment)
                getFragmentManager().findFragmentById(R.id.detalle_fragment);

        if (detalleFragment == null ) {
            ((MainActivity) getActivity()).mostrarElementos(false);
        }

        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        try {
            detallePresenter.exit();
        } catch (Exception e) {
            Log.d("Audiolibros", "Error en mediaPlayer.stop()");
        }
        super.onDestroy();
    }

    private void ponInfoLibro(Libro libro, View vista) {

        ((TextView) vista.findViewById(R.id.titulo)).setText(libro.titulo);
        ((TextView) vista.findViewById(R.id.autor)).setText(libro.autor);
        ((NetworkImageView) vista.findViewById(R.id.portada)).setImageUrl(
                libro.urlImagen, VolleySingleton.getInstance(getActivity().getApplicationContext()).getLectorImagenes());
        vista.setOnTouchListener(this);

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaController = new MediaController(getActivity());
        Uri audio = Uri.parse(libro.urlAudio);
        try {
            mediaPlayer.setDataSource(getActivity(), audio);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e("Audiolibros", "ERROR: No se puede reproducir " + audio, e);
        }
    }
    public void ponInfoLibro(int id) {
        detallePresenter.setBook(id, getView());
    }

    @Override public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("Audiolibros", "Entramos en onPrepared de MediaPlayer");

        setZoomSeekBar(mediaPlayer);

        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(getView().findViewById(
                R.id.fragment_detalle));
        mediaController.setEnabled(true);
        mediaController.setPadding(0,0,0,110);
        mediaController.show();

        detallePresenter.autoPlay();
    }
    @Override public boolean onTouch(View vista, MotionEvent evento) {
        mediaController.show();
        return false;
    }
    @Override public void onStop() {
        mediaController.hide();
//        mediaPlayer.pause();
        super.onStop();
    }
    @Override public boolean canPause() {
        return true;
    }
    @Override public boolean canSeekBackward() {
        return true;
    }
    @Override public boolean canSeekForward() {
        return true;
    }
    @Override public int getBufferPercentage() {
        return 0;
    }
    @Override public int getCurrentPosition() {
        try {
            return mediaPlayer.getCurrentPosition();
        } catch (Exception e) {
            return 0;
        }
    }
    @Override public int getDuration() {
        return mediaPlayer.getDuration();
    }
    @Override public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }
    @Override public void pause() {
        mediaPlayer.pause();
        updateWidget(libro);
        sendNotification(libro);
    }
    @Override public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }
    @Override public void start() {
        detallePresenter.play(libro);
    }
    @Override public int getAudioSessionId() {
        return 0;
    }


    @Override
    public void play() {
        mediaPlayer.start();
    }

    @Override
    public void release() {
        mediaPlayer.pause();
        mediaPlayer.release();
        isMediaPlayerReleased = true;
    }

    @Override
    public void updateWidget(Libro libro){

        Activity context = getActivity();

        if(context == null) {
            return;
        }

        ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget);

        remoteViews.setTextViewText(R.id.titulo_widget, libro.titulo);
        remoteViews.setTextViewText(R.id.autor_widget, libro.autor);
        if(!isPlaying()){
            remoteViews.setImageViewResource(R.id.play_widget, R.drawable.play);
        }
        else {
            remoteViews.setImageViewResource(R.id.play_widget, R.drawable.pause);
        }

        ImageLoader imageLoader = VolleySingleton.getInstance(getActivity().getApplicationContext()).getLectorImagenes();
        final Bitmap[] portada = new Bitmap[1];
        imageLoader.get(libro.urlImagen, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                portada[0] = response.getBitmap();
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        remoteViews.setImageViewBitmap(R.id.portada_widget, portada[0]);

        AppWidgetManager.getInstance(context).updateAppWidget(thisWidget,
                remoteViews);
    }

    @Override
    public void showNotification(Libro libro) {
        sendNotification(libro);
    }

    @Override
    public void setBook(Libro libro, View view) {
        this.libro = libro;
        ponInfoLibro(libro, view);
    }

    @Override
    public void clearWidget(){
        Activity context = getActivity();

        if(context == null) {
            return;
        }

        ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget);

        remoteViews.setTextViewText(R.id.titulo_widget, "Sin libro");
        remoteViews.setTextViewText(R.id.autor_widget, "");

        remoteViews.setImageViewResource(R.id.play_widget, android.R.color.transparent);

        remoteViews.setImageViewResource(R.id.portada_widget, android.R.color.transparent);

        AppWidgetManager.getInstance(context).updateAppWidget(thisWidget,
                remoteViews);
    }

    private void sendNotification(Libro libro){
        Activity context = getActivity();

        if(context == null){
            return;
        }

        final Bitmap[] portada = new Bitmap[1];
        ImageLoader imageLoader = VolleySingleton.getInstance(getActivity().getApplicationContext()).getLectorImagenes();
        imageLoader.get(libro.urlImagen, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                portada[0] = response.getBitmap();
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification);
        if(!isPlaying()){
            remoteViews.setImageViewResource(R.id.reproducir_notification, R.drawable.play);
        }
        else{
            remoteViews.setImageViewResource(R.id.reproducir_notification, R.drawable.pause);
        }

        remoteViews.setImageViewBitmap(R.id.portada_notification, portada[0]);
        remoteViews.setTextViewText(R.id.titulo_notification, libro.titulo);
        remoteViews.setTextColor(R.id.titulo_notification, Color.BLACK);
        remoteViews.setTextViewText(R.id.autor_notification, libro.autor);
        remoteViews.setTextColor(R.id.autor_notification, Color.BLACK);

        NotificationCompat.Builder notificacion;
        notificacion = new NotificationCompat.Builder(context);
        notificacion.setContent(remoteViews);
        notificacion.setPriority(10);
        notificacion.setSmallIcon(R.mipmap.ic_launcher);
        notificacion.setContentTitle("Escuchando");

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        notificacion.setContentIntent(pendingIntent);

        Intent intentClick = new Intent();
        intentClick.setAction(WidgetProvider.ACCION_REPRODUCTOR);

        PendingIntent pendingIntentClick = PendingIntent.getBroadcast(context, 0, intentClick,
                PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.reproducir_notification, pendingIntentClick);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_NOTIFICACION, notificacion.build());
    }

    @Override
    public void clearNotification(){
        NotificationManager mNotificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(ID_NOTIFICACION);
    }

    private void setZoomSeekBar(final MediaPlayer player){

        zoomSeekBar = (ZoomSeekBar) getActivity().findViewById(R.id.zoomSeekBar);

        zoomSeekBar.setValMin(0);
        zoomSeekBar.setValMax(player.getDuration() / 1000);
        zoomSeekBar.setVal(0);

        zoomSeekBar.setEscalaRayaLarga(20);
        zoomSeekBar.setEscalaRaya(10);

        zoomSeekBar.setOnSeekBarListener(this);

        zoomSeekBar.setVisibility(View.VISIBLE);
    }

    private void changeSeek(){
        if(zoomSeekBar != null && mediaPlayer.isPlaying()) {
            zoomSeekBar.setVal(mediaPlayer.getCurrentPosition() / 1000);
        }
        seekHandler.postDelayed(runnable, 1);
    }

    @Override
    public void onValChanged(int value) {
        int milisecondsValue = value * 1000;
        seekTo(milisecondsValue);
    }
}
