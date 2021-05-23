package com.example.homeassistant.devices;

import com.example.homeassistant.model.DeviceModel;
import com.example.homeassistant.repositiories.DevicesRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CameraDevice extends DeviceModel {

    private static final String TAG = DevicesRepository.class.getSimpleName();

    private String image;
    private String message;
    private int timestamp;

    public CameraDevice(String id, String name, String type, String image, String message, int timestamp) {
        super(id, name, type);
        this.image = image;
        this.message = message;
        this.timestamp = timestamp;
    }

    public CameraDevice(String id, String name, String type, String technology, String image, String message, int timestamp) {
        super(id, name, type, technology);
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
                ", image='" + image + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public Map<String,String> getStream() {
        Map<String,String> map=new HashMap<String,String>();
        long timestamp = System.currentTimeMillis();
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", "stream");
            obj.put("timestamp", Long.toString(timestamp));
            obj.put("value", "GET_STREAM");
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
        map.put("message", obj.toString());
        map.put("topicIn", "BRQ/BUT/" + id + "/stream/in");
        map.put("topicOut", "BRQ/BUT/" + id + "/stream/out");
        return map;
    }
}
