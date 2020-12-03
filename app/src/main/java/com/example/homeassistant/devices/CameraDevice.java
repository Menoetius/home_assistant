package com.example.homeassistant.devices;

import com.example.homeassistant.model.DeviceModel;

public class CameraDevice extends DeviceModel {
    String image;
    String message;
    int timestamp;

    public CameraDevice(String id, String name, String type, String image, String message, int timestamp) {
        super(id, name, type);
        this.image = image;
        this.message = message;
        this.timestamp = timestamp;
    }

    public CameraDevice(String image, String message, int timestamp) {
        this.image = image;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "CameraDevice{" +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                "image='" + image + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
