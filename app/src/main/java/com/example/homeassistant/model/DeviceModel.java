package com.example.homeassistant.model;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeviceModel {
    protected String id;
    protected String name;
    protected String type;
    protected String technology;

    public DeviceModel(String id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public DeviceModel(String id, String name, String type, String technology) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.technology = technology;
    }

    public DeviceModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public String getFeaturedValue(){
        return "";
    }

    public boolean isStateOn(){
        return false;
    }

    public String getStringValue(String name) {
        return "";
    }

    public Map<String,String> getSwitchMessage(boolean state) {
        Map<String,String> map=new HashMap<String,String>();
        long timestamp = System.currentTimeMillis();
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", "set");
            obj.put("timestamp", Long.toString(timestamp));
            obj.put("value", state ? "on" : "off");
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
        map.put("message", obj.toString());
        map.put("topicIn", "BRQ/BUT/" + id + "/switch/in");
        return map;
    }

    public Map<String,String> getSetMessage(String valueType, int value) {
        return null;
    }

    public boolean processMessage(String topic, String message, String actualDevice) {
        return false;
    }

    public ArrayList<Map<String, String>> getAttributesListForRv() {
        return null;
    }
}
