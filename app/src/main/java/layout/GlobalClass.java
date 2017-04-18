package layout;

import android.app.Application;

import layout.PavelSh.QuotesRepositoryRefactored;

/**
 * Created by TechnoA on 17.04.2017.
 */

public class GlobalClass extends Application {

    private QuotesRepository quotesRepository;
    private QuotesRepositoryRefactored quotesRepositoryRefactored;

    public QuotesRepository getQuotesRepository() {

        if(quotesRepository == null){
            quotesRepository = new QuotesRepository(this);
        }
        return quotesRepository;
    }

    public QuotesRepositoryRefactored getQuotesRepositoryRefactored() {

        if(quotesRepositoryRefactored == null){
            quotesRepositoryRefactored = new QuotesRepositoryRefactored(this);
        }
        return quotesRepositoryRefactored;
    }
}
