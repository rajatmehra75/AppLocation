package com.rajat.applocation.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rajat.applocation.data.History;

import java.util.ArrayList;
import java.util.List;

public class DB extends SQLiteOpenHelper {
    static DB instance = null;
    private String TABLE_PARK = "park_table";

    public DB(Context context) {
        super(context, "location.db", null, 1);
        // TODO Auto-generated constructor stub
    }

    public static DB getInstance(Context context) {
        if (instance == null) {
            instance = new DB(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PARK + "(id INTEGER PRIMARY KEY,latitude REAL,longitude REAL,name TEXT, is_uploaded TEXT DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    public void insertData(double lat, double lon, String name) {
        ContentValues cv = new ContentValues();
        cv.put("latitude", lat);
        cv.put("longitude", lon);
        cv.put("name", name);
        getWritableDatabase().insert(TABLE_PARK, null, cv);
    }

    public List<History> getHistory() {
        List<History> data = new ArrayList<History>();
        Cursor cursor = getReadableDatabase().rawQuery("select * from " + TABLE_PARK, new String[]{});
        while (cursor.moveToNext()) {
            History history = new History();
            history.setId(cursor.getInt(0));//to set id id is on 0th postn
            history.setName(cursor.getString(3));
            history.setLatitude(cursor.getDouble(1));
            history.setLongitude(cursor.getDouble(2));
            data.add(history);
        }
        return data;
    }

    public List<History> getHistoryToUpload() {
        List<History> data = new ArrayList<History>();
        Cursor cursor = getReadableDatabase().rawQuery("select * from " + TABLE_PARK + " where is_uploaded=?", new String[]{"0"});
        while (cursor.moveToNext()) {
            History history = new History();
            history.setId(cursor.getInt(0));
            history.setName(cursor.getString(3));
            history.setLatitude(cursor.getDouble(1));
            history.setLongitude(cursor.getDouble(2));
            data.add(history);
        }
        return data;
    }

    public void updateUploadStatus(int id, String status) {
        ContentValues cv = new ContentValues();
        cv.put("is_uploaded", status);
        getWritableDatabase().update(TABLE_PARK, cv, "id=?", new String[]{"" + id});
    }

    public void deleteHistory(int historyId) {
        getWritableDatabase().delete(TABLE_PARK, "id=?", new String[]{"" + historyId});
    }

}
