package layout.PavelSh;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Angelo W on 19.04.2017.
 */

public class Settings {

    private static final String PREF_NAME = "com.example.david.PREFERENCE_FILE_KEY";
    private static final String QUOTE_ID = "quote_id",
            QUOTE_TEXT = "quote_text",
            RINGTONE = "ringtone",
            USE_SOUND = "pref_sound_use";

    private Context context;

    public Settings(Context context) {

        this.context = context;
    }

    public String getQuote() {

        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(QUOTE_TEXT, "");
    }

    public void setQuote(String quote) {

        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(QUOTE_TEXT, quote);
        editor.commit();
    }

    public long getQuoteId() {

        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPref.getLong(QUOTE_ID, -1);
    }

    public void setQuoteId(long id) {

        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putLong(QUOTE_ID, id);
        editor.commit();
    }

    // TODO: Доделай.
    public long getRingtone() {
        return -1;
    }

    public String setRingtone() {
        return "default ringtone";
    }

    public boolean getUseSound() {

        return false;
    }

    public void setUseSound() {
    }

    public void close()  {

        TraceUtils.LogInfo("MonitorQuotes deleteTitlePref");
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREF_NAME, 0).edit();
        prefs.remove(QUOTE_ID);
        prefs.remove(QUOTE_TEXT);
        prefs.remove(RINGTONE);
        prefs.remove(USE_SOUND);
        prefs.apply();
    }
}
