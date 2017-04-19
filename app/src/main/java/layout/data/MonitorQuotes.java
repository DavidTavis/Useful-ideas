package layout.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.Log;

import layout.PavelSh.TraceUtils;

/**
 * Created by TechnoA on 17.04.2017.
 */

public class MonitorQuotes {

    public static final String PREF_NAME = "com.example.david.PREFERENCE_FILE_KEY";
    public static final String CURRENT_QUOTE = "current quote";
    public static final String RINGTONE = "ringtone";
    public static final String USE_SOUND = "pref_sound_use";


    Context mContext;

    public MonitorQuotes(Context context) {
        this.mContext = context;
    }

    public void setCurrentQuote(String quote){

        TraceUtils.LogInfo("MonitorQuotes setCurrentQuote");

        SharedPreferences.Editor editor = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(CURRENT_QUOTE,quote);
        editor.commit();
    }

    public String getCurrentQuote(){

        TraceUtils.LogInfo("MonitorQuotes getCurrentQuote");

        if(mContext == null){
            TraceUtils.LogInfo("mContext == null");
            return "";
        }
        SharedPreferences sharedPref = mContext.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        String defaultValue = "Quote undefined";
        String currentQuote = sharedPref.getString(CURRENT_QUOTE, defaultValue);
        return currentQuote;
    }

    public void clearPreferences() {

        TraceUtils.LogInfo("MonitorQuotes deleteTitlePref");
        SharedPreferences.Editor prefs = mContext.getSharedPreferences(PREF_NAME, 0).edit();
        prefs.remove(CURRENT_QUOTE);
        prefs.remove(RINGTONE);
        prefs.remove(USE_SOUND);
        prefs.apply();
    }
}
