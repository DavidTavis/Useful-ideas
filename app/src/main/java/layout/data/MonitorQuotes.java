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


}
