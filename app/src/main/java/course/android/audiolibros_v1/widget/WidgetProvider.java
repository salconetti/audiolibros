package course.android.audiolibros_v1.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import course.android.audiolibros_v1.AudioLibrosReceiver;
import course.android.audiolibros_v1.MainActivity;
import course.android.audiolibros_v1.R;
import course.android.audiolibros_v1.fragments.DetalleFragment;

/**
 * Created by Casa on 07/01/2017.
 */

public class WidgetProvider extends AppWidgetProvider {
    public static final String ACCION_REPRODUCTOR =
            "course.android.audiolibros_v1.ACCION_REPRODUCTOR";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int widgetId : appWidgetIds) {
            updateWidget(context, widgetId);
        }
    }

    public static void updateWidget(Context context, int widgetId){
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget);

        setImageClickEvent(context, remoteViews);
        setActionClickEvent(context, remoteViews, widgetId);
        setUserTitle(context, remoteViews);

        AppWidgetManager.getInstance(context).updateAppWidget(widgetId,
                remoteViews);

    }

    private static void setImageClickEvent(Context context, RemoteViews remoteViews){
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.portada_widget, pendingIntent);
    }

    private static void setActionClickEvent(Context context, RemoteViews remoteViews, int widgetId){

        Intent intentClick = new Intent();
        intentClick.setAction(ACCION_REPRODUCTOR);

        PendingIntent pendingIntentClick = PendingIntent.getBroadcast(context, 0, intentClick,
                PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.play_widget, pendingIntentClick);
    }

    private static String getUserTitle(Context context){
        SharedPreferences prefs = context.getSharedPreferences("widget",
                Context.MODE_PRIVATE);
        String userTitle = prefs.getString("title","Sin título usuario");

        return userTitle;
    }

    private static void setUserTitle(Context context, RemoteViews remoteViews){
        String userTitle = getUserTitle(context);
        remoteViews.setTextViewText(R.id.user_title, userTitle);
    }
}
