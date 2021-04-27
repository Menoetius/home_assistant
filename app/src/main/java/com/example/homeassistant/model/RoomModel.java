package com.example.homeassistant.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class RoomModel {
    private int id;
    private String room_id;
    private String name;
    private String backgroundImage;

    public RoomModel() {
    }

    public RoomModel(int id, String room_id, String name, Bitmap backgroundImage) {
        this.id = id;
        this.room_id = room_id;
        this.name = name;
        this.backgroundImage = bitmapToString(backgroundImage);
    }

    public RoomModel(int id, String room_id, String name, String backgroundImage) {
        this.id = id;
        this.room_id = room_id;
        this.name = name;
        this.backgroundImage = backgroundImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
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
                ", room_id='" + room_id + '\'' +
                ", name='" + name + '\'' +
                ", backgroundImage='" + backgroundImage + '\'' +
                '}';
    }

    private static String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private static Bitmap stringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static Bitmap resizeBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = 1080 / width;
        float scaleHeight = 1920 / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, width, height, matrix, false);
        bitmap.recycle();
        return resizedBitmap;
    }
}
