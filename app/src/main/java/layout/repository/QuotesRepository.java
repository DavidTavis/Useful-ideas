package layout.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.widget.Toast;

import java.security.InvalidParameterException;

import layout.models.QuoteModel;
import layout.utils.TraceUtils;

/**
 * Created by TechnoA on 17.04.2017.
 */

public class QuotesRepository {

    private static final String TABLE_NAME = "quotes3";
    private static final String COLUMN_QUOTE = "quote";
    public static final String _ID = BaseColumns._ID;
    private SQLite sqlite;

    public QuotesRepository(Context context) {

        sqlite = new SQLite(context, TABLE_NAME);

    }

    private class SQLite extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "Quotes.db";
        private static final int DATABASE_VERSION = 3;
        private String tableName;

        public SQLite(Context context, String tableName) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

            this.tableName = tableName;
            TraceUtils.logInfo("Create SQLiteHelper");
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

            TraceUtils.logInfo("Drop Database.");
            db.execSQL(String.format("DROP TABLE IF EXISTS %s;", TABLE_NAME));
            TraceUtils.logInfo("Create Database.");
            String query = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL);", TABLE_NAME, _ID, COLUMN_QUOTE);
            db.execSQL(query);

        }
    }

    public QuoteModel addQuote(String quote) throws InvalidParameterException {

        SQLiteDatabase db = sqlite.getWritableDatabase();

        boolean quoteIsExists = isQuoteExists(quote);
        if(quoteIsExists) {
            try {
                throw new InvalidParameterException("This idea already exists.");
            } catch (InvalidParameterException e) {
                TraceUtils.logInfo("This idea already exists");
            }
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_QUOTE, quote);
        long id = db.insert(TABLE_NAME, null, values);
        return new QuoteModel(quote, id);
    }

    public QuoteModel findQuoteByID(long id){

        Cursor cursor = getCursor(id, " = ?");
        return getQuoteModelByCursorFirst(cursor);

    }

    public QuoteModel getNextQuote(long currentQuoteId) {

        Cursor cursor = getCursor(currentQuoteId, " > ?");
        if(cursor.isAfterLast()){
            return getFirstQuote();
        }
        return getQuoteModelByCursorFirst(cursor);

    }

    public QuoteModel getPrevQuote(long currentQuoteId) {

        Cursor cursor = getCursor(currentQuoteId, " < ?");
        if(cursor.isAfterLast()){
            TraceUtils.logInfo("IsAfterLast");
            return getLastQuote();
        }
        return getQuoteModelByCursorLast(cursor);

    }

    public void deleteQuote(long id){

        SQLiteDatabase db = sqlite.getWritableDatabase();
        String query = String.format("DELETE FROM %s WHERE _id = %s;", TABLE_NAME, id);
        db.execSQL(query);

    }

    public QuoteModel getFirstQuote(){

        TraceUtils.logInfo("SQLite getFirstQuote");

        SQLiteDatabase db = sqlite.getReadableDatabase();
        String query = String.format("SELECT %s, MIN(%s) as _id  FROM %s", COLUMN_QUOTE, _ID, TABLE_NAME);;
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        return new QuoteModel(cursor.getString(cursor.getColumnIndex(COLUMN_QUOTE)),cursor.getLong(cursor.getColumnIndex(_ID)));
    }

    public void close(){
        TraceUtils.logInfo("SQLite clearTable");
        SQLiteDatabase db = sqlite.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
        sqlite.close();
    }

    public QuoteModel getLastQuote(){

        TraceUtils.logInfo("SQLite getLastQuote");
        SQLiteDatabase db = sqlite.getReadableDatabase();
        String query = String.format("SELECT %s, MAX(%s) as _id  FROM %s", COLUMN_QUOTE, _ID, TABLE_NAME);
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();

        return new QuoteModel(cursor.getString(cursor.getColumnIndex(COLUMN_QUOTE)),cursor.getLong(cursor.getColumnIndex(_ID)));
    }

    public int count(){
        SQLiteDatabase db = sqlite.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
//        TraceUtils.logInfo("TABLE SIZE = " + count);
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

        columns = new String[] { COLUMN_QUOTE, _ID };
        selection = _ID + condition;
        selectionArgs = new String[] { String.valueOf(currentID) };

        return db.query(TABLE_NAME, columns, selection, selectionArgs, null, null,null);
    }

    private QuoteModel getQuoteModelByCursorFirst(Cursor cursor){

        if (cursor.moveToNext()) {
            String quote = cursor.getString(cursor.getColumnIndex(COLUMN_QUOTE));
            long id = cursor.getLong(cursor.getColumnIndex(_ID));
            return new QuoteModel(quote,id);
        }
        return null;

    }

    private QuoteModel getQuoteModelByCursorLast(Cursor cursor){

        if (cursor.moveToLast()) {
            String quote = cursor.getString(cursor.getColumnIndex(COLUMN_QUOTE));
            long id = cursor.getLong(cursor.getColumnIndex(_ID));
            return new QuoteModel(quote,id);
        }
        return null;
    }

}
