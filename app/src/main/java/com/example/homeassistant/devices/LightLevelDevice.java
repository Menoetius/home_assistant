package com.example.homeassistant.devices;

import android.graphics.Color;
import android.util.Log;

import com.example.homeassistant.model.DeviceModel;
import com.example.homeassistant.repositiories.DevicesRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LightLevelDevice extends DeviceModel {

    private static final String TAG = DevicesRepository.class.getSimpleName();

    private int color;
    private boolean hasBrightness;
    private int brightness;
    private String state;

    public LightLevelDevice(String id, String name, String type, String state, int color, int brightness) {
        super(id, name, type);
        this.color = color;
        this.brightness = brightness;
        this.state = state;
    }

    public LightLevelDevice(String id, String name, String type, String technology, String state, int color, boolean hasBrightness) {
        super(id, name, type, technology);
        this.color = color;
        this.hasBrightness = hasBrightness;
        this.state = state;
    }

    public boolean hasBrightness() {
        return hasBrightness;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String getStringValue(String name) {
        String result = "";
        switch(name) {
            case "brightness":
                if (hasBrightness()) {
                    result = getBrightness() + "";
                }
                break;
            case "color":
                result = getColor() + "";
                break;
            case "state":
                result = getState();
                break;
        }
        return result;
    }

    @Override
    public String toString() {
        return "LightLevelDevice{" +
                "color=" + color +
                ", brightness=" + brightness +
                ", state='" + state + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", technology='" + technology + '\'' +
                '}';
    }

    @Override
    public String getFeaturedValue() {
        if (state.equals("on")) {
            if (hasBrightness && brightness != 0) {
                return brightness + "%";
            }
            return "On"; //@todo resource
        }
        return "Off"; //@todo resource
    }

    @Override
    public Map<String, String> getSetMessage(String valueType, int value) {
        Map<String,String> map=new HashMap<String,String>();
        long timestamp = System.currentTimeMillis();
        JSONObject obj = new JSONObject();
        try {
            obj.put("timestamp", Long.toString(timestamp));
            if (valueType.equals("brightness")) {
                obj.put("type", "brightness");
                obj.put("value", value);
            }
            if (valueType.equals("color")) {
                obj.put("type", "color");
                Color color = Color.valueOf(value);
                JSONObject colorObj = new JSONObject();
                colorObj.put("r", Math.round(255 * color.red()));
                colorObj.put("g", Math.round(255 * color.green()));
                colorObj.put("b", Math.round(255 * color.blue()));
                obj.put("value", colorObj);
            }
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
        map.put("message", obj.toString());
        map.put("topicIn", "BRQ/BUT/" + id + "/" + valueType + "/in");
        return map;
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
            case "color":
                processColorMessage(message);
                if (actualDevice.equals("all")) {
                    result = true;
                }
                break;
            case "brightness":
                if (hasBrightness()) {
                    processBrightnessMessage(message);
                    if (actualDevice.equals("all")) {
                        result = true;
                    }
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

    private void processColorMessage(String message) {
        try {
            JSONObject obj = new JSONObject(message);
            String type = obj.getString("type");

            switch (type) {
                case "color":
                    JSONObject valueObj = obj.getJSONObject("value");
                    if (valueObj.has("r") && valueObj.has("g") && valueObj.has("b")) {
                        int color = Color.rgb((float) valueObj.getInt("r"), (float) valueObj.getInt("g"), (float) valueObj.getInt("b"));
                        setColor(color);
                        setState("on");
                    }
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processBrightnessMessage(String message) {
        try {
            JSONObject obj = new JSONObject(message);
            String type = obj.getString("type");

            switch (type) {
                case "brightness":
                    int value = obj.getInt("value");
                    setBrightness(value);
                    setState("on");
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
