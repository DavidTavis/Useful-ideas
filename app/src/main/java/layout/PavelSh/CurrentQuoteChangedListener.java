package layout.PavelSh;
import layout.models.QuoteModel;

/**
 * Created by Angelo W on 19.04.2017.
 */

public interface CurrentQuoteChangedListener {

    void onCurrentQuoteChanged(QuoteModel currentQuote);
}
