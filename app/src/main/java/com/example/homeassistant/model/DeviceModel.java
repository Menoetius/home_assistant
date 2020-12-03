package com.example.homeassistant.model;

public class DeviceModel {
    protected String id;
    protected String name;
    protected String type;

    public DeviceModel(String id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
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

}
