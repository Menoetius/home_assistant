package com.example.homeassistant.model;

import com.example.homeassistant.devices.CameraDevice;

import java.util.Map;
import java.util.TreeMap;

public class Room {
    private String id;
    private TreeMap<String, DeviceModel> devices;

    public Room(String id, TreeMap<String, DeviceModel> devices) {
        this.id = id;
        this.devices = devices;
    }

    public Room() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TreeMap<String, DeviceModel> getDevices() {
        return devices;
    }

    public void setDevices(TreeMap<String, DeviceModel> devices) {
        this.devices = devices;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", devices=" + devices +
                '}';
    }

    public CameraDevice getRoomCamera() {
        for(Map.Entry<String,DeviceModel> entry : devices.entrySet()) {
            if (entry.getValue().type.equals("camera")) {
                CameraDevice camera = (CameraDevice) entry.getValue();
                return camera;
            }
        }
        return null;
    }
}
