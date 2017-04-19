package layout;

import android.app.Application;

import layout.PavelSh.QuotesRepository;
import layout.PavelSh.Settings;
import layout.data.MonitorQuotes;

/**
 * Created by TechnoA on 17.04.2017.
 */

public class GlobalClass extends Application {

    private QuotesRepository quotesRepositoryRefactored;
    private MonitorQuotes monitorQuotes;
    private Settings settings;

    public Settings getSettings() {

        if(settings==null){
            settings = new Settings(this);
        }
        return settings;
    }

    public MonitorQuotes getMonitorQuotes() {

        if(monitorQuotes==null){
            monitorQuotes = new MonitorQuotes(this);
        }
        return monitorQuotes;
    }

    public QuotesRepository getQuotesRepository() {

        if(quotesRepositoryRefactored == null){
            quotesRepositoryRefactored = new QuotesRepository(this);
        }
        return quotesRepositoryRefactored;
    }

}
