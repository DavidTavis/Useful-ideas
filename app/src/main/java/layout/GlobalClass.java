package layout;

import android.app.Application;

import layout.MonitorQuotesRefactored;
import layout.repository.QuotesRepository;
import layout.settings.Settings;

/**
 * Created by TechnoA on 17.04.2017.
 */

public class GlobalClass extends Application {

    private QuotesRepository quotesRepository;
    private MonitorQuotesRefactored monitorQuotesRefactored;
    private Settings settings;

    public Settings getSettings() {

        if(settings==null){
            settings = new Settings(this);
        }
        return settings;
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
