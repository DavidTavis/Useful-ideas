package layout.views;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.david.mywidgetnewattempt.R;

import layout.MonitorQuotes;
import layout.NewAppWidget;
import layout.utils.TraceUtils;
import layout.utils.Utils;
import layout.settings.SettingsFragment;
import layout.models.QuoteModel;
import layout.repository.QuotesRepository;

/**
 * Created by TechnoA on 01.03.2017.
 */

public class InfoActivity extends Activity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    public static final String LOG_TAG = "MyLogWidget";
    public static final String TRANSPARENCY = "Transparency";

    public void onClick(View v){

        final Context context = getApplicationContext();

        //заполняем таблицу цитатами
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                QuotesRepository quotesRepositoryRefactored = Utils.getGlobal(context).getQuotesRepository();
                MonitorQuotes monitorQuotes = Utils.getGlobal(context).getMonitorQuotes();
                if (quotesRepositoryRefactored.count() == 0) {

                    String[] quotes = context.getResources().getStringArray(R.array.array_quotes);

                    QuoteModel quoteModel = null;

                    for (String myQuote : quotes) {
                        quoteModel = quotesRepositoryRefactored.addQuote(myQuote);
                    }

                    monitorQuotes.setLast(quoteModel);
                    monitorQuotes.setNext();

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
        TraceUtils.LogInfo("InfoActivity onCreate");
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
