package layout;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

import layout.models.QuoteModel;
import layout.data.MonitorQuotes;

/**
 * Created by TechnoA on 17.04.2017.
 */

public class QuotesRepository {

    private Context mContext;
    private MyDBHelper myDBHelper;
    private MonitorQuotes monitorQuotes;

    public QuotesRepository(Context context) {
        mContext = context;
        myDBHelper = new MyDBHelper(context);
        monitorQuotes = new MonitorQuotes(context);
    }

    public MonitorQuotes getMonitorQuotes() {
        return monitorQuotes;
    }

    public MyDBHelper getMyDBHelper() {
        return myDBHelper;
    }

    public QuoteModel addQuote(String quote){
        long id = myDBHelper.writeQuoteToDBSQLite(quote);
        return new QuoteModel(quote,id);
    }

    public QuoteModel findQuoteByID(long id){

        return new QuoteModel("someQuote", 1); //temporarily
    }

    public void removeQuote(Integer id){
        MyDBHelper myDBHelper = new MyDBHelper(mContext);
        myDBHelper.deleteQuote();
    }

    public class MyDBHelper extends SQLiteOpenHelper {

        public static final String LOG_TAG = "MyLogWidget";

        public static final String DATABASE_NAME = "Quotes.db";
        private static final int DATABASE_VERSION = 3;
        public final static String TABLE_NAME = "quotes3";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_QUOTE = "quote";
        public static final String PREF_NAME = "com.example.david.PREFERENCE_FILE_KEY";
        public static final String CURRENT_QUOTE = "current quote";

        private Context mContext;

        public MyDBHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            Log.d(LOG_TAG,"MyDBHelper Constructor");
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG,"MyDBHelper onCreate");
            // Строка для создания таблицы
            String SQL_CREATE_GUESTS_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                    + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_QUOTE + " TEXT NOT NULL);";

            // Запускаем создание таблицы
            db.execSQL(SQL_CREATE_GUESTS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG,"MyDBHelper onUpgrade");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

        public long writeQuoteToDBSQLite(String quote) {
//        Log.d(LOG_TAG,"MyDBHelper writeQuoteToDBSQLite");
            SQLiteDatabase db = getWritableDatabase();

            boolean quoteIsExist = checkQuoteIsExist(quote);
            if(quoteIsExist){
                Toast.makeText(mContext, "This quote already exists", Toast.LENGTH_SHORT).show();
                return 0;
            }
            ContentValues values = new ContentValues();
            values.put(COLUMN_QUOTE, quote);
            long id = db.insert(TABLE_NAME, null, values);
            monitorQuotes.setCurrentQuote(quote);

            return id;

        }

        public void deleteQuote(){
//        Log.d(LOG_TAG,"MyDBHelper deleteQuote");
            String quoteForDelete = monitorQuotes.getCurrentQuote();
            nextQuote();
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL(" DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_QUOTE + "=\"" + quoteForDelete + "\";");
        }

        public void prevQuote(){
//        Log.d(LOG_TAG,"MyDBHelper prevQuote");
            String currentQuote = monitorQuotes.getCurrentQuote();
            String prevQuote = getQuoteBeforeCurrent(currentQuote);
            monitorQuotes.setCurrentQuote(prevQuote);
        }
        public String getQuoteBeforeCurrent(String quote){

//        Log.d(LOG_TAG,"MyDBHelper getQuoteBeforeCurrent");
            String prevQuote = "Prev quote";

            int countRows = getTableSize();

            int currentID = getCurrentID(quote);

            SQLiteDatabase db = getWritableDatabase();

            String selection = null;
            String[] selectionArgs = null;
            String[] columns = null;

            columns = new String[] { COLUMN_QUOTE };
            selection = _ID +" < ?";
            selectionArgs = new String[] { String.valueOf(currentID) };

            Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null,null);

            if(cursor.isAfterLast()){
                prevQuote = getLastQuote();
            }

            while (cursor.moveToNext()) {
                prevQuote = cursor.getString(cursor.getColumnIndex(COLUMN_QUOTE));
            }
            return prevQuote;
        }

        public void nextQuote(){
            Log.d(LOG_TAG,"MyDBHelper nextQuote");
            String currentQuote = monitorQuotes.getCurrentQuote();
            String nextQuote = getQuoteAfterCurrent(currentQuote);
            monitorQuotes.setCurrentQuote(nextQuote);
        }

        public String getQuoteAfterCurrent(String quote){

//        Log.d(LOG_TAG,"MyDBHelper getQuoteAfterCurrent");
            String nextQuote = "Next quote";

            int countRows = getTableSize();

            int currentID = getCurrentID(quote);

            SQLiteDatabase db = getWritableDatabase();

            String selection = null;
            String[] selectionArgs = null;
            String[] columns = null;

            columns = new String[] { COLUMN_QUOTE };
            selection = _ID +" > ?";
            selectionArgs = new String[] { String.valueOf(currentID) };

            Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null,null);

            if(cursor.isAfterLast()){
                nextQuote = getFirstQuote();
            }

            while (cursor.moveToNext()) {
                nextQuote = cursor.getString(cursor.getColumnIndex(COLUMN_QUOTE));
                break;
            }
            return nextQuote;
        }

        public boolean checkQuoteIsExist(String quote){
//        Log.d(LOG_TAG,"MyDBHelper checkQuoteIsExist");
            boolean exist = false;

            SQLiteDatabase db = getReadableDatabase();

            String selection = null;
            String[] selectionArgs = null;
            String[] columns = null;

            columns = new String[] { COLUMN_QUOTE };
            selection = COLUMN_QUOTE +" = ?";
            selectionArgs = new String[] { quote };

            Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null,null);

            if(!cursor.isAfterLast()){
                exist = true;
            }

            return exist;
        }

        public int getTableSize(){
//        Log.d(LOG_TAG,"MyDBHelper getTableSize");
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
            int count = cursor.getCount();
            cursor.close();
            return count;
        }

        public int getCurrentID(String quote){
            Log.d(LOG_TAG,"MyDBHelper getCurrentID");
            int currentID = 1;
            SQLiteDatabase db = getReadableDatabase();
            String query = " SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_QUOTE + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{quote});
            int idColumnIndex = cursor.getColumnIndex(_ID);
            while (cursor.moveToNext()) {
                currentID = cursor.getInt(idColumnIndex);
            }
            return currentID;
        }

        public String getFirstQuote(){
//        Log.d(LOG_TAG,"MyDBHelper getFirstQuote");
            SQLiteDatabase db = getReadableDatabase();
            String query = "SELECT " + COLUMN_QUOTE +  ", MIN(_ID)  FROM " + TABLE_NAME ;
            Cursor cursor = db.rawQuery(query,null);
            cursor.moveToFirst();
            String firstQuote = cursor.getString(cursor.getColumnIndex(COLUMN_QUOTE));
            return firstQuote;
        }
        public String getLastQuote(){
            Log.d(LOG_TAG,"MyDBHelper getLastQuote");
            SQLiteDatabase db = getReadableDatabase();
            String query = "SELECT " + COLUMN_QUOTE +  ", MAX(_ID)  FROM " + TABLE_NAME ;
            Cursor cursor = db.rawQuery(query,null);
            cursor.moveToFirst();
            String lastQuote = cursor.getString(cursor.getColumnIndex(COLUMN_QUOTE));
            return lastQuote;
        }


        public void clearTable(){
//        Log.d(LOG_TAG,"MyDBHelper clearTable");
            SQLiteDatabase db = getWritableDatabase();
            db.delete(TABLE_NAME,null,null);
        }


    }
}
