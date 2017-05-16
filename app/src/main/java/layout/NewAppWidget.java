package layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.app.david.mywidget.R;

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

        if (keyName.equals("interval")) {

            String interval = ((GlobalClass)context.getApplicationContext()).getSettings().getInterval();
            TraceUtils.logInfo("NewAppWidget onSettingsChanged interval = " + interval);
            Scheduler.scheduleUpdate(context,interval);
        }

        if(keyName.equals("fontSize")){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context, NewAppWidget.class));
            for (int id: ids) {
                updateAppWidget(context, appWidgetManager, id);
            }
        }
    }

    @Override
    public void onCurrentQuoteChanged(QuoteModel currentQuote, Context context) {

        TraceUtils.logInfo("onCurrentQuoteChanged");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context, NewAppWidget.class));
        for (int id: ids) {
            updateAppWidget(context, appWidgetManager, id);
        }

    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        TraceUtils.logInfo("updateAppWidget");
        QuoteModel quoteModel = Utils.getGlobal(context).getMonitorQuotes().getCurrentQuote();

        if(quoteModel == null){
            TraceUtils.logInfo("updateAppWidget quoteModel == null");
            return;
        }


        widgetText = quoteModel.getQuote();
        String fontSize = ((GlobalClass)context.getApplicationContext()).getSettings().getFontSize();

        RemoteViews views = ((GlobalClass)context.getApplicationContext()).getRemoteViews();
        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setTextViewTextSize(R.id.appwidget_text, TypedValue.COMPLEX_UNIT_DIP,Integer.valueOf(fontSize));

        setHandlerButtons(context, views, appWidgetId);

        // Обновляем виджет
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    public static void setHandlerButtons(Context context, RemoteViews views, int appWidgetId){

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

    }

    @Override
    public void onEnabled(Context context) {

        // Устанавливаем себя слушателем Настроек и цитат
        Utils.getGlobal(context).getSettings().setSettingsChangedListener(this);
        Utils.getGlobal(context).getMonitorQuotes().setCurrentQuoteChangedListener(this);

        TraceUtils.logInfo("onEnabled");
        TraceUtils.toast(context,"onEnabled");
        String interval = ((GlobalClass)context.getApplicationContext()).getSettings().getInterval();
        Scheduler.scheduleUpdate(context,interval);

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);
        TraceUtils.logInfo("onReceive");

        // Устанавливаем себя слушателем Настроек и цитат
        Utils.getGlobal(context).getSettings().setSettingsChangedListener(this);
        Utils.getGlobal(context).getMonitorQuotes().setCurrentQuoteChangedListener(this);

        //Обновление виджета по расписанию
        updateWidgetByScheduler(intent, context);

        //Обработка нажатия кнопок
        handleButtonClick(intent, context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        TraceUtils.logInfo("onUpdate");
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        // обновляем все экземпляры
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

        TraceUtils.logInfo("onDeleted");
        // При удалении виджета, удаляем данные из SharedPreferences
        Utils.getGlobal(context).getSettings().close();
        // Очищаем таблицу и закрываем базу
        Utils.getGlobal(context).getQuotesRepository().close();
    }

    @Override
    public void onDisabled(Context context) {

        Utils.getGlobal(context).getSettings().setSettingsChangedListener(null);

        TraceUtils.logInfo("onDisabled");
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
                TraceUtils.logInfo("NEXT_CLICKED");
                nextQuote(context);
                break;

            case (PREV_CLICKED):
                TraceUtils.logInfo("PREV_CLICKED");
                prevQuote(context);
                break;

            case (DELETE_QUOTE):
                TraceUtils.logInfo("DELETE_QUOTE");
                deleteQuote(context);
                break;
        }

    }

    public void nextQuote(Context context){
        Utils.getGlobal(context).getMonitorQuotes().setNext();
    }

    public void prevQuote(Context context){
        Utils.getGlobal(context).getMonitorQuotes().setPrev();
    }

    public void deleteQuote(Context context){
        Utils.getGlobal(context).getMonitorQuotes().deleteQuote();
        Toast.makeText(context, "You deleted current idea", Toast.LENGTH_SHORT).show();
    }

    private void updateWidgetByScheduler(Intent intent, Context context) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        //Обновление виджета по расписанию
        if (intent.getAction().equalsIgnoreCase(UPDATE_ALL_WIDGETS)) {

            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
            // следующая цитата
            ((GlobalClass)context.getApplicationContext()).getMonitorQuotes().setNext();
            for (int appWidgetID : ids) {
                TraceUtils.logInfo("updateWidgetByScheduler");
                NewAppWidget.updateAppWidget(context, appWidgetManager, appWidgetID);
            }
        }
    }

}

