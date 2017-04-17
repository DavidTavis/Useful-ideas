package layout.PavelSh;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.security.InvalidParameterException;

import layout.QuoteModel;

/**
 * Created by TechnoA on 17.04.2017.
 */

public class QuotesRepositoryRefactored {

    private static final String TABLE_NAME = "quotes3";
    private static final String COLUMN_QUOTE = "quote";
    private SQLite sqlite;

    public QuotesRepositoryRefactored(Context context) {

        sqlite = new SQLite(context, TABLE_NAME);
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
            String query = String.format("CREATE TABLE %1 (%2 INTEGER PRIMARY KEY AUTOINCREMENT, quote TEXT NOT NULL);", tableName, BaseColumns._ID );
            db.execSQL(query);
        }
    }

    public QuoteModel addQuote(String quote) throws InvalidParameterException {

        SQLiteDatabase db = sqlite.getWritableDatabase();

        boolean quoteIsExist = checkQuoteIsExist(quote);
        if(quoteIsExist)
            throw new InvalidParameterException("Qoute already exists.");

        ContentValues values = new ContentValues();
        values.put(COLUMN_QUOTE, quote);
        long id = db.insert(TABLE_NAME, null, values);
        return new QuoteModel(quote, id);
    }

    public QuoteModel findQuoteByID(long id){

        return null;
    }

    public void deleteQuote(int id){

        SQLiteDatabase db = sqlite.getWritableDatabase();
        String query = String.format("DELETE FROM %1 WHERE _id = %2", TABLE_NAME, id);
        db.execSQL(query);
    }

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
}
