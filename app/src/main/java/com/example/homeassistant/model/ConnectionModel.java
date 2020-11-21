package com.example.homeassistant.model;

public class ConnectionModel {
    private int id;
    private String deviceId;
    private String userName;
    private String password;
    private int port;
    private String url;
    private String protocol;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public ConnectionModel(int id, String deviceId) {
        this.id = id;
        this.deviceId = deviceId;
    }

    public ConnectionModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "ConnectionModel{" +
                "id=" + id +
                ", deviceId='" + deviceId + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", port=" + port +
                ", url='" + url + '\'' +
                ", protocol='" + protocol + '\'' +
                '}';
    }
}
