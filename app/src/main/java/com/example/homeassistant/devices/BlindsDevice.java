package com.example.homeassistant.devices;

import android.content.res.Resources;
import android.util.Log;

import com.example.homeassistant.model.DeviceModel;
import com.example.homeassistant.repositiories.DevicesRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BlindsDevice extends DeviceModel {

    private static final String TAG = DevicesRepository.class.getSimpleName();

    private int position;

    public BlindsDevice(String id, String name, String type, int position) {
        super(id, name, type);
        this.position = position;
    }

    public BlindsDevice(String id, String name, String type, String technology, int position) {
        super(id, name, type, technology);
        this.position = position;
    }

    public BlindsDevice(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "BlindsDevice{" +
                "position='" + position + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public String getFeaturedValue() {
        if (position == 0) {
            return "Down";//@todo resource Resources.getSystem().getString(android.R.string.blinds_down);
        } else if (position == 100) {
            return "Up";//@todo resource Resources.getSystem().getString(android.R.string.blinds_up);
        }
        return position + "%";
    }

    @Override
    public boolean isStateOn() {
        return position != 100;
    }

    @Override
    public Map<String, String> getSwitchMessage(boolean state) {
        return getSetMessage("position", position != 100 ? 100 : 0);
    }

    @Override
    public Map<String, String> getSetMessage(String valueType, int position) {
        Map<String,String> map=new HashMap<String,String>();
        long timestamp = System.currentTimeMillis();
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", "set");
            obj.put("timestamp", Long.toString(timestamp));
            obj.put("value", position);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
        map.put("message", obj.toString());
        map.put("topicIn", "BRQ/BUT/" + id + "/" + valueType + "/in");
        return map;
    }

    @Override
    public boolean processMessage(String topic, String message, String actualDevice) {
        boolean result = false;
        String attrName = topic.split("/")[3];

        switch(attrName) {
            case "position":
                processPositionMessage(message);
                if (actualDevice.equals("all")) {
                    result = true;
                }
                break;
        }

        if (actualDevice.equals(getId())) {
            result = true;
        }

        return result;
    }

    private void processPositionMessage(String message) {
        try {
            JSONObject obj = new JSONObject(message);
            String type = obj.getString("type");

            switch (type) {
                case "command_response":
                    int value = obj.getInt("value");
                    setPosition(value);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
