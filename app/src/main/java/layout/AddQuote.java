package layout;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.melnykov.fab.*;
import com.example.david.mywidgetnewattempt.R;

import layout.data.MyDBHelper;

/**
 * Created by TechnoA on 22.02.2017.
 */

public class AddQuote extends Activity {

    public static final String LOG_TAG = "MyLogWidget";

    EditText etQuote;
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public void onClick(View v){
        final Context context = getApplicationContext();
        String widgetText = etQuote.getText().toString();

        //добавляем цитату в таблицу
        if(!widgetText.equals("")) {
            MyDBHelper myDBHelper = new MyDBHelper(context);
            myDBHelper.writeQuoteToDBSQLite(widgetText);
        }else{
            Toast.makeText(AddQuote.this, "You have not typed a quote", Toast.LENGTH_SHORT).show();
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        NewAppWidget.updateAppWidget(context, appWidgetManager, null, mAppWidgetId);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        Log.d(LOG_TAG,"AddQuote mAppWidgetId = " + mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_quote);

        etQuote = (EditText) findViewById(R.id.quote);

        // извлекаем ID конфигурируемого виджета
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        // и проверяем его корректность
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
    }
}
