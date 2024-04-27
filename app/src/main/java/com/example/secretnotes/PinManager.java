package com.example.secretnotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PinManager {
    private DatabaseHelper dbHelper;

    public PinManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void storePin(String pin) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(dbHelper.getColumnPin(), pin);
        db.insert(dbHelper.getTableName(), null, values);
        db.close();
    }

        public String getPin() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(dbHelper.getTableName(), new String[]{dbHelper.getColumnPin()}, null, null, null, null, null);
        String pin = null;
        int columnIndex = cursor.getColumnIndex(dbHelper.getColumnPin());
        if (columnIndex != -1 && cursor.moveToFirst()) {
            pin = cursor.getString(columnIndex);
        }
        cursor.close();
        db.close();
        return pin;
    }
}

