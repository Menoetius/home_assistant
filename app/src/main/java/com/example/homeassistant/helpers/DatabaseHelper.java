package com.example.homeassistant.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.homeassistant.MainActivity;
import com.example.homeassistant.model.ConnectionModel;
import com.example.homeassistant.model.MainViewModel;

public class DatabaseHelper extends SQLiteOpenHelper {
    //Connection
    public static final String CONNECTION_TABLE = "CONNECTION_TABLE";
    public static final String ID = "ID";
    public static final String DEVICE_ID = "DEVICEID";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String URL = "URL";
    public static final String PROTOCOL = "PROTOCOL";
    public static final String PORT = "PORT";

    public DatabaseHelper(@Nullable MainActivity context) {
        super(context, "app.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + CONNECTION_TABLE + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DEVICE_ID + " TEXT, " + USERNAME + " TEXT, " + PASSWORD + " TEXT, " + URL + " TEXT, " + PROTOCOL + " TEXT, " + PORT + " TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addConnection(ConnectionModel customerModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DEVICE_ID, customerModel.getDeviceId());
        values.put(USERNAME, customerModel.getDeviceId());
        values.put(PASSWORD, customerModel.getDeviceId());
        values.put(URL, customerModel.getDeviceId());
        values.put(PROTOCOL, customerModel.getDeviceId());
        values.put(PORT, customerModel.getDeviceId());

        long insert = db.insert(CONNECTION_TABLE, null, values);

        return insert != -1;
    }
}
