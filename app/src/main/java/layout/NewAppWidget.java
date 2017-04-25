package layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import com.example.david.mywidgetnewattempt.R;

import layout.settings.SettingsChangedListener;
import layout.utils.TraceUtils;
import layout.utils.Utils;
import layout.models.QuoteModel;
import layout.views.AddQuote;
import layout.views.InfoActivity;
import layout.views.ShareOnFacebook;


public class NewAppWidget extends AppWidgetProvider implements SettingsChangedListener, CurrentQuoteChangedListener {

    public static final String UPDATE_ALL_WIDGETS = "update_all_widgets";
    private static final String NEXT_CLICKED = "com.example.david.mywidgetnewattempt.ButtonClickNext";
    private static final String PREV_CLICKED = "com.example.david.mywidgetnewattempt.ButtonClickPrev";
    private static final String DELETE_QUOTE = "com.example.david.mywidgetnewattempt.ButtonClickDelete";
    private static final String KEY_UPDATE = "UPDATE";

    private static final int VALUE_NEXT = 1;
    private static final int VALUE_PREV = 2;
    private static final int VALUE_DEL = 3;

    private static CharSequence widgetText;

    @Override
    public void onSettingsChanged(String keyName, Context context) {
        if (keyName.equals("listPref")) {
            TraceUtils.LogInfo("SettingsFragment listPref listener");
            Scheduler.scheduleUpdate(context);
        }
    }

    @Override
    public void onCurrentQuoteChanged(QuoteModel currentQuote, Context context) {

        TraceUtils.LogInfo("onCurrentQuoteChanged");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context, NewAppWidget.class));
        for (int id: ids) {
            updateAppWidget(context, appWidgetManager, id);
        }

    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        TraceUtils.LogInfo("updateAppWidget");
        QuoteModel quoteModel = Utils.getGlobal(context).getMonitorQuotes().getCurrentQuote();

        if(quoteModel == null)
            return;

        widgetText = quoteModel.getQuote();

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

        // Обновляем виджет
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);
        TraceUtils.LogInfo("onReceive");

        //Обновление виджета по расписанию
        updateWidgetByScheduler(intent, context);
        //Обработка нажатия кнопок
        handleButtonClick(intent, context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        TraceUtils.LogInfo("onUpdate");
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        // обновляем все экземпляры
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

        TraceUtils.LogInfo("onDeleted");
        // При удалении виджета, удаляем данные из SharedPreferences
        Utils.getGlobal(context).getSettings().close();
        // Очищаем таблицу и закрываем базу
        Utils.getGlobal(context).getQuotesRepository().close();
    }

    @Override
    public void onEnabled(Context context) {

        Utils.getGlobal(context).getSettings().setSettingsChangedListener(this);
        Utils.getGlobal(context).getMonitorQuotes().setCurrentQuoteChangedListener(this);

        TraceUtils.LogInfo("onEnabled");
        Scheduler.scheduleUpdate(context);
    }

    @Override
    public void onDisabled(Context context) {

        Utils.getGlobal(context).getSettings().setSettingsChangedListener(null);

        TraceUtils.LogInfo("onDisabled");
        //Отменяем обновление виджета
        Scheduler.clearUpdate(context);
    }

    private void handleButtonClick(Intent intent, Context context){

        // определяем сигнал установленный в ringtone preferences и признак его использования
        Uri uri = Uri.parse(Utils.getGlobal(context).getSettings().getRingtone());
        Boolean useSound = Utils.getGlobal(context).getSettings().getUseSound();

        String str = intent.getStringExtra(KEY_UPDATE);

        if(str==null)
            return;

        if (useSound) {
            Utils.playSound(context, uri);
        }
        switch (str) {
            case (NEXT_CLICKED):
                nextQuote(context);
                break;

            case (PREV_CLICKED):
                prevQuote(context);
                break;

            case (DELETE_QUOTE):
                deleteQuote(context);
                break;
        }

    }

    public void nextQuote(Context context){
        TraceUtils.LogInfo("NEXT_CLICKED");
        Utils.getGlobal(context).getMonitorQuotes().setNext();

    }

    public void prevQuote(Context context){
        TraceUtils.LogInfo("PREV_CLICKED");
        Utils.getGlobal(context).getMonitorQuotes().setPrev();
    }

    public void deleteQuote(Context context){
        TraceUtils.LogInfo("DELETE_QUOTE");
        Utils.getGlobal(context).getMonitorQuotes().deleteQuote();
    }

    private void updateWidgetByScheduler(Intent intent, Context context) {

        ((GlobalClass)context.getApplicationContext()).getMonitorQuotes().setNext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        //Обновление виджета по расписанию
        if (intent.getAction().equalsIgnoreCase(UPDATE_ALL_WIDGETS)) {
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);

            for (int appWidgetID : ids) {
                NewAppWidget.updateAppWidget(context, appWidgetManager, appWidgetID);
            }
        }
    }

}

