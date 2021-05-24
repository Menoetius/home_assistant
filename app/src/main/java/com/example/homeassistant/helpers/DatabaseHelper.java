package com.example.homeassistant.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.homeassistant.model.RoomModel;
import com.example.homeassistant.views.MainActivity;
import com.example.homeassistant.model.ConnectionModel;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String ID = "ID";

    //Connection
    public static final String CONNECTION_TABLE = "CONNECTION_TABLE";
    public static final String DEVICE_ID = "DEVICEID";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String URL = "URL";
    public static final String PROTOCOL = "PROTOCOL";
    public static final String PORT = "PORT";
    //Rooms
    public static final String ROOM_TABLE = "ROOM_TABLE";
    public static final String ROOM_ID = "ROOM_ID";
    public static final String ROOM_NAME = "ROOM_NAME";
    public static final String BACKGROUND = "BACKGROUND_ID";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "app.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + CONNECTION_TABLE + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DEVICE_ID + " TEXT, " + USERNAME + " TEXT, " + PASSWORD + " TEXT, " + URL + " TEXT, " + PROTOCOL + " TEXT, " + PORT + " INTEGER)";

        db.execSQL(createTable);

        createTable = "CREATE TABLE " + ROOM_TABLE + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ROOM_ID + " TEXT, " + ROOM_NAME + " TEXT, " + BACKGROUND + " TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addConnection(ConnectionModel customerModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DEVICE_ID, customerModel.getDeviceId());
        values.put(USERNAME, customerModel.getUserName());
        values.put(PASSWORD, customerModel.getPassword());
        values.put(URL, customerModel.getUrl());
        values.put(PROTOCOL, customerModel.getProtocol());
        values.put(PORT, customerModel.getPort());

        long insert = db.insert(CONNECTION_TABLE, null, values);

        return insert != -1;
    }

    public boolean addRoom(RoomModel roomModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ROOM_ID, roomModel.getRoomId());
        values.put(ROOM_NAME, roomModel.getName());
        values.put(BACKGROUND, roomModel.getBackgroundImage());

        long insert = db.insert(ROOM_TABLE, null, values);

        return insert != -1;
    }

    public boolean updateRoom(RoomModel roomModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ROOM_ID, roomModel.getRoomId());
        values.put(ROOM_NAME, roomModel.getName());
        values.put(BACKGROUND, roomModel.getBackgroundImage());

        long insert = db.update(ROOM_TABLE, values, ROOM_ID + " = ?", new String[] {roomModel.getRoomId()});

        return insert != -1;
    }

    public RoomModel getRoomModelByRoomId(String roomId) {
        String query = "SELECT * FROM " + ROOM_TABLE + " WHERE " + ROOM_ID + " = ? LIMIT 1";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, new String[] {roomId});

        if (cursor.moveToFirst()) {
            return new RoomModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
        } else {
            return null;
        }
    }

    public ConnectionModel getLast() {
        String query = "SELECT * FROM " + CONNECTION_TABLE + " ORDER BY " + ID + " DESC LIMIT 1";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            return new ConnectionModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(6),cursor.getString(4),cursor.getString(5));
        } else {
            return null;
        }
    }
}
