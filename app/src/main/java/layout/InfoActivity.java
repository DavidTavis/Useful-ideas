package layout;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.david.mywidgetnewattempt.R;
import com.melnykov.fab.FloatingActionButton;

import layout.data.MyDBHelper;

/**
 * Created by TechnoA on 01.03.2017.
 */

public class InfoActivity extends Activity {

//    Context context;
    FloatingActionButton btnOk;
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    public static final String LOG_TAG = "MyLogWidget";
    public static MyDBHelper myDBHelper;

    public InfoActivity() {
        super();
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            final Context context = getApplicationContext();

            Log.d(LOG_TAG,String.valueOf(mAppWidgetId));


            //заполняем таблицу цитатами
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    myDBHelper = new MyDBHelper(context);
                    String[] quotes = context.getResources().getStringArray(R.array.array_quotes);
                    for(String myQuote: quotes){
                        myDBHelper.writeQuoteToDBSQLite(myQuote);
                    }

                    myDBHelper.nextQuote();
                    // It is the responsibility of the configuration activity to update the app widget
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    NewAppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);
                }
            });
            thread.start();

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setResult(RESULT_CANCELED);
        setContentView(R.layout.info);

        Log.d(LOG_TAG,"InfoActivity onCreate");
        int[] ids = AppWidgetManager.getInstance(getApplicationContext()).getAppWidgetIds(
                new ComponentName(getApplicationContext(), NewAppWidget.class));

        if(ids.length > 1){
            Toast.makeText(getApplicationContext(), "Widget already exist", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnOk = (FloatingActionButton) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
    }
}
