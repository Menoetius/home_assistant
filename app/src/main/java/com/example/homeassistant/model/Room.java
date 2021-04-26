package com.example.homeassistant.model;

import com.example.homeassistant.devices.CameraDevice;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Room {
    private String id;
    private String name;
    private CameraDevice camera;
    private TreeMap<String, DeviceModel> devices;

    public Room(String id, String name, TreeMap<String, DeviceModel> devices) {
        this.id = id;
        this.devices = devices;
        this.name = name;
    }

    public Room() {
    }

    public CameraDevice getCamera() {
        return camera;
    }

    public void setCamera(CameraDevice camera) {
        this.camera = camera;
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

    public ArrayList<DeviceModel> getDevices() {
        return new ArrayList<>(devices.values());
    }

    public void setDevices(TreeMap<String, DeviceModel> devices) {
        this.devices = devices;
    }

    public DeviceModel getDeviceById(String key) {
        return devices.get(key);
    }

    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", devices=" + devices +
                '}';
    }
}
