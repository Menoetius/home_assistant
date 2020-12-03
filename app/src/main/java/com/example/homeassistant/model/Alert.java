package com.example.homeassistant.model;

public class Alert {
    String name;
    String message;
    String status;
    Double timestamp;

    public Alert(String name, String message, String status, Double timestamp) {
        this.name = name;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }

    public Alert() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Double timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "name='" + name + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
