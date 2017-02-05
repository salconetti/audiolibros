package course.android.audiolibros_v1.infraestructure.storages;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

/**
 * Created by Casa on 05/02/2017.
 */

public class UserConfigSharedPreferencesStorage implements UserConfigStorage{
    private static final String AUTOPLAY_PREF = "pref_autoreproducir";
    private Context context;
    private static UserConfigSharedPreferencesStorage instance;

    private UserConfigSharedPreferencesStorage(Context context) {
        this.context = context;
    }

    @Override
    public Boolean isAutoplaySelected() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean autoplay = sharedPref.getBoolean(AUTOPLAY_PREF, true);

        return autoplay;
    }

    public static UserConfigStorage getInstance(Context context){
        if(instance == null){
            instance = new UserConfigSharedPreferencesStorage(context);
        }
        return instance;
    }
}
