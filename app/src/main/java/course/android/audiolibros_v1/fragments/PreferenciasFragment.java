package course.android.audiolibros_v1.fragments;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import course.android.audiolibros_v1.R;

/**
 * Created by Casa on 31/12/2016.
 */

public class PreferenciasFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
