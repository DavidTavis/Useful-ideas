package layout;

import android.content.Context;
import android.widget.Toast;

import layout.models.QuoteModel;
import layout.utils.NullQuoteException;
import layout.utils.TraceUtils;
import layout.utils.Utils;

/**
 * Created by TechnoA on 17.04.2017.
 */

public class MonitorQuotes {

    private CurrentQuoteChangedListener listener;
    private QuoteModel currentQuote;
    private Context context;

    public MonitorQuotes(Context context) {

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
        if (id < 0) {
            try{
                throw new NullQuoteException("Current quote undefined");
            }catch (NullQuoteException e) {
                return null;
            }
        }

        String quote = Utils.getGlobal(context).getSettings().getQuote();
        QuoteModel quoteModel = new QuoteModel(quote, id);

        setCurrentQuote(quoteModel);

        return quoteModel;

    }

    public void setNext() {
        int tableSize = ((GlobalClass) context).getQuotesRepository().count();
        if(tableSize == 0) {
            TraceUtils.Toast(context,"tableSize == 0");
            return;
        }
        if(currentQuote == null){
            currentQuote = getCurrentQuote();
        }
        QuoteModel nextQuote = Utils.getGlobal(context).getQuotesRepository().getNextQuote(currentQuote.getId());
        TraceUtils.LogInfo(nextQuote.getQuote());
        setCurrentQuote(nextQuote);
    }

    public void setPrev() {
        int tableSize = ((GlobalClass) context).getQuotesRepository().count();
        if(tableSize == 0) return;

        QuoteModel prevQuote = Utils.getGlobal(context).getQuotesRepository().getPrevQuote(currentQuote.getId());
        setCurrentQuote(prevQuote);
    }

    public void deleteQuote(){
        long quoteIdForDeleting = currentQuote.getId();
        int tableSize = ((GlobalClass) context).getQuotesRepository().count();
        if(tableSize > 1) {
            setNext();
            Utils.getGlobal(context).getQuotesRepository().deleteQuote(quoteIdForDeleting);
        }else if(tableSize == 1){
            setCurrentQuote(new QuoteModel("Quotes are deleted",1));
            Utils.getGlobal(context).getQuotesRepository().deleteQuote(quoteIdForDeleting);
        }
    }

    public void setLast(QuoteModel quoteModel){
        setCurrentQuote(quoteModel);
    }

    private void setCurrentQuote(QuoteModel quote){

        Utils.getGlobal(context).getSettings().setQuoteId(quote.getId());
        Utils.getGlobal(context).getSettings().setQuote(quote.getQuote());

        currentQuote = quote;
        if(listener != null) {
            listener.onCurrentQuoteChanged(currentQuote, context);
        }else
            TraceUtils.LogInfo("listener = null");
        }

}
