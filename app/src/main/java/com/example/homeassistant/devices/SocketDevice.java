package com.example.homeassistant.devices;

import android.nfc.Tag;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeassistant.R;
import com.example.homeassistant.adapters.SocketParametersAdapter;
import com.example.homeassistant.model.DeviceModel;
import com.example.homeassistant.model.Scene;
import com.example.homeassistant.repositiories.DevicesRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SocketDevice extends DeviceModel {

    private static final String TAG = DevicesRepository.class.getSimpleName();

    private boolean hasVoltage;
    private double voltage;
    private boolean hasCurrent;
    private double current;
    private boolean hasConsumption;
    private double consumption;
    private boolean hasEnergy;
    private double energy;
    private boolean hasTemperature;
    private double temperature;
    private String state;

    public SocketDevice(String id, String name, String type, String state, boolean hasVoltage, boolean hasCurrent, boolean hasConsumption, boolean hasEnergy, boolean hasTemperature) {
        super(id, name, type);
        this.hasVoltage = hasVoltage;
        this.hasCurrent = hasCurrent;
        this.hasConsumption = hasConsumption;
        this.hasEnergy = hasEnergy;
        this.hasTemperature = hasTemperature;
        this.state = state;
    }

    public SocketDevice(String id, String name, String type, String technology, String state, boolean hasVoltage, boolean hasCurrent, boolean hasConsumption, boolean hasEnergy, boolean hasTemperature) {
        super(id, name, type, technology);
        this.hasVoltage = hasVoltage;
        this.hasCurrent = hasCurrent;
        this.hasConsumption = hasConsumption;
        this.hasEnergy = hasEnergy;
        this.hasTemperature = hasTemperature;
        this.state = state;
    }

    public boolean hasVoltage() {
        return hasVoltage;
    }

    public boolean hasCurrent() {
        return hasCurrent;
    }

    public boolean hasConsumption() {
        return hasConsumption;
    }

    public boolean hasTemperature() {
        return hasTemperature;
    }

    public boolean hasEnergy() {
        return hasEnergy;
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        if (this.hasVoltage) {
            this.voltage = voltage;
        }
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        if (this.hasCurrent) {
            this.current = current;
        }
    }

    public double getConsumption() {
        return consumption;
    }

    public void setConsumption(double consumption) {
        if (this.hasConsumption) {
            this.consumption = consumption;
        }
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        if (this.hasEnergy) {
            this.energy = energy;
        }
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

        if (hasVoltage()) {
            result.add(new HashMap<String, String>()
            {
                {
                    put("icon", "ic_voltmeter");
                    put("name", "socket_voltage");
                    put("value", getVoltage() + " V");
                }
            });
        }

        if (hasCurrent()) {
            result.add(new HashMap<String, String>()
            {
                {
                    put("icon", "ic_ammeter");
                    put("name", "socket_current");
                    put("value", getCurrent() + " A");
                }
            });
        }

        if (hasEnergy()) {
            result.add(new HashMap<String, String>()
            {
                {
                    put("icon", "ic_watt");
                    put("name", "socket_energy");
                    put("value", getEnergy() + " Ws");
                }
            });
        }

        if (hasConsumption()) {
            result.add(new HashMap<String, String>()
            {
                {
                    put("icon", "ic_consumption");
                    put("name", "socket_consumption");
                    put("value", getConsumption() + " W");
                }
            });
        }

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

        return result;
    }

    @Override
    public String toString() {
        return "SocketDevice{" +
                "voltage=" + voltage +
                ", current=" + current +
                ", consumption=" + consumption +
                ", energy=" + energy +
                ", state='" + state + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public String getFeaturedValue() {
        if (state.equals("on")) {
            return "On"; //@todo resource
        } else {
            return "Off"; //@todo resource
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
            case "voltage":
                if (hasVoltage()) {
                    processVoltageMessage(message);
                } else {
                    Log.w(TAG, "Warning! Trying to update not initialised value.");
                }
                break;
            case "current":
                if (hasCurrent()) {
                    processCurrentMessage(message);
                } else {
                    Log.w(TAG, "Warning! Trying to update not initialised value.");
                }
                break;
            case "consumption":
                if (hasConsumption()) {
                    processConsumptionMessage(message);
                } else {
                    Log.w(TAG, "Warning! Trying to update not initialised value.");
                }
                break;
            case "energy":
                if (hasEnergy()) {
                    processEnergyMessage(message);
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

    private void processVoltageMessage(String message) {
        try {
            JSONObject obj = new JSONObject(message);
            String type = obj.getString("type");

            switch (type) {
                case "periodic_report":
                    JSONObject report = obj.getJSONObject("report");
                    double value = report.getDouble("value");
                    String unit = report.getString("unit");
                    setVoltage(value);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processCurrentMessage(String message) {
        try {
            JSONObject obj = new JSONObject(message);
            String type = obj.getString("type");

            switch (type) {
                case "periodic_report":
                    JSONObject report = obj.getJSONObject("report");
                    double value = report.getDouble("value");
                    String unit = report.getString("unit");
                    setCurrent(value);
                    break;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processConsumptionMessage(String message) {
        try {
            JSONObject obj = new JSONObject(message);
            String type = obj.getString("type");

            switch (type) {
                case "periodic_report":
                    JSONObject report = obj.getJSONObject("report");
                    double value = report.getDouble("value");
                    String unit = report.getString("unit");
                    setConsumption(value);
                    break;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processEnergyMessage(String message) {
        try {
            JSONObject obj = new JSONObject(message);
            String type = obj.getString("type");

            switch (type) {
                case "periodic_report":
                    JSONObject report = obj.getJSONObject("report");
                    double value = report.getDouble("value");
                    String unit = report.getString("unit");
                    setConsumption(value);
                    break;
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
                    String value = obj.getString("value");
                    setState(value);
                    break;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
