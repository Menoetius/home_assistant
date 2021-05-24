package com.example.homeassistant.model;

import com.example.homeassistant.devices.CameraDevice;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class BrokerData {
    private int timestamp;
    private String type;
    private TreeMap<String, Room> rooms;
    private TreeMap<String, Scene> scenes;
    private TreeMap<String, Alert> alerts;
    private ArrayList<Activity> activities;
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

    public void setActivities(ArrayList<Activity> activities) {
        this.activities = activities;
    }

    public ArrayList<Activity> getActivities() {
        return activities;
    }

    public void addActivity(Activity activity) {
        this.activities.add(0, activity);
    }

    public void addActivities(ArrayList<Activity> activities) {
        this.activities.addAll(activities);
    }

    public TreeMap<String, Room> getRooms() {
        return new TreeMap<>(rooms);
    }

    public ArrayList<Room> getRoomsList() {
        return new ArrayList<>(rooms.values());
    }

    public Room getRoomById(String roomId) {
        return rooms.get(roomId);
    }

    public ArrayList<Security> getSecurityList() {
        return new ArrayList<>(security.values());
    }

    public void setActiveSecurity(String id) {
        for(Map.Entry<String,Security> securityEntry : security.entrySet()) {
            securityEntry.getValue().setActive(securityEntry.getValue().getId().equals(id));
        }
    }

    public Alert getAlertByName(String name) {
        if(alerts.containsKey(name)) {
            return alerts.get(name);
        }

        return null;
    }

    public ArrayList<Alert> getAlertsList() {
        return new ArrayList<>(alerts.values());
    }

    public ArrayList<String> getRoomIds() {
        return new ArrayList<>(rooms.keySet());
    }

    @Override
    public String toString() {
        return "BrokerData{" +
                "timestamp=" + timestamp +
                ", type='" + type + '\'' +
                ", rooms=" + rooms +
                ", scenes=" + scenes +
                ", alerts=" + alerts +
                ", activities=" + activities +
                ", security=" + security +
                '}';
    }

    public ArrayList<CameraDevice> getRoomCameras() {
        ArrayList<CameraDevice> cameras = new ArrayList<>();
        for(Map.Entry<String,Room> entry : rooms.entrySet()) {
            Room room = entry.getValue();
            cameras.add(room.getCamera());
        }
        return cameras;
    }

    public ArrayList<Scene> getScenes() {
        return new ArrayList<>(scenes.values());
    }

    public void setActiveScene(String id) {
        for(Map.Entry<String,Scene> scene : scenes.entrySet()) {
            scene.getValue().setActive(scene.getKey().equals(id));
        }
    }

    public ArrayList<DeviceModel> getAllDevices() {
        ArrayList<DeviceModel> devices = new ArrayList<DeviceModel>();
        for(Map.Entry<String,Room> entry : rooms.entrySet()) {
            Room room = entry.getValue();

            devices.addAll(room.getDevices());
            devices.add(room.getCamera());
        }

        return devices;
    }

    public DeviceModel getDeviceById(String id) {
        for(Map.Entry<String,Room> entry : rooms.entrySet()) {
            Room room = entry.getValue();

            for(DeviceModel device : room.getDevices()) {
                if (device.getId().equals(id)) {
                    return device;
                }
            }

            if (room.getCamera().getId().equals(id)) {
                return room.getCamera();
            }
        }

        return null;
    }
}
