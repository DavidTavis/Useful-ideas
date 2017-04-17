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

import com.example.david.mywidgetnewattempt.R;


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

        final GlobalClass globalVariable = (GlobalClass) context.getApplicationContext();

        QuotesRepository quotesRepository = globalVariable.getQuotesRepository();
        if(quotesRepository == null){
            globalVariable.setQuotesRepository(new QuotesRepository(context));
            quotesRepository = globalVariable.getQuotesRepository();
        }

        //добавляем цитату в таблицу
        if(!widgetText.equals("")) {
            QuotesRepository.MyDBHelper myDBHelper = quotesRepository.getMyDBHelper();
            myDBHelper.writeQuoteToDBSQLite(widgetText);
        }else{
            Toast.makeText(AddQuote.this, "You have not typed a quote", Toast.LENGTH_SHORT).show();
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        NewAppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

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
