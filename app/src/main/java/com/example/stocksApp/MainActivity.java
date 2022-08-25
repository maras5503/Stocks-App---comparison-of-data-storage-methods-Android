package com.example.stocksApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TableLayout tableLayout;
    String url = "https://m.bankier.pl/json/get_instruments?limit=500&page=1&type=gpw-akcje";
    String response;
    private SharedPreferences sharedPreferences;
    private String preffilename = "PREFERENCES";
    ProgressDialog dialog;
    List<Quote> stockQuotes;
    String storage = "Shared Preferences";
    private Context context = this;
    CbDatabase cbDatabase = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tableLayout = (TableLayout) findViewById(R.id.mytable);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Ładowanie....");
        dialog.show();

        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                try {
                    response = string;
                    stockQuotes = parseJsonDataMod(response);
                    makeTable(stockQuotes);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Some error occurred!!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(MainActivity.this);
        rQueue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.refresh:
            DatabaseHandler db = new DatabaseHandler(this);
            db.deleteAllRows();
            CbDatabase cbDatabase = new CbDatabase(context);
            cbDatabase.deleteAllDocuments();
            finish();
            startActivity(getIntent());
            return(true);
        case R.id.save:
            try {
                stockQuotes = parseJsonDataMod(response);
                switch (storage){
                    case "Shared Preferences":
                        clearSharedPreferences();
                        saveAllStockQuotesSharedPreferences(stockQuotes);
                        break;
                    case "Internal Storage":
                        saveAllStockQuotesInternalStorage(stockQuotes);
                        break;
                    case "External Storage":
                        saveAllStockQuotesExternalStorage(stockQuotes);
                        break;
                    case "SQLite":
                        saveAllStockQuotesSQLite(stockQuotes);
                        break;
                    case "NoSQL":
                        saveAllStockQuotesNoSQL(stockQuotes);
                        break;
                    default:

                        break;
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(MainActivity.this,"Zapis zakończony", Toast.LENGTH_LONG).show();
            return(true);
        case R.id.load:
            try {
                switch (storage){
                    case "Shared Preferences":
                        stockQuotes = readSharedPreferences();
                        break;
                    case "Internal Storage":
                        stockQuotes = readInternalStorage();
                        break;
                    case "External Storage":
                        stockQuotes = readExternalStorage();
                        break;
                    case "SQLite":
                        stockQuotes = readSQLite();
                        break;
                    case "NoSQL":
                        stockQuotes = readNoSQL();
                        break;
                    default:

                        break;
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(MainActivity.this,"Odczyt zakończony", Toast.LENGTH_LONG).show();

            return(true);
        case R.id.data_storage_menu_item:
            showDialog();
            return(true);
        case R.id.exit:
            finish();
            return(true);
    }
        return(super.onOptionsItemSelected(item));
    }


    public List<Quote> parseJsonData(String jsonString) throws JSONException {
        JSONObject object = new JSONObject(jsonString);
        JSONArray jsonArray = object.getJSONArray("quotes");
        List<Quote> quotes = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String symbol = jsonObject.getString("symbol");
                String change = jsonObject.getString("chg_perc");
                String price = jsonObject.getString("last");
                quotes.add(new Quote(symbol, change, price));
            }
        dialog.dismiss();
        return quotes;
    }
    public List<Quote> parseJsonDataMod(String jsonString) throws JSONException {
        JSONObject object = new JSONObject(jsonString);
        JSONArray jsonArray = object.getJSONArray("quotes");
        List<Quote> quotes = new ArrayList<>();
        for (int j=0; j<3;j++) {
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String symbol = jsonObject.getString("symbol");
                String change = jsonObject.getString("chg_perc");
                String price = jsonObject.getString("last");
                quotes.add(new Quote(symbol, change, price));
            }
        }
        dialog.dismiss();
        return quotes;
    }
//--------------SHARED PREFERENCES------------------------------------------------------------------
    void saveAllStockQuotesSharedPreferences(List<Quote> quotes) throws JSONException {
        for (int i = 0; i < quotes.size(); i++) {
            sharedPreferences = getSharedPreferences(preffilename, 0);
            writeSharedPreferences(i, quotes.get(i));
        }
    }

    List<Quote> readSharedPreferences() throws JSONException {
        sharedPreferences = getSharedPreferences(preffilename, 0);
        List<Quote> quotes = new ArrayList<>();
        Map<String, ?> map = sharedPreferences.getAll();
        for (int i = 0; i < map.size(); i++) {

                JSONObject jsonObject = new JSONObject(sharedPreferences.getString(String.valueOf(i), ""));
                quotes.add(new Quote(jsonObject.getString("symbol"), jsonObject.getString("chg_perc"), jsonObject.getString("last")));

        }
        return quotes;
    }

    void writeSharedPreferences(int key, Quote quote) throws JSONException {
        sharedPreferences = getSharedPreferences(preffilename, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("symbol", quote.symbol);
        jsonObject.put("chg_perc", quote.change);
        jsonObject.put("last", quote.price);

        editor.putString(String.valueOf(key), jsonObject.toString());
        editor.commit();
    }

    void clearSharedPreferences(){
        sharedPreferences = getSharedPreferences(preffilename, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
    //----------------------INTERNAL STORAGE--------------------------------------------------------
    void saveAllStockQuotesInternalStorage(List<Quote> quotes) throws IOException, JSONException {
        File path = getFilesDir();
        File file = new File(path,"stockquotes.json");

        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        JSONArray jsonArray = new JSONArray();

        try {
            for (int i=0; i<quotes.size();i++){
                jsonArray.put(getJsonQuote(quotes.get(i)));
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("quotes",jsonArray);
            bufferedWriter.write(jsonObject.toString());
        } finally {
            bufferedWriter.flush();
            bufferedWriter.close();
        }
    }
    List<Quote> readInternalStorage() throws JSONException, IOException {
        File path = getFilesDir();
        File file = new File(path,"stockquotes.json");

        byte [] content = new byte[(int)file.length()];
        FileInputStream stream = new FileInputStream(file);
        stream.read(content);
        String jsonString=new String(content);
        List<Quote> quotes=parseJsonData(jsonString);

        return quotes;
    }
    JSONObject getJsonQuote(Quote quote) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("symbol", quote.symbol);
        jsonObject.put("chg_perc", quote.change);
        jsonObject.put("last", quote.price);
        return jsonObject;
    }
//--------------------------EXTERNAL STORAGE--------------------------------------------------------
void saveAllStockQuotesExternalStorage(List<Quote> quotes) throws IOException, JSONException {
    if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
        return;
    }
    if ((!Environment.MEDIA_MOUNTED.equals( Environment.getExternalStorageState()))){
        return;
    }

    File file = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/stockquotes.json");
    FileWriter fileWriter = new FileWriter(file, true);
    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
    JSONArray jsonArray = new JSONArray();

    try {
        for (int i=0; i<quotes.size();i++){
            jsonArray.put(getJsonQuote(quotes.get(i)));
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("quotes",jsonArray);
        bufferedWriter.write(jsonObject.toString());
    } finally {
        bufferedWriter.flush();
        bufferedWriter.close();
    }
}
    List<Quote> readExternalStorage() throws JSONException, IOException {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            return null;
        }
        if ((!Environment.MEDIA_MOUNTED.equals( Environment.getExternalStorageState()))){
            return null;
        }
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM),"stockquotes.json");

        byte [] content = new byte[(int)file.length()];
        FileInputStream stream = new FileInputStream(file);
        stream.read(content);
        String jsonString=new String(content);
        List<Quote> quotes=parseJsonData(jsonString);

        return quotes;
    }

    //--------------------------------SQLite----------------------------------------------------------

    void saveAllStockQuotesSQLite(List<Quote> quotes){
        DatabaseHandler db = new DatabaseHandler(this);
        for (Quote q : quotes){
            db.addStockQuote(new Stockquotes(q.symbol, q.price, q.change));
        }
    }
    List<Quote> readSQLite(){
        DatabaseHandler db = new DatabaseHandler(this);
         List<Stockquotes> stockquotes = db.getAllStockQuotes();
        List<Quote> quotes = new ArrayList<>();
        for (Stockquotes s : stockquotes){
            quotes.add(new Quote(s.get_symbol(),s.get_price(),s.get_change()));
        }
        return quotes;
    }

    //------------------------------------------------NoSQL----------------------------------------------
    void saveAllStockQuotesNoSQL(List<Quote> quotes){
        cbDatabase = new CbDatabase(context);
        for (int i=0;i<quotes.size();i++){
            cbDatabase.createDocument(quotes.get(i));
        }
    }
    List<Quote> readNoSQL(){
        cbDatabase = new CbDatabase(context);
        List <Quote> quotes = cbDatabase.readAllDocuments();
        return quotes;
    }
    //---------------------------------------------FRONTEND----------------------------------------------
    void makeTable(List<Quote> quotes) {
        for (int i = 0; i < quotes.size(); i++) {
            Quote q = quotes.get(i);

            TableRow tableRow = new TableRow(this);
            TextView symbolTextView = new TextView(this);
            TextView changeTextView = new TextView(this);
            TextView priceTextView = new TextView(this);

            symbolTextView.setBackgroundColor(Color.DKGRAY);
            changeTextView.setBackgroundColor(Color.DKGRAY);
            priceTextView.setBackgroundColor(Color.DKGRAY);

            symbolTextView.setTextAppearance(this, android.R.style.TextAppearance_Medium);
            changeTextView.setTextAppearance(this, android.R.style.TextAppearance_Medium);
            priceTextView.setTextAppearance(this, android.R.style.TextAppearance_Medium);

            symbolTextView.setTextColor(Color.LTGRAY);
            priceTextView.setTextColor(Color.LTGRAY);

            symbolTextView.setText((CharSequence) q.symbol);

            if (q.change.startsWith("-")) {
                changeTextView.setTextColor(Color.RED);
                changeTextView.setText((CharSequence) q.change+"%");

            } else if (q.change.equals("0.00")) {
                changeTextView.setTextColor(Color.LTGRAY);
                changeTextView.setText((CharSequence) q.change+"%");
            } else {
                changeTextView.setText("+" + (CharSequence) q.change+"%");
                changeTextView.setTextColor(Color.GREEN);
            }
            changeTextView.setGravity(Gravity.CENTER_HORIZONTAL);

            priceTextView.setText((CharSequence) q.price.substring(0, q.price.length() - 2) + "zł");
            priceTextView.setGravity(Gravity.CENTER_HORIZONTAL);

            tableRow.addView(symbolTextView);
            tableRow.addView(changeTextView);
            tableRow.addView(priceTextView);
            tableLayout.addView(tableRow);
        }
    }
    public void showDialog(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog);
        RadioGroup radioGroup = dialog.findViewById(R.id.radiogroup);


        RadioButton sharedPreferencesRadioBtn = dialog.findViewById(R.id.radio_sharedpreferences);
        RadioButton internalStorageRadioBtn = dialog.findViewById(R.id.radio_internalstorage);
        RadioButton externalStorageRadioBtn = dialog.findViewById(R.id.radio_externalstorage);
        RadioButton sqliteRadioBtn = dialog.findViewById(R.id.radio_SQLite);
        RadioButton nosqlRadioBtn = dialog.findViewById(R.id.radio_nosql);

        switch (storage){
            case "Shared Preferences":
                sharedPreferencesRadioBtn.setChecked(true);
                break;
            case "Internal Storage":
                internalStorageRadioBtn.setChecked(true);
                break;
            case "External Storage":
                externalStorageRadioBtn.setChecked(true);
                break;
            case "SQLite":
                sqliteRadioBtn.setChecked(true);
                break;
            case "NoSQL":
                nosqlRadioBtn.setChecked(true);
                break;
            default:
                sharedPreferencesRadioBtn.setChecked(true);
                break;
        }

        Button applyBtn = dialog.findViewById(R.id.applyBtn);

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton r = dialog.findViewById(selectedId);
                storage = (String) r.getText();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

}