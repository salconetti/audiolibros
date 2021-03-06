package course.android.audiolibros_v1.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;
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

import course.android.audiolibros_v1.Aplicacion;
import course.android.audiolibros_v1.Libro;
import course.android.audiolibros_v1.MainActivity;
import course.android.audiolibros_v1.R;
import course.android.audiolibros_v1.components.OnSeekBarListener;
import course.android.audiolibros_v1.components.ZoomSeekBar;
import course.android.audiolibros_v1.widget.WidgetProvider;

/**
 * Created by Casa on 26/12/2016.
 */

public class DetalleFragment extends Fragment
        implements View.OnTouchListener,
        MediaPlayer.OnPreparedListener,
        MediaController.MediaPlayerControl,
        OnSeekBarListener {
    public static String ARG_ID_LIBRO = "id_libro";
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeSeek();
    }

    @Override public View onCreateView(LayoutInflater inflador, ViewGroup
            contenedor, Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.fragment_detalle,
                contenedor, false);
        Bundle args = getArguments();
        if (args != null) {
            int position = args.getInt(ARG_ID_LIBRO);
            ponInfoLibro(position, vista);
        } else {
            ponInfoLibro(0, vista);
        }
        this.zoomSeekBar = (ZoomSeekBar) contenedor.findViewById(R.id.zoomSeekBar);
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
            mediaPlayer.pause();
            mediaPlayer.release();
            isMediaPlayerReleased = true;

            clearNotification();
            clearWidget();
        } catch (Exception e) {
            Log.d("Audiolibros", "Error en mediaPlayer.stop()");
        }
        super.onDestroy();
    }

    private void ponInfoLibro(int id, View vista) {

        this.libro = ((Aplicacion) getActivity().getApplication())
                .getVectorLibros().elementAt(id);

        ((TextView) vista.findViewById(R.id.titulo)).setText(libro.titulo);
        ((TextView) vista.findViewById(R.id.autor)).setText(libro.autor);
        Aplicacion aplicacion = (Aplicacion) getActivity().getApplication();
        ((NetworkImageView) vista.findViewById(R.id.portada)).setImageUrl(
                libro.urlImagen, aplicacion.getLectorImagenes());
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
        ponInfoLibro(id, getView());
    }

    @Override public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("Audiolibros", "Entramos en onPrepared de MediaPlayer");

        setZoomSeekBar(mediaPlayer);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean autoPlay = sharedPref.getBoolean("pref_autoreproducir", true);

        if (autoPlay) {
            mediaPlayer.start();
        }

        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(getView().findViewById(
                R.id.fragment_detalle));
        mediaController.setEnabled(true);
        mediaController.setPadding(0,0,0,110);
        mediaController.show();

        updateWidget(libro);
        sendNotification(libro);


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
        mediaPlayer.start();
        updateWidget(libro);
        sendNotification(libro);
    }
    @Override public int getAudioSessionId() {
        return 0;
    }


    private void updateWidget(Libro libro){

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

        Aplicacion aplicacion = (Aplicacion) getActivity().getApplication();
        ImageLoader imageLoader = aplicacion.getLectorImagenes();
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

    private void clearWidget(){
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

        Aplicacion aplicacion = (Aplicacion) context.getApplication();

        final Bitmap[] portada = new Bitmap[1];
        ImageLoader imageLoader = aplicacion.getLectorImagenes();
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

    private void clearNotification(){
        Activity context = getActivity();
        Aplicacion aplicacion = (Aplicacion) context.getApplication();
        NotificationManager mNotificationManager = (NotificationManager) aplicacion.getSystemService(Context.NOTIFICATION_SERVICE);
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
