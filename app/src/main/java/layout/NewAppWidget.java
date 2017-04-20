package layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import com.example.david.mywidgetnewattempt.R;
import layout.PavelSh.SettingsChangedListener;
import layout.PavelSh.TraceUtils;
import layout.PavelSh.Utils;


// TODO: Реализуй интерфейс CurrentQuoteChangedListener по примеру SettingsChangedListener. И обновляй виджет.

public class NewAppWidget extends AppWidgetProvider implements SettingsChangedListener {

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

        widgetText = Utils.getGlobal(context).getMonitorQuotes().getCurrentQuote();

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
        //Обновление виджета по расписанию
        updateWidgetByScheduler(intent, context);
        //Обработка нажатия кнопок
        handleButtonClick(intent, context);
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
        // При удалении виджета, удаляем данные из SharedPreferences
        Utils.getGlobal(context).getMonitorQuotes().clearPreferences();
        // Очищаем таблицу и закрываем базу
        Utils.getGlobal(context).getQuotesRepository().close();
    }

    @Override
    public void onEnabled(Context context) {

        // TODO: Точно не знаю где нужно расположить эту подписку. Но коцепция такая. Разберись где это правильно логически.
        Utils.getGlobal(context).getSettings().setSettingsChangedListener(this);

        Log.d(LOG_TAG, "onEnabled");
        Scheduler.scheduleUpdate(context);
    }

    @Override
    public void onDisabled(Context context) {

        Utils.getGlobal(context).getSettings().setSettingsChangedListener(null);

        Log.d(LOG_TAG, "onDisabled");
        //Отменяем обновление виджета
        Scheduler.clearUpdate(context);
    }

    @Override
    public void onSettingsChanged(String keyName) {

        if (keyName.equals("listPref")) {
            TraceUtils.LogInfo("SettingsFragment listPref listener");
            // TODO: Напиши правильно код обновления виджета.
            //AppWidgetManager appWidgetManager = AppWidgetManager.getInstance();
            //NewAppWidget.updateAppWidget(mContext, appWidgetManager, mAppWidgetId);
            //Scheduler.scheduleUpdate(getActivity());
        }
    }

    private void handleButtonClick(Intent intent, Context mContext){

//        QuotesRepositoryRefactored quotesRepositoryRefactored = NewAppWidget.getQuotesRepositoryRefactored(mContext);

        int mAppWidgetId;
        Bundle extras = intent.getExtras();

        //Нахождение id виджета
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                return;
            }


            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
            String str = intent.getStringExtra(KEY_UPDATE);
            if (str != null) {
                // определяем сигнал установленный в ringtone preferences и признак его использования
                String alarms = Utils.getGlobal(mContext).getSettings().getRingtone();
                Boolean useSound = Utils.getGlobal(mContext).getSettings().getUseSound();
                Uri uri = Uri.parse(alarms);
                // TODO: Повыноси наждый хендлер в свой метод.
                switch (str) {

                    //Обработка нажатия "Следующая цитата"
                    case (NEXT_CLICKED):
                        // TODO: Везде используй TraceUtils.
                        Log.d(LOG_TAG, "NEXT_CLICKED");
//                        quotesRepositoryRefactored.nextQuote();
                        NewAppWidget.updateAppWidget(mContext, appWidgetManager, mAppWidgetId);
                        if (useSound) {
                            Utils.playSound(mContext, uri);
                        }
                        break;

                    //Обработка нажатия "Предыдущая цитата"
                    case (PREV_CLICKED):
                        Log.d(LOG_TAG, "PREV_CLICKED");
//                        quotesRepositoryRefactored.prevQuote();
                        if (useSound) {
                            Utils.playSound(mContext, uri);
                        }
                        NewAppWidget.updateAppWidget(mContext, appWidgetManager, mAppWidgetId);
                        break;

                    //Обработка нажатия "Удаление цитаты"
                    case (DELETE_QUOTE):
                        Log.d(LOG_TAG, "DELETE_QUOTE");
//                        quotesRepositoryRefactored.deleteQuote();
                        if (useSound) {
                            Utils.playSound(mContext, uri);
                        }
                        NewAppWidget.updateAppWidget(mContext, appWidgetManager, mAppWidgetId);
                        break;

                }

            }
        }
    }

    private void updateWidgetByScheduler(Intent intent, Context mContext) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        //Обновление виджета по расписанию
        if (intent.getAction().equalsIgnoreCase(UPDATE_ALL_WIDGETS)) {
            ComponentName thisAppWidget = new ComponentName(mContext.getPackageName(), getClass().getName());
            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);

//            quotesRepositoryRefactored.nextQuote();
            for (int appWidgetID : ids) {
                NewAppWidget.updateAppWidget(mContext, appWidgetManager, appWidgetID);
            }
        }
    }
}

