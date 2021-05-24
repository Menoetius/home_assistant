package com.example.homeassistant.devices;

import android.util.Log;

import com.example.homeassistant.model.DeviceModel;
import com.example.homeassistant.repositiories.DevicesRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SwitchDevice extends DeviceModel {

    private static final String TAG = DevicesRepository.class.getSimpleName();

    private boolean hasTemperature;
    private double temperature;
    private boolean hasHumidity;
    private double humidity;
    private String state;

    public SwitchDevice(String id, String name, String type, String state, String technology, double temperature, double humidity) {
        super(id, name, type, technology);
        this.temperature = temperature;
        this.humidity = humidity;
        this.technology = technology;
        this.state = state;
    }

    public SwitchDevice(String id, String name, String type, String state, String technology, boolean hasTemperature, boolean hasHumidity) {
        super(id, name, type, technology);
        this.hasTemperature = hasTemperature;
        this.hasHumidity = hasHumidity;
        this.state = state;
    }

    public SwitchDevice(String id, String name, String type, String state, boolean hasTemperature, boolean hasHumidity) {
        super(id, name, type);
        this.hasTemperature = hasTemperature;
        this.hasHumidity = hasHumidity;
        this.state = state;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public ArrayList<Map<String, String>> getAttributesListForRv() {
        ArrayList<Map<String, String>> result = new ArrayList<>();

        if (hasTemperature()) {
            result.add(new HashMap<String, String>()
            {
                {
                    put("icon", "ic_temperature");
                    put("name", "temperature");
                    put("value", getTemperature() + " °C");
                }
            });
        }

        if (hasHumidity()) {
            result.add(new HashMap<String, String>()
            {
                {
                    put("icon", "ic_drop");
                    put("name", "humidity");
                    put("value", getHumidity() + " %");
                }
            });
        }

        return result;
    }

    @Override
    public String toString() {
        return "SwitchDevice{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                ", technology='" + technology + '\'' +
                ", state='" + state + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public String getFeaturedValue() {
        if (state.equals("on")) {
            return "On";
        } else {
            return "Off";
        }
    }

    @Override
    public boolean isStateOn() {
        return state.equals("on");
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
            case "switch":
                processSwitchMessage(message);
                if (actualDevice.equals("all")) {
                    result = true;
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

    private void processSwitchMessage(String message) {
        try {
            JSONObject obj = new JSONObject(message);
            String type = obj.getString("type");

            switch (type) {
                case "command_response":
                case "set":
                    String value = obj.getString("value");
                    setState(value);
                    break;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
