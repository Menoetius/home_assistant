package com.example.homeassistant.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class RoomModel {
    private int id;
    private String roomId;
    private String name;
    private String backgroundImage;

    public RoomModel() {
    }

    public RoomModel(int id, String roomId, String name, Bitmap backgroundImage) {
        this.id = id;
        this.roomId = roomId;
        this.name = name;
        this.backgroundImage = bitmapToString(backgroundImage);
    }

    public RoomModel(int id, String roomId, String name, String backgroundImage) {
        this.id = id;
        this.roomId = roomId;
        this.name = name;
        this.backgroundImage = backgroundImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public Bitmap getBackgroundImageAsBitmap() {
        return stringToBitmap(backgroundImage);
    }

    public void setBackgroundImage(Bitmap backgroundImage) {
        this.backgroundImage = bitmapToString(backgroundImage);
    }

    @Override
    public String toString() {
        return "RoomModel{" +
                "id=" + id +
                ", roomId='" + roomId + '\'' +
                ", name='" + name + '\'' +
                ", backgroundImage='" + backgroundImage + '\'' +
                '}';
    }

    private static String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayStream);
        byte[] byteArray = byteArrayStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private static Bitmap stringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            return null;
        }
    }
}
