package layout.data;

import android.appwidget.AppWidgetManager;
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

import java.io.IOException;

import layout.InfoActivity;
import layout.NewAppWidget;
import layout.PavelSh.QuotesRepositoryRefactored;
import layout.QuotesRepository;

/**
 * Created by TechnoA on 17.04.2017.
 */

public class MonitorQuotes {

    public static final String LOG_TAG = "MyLogWidget";

    private static final String NEXT_CLICKED = "com.example.david.mywidgetnewattempt.ButtonClickNext";
    private static final String PREV_CLICKED = "com.example.david.mywidgetnewattempt.ButtonClickPrev";
    private static final String DELETE_QUOTE = "com.example.david.mywidgetnewattempt.ButtonClickDelete";
    private static final String KEY_UPDATE = "UPDATE";
    public static final String UPDATE_ALL_WIDGETS = "update_all_widgets";
    public static final String PREF_NAME = "com.example.david.PREFERENCE_FILE_KEY";
    public static final String CURRENT_QUOTE = "current quote";
    public static final String CURRENT_QUOTE_ID = "current quote id";
    public static final String RINGTONE = "ringtone";
    public static final String USE_SOUND = "pref_sound_use";

    Context mContext;

    public MonitorQuotes(Context context) {
        this.mContext = context;
    }

    public void setCurrentQuote(String quote){
        Log.d(LOG_TAG,"MonitorQuotes setCurrentQuote");

        SharedPreferences.Editor editor = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(CURRENT_QUOTE,quote);
        editor.commit();
    }

    public String getCurrentQuote(){
        Log.d(LOG_TAG,"MonitorQuotes getCurrentQuote");

        if(mContext == null){
            Log.d(LOG_TAG,"mContext == null");
            return "";
        }
        SharedPreferences sharedPref = mContext.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        String defaultValue = "Quote undefined";
        String currentQuote = sharedPref.getString(CURRENT_QUOTE, defaultValue);
        return currentQuote;
    }

    public void deleteTitlePref() {
        Log.d(LOG_TAG,"MonitorQuotes deleteTitlePref");

        SharedPreferences.Editor prefs = mContext.getSharedPreferences(PREF_NAME, 0).edit();
        prefs.remove(CURRENT_QUOTE);
        prefs.remove(RINGTONE);
        prefs.remove(USE_SOUND);
        prefs.apply();
    }

    // TODO: Это отправь в NewAppWidget который будет реализовывать CurrentQouteChangedListener
    // и обновлять виджет по событию изменения текущей квоты.
    public void updateWidgetByScheduler(Intent intent, QuotesRepositoryRefactored quotesRepositoryRefactored) {

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

    // TODO: Это отправь в NewAppWidget
    public void handleButtonClick(Intent intent){

        QuotesRepositoryRefactored quotesRepositoryRefactored = NewAppWidget.getQuotesRepositoryRefactored(mContext);

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
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
                String alarms = settings.getString(MonitorQuotes.RINGTONE, "default ringtone");
                Boolean useSound = settings.getBoolean(MonitorQuotes.USE_SOUND, true);
                Uri uri = Uri.parse(alarms);

                switch (str) {

                    //Обработка нажатия "Следующая цитата"
                    case (NEXT_CLICKED):
                        Log.d(LOG_TAG, "NEXT_CLICKED");
//                        quotesRepositoryRefactored.nextQuote();
                        NewAppWidget.updateAppWidget(mContext, appWidgetManager, mAppWidgetId);
                        if (useSound) {
                            playSound(mContext, uri);
                        }
                        break;

                    //Обработка нажатия "Предыдущая цитата"
                    case (PREV_CLICKED):
                        Log.d(LOG_TAG, "PREV_CLICKED");
//                        quotesRepositoryRefactored.prevQuote();
                        if (useSound) {
                            playSound(mContext, uri);
                        }
                        NewAppWidget.updateAppWidget(mContext, appWidgetManager, mAppWidgetId);
                        break;

                    //Обработка нажатия "Удаление цитаты"
                    case (DELETE_QUOTE):
                        Log.d(LOG_TAG, "DELETE_QUOTE");
//                        quotesRepositoryRefactored.deleteQuote();
                        if (useSound) {
                            playSound(mContext, uri);
                        }
                        NewAppWidget.updateAppWidget(mContext, appWidgetManager, mAppWidgetId);
                        break;

                }

            }
        }
    }

    // TODO: Это в NewAppWidget
    public void playSound(Context context, Uri alert) {

        MediaPlayer mMediaPlayer = new MediaPlayer();

        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }
}
