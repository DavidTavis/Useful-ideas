package layout.PavelSh;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.security.InvalidParameterException;

import layout.models.QuoteModel;
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

    // TODO: Убить.
    private MonitorQuotesRefactored monitorQuotesRefactored;

    public QuotesRepositoryRefactored(Context context) {

        sqlite = new SQLite(context, TABLE_NAME);
        monitorQuotes = new MonitorQuotes(context);
        // TODO: Это здесь не нужно.
        monitorQuotesRefactored = new MonitorQuotesRefactored(context);

    }

    // TODO: Реализовать доступ к этому объектоу ч-з контекст.
    public MonitorQuotes getMonitorQuotes() {
        return monitorQuotes;
    }

    private class SQLite extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "Quotes.db";
        private static final int DATABASE_VERSION = 3;
        private String tableName;  // поле не используется. Удалить?

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
            db.execSQL(String.format("DROP TABLE IF EXISTS %1;", TABLE_NAME));
            TraceUtils.LogInfo("Create Database.");
            String query = String.format("CREATE TABLE %1 (%2 INTEGER PRIMARY KEY AUTOINCREMENT, %3 TEXT NOT NULL);", TABLE_NAME, _ID, COLUMN_QUOTE);
            db.execSQL(query);

        }
    }

    public QuoteModel addQuote(String quote) throws InvalidParameterException {
        TraceUtils.LogInfo("SQLite addQuote");
        SQLiteDatabase db = sqlite.getWritableDatabase();

        boolean quoteIsExists = isQuoteExists(quote);
        if(quoteIsExists)
            throw new InvalidParameterException("Quote already exists.");

        ContentValues values = new ContentValues();
        values.put(COLUMN_QUOTE, quote);
        long id = db.insert(TABLE_NAME, null, values);
        return new QuoteModel(quote, id);
    }

    public QuoteModel findQuoteByID(long id){

        Cursor cursor = getCursor(id, " = ?");
        return getQuoteModelByCursor(cursor);

    }

    public QuoteModel getNextQuote(long currentQuoteId) {

        Cursor cursor = getCursor(currentQuoteId, " > ?");

        if(cursor.isAfterLast()){
            return getFirstQuote();
        }

        return getQuoteModelByCursor(cursor);
    }

    public QuoteModel getPrevQuote(long currentQuoteId) {

        Cursor cursor = getCursor(currentQuoteId, " < ?");

        if(cursor.isAfterLast()){
            return getLastQuote();
        }

        return getQuoteModelByCursor(cursor);

    }

    public void deleteQuote(long id){

        SQLiteDatabase db = sqlite.getWritableDatabase();
        String query = String.format("DELETE FROM %1 WHERE _id = %2;", TABLE_NAME, id);
        db.execSQL(query);
    }

    public QuoteModel getFirstQuote(){

        TraceUtils.LogInfo("SQLite getFirstQuote");
        SQLiteDatabase db = sqlite.getReadableDatabase();
        String query = "SELECT " + COLUMN_QUOTE +  ", MIN(_ID)  FROM " + TABLE_NAME ;
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        return new QuoteModel(cursor.getString(cursor.getColumnIndex(COLUMN_QUOTE)),cursor.getLong(cursor.getColumnIndex(_ID)));
    }

    public void clearTable(){
        TraceUtils.LogInfo("SQLite clearTable");
        SQLiteDatabase db = sqlite.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
        sqlite.close();
    }

    public QuoteModel getLastQuote(){

        TraceUtils.LogInfo("SQLite getLastQuote");
        SQLiteDatabase db = sqlite.getReadableDatabase();
        String query = "SELECT " + COLUMN_QUOTE +  ", MAX(_ID)  FROM " + TABLE_NAME ;
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();

        return new QuoteModel(cursor.getString(cursor.getColumnIndex(COLUMN_QUOTE)),cursor.getLong(cursor.getColumnIndex(_ID)));
    }

    public int getTableSize(){
        TraceUtils.LogInfo("SQLite getTableSize");
        SQLiteDatabase db = sqlite.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    private boolean isQuoteExists(String quote){

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

    private Cursor getCursor(long currentID, String condition){

        SQLiteDatabase db = sqlite.getWritableDatabase();

        String selection = null;
        String[] selectionArgs = null;
        String[] columns = null;

        columns = new String[] { COLUMN_QUOTE };
        selection = _ID + condition;
        selectionArgs = new String[] { String.valueOf(currentID) };

        return db.query(TABLE_NAME, columns, selection, selectionArgs, null, null,null);
    }

    private QuoteModel getQuoteModelByCursor(Cursor cursor){

        // TODO: Фигня какая-то написана. Переделай.
        while (cursor.moveToNext()) {
            return new QuoteModel(cursor.getString(cursor.getColumnIndex(COLUMN_QUOTE)),cursor.getLong(cursor.getColumnIndex(_ID)));
        }
        return null;
    }
}
