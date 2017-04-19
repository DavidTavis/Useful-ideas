package layout.PavelSh;

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
import layout.models.QuoteModel;

import java.io.IOException;

import layout.NewAppWidget;

/**
 * Created by TechnoA on 17.04.2017.
 */

/// Класс занимается хранением состояния текущей квоты.
/// Может также лежать в конетксте, но возможно им владеет только NewAppWidget.
public class MonitorQuotesRefactored {

    private static final String PREF_NAME = "com.example.david.PREFERENCE_FILE_KEY";
    private static final String QUOTE_ID = "quote_id",
                                QUOTE_TEXT = "quote_text";

    private CurrentQuoteChangedListener listener;
    private Context context;
    private QuoteModel currentQuote;

    public MonitorQuotesRefactored(Context context) {

        this.context = context;
    }

    public void setCurrentQuoteChangedListener(CurrentQuoteChangedListener listener) {

        this.listener = listener;
    }

    public QuoteModel getCurrentQuote(){

        TraceUtils.LogInfo("MonitorQuotes getCurrentQuote");

        // Warning! Стремный код!-------> Да. Это был костыль который нужно было убрать
        //if(mContext == null){
        //    Log.d(LOG_TAG,"mContext == null");
        //    return "";
        //}

        if(currentQuote != null)
            return currentQuote;

        SharedPreferences sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long id = sharedPref.getLong(QUOTE_ID, -1);
        String quote = sharedPref.getString(QUOTE_TEXT, "");

        if(id < 0)
            return null;

        return new QuoteModel(quote, id);
    }

    public void nextQuote() {
    }

    public void prevQuote() {
    }

    private void setCurrentQuote(QuoteModel quote){

        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putLong(QUOTE_ID, quote.getId());
        editor.putString(QUOTE_TEXT, quote.getQuote());
        editor.commit();

        currentQuote = quote;
        if(listener != null)
            listener.onCurrentQuoteChanged(currentQuote);
    }
}
