package layout;

/**
 * Created by TechnoA on 17.04.2017.
 */

public class QuoteModel {
    private String quote;
    private long id;

    public QuoteModel(String quote, long id) {
        this.quote = quote;
        this.id = id;
    }

    public String getQuote() {
        return quote;
    }

    public long getId() {
        return id;
    }
}
