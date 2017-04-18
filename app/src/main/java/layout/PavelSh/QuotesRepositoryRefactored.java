package layout.PavelSh;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.security.InvalidParameterException;

import layout.QuoteModel;
import layout.data.MonitorQuotes;

/**
 * Created by TechnoA on 17.04.2017.
 */

public class QuotesRepositoryRefactored {

    private static final String TABLE_NAME = "quotes3";
    private static final String COLUMN_QUOTE = "quote";
    public static final String _ID = BaseColumns._ID;
    private SQLite sqlite;
    private MonitorQuotes monitorQuotes;

    public QuotesRepositoryRefactored(Context context) {

        sqlite = new SQLite(context, TABLE_NAME);
        monitorQuotes = new MonitorQuotes(context);
    }

    public MonitorQuotes getMonitorQuotes() {
        return monitorQuotes;
    }

    private class SQLite extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "Quotes.db";
        private static final int DATABASE_VERSION = 3;
        private String tableName;

        public SQLite(Context context, String tableName) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

            TraceUtils.LogInfo("Create SQLiteHelper");
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

            createDatabase(sqLiteDatabase, tableName);

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

            createDatabase(sqLiteDatabase, tableName);

        }

        private void createDatabase(SQLiteDatabase db, String tableName) {

            TraceUtils.LogInfo("Drop Database.");
            db.execSQL(String.format("DROP TABLE IF EXISTS %1", TABLE_NAME));
            TraceUtils.LogInfo("Create Database.");
            String query = String.format("CREATE TABLE %1 (%2 INTEGER PRIMARY KEY AUTOINCREMENT, %3 TEXT NOT NULL);", TABLE_NAME, _ID, COLUMN_QUOTE);
            db.execSQL(query);

        }
    }

    public QuoteModel addQuote(String quote) throws InvalidParameterException {
        TraceUtils.LogInfo("SQLite addQuote");
        SQLiteDatabase db = sqlite.getWritableDatabase();

        boolean quoteIsExist = checkQuoteIsExist(quote);
        if(quoteIsExist)
            throw new InvalidParameterException("Quote already exists.");

        ContentValues values = new ContentValues();
        values.put(COLUMN_QUOTE, quote);
        long id = db.insert(TABLE_NAME, null, values);
        return new QuoteModel(quote, id);
    }

    public QuoteModel findQuoteByID(long id){

        return null;
    }

//    public void deleteQuote(int id){
//
//        SQLiteDatabase db = sqlite.getWritableDatabase();
//        String query = String.format("DELETE FROM %1 WHERE _id = %2", TABLE_NAME, id);
//        db.execSQL(query);
//    }

    private boolean checkQuoteIsExist(String quote){

        SQLiteDatabase db = sqlite.getReadableDatabase();

        String selection = null;
        String[] selectionArgs = null;
        String[] columns = null;

        columns = new String[] { COLUMN_QUOTE };
        selection = COLUMN_QUOTE +" = ?";
        selectionArgs = new String[] { quote };

        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null,null);
        return !cursor.isAfterLast();
    }

    public void deleteQuote(){
        TraceUtils.LogInfo("SQLite deleteQuote");
        String quoteForDelete = monitorQuotes.getCurrentQuote();
        nextQuote();
        SQLiteDatabase db = sqlite.getWritableDatabase();
        db.execSQL(" DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_QUOTE + "=\"" + quoteForDelete + "\";");
    }

    public void nextQuote(){
        TraceUtils.LogInfo("SQLite nextQuote");
        String currentQuote = monitorQuotes.getCurrentQuote();
        String nextQuote = getQuoteAfterCurrent(currentQuote);
        monitorQuotes.setCurrentQuote(nextQuote);
    }

    public String getQuoteAfterCurrent(String quote){
        TraceUtils.LogInfo("SQLite getQuoteAfterCurrent");

        String nextQuote = "Next quote";

        int countRows = getTableSize();

        int currentID = getCurrentID(quote);

        SQLiteDatabase db = sqlite.getWritableDatabase();

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

    public int getTableSize(){
        TraceUtils.LogInfo("SQLite getTableSize");
        SQLiteDatabase db = sqlite.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getCurrentID(String quote){
        TraceUtils.LogInfo("SQLite getCurrentID");

        int currentID = 1;
        SQLiteDatabase db = sqlite.getReadableDatabase();
        String query = " SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_QUOTE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{quote});
        int idColumnIndex = cursor.getColumnIndex(_ID);
        while (cursor.moveToNext()) {
            currentID = cursor.getInt(idColumnIndex);
        }
        return currentID;
    }

    public String getFirstQuote(){
        TraceUtils.LogInfo("SQLite getFirstQuote");
        SQLiteDatabase db = sqlite.getReadableDatabase();
        String query = "SELECT " + COLUMN_QUOTE +  ", MIN(_ID)  FROM " + TABLE_NAME ;
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        String firstQuote = cursor.getString(cursor.getColumnIndex(COLUMN_QUOTE));
        return firstQuote;
    }
    public String getLastQuote(){
        TraceUtils.LogInfo("SQLite getLastQuote");
        SQLiteDatabase db = sqlite.getReadableDatabase();
        String query = "SELECT " + COLUMN_QUOTE +  ", MAX(_ID)  FROM " + TABLE_NAME ;
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        String lastQuote = cursor.getString(cursor.getColumnIndex(COLUMN_QUOTE));
        return lastQuote;
    }

    public void clearTable(){
        TraceUtils.LogInfo("SQLite clearTable");
        SQLiteDatabase db = sqlite.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
        sqlite.close();
    }

    public void prevQuote(){
        TraceUtils.LogInfo("SQLite prevQuote");
        String currentQuote = monitorQuotes.getCurrentQuote();
        String prevQuote = getQuoteBeforeCurrent(currentQuote);
        monitorQuotes.setCurrentQuote(prevQuote);
    }
    public String getQuoteBeforeCurrent(String quote){
        TraceUtils.LogInfo("SQLite getQuoteBeforeCurrent");

        String prevQuote = "Prev quote";

        int countRows = getTableSize();

        int currentID = getCurrentID(quote);

        SQLiteDatabase db = sqlite.getWritableDatabase();

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


}
