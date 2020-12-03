package com.example.homeassistant.model;

public class Security {
    String id;
    String type;
    String name;
    String icon;
    Boolean active;

    public Security(String id, String type, String name, String icon, Boolean active) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.icon = icon;
        this.active = active;
    }

    public Security() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        return "Security{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", active=" + active +
                '}';
    }
}
