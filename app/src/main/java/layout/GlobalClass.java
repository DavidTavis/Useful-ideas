package layout;

import android.app.Application;

import layout.repository.QuotesRepository;
import layout.settings.Settings;

/**
 * Created by TechnoA on 17.04.2017.
 */

public class GlobalClass extends Application {

    private QuotesRepository quotesRepository;
    private MonitorQuotes monitorQuotes;
    private Settings settings;

    public Settings getSettings() {

        if(settings==null){
            settings = new Settings(this);
        }
        return settings;
    }

    public MonitorQuotes getMonitorQuotes() {

        if (monitorQuotes == null) {
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

}
