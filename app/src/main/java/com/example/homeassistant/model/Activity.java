package com.example.homeassistant.model;

public class Activity implements Comparable<Activity>{
    double timestamp;
    String message;

    public Activity() {
    }

    public Activity(double timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "timestamp=" + timestamp +
                ", message='" + message + '\'' +
                '}';
    }

    @Override
    public int compareTo(Activity o) {
        return (int) Math.round((this.timestamp - o.getTimestamp()) * 1000000);
    }
}
