package layout;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.david.mywidgetnewattempt.R;

import layout.models.QuoteModel;
import layout.PavelSh.QuotesRepositoryRefactored;

/**
 * Created by TechnoA on 01.03.2017.
 */

public class InfoActivity extends Activity{

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    public static final String LOG_TAG = "MyLogWidget";
    public static final String TRANSPARENCY = "Transparency";

    public void onClick(View v){

        final Context context = getApplicationContext();



        //заполняем таблицу цитатами
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                final GlobalClass globalVariable = (GlobalClass) context;
                QuotesRepositoryRefactored quotesRepositoryRefactored = globalVariable.getQuotesRepositoryRefactored();
                if (quotesRepositoryRefactored.getTableSize() == 0) {
                    String[] quotes = context.getResources().getStringArray(R.array.array_quotes);
                    for (String myQuote : quotes) {
                        QuoteModel quoteModel = quotesRepositoryRefactored.addQuote(myQuote);
                    }
                    quotesRepositoryRefactored.nextQuote();
                    //Обновляем виджет
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    NewAppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);
                }
            }
        });
        thread.start();

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG,"InfoActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        // Добавляем фрагмент с настройками Preferences.
        getFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();

        if (checkSingleWidget()){
            return;
        }

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

        // формируем intent ответа
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);

        // отрицательный ответ
        setResult(RESULT_CANCELED, resultValue);
    }

    public boolean checkSingleWidget() {
        int[] ids = AppWidgetManager.getInstance(getApplicationContext()).getAppWidgetIds(new ComponentName(getApplicationContext(), NewAppWidget.class));

        if (ids.length > 1) {
            Toast.makeText(getApplicationContext(), "Widget already exist" + ids.length, Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }else
            return false;
    }

}
