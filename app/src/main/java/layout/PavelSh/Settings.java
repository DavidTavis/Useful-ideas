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
    private SharedPreferences sharedPref;
    public Settings(Context context) {

        this.context = context;
        this.sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getQuote() {

        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(QUOTE_TEXT, "");
    }

    public void setQuote(String quote) {

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(QUOTE_TEXT, quote);
        editor.commit();
    }

    public long getQuoteId() {
        return sharedPref.getLong(QUOTE_ID, -1);
    }

    public void setQuoteId(long id) {

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(QUOTE_ID, id);
        editor.commit();
    }

    public String getRingtone() {
        return sharedPref.getString(RINGTONE,"default ringtone");
    }

    // TODO: PavelSh не нужно устанавливать значение, так как при выборе оно уже записывается.
    public String setRingtone() {
        return "default ringtone";
    }

    public boolean getUseSound() {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(USE_SOUND,true);
    }

    // TODO: PavelSh не нужно устанавливать значение, так как при выборе оно уже записывается.
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
