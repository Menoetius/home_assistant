package com.example.homeassistant.devices;

import android.util.Log;

import com.example.homeassistant.model.DeviceModel;
import com.example.homeassistant.repositiories.DevicesRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SensorDevice extends DeviceModel {

    private static final String TAG = DevicesRepository.class.getSimpleName();

    private boolean hasTemperature;
    private double temperature;
    private boolean hasHumidity;
    private double humidity;

    public SensorDevice(String id, String name, String type, boolean hasTemperature, boolean hasHumidity) {
        super(id, name, type);
        this.hasTemperature = hasTemperature;
        this.hasHumidity = hasHumidity;
    }

    public SensorDevice(String id, String name, String type, String technology, boolean hasTemperature, boolean hasHumidity) {
        super(id, name, type, technology);
        this.hasTemperature = hasTemperature;
        this.hasHumidity = hasHumidity;
    }

    public boolean hasTemperature() {
        return hasTemperature;
    }

    public boolean hasHumidity() {
        return hasHumidity;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    @Override
    public String toString() {
        return "SensorDevice{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", technology='" + technology + '\'' +
                '}';
    }

    @Override
    public String getFeaturedValue() {
        return "";
    }

    @Override
    public boolean isStateOn() {
        return false;
    }

    @Override
    public boolean processMessage(String topic, String message, String actualDevice) {
        boolean result = false;
        String attrName = topic.split("/")[3];

        switch(attrName) {
            case "temperature":
                if (hasTemperature()) {
                    processTemperatureMessage(message);
                } else {
                    Log.w(TAG, "Warning! Trying to update not initialised value.");
                }
                break;
            case "humidity":
                if (hasHumidity()) {
                    processHumidityMessage(message);
                } else {
                    Log.w(TAG, "Warning! Trying to update not initialised value.");
                }
                break;
        }

        if (actualDevice.equals(getId())) {
            result = true;
        }

        return result;
    }

    private void processTemperatureMessage(String message) {
        try {
            JSONObject obj = new JSONObject(message);
            String type = obj.getString("type");

            if ("periodic_report".equals(type)) {
                JSONObject report = obj.getJSONObject("report");
                double value = report.getDouble("value");
                String unit = report.getString("unit");
                setTemperature(value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processHumidityMessage(String message) {
        try {
            JSONObject obj = new JSONObject(message);
            String type = obj.getString("type");

            if ("periodic_report".equals(type)) {
                JSONObject report = obj.getJSONObject("report");
                double value = report.getDouble("value");
                String unit = report.getString("unit");
                setHumidity(value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}