package com.example.stocksApp;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DataInstrumentedTest {
    static MainActivity mainActivity;
    SharedPreferences sharedPreferences;
    private String preffilename = "PREFERENCES";
    List<Quote> quotes=new ArrayList<>();
    File internalFile,externalFile;
    static DatabaseHandler db;
    static CbDatabase cbDatabase = null;
    @Rule
    public ActivityTestRule<MainActivity> mMainActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void init(){

        mainActivity=mMainActivityRule.getActivity();

        sharedPreferences=mainActivity.getSharedPreferences(preffilename,0);

        File path = mainActivity.getFilesDir();
        internalFile = new File(path,"stockquotes.json");

        externalFile = new File(mainActivity.getExternalFilesDir(Environment.DIRECTORY_DCIM),"stockquotes.json");

        db=new DatabaseHandler(mainActivity);

        cbDatabase=new CbDatabase(mainActivity);

    }
    @Test
    public void A_saveSharedPreferencesTest() throws JSONException {
        long start = System.currentTimeMillis();
        mainActivity.saveAllStockQuotesSharedPreferences(mainActivity.stockQuotes);
        long end2 = System.currentTimeMillis();
        Log.d("TEST SHARED PREFERENCES - ZAPIS:", String.valueOf((end2-start)));
        assertEquals(sharedPreferences.getAll().size(),1341);
    }

    @Test
    public void B_readSharedPreferencesTest() throws JSONException {
        long start = System.currentTimeMillis();
        quotes=mainActivity.readSharedPreferences();
        long end2 = System.currentTimeMillis();
        Log.d("TEST SHARED PREFERENCES - ODCZYT:", String.valueOf((end2-start)));
        assertEquals(quotes.size(),1341);
    }

    @Test
    public void C_saveInternalStorageTest() throws JSONException, IOException {
        long start = System.currentTimeMillis();
        mainActivity.saveAllStockQuotesInternalStorage(mainActivity.stockQuotes);
        long end2 = System.currentTimeMillis();
        Log.d("TEST INTERNAL STORAGE - ZAPIS:", String.valueOf((end2-start)));
        assertNotNull(internalFile);
    }

    @Test
    public void D_readInternalStorageTest() throws IOException, JSONException {
        long start = System.currentTimeMillis();
        quotes=mainActivity.readInternalStorage();
        long end2 = System.currentTimeMillis();
        Log.d("TEST INTERNAL STORAGE - ODCZYT:", String.valueOf((end2-start)));
        assertEquals(quotes.size(),1341);
    }

    @Test
    public void E_saveExternalStorageTest() throws IOException, JSONException {
        long start = System.currentTimeMillis();
        mainActivity.saveAllStockQuotesExternalStorage(mainActivity.stockQuotes);
        long end2 = System.currentTimeMillis();
        Log.d("TEST EXTERNAL STORAGE - ZAPIS:", String.valueOf((end2-start)));
        assertNotNull(externalFile);

    }

    @Test
    public void F_readExternalStorageTest() throws IOException, JSONException {
        long start = System.currentTimeMillis();
        quotes=mainActivity.readExternalStorage();
        long end2 = System.currentTimeMillis();
        Log.d("TEST EXTERNAL STORAGE - ODCZYT:", String.valueOf((end2-start)));
        assertEquals(quotes.size(),1341);
    }

    @Test
    public void G_saveSQLiteTest(){
        long start = System.currentTimeMillis();
        mainActivity.saveAllStockQuotesSQLite(mainActivity.stockQuotes);
        long end2 = System.currentTimeMillis();
        Log.d("TEST SQLITE - ZAPIS:", String.valueOf((end2-start)));
        assertEquals(db.getAllStockQuotes().size(),1341);
    }

    @Test
    public void H_readSQLiteTest(){
        long start = System.currentTimeMillis();
        quotes=mainActivity.readSQLite();
        long end2 = System.currentTimeMillis();
        Log.d("TEST SQLITE - ODCZYT:", String.valueOf((end2-start)));
        assertEquals(quotes.size(),1341);
    }

    @Test
    public void I_saveNoSQLTest(){
        long start = System.currentTimeMillis();
        mainActivity.saveAllStockQuotesNoSQL(mainActivity.stockQuotes);
        long end2 = System.currentTimeMillis();
        Log.d("TEST NOSQL - ZAPIS:", String.valueOf((end2-start)));
        assertEquals(mainActivity.cbDatabase.readAllDocuments().size(),1341);
    }

    @Test
    public void J_readNoSQLTest(){
        long start = System.currentTimeMillis();
        quotes=mainActivity.readNoSQL();
        long end2 = System.currentTimeMillis();
        Log.d("TEST NOSQL - ODCZYT:", String.valueOf((end2-start)));
        assertEquals(quotes.size(),1341);
    }

    @AfterClass
    public static void logout(){
        mainActivity.clearSharedPreferences();
        db.deleteAllRows();
        cbDatabase.deleteAllDocuments();
    }
}