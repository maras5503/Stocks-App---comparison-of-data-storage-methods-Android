package com.example.stocksApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "testDB";
    private static final String TABLE_STOCKQUOTES = "stockquotes";
    private static final String KEY_ID = "id";
    private static final String KEY_SYMBOL = "symbol";
    private static final String KEY_PRICE = "price";
    private static final String KEY_CHANGE = "change";
    public DatabaseHandler(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override public void onCreate(SQLiteDatabase db) {
        String CREATE_STOCKQUOTES_TABLE = "CREATE TABLE " + TABLE_STOCKQUOTES + "(" + KEY_ID + "INTEGER PRIMARY KEY," + KEY_SYMBOL + " TEXT," + KEY_PRICE + " TEXT," + KEY_CHANGE + " TEXT " + ")";
        db.execSQL(CREATE_STOCKQUOTES_TABLE);
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCKQUOTES); onCreate(db);
        onCreate(db);
    }
    void addStockQuote(Stockquotes stockquote){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_SYMBOL, stockquote.get_symbol());
        contentValues.put(KEY_PRICE, stockquote.get_price());
        contentValues.put(KEY_CHANGE, stockquote.get_change());
        db.insert(TABLE_STOCKQUOTES, null, contentValues);
        db.close();
    }
    public List<Stockquotes> getAllStockQuotes() {
        List<Stockquotes> stockquotesList = new ArrayList<Stockquotes>();
        String selectQuery = "SELECT  * FROM " + TABLE_STOCKQUOTES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Stockquotes stockquote = new Stockquotes();
                stockquote.set_symbol(cursor.getString(1));
                stockquote.set_price(cursor.getString(2));
                stockquote.set_change(cursor.getString(3));
                stockquotesList.add(stockquote);
            } while (cursor.moveToNext());
        }
        return stockquotesList;
    }
    public void deleteAllRows(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STOCKQUOTES,null,null);
    }
    void drop(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCKQUOTES);
    }
}
