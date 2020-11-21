package com.example.homeassistant.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.homeassistant.model.ConnectionModel;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String CONNECTION_TABLE = "CONNECTION_TABLE";
    public static final String ID = "ID";
    public static final String DEVICE_ID = "DEVICE_" + ID;

    public DatabaseHelper(@Nullable Context context) {
        super(context, "app.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + CONNECTION_TABLE + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DEVICE_ID + " TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addToConnection(ConnectionModel customerModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DEVICE_ID, customerModel.getDeviceId());

        long insert = db.insert(CONNECTION_TABLE, null, values);

        return insert != -1;
    }
}
