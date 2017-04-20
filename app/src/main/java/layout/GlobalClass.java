package layout;

import android.app.Application;

import layout.PavelSh.MonitorQuotesRefactored;
import layout.PavelSh.QuotesRepository;
import layout.PavelSh.Settings;
import layout.data.MonitorQuotes;

/**
 * Created by TechnoA on 17.04.2017.
 */

public class GlobalClass extends Application {

    private QuotesRepository quotesRepository;
    private MonitorQuotes monitorQuotes;
    private MonitorQuotesRefactored monitorQuotesRefactored;
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

    public MonitorQuotesRefactored getMonitorQuotesRefactored() {
        if (monitorQuotesRefactored == null) {
            monitorQuotesRefactored = new MonitorQuotesRefactored(this);
        }
        return monitorQuotesRefactored;
    }

    public QuotesRepository getQuotesRepository() {

        if(quotesRepository == null){
            quotesRepository = new QuotesRepository(this);
        }
        return quotesRepository;
    }

}
