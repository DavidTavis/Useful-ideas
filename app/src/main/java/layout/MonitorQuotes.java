package layout;

import android.content.Context;

import layout.models.QuoteModel;
import layout.repository.QuotesRepository;
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
        // TODO: PavelSh what exception should i choose?

        if (id < 0) {
//            throw new NullPointerException("Current quote undefined");
            return null;
        }

        String quote = Utils.getGlobal(context).getSettings().getQuote();
        QuoteModel quoteModel = new QuoteModel(quote, id);

        setCurrentQuote(quoteModel);

        return quoteModel;

    }

    public void setNext() {
        int tableSize = ((GlobalClass) context).getQuotesRepository().getTableSize();
        if(tableSize == 0) return;

        QuoteModel nextQuote = Utils.getGlobal(context).getQuotesRepository().getNextQuote(currentQuote.getId());
        setCurrentQuote(nextQuote);
    }

    public void setPrev() {
        int tableSize = ((GlobalClass) context).getQuotesRepository().getTableSize();
        if(tableSize == 0) return;

        QuoteModel prevQuote = Utils.getGlobal(context).getQuotesRepository().getPrevQuote(currentQuote.getId());
        setCurrentQuote(prevQuote);
    }

    public void deleteQuote(){
        // TODO: А если следующей квоты нет?
        // TODO: PavelSh it's my solution this issue
        long quoteIdForDeleting = currentQuote.getId();
        int tableSize = ((GlobalClass) context).getQuotesRepository().getTableSize();
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
        if(listener != null)
            listener.onCurrentQuoteChanged(currentQuote, context);
    }

}
