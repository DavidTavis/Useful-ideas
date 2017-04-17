package layout;

import android.app.Application;

/**
 * Created by TechnoA on 17.04.2017.
 */

public class GlobalClass extends Application {

    private QuotesRepository quotesRepository;

    public QuotesRepository getQuotesRepository() {

        return quotesRepository;
    }

    public void setQuotesRepository(QuotesRepository aQuotesRepository) {

        quotesRepository = aQuotesRepository;
    }

}
