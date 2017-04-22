package layout;

import android.content.Context;

import layout.models.QuoteModel;
import layout.utils.TraceUtils;
import layout.utils.Utils;

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

        TraceUtils.LogInfo("MonitorQuotesRefactored getCurrentQuote");

        if(currentQuote != null)
            return currentQuote;


        long id = Utils.getGlobal(context).getSettings().getQuoteId();
        if(id < 0)
            return null;

        String quote = Utils.getGlobal(context).getSettings().getQuote();
        return new QuoteModel(quote, id);

    }

    public void setNext() {

        if(currentQuote==null) {
            QuoteModel quoteModel = Utils.getGlobal(context).getMonitorQuotesRefactored().getCurrentQuote();
            setCurrentQuote(quoteModel);
            if(currentQuote==null)
                return;
        }
        QuoteModel nextQuote = Utils.getGlobal(context).getQuotesRepository().getNextQuote(currentQuote.getId());
        TraceUtils.LogInfo(nextQuote.getQuote());
        setCurrentQuote(nextQuote);

    }

    public void setPrev() {

        if(currentQuote==null) {
            QuoteModel quoteModel = Utils.getGlobal(context).getMonitorQuotesRefactored().getCurrentQuote();
            setCurrentQuote(quoteModel);
            if(currentQuote==null)
                return;
        }
        QuoteModel prevQoute = Utils.getGlobal(context).getQuotesRepository().getPrevQuote(currentQuote.getId());
        setCurrentQuote(prevQoute);

    }

    public void setLast(QuoteModel quoteModel){
        setCurrentQuote(quoteModel);
    }

    public void deleteQuote(){

        if(currentQuote==null) {
            QuoteModel quoteModel = Utils.getGlobal(context).getMonitorQuotesRefactored().getCurrentQuote();
            setCurrentQuote(quoteModel);
            if(currentQuote==null)
                return;
        }
        QuoteModel nextQuote = Utils.getGlobal(context).getQuotesRepository().deleteQuote(currentQuote.getId());
        setCurrentQuote(nextQuote);

    }

    private void setCurrentQuote(QuoteModel quote){

        Utils.getGlobal(context).getSettings().setQuoteId(quote.getId());
        Utils.getGlobal(context).getSettings().setQuote(quote.getQuote());

        currentQuote = quote;
        if(listener != null)
            listener.onCurrentQuoteChanged(currentQuote,context);
    }
}
