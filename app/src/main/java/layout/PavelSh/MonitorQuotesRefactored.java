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

import layout.GlobalClass;
import layout.models.QuoteModel;

import java.io.IOException;

import layout.NewAppWidget;

/**
 * Created by TechnoA on 17.04.2017.
 */

/// Класс занимается хранением состояния текущей квоты.
/// Может также лежать в конетксте, но возможно им владеет только NewAppWidget.
public class MonitorQuotesRefactored {

    private CurrentQuoteChangedListener listener;
    private QuoteModel currentQuote;
    private Context context;

    public MonitorQuotesRefactored(Context context) {

        this.context = context;
    }

    public void setCurrentQuoteChangedListener(CurrentQuoteChangedListener listener) {

        this.listener = listener;
    }

    public QuoteModel getCurrentQuote(){

        TraceUtils.LogInfo("MonitorQuotes getCurrentQuote");

        if(currentQuote != null)
            return currentQuote;


        long id = Utils.getGlobal(context).getSettings().getQuoteId();
        if(id < 0)
            return null;

        String quote = Utils.getGlobal(context).getSettings().getQuote();
        return new QuoteModel(quote, id);
    }

    public void nextQuote() {

        QuoteModel nextQuote = Utils.getGlobal(context).getQuotesRepository().getNextQuote(currentQuote.getId());
        setCurrentQuote(nextQuote);
    }

    public void prevQuote() {

        QuoteModel prevQoute = Utils.getGlobal(context).getQuotesRepository().getPrevQuote(currentQuote.getId());
        setCurrentQuote(prevQoute);
    }

    private void setCurrentQuote(QuoteModel quote){

        Utils.getGlobal(context).getSettings().setQuoteId(quote.getId());
        Utils.getGlobal(context).getSettings().setQuote(quote.getQuote());

        currentQuote = quote;
        if(listener != null)
            listener.onCurrentQuoteChanged(currentQuote);
    }
}
