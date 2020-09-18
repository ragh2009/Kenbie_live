package com.kenbie.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

public class TranslateDataSource {
    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    public TranslateDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Get titles from DB
    public ArrayList<String> getTitles(String values) {
        ArrayList<String> comments = new ArrayList<String>();

//        Cursor cursor = database.query(MySQLiteHelper.TABLE_TRANSLATE,
//                allColumns, null, null, null, null, null);
        Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_TRANSLATE + " where " + MySQLiteHelper.COLUMN_ID + "  In(" + values + ")", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            comments.add(cursor.getString(1));
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return comments;
    }

    // Save titles into DB
    public boolean saveTitles(String response) {
        try {
            if (response != null) {
                JSONObject jo = new JSONObject(response);
                if (jo.has("data")) {
                    Object rData = new JSONTokener(jo.getString("data")).nextValue();
                    JSONObject jo1 = null;
                    if (rData instanceof JSONArray) {
                        JSONArray mData = new JSONArray(jo.getString("data"));
                        jo1 = new JSONObject(mData.getString(0));
                    } else
                        jo1 = new JSONObject(jo.getString("data"));

                    if (jo1.has("en")) {
                        JSONArray ja = new JSONArray(jo1.getString("en"));
                        if (ja != null && ja.length() > 0) {
                            database.execSQL("delete FROM " + MySQLiteHelper.TABLE_TRANSLATE);

                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject jo2 = new JSONObject(ja.getString(i));
                                ContentValues cv = new ContentValues();
                                cv.put(MySQLiteHelper.COLUMN_ID, jo2.getInt("id"));
                                cv.put(MySQLiteHelper.COLUMN_SENTENCE, jo2.getString("title"));
                                database.insert(MySQLiteHelper.TABLE_TRANSLATE, null, cv);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}