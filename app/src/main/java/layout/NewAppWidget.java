package layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.david.mywidgetnewattempt.R;

import layout.data.MyDBHelper;


public class NewAppWidget extends AppWidgetProvider {

    private static final String LOG_TAG = "MyLogWidget";

    private static final String NEXT_CLICKED    = "ButtonClickNext";
    private static final String PREV_CLICKED    = "ButtonClickPrev";
    private static final String DELETE_QUOTE    = "ButtonClickDelete";

    private static CharSequence widgetText;
    private int mAppWidgetId;
    private MediaPlayer mp;

    public void playSound(Context context){
        if (mp == null) {
            mp = MediaPlayer.create(context.getApplicationContext(), R.raw.sounds);
        }

        if (mp.isPlaying()){
            mp.stop();
            mp.release();
            mp = MediaPlayer.create(context.getApplicationContext(), R.raw.sounds);
        }
        else {
            mp.start();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(LOG_TAG,"onReceive");
        Bundle extras = intent.getExtras();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        //Обработка нажатия "Следующая цитата"
        if (NEXT_CLICKED.equals(intent.getAction()) && extras != null) {

            Log.d(LOG_TAG,"NEXT_CLICKED");
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            MyDBHelper myDBHelper = new MyDBHelper(context);
            widgetText = myDBHelper.nextQuote();

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
            views.setTextViewText(R.id.appwidget_text, widgetText);

            playSound(context);

            appWidgetManager.updateAppWidget(mAppWidgetId, views);

        //Обработка нажатия "Предыдущая цитата"
        }else if(PREV_CLICKED.equals(intent.getAction()) && extras != null) {

            Log.d(LOG_TAG,"PREV_CLICKED");
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            MyDBHelper myDBHelper = new MyDBHelper(context);
            widgetText = myDBHelper.prevQuote();

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
            views.setTextViewText(R.id.appwidget_text, widgetText);

            playSound(context);
            appWidgetManager.updateAppWidget(mAppWidgetId, views);

        //Обработка нажатия "Удаление цитаты"
        }else if (DELETE_QUOTE.equals(intent.getAction())) {

            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            MyDBHelper myDBHelper = new MyDBHelper(context);
            widgetText = myDBHelper.deleteQuote();

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
            views.setTextViewText(R.id.appwidget_text, widgetText);

            appWidgetManager.updateAppWidget(mAppWidgetId, views);

        }

    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOG_TAG,"onUpdate");
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(LOG_TAG,"onDeleted");
        // При удалении цитаты, удаляем данные из SharedPreferences
        MyDBHelper myDBHelper = new MyDBHelper(context);
        myDBHelper.deleteTitlePref(context);
    }
    @Override
    public void onEnabled(Context context){
        Log.d(LOG_TAG,"onEnabled");
    }
    @Override
    public void onDisabled(Context context) {
        Log.d(LOG_TAG,"onDisabled");
        // Закрываем базу и очищаем таблицу
        MyDBHelper myDBHelper = new MyDBHelper(context);
        myDBHelper.clearTable();
        myDBHelper.close();
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        MyDBHelper myDBHelper = new MyDBHelper(context);
        widgetText = myDBHelper.getCurrentQuote();

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        //Обработчик на запуск AddQuote activity
        onClickLaunchAddQuoteActivity(views, context, appWidgetManager, appWidgetId);

        //Обработчик на удаление текущей цитаты
        views.setOnClickPendingIntent(R.id.btnDelete, createPendingIntent(context, DELETE_QUOTE, appWidgetId));

        //Обработчик нажатия Следующей цитаты
        views.setOnClickPendingIntent(R.id.btnNext, createPendingIntent(context, NEXT_CLICKED, appWidgetId));

        //Обработчик нажатия Предыдущей цитаты
        views.setOnClickPendingIntent(R.id.btnPrev, createPendingIntent(context, PREV_CLICKED, appWidgetId));

        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    //Интент на запуск Активити для добавления цитаты
    static void onClickLaunchAddQuoteActivity(RemoteViews views, Context context, AppWidgetManager appWidgetManager, int appWidgetId){

        Log.d(LOG_TAG,"onClickLaunchAddQuoteActivity");
        Intent intentOpenConfigActivity = new Intent(context, AddQuote.class);
        intentOpenConfigActivity.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        intentOpenConfigActivity.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pIntent = PendingIntent.getActivity(context, appWidgetId, intentOpenConfigActivity, 0);

        views.setOnClickPendingIntent(R.id.btnAdd, pIntent);

    }

    //Широковещательное сообщение для различных действий(следующая цитата, предыдущая цитата, удаление цитаты)
    static PendingIntent createPendingIntent(Context context, String action, int appWidgetId){
        Intent intent = new Intent(context, NewAppWidget.class);
        intent.setAction(action);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, 0);

        return pendingIntent;
    }

}

