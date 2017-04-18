package layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.david.mywidgetnewattempt.R;

import java.io.IOException;

import layout.PavelSh.QuotesRepositoryRefactored;
import layout.data.MonitorQuotes;


public class NewAppWidget extends AppWidgetProvider {

    private static final String LOG_TAG = "MyLogWidget";

    public static final String UPDATE_ALL_WIDGETS = "update_all_widgets";
    private static final String NEXT_CLICKED = "com.example.david.mywidgetnewattempt.ButtonClickNext";
    private static final String PREV_CLICKED = "com.example.david.mywidgetnewattempt.ButtonClickPrev";
    private static final String DELETE_QUOTE = "com.example.david.mywidgetnewattempt.ButtonClickDelete";
    private static final String KEY_UPDATE = "UPDATE";
    private static final int VALUE_NEXT = 1;
    private static final int VALUE_PREV = 2;
    private static final int VALUE_DEL = 3;

    private static CharSequence widgetText;
    private int mAppWidgetId;

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        Log.d(LOG_TAG, "updateAppWidget");

        QuotesRepositoryRefactored quotesRepositoryRefactored = getQuotesRepositoryRefactored(context);

        widgetText = quotesRepositoryRefactored.getMonitorQuotes().getCurrentQuote();

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Конфигурационное активити
        Intent infoIntent = new Intent(context, InfoActivity.class);
        infoIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        infoIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pIntent = PendingIntent.getActivity(context, appWidgetId, infoIntent, 0);
        views.setOnClickPendingIntent(R.id.btnInfo, pIntent);

        // facebook
        Intent facebookIntent = new Intent(context, ShareOnFacebook.class);
        facebookIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        facebookIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        facebookIntent.putExtra("FACEBOOK", "FACEBOOK");
        pIntent = PendingIntent.getActivity(context, 4, facebookIntent, 0);
        views.setOnClickPendingIntent(R.id.btnFacebook, pIntent);

        // Добавление цитаты (кнопка "плюс")
        Intent addIntent = new Intent(context, AddQuote.class);
        addIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        addIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        pIntent = PendingIntent.getActivity(context, appWidgetId, addIntent, 0);
        views.setOnClickPendingIntent(R.id.btnAdd, pIntent);

        // Следующая цитата (кнопка "вправо")
        Intent nextIntent = new Intent(context, NewAppWidget.class);
        nextIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        nextIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        nextIntent.putExtra(KEY_UPDATE, NEXT_CLICKED);
        pIntent = PendingIntent.getBroadcast(context, VALUE_NEXT, nextIntent, 0);
        views.setOnClickPendingIntent(R.id.btnNext, pIntent);

        // Предыдущая цитата (кнопка "влево")
        Intent prevIntent = new Intent(context, NewAppWidget.class);
        prevIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        prevIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        prevIntent.putExtra(KEY_UPDATE, PREV_CLICKED);
        pIntent = PendingIntent.getBroadcast(context, VALUE_PREV, prevIntent, 0);
        views.setOnClickPendingIntent(R.id.btnPrev, pIntent);

        // Удаление цитаты (кнопка "минус")
        Intent deleteIntent = new Intent(context, NewAppWidget.class);
        deleteIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        deleteIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        deleteIntent.putExtra(KEY_UPDATE, DELETE_QUOTE);
        pIntent = PendingIntent.getBroadcast(context, VALUE_DEL, deleteIntent, 0);
        views.setOnClickPendingIntent(R.id.btnDelete, pIntent);
        Log.d(LOG_TAG, String.valueOf(appWidgetId));

        // Обновляем виджет
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Log.d(LOG_TAG, "onReceive");

        QuotesRepositoryRefactored quotesRepositoryRefactored = getQuotesRepositoryRefactored(context);
        MonitorQuotes monitorQuotes = quotesRepositoryRefactored.getMonitorQuotes();

        //Обновление виджета по расписанию
        monitorQuotes.updateWidgetByScheduler(intent, quotesRepositoryRefactored);

        //Обработка нажатия кнопок
        monitorQuotes.handleButtonClick(intent);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOG_TAG, "onUpdate");
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        // обновляем все экземпляры
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(LOG_TAG, "onDeleted");

        QuotesRepositoryRefactored quotesRepositoryRefactored = getQuotesRepositoryRefactored(context);
        // При удалении виджета, удаляем данные из SharedPreferences
        quotesRepositoryRefactored.getMonitorQuotes().deleteTitlePref();
        // Очищаем таблицу и закрываем базу
        quotesRepositoryRefactored.clearTable();
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(LOG_TAG, "onEnabled");
        Scheduler.scheduleUpdate(context);
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(LOG_TAG, "onDisabled");
        //Отменяем обновление виджета
        Scheduler.clearUpdate(context);

    }

    public static QuotesRepositoryRefactored getQuotesRepositoryRefactored(Context context){
        final GlobalClass globalVariable = (GlobalClass) context.getApplicationContext();
        return globalVariable.getQuotesRepositoryRefactored();
    }

}

