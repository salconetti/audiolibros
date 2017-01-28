package course.android.audiolibros_v1.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import course.android.audiolibros_v1.R;

/**
 * Created by Casa on 08/01/2017.
 */

public class ConfiguraWidget extends Activity {
    int widgetId;
    EditText editText;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configura_widget);
        editText = (EditText) findViewById(R.id.title_edit_text);
        setResult(RESULT_CANCELED);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
        }
        widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    public void buttonOK(View view) {
        SharedPreferences prefs = getSharedPreferences("widget",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("title", editText.getText().toString());
        editor.commit();
        WidgetProvider.updateWidget(this, widgetId);
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}
