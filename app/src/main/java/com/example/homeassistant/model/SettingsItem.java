package com.example.homeassistant.model;

public class SettingsItem {
    private int icon;
    private String title;
    private String destinationFragment;

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDestinationFragment() {
        return destinationFragment;
    }

    public void setDestinationFragment(String destinationFragment) {
        this.destinationFragment = destinationFragment;
    }
}
