package layout;

import android.app.Application;

import layout.PavelSh.QuotesRepositoryRefactored;
import layout.data.MonitorQuotes;

/**
 * Created by TechnoA on 17.04.2017.
 */

public class GlobalClass extends Application {

    private QuotesRepository quotesRepository;
    private QuotesRepositoryRefactored quotesRepositoryRefactored;
    private MonitorQuotes monitorQuotes;

    public MonitorQuotes getMonitorQuotes() {
        if(monitorQuotes==null){
            monitorQuotes = new MonitorQuotes(this);
        }
        return monitorQuotes;
    }

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
