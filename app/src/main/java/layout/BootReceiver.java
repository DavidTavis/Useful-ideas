package layout;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import layout.data.MyDBHelper;

/**
 * Created by TechnoA on 06.03.2017.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();

        if (extras != null) {
//            MyDBHelper myDBHelper = new MyDBHelper(context);
//            String currentQuote = myDBHelper.getCurrentQuote();
//            Log.d(InfoActivity.LOG_TAG,"BootReceiver getCurrentQuote = " + currentQuote);

//            String firstQuote = myDBHelper.getFirstQuote();
//            Log.d(InfoActivity.LOG_TAG,"BootReceiver getFirstQuote = " + firstQuote);

            int mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
//            Log.d(InfoActivity.LOG_TAG,"BootReceiver appWidgetId = " + mAppWidgetId);
//            MyDBHelper.setCurrentQuote(currentQuote);

//            Toast.makeText(context, "currentQuote = " + currentQuote, Toast.LENGTH_SHORT).show();
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            NewAppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);
        }
    }
}
