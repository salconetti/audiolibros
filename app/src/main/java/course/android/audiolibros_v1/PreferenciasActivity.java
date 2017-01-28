package course.android.audiolibros_v1;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import course.android.audiolibros_v1.fragments.PreferenciasFragment;

/**
 * Created by Casa on 31/12/2016.
 */

public class PreferenciasActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferenciasFragment())
                .commit();
    }
}
