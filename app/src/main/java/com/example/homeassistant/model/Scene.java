package com.example.homeassistant.model;

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
}
