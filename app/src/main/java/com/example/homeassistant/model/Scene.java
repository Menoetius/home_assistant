package com.example.homeassistant.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Scene {
    String id;
    String name;
    String icon;
    Boolean active;

    public Scene(String id, String name, String icon, Boolean active) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.active = active;
    }

    public Scene() {
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Scene{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", active=" + active +
                '}';
    }

    public String getSetMessage() {
        long timestamp = System.currentTimeMillis();
        JSONObject obj = new JSONObject();
        JSONObject value = new JSONObject();
        try {
            value.put("scene_id", id);
            obj.put("type", "set");
            obj.put("timestamp", Long.toString(timestamp));
            obj.put("priority_level", 2);
            obj.put("value", value);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

}
