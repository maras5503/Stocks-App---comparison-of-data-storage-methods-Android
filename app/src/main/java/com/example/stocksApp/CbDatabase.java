package com.example.stocksApp;

import android.content.Context;

import com.couchbase.lite.*;

import java.util.ArrayList;
import java.util.List;

public class CbDatabase {
    Database database = null;

    public CbDatabase(Context context){
        CouchbaseLite.init(context);
        DatabaseConfiguration cfg = new DatabaseConfiguration();
        try {
            database = new Database(  "mydb", cfg);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }
    public void createDocument(Quote quote){
        MutableDocument mutableDoc =
                new MutableDocument().setString("symbol", quote.symbol)
                        .setString("price", quote.price)
                            .setString("change", quote.change);
        try {
            database.save(mutableDoc);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }
    public List<Quote> readAllDocuments(){
        try {
            ResultSet rs =
                    QueryBuilder.select(SelectResult.all())
                            .from(DataSource.database(database))
                            .execute();
            List<Quote> quotes = new ArrayList<>();
            for (Result result : rs.allResults()) {
                String symbol = result.getDictionary(0).getString("symbol");
                String price = result.getDictionary(0).getString("price");
                String change = result.getDictionary(0).getString("change");
                quotes.add(new Quote(symbol,change,price));
            }
            return quotes;
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void deleteAllDocuments(){
        try {
            ResultSet rs =
                    QueryBuilder.select(SelectResult.expression(Meta.id),
                            SelectResult.all())
                            .from(DataSource.database(database))
                            .execute();
            for (Result result : rs.allResults()){
                String id = result.getString(0);

                Document document = database.getDocument(id);

                if(document != null)
                {
                    database.delete(document);
                }
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }
}
