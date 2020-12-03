package com.example.homeassistant.model;

import com.example.homeassistant.devices.CameraDevice;
import com.example.homeassistant.model.Room;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class BrokerData {
    private int timestamp;
    private String type;
    private TreeMap<String, Room> rooms;
    private TreeMap<String, Scene> scenes;
    private TreeMap<String, Alert> alerts;
//    private Room[] activities;
    private TreeMap<String, Security> security;

    public BrokerData() {
    }

    public BrokerData(int timestamp, String type, TreeMap<String, Room> rooms, TreeMap<String, Scene> scenes, TreeMap<String, Alert> alerts, TreeMap<String, Security> security) {
        this.timestamp = timestamp;
        this.type = type;
        this.rooms = rooms;
        this.scenes = scenes;
        this.alerts = alerts;
        this.security = security;
    }

    public TreeMap<String, Room> getRooms() {
        return rooms;
    }

    @Override
    public String toString() {
        return "BrokerData{" +
                "timestamp=" + timestamp +
                ", type='" + type + '\'' +
                ", rooms=" + rooms +
                '}';
    }

    public ArrayList<CameraDevice> getRoomCameras() {
        ArrayList<CameraDevice> cameras = new ArrayList<CameraDevice>();
        for(Map.Entry<String,Room> entry : rooms.entrySet()) {
            Room room = entry.getValue();
            cameras.add(room.getRoomCamera());
        }

        return cameras;
    }

    public ArrayList<Scene> getScenes() {
        return new ArrayList<>(scenes.values());
    }
}
