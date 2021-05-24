package com.example.homeassistant.helpers;

import android.util.Log;

import com.example.homeassistant.devices.BlindsDevice;
import com.example.homeassistant.devices.CameraDevice;
import com.example.homeassistant.devices.LightLevelDevice;
import com.example.homeassistant.devices.LightRgbDevice;
import com.example.homeassistant.devices.SensorDevice;
import com.example.homeassistant.devices.SocketDevice;
import com.example.homeassistant.devices.SwitchDevice;
import com.example.homeassistant.model.Activity;
import com.example.homeassistant.model.Alert;
import com.example.homeassistant.model.BrokerData;
import com.example.homeassistant.model.DeviceModel;
import com.example.homeassistant.model.Room;
import com.example.homeassistant.model.Scene;
import com.example.homeassistant.model.Security;

import org.json.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class JsonHelper {
    public JsonHelper() {
    }

    static public BrokerData brokerData(String jsonString) throws JSONException {
        JSONObject obj = new JSONObject(jsonString);
        int timestamp = 0;

        String type = obj.getString("type");
        JSONObject values = obj.getJSONObject("value");
        return new BrokerData(timestamp, type, getRooms(values), getScenes(values), getAlerts(values), getSecurity(values));
    }

    static public TreeMap<String, Room> getRooms(JSONObject obj) throws JSONException {
        TreeMap<String, Room> rooms = new TreeMap<>();

        JSONArray arr = obj.getJSONArray("rooms");
        for (int i = 0; i < arr.length(); i++) {
            String roomId = arr.getJSONObject(i).getString("id");
            String roomName = "";
            JSONArray devicesArr = arr.getJSONObject(i).getJSONArray("devices");
            TreeMap<String, DeviceModel> devices = new TreeMap<>();
            CameraDevice camera = null;
            for (int j = 0; j < devicesArr.length(); j++) {
                JSONObject device = devicesArr.getJSONObject(j);
                String deviceId = device.getString("id");
                String deviceName = device.getString("name");
                String deviceType = device.getString("type");
                String technology = device.has("technology") ? device.getString("technology") : "";
                switch(deviceType) {
                    case "camera":
                        roomName = device.getString("name");
                        String image = device.has("image") ? device.getString("image") : "";
                        String message = device.has("events") ? device.getJSONObject("events").getString("message") : "";
                        double timestamp = device.has("events") ? parseDouble(device.getJSONObject("events").getString("timestamp")) : 0;

                        camera = new CameraDevice(deviceId, deviceName, deviceType, technology, image, message, timestamp);
                        break;
                    case "blinds":
                        int position = parseInt(device.getString("position"));

                        BlindsDevice blinds = new BlindsDevice(deviceId, deviceName, deviceType, technology, position);
                        devices.put(deviceId, blinds);
                        break;
                    case "socket":
                        SocketDevice socket = new SocketDevice(deviceId, deviceName, deviceType, technology, device.getString("state"), device.has("voltage"), device.has("current"), device.has("consumption"), device.has("energy"), device.has("temperature"));

                        if (device.has("voltage")) {
                            socket.setVoltage(parseDouble(device.getString("voltage")));
                        }
                        if (device.has("current")) {
                            socket.setCurrent(parseDouble(device.getString("current")));
                        }
                        if (device.has("consumption")) {
                            socket.setConsumption(parseDouble(device.getString("consumption")));
                        }
                        if (device.has("energy")) {
                            socket.setEnergy(parseDouble(device.getString("energy")));
                        }
                        if (device.has("temperature")) {
                            socket.setTemperature(parseDouble(device.getString("temperature")));
                        }

                        devices.put(deviceId, socket);
                        break;
                    case "light_rgb":
                        int color = parseInt(device.getString("color"));
                        int brightness = parseInt(device.getString("brightness"));
                        String state = device.getString("state");

                        LightRgbDevice light = new LightRgbDevice(deviceId, deviceName, deviceType, technology, state, color, brightness);
                        devices.put(deviceId, light);
                        break;
                    case "switch":
                        SwitchDevice switchDevice = new SwitchDevice(deviceId, deviceName, deviceType, technology, device.getString("state"), device.has("temperature"), device.has("humidity"));

                        if (device.has("humidity")) {
                            switchDevice.setHumidity(parseDouble(device.getString("humidity")));
                        }
                        if (device.has("temperature")) {
                            switchDevice.setTemperature(parseDouble(device.getString("temperature")));
                        }

                        devices.put(deviceId, switchDevice);
                        break;
                    case "sensor":
                        SensorDevice sensor = new SensorDevice(deviceId, deviceName, deviceType, technology, device.has("temperature"), device.has("humidity"));

                        if (device.has("humidity")) {
                            sensor.setHumidity(parseDouble(device.getString("humidity")));
                        }
                        if (device.has("temperature")) {
                            sensor.setTemperature(parseDouble(device.getString("temperature")));
                        }

                        devices.put(deviceId, sensor);
                        break;
                    case "light_level":
                        LightLevelDevice lightLevelDevice = new LightLevelDevice(deviceId, deviceName, deviceType, technology, device.getString("state"), parseInt(device.getString("color")), device.has("brightness"));

                        if (device.has("brightness")) {
                            lightLevelDevice.setBrightness(parseInt(device.getString("brightness")));
                        }

                        devices.put(deviceId, lightLevelDevice);
                        break;
                    default:
                        Log.w("JSON parser", "Unknown device type");
                }
            }
            Room room = new Room(roomId, roomName, devices);
            if (camera != null) {
                room.setCamera(camera);
            }
            rooms.put(roomId, room);
        }

        return rooms;
    }

    static public TreeMap<String, Scene> getScenes(JSONObject obj) throws JSONException {
        TreeMap<String, Scene> scenes = new TreeMap<>();

        JSONArray arr = obj.getJSONArray("scenes");
        for (int i = 0; i < arr.length(); i++) {
            String id = arr.getJSONObject(i).getString("id");
            String name = arr.getJSONObject(i).getString("name");
            String icon = arr.getJSONObject(i).getString("icon");
            Boolean active = arr.getJSONObject(i).getBoolean("active");

            Scene scene = new Scene(id, name, icon, active);
            scenes.put(id, scene);
        }

        return scenes;
    }

    static public TreeMap<String, Alert> getAlerts(JSONObject obj) throws JSONException {
        TreeMap<String, Alert> alerts = new TreeMap<>();

        JSONArray arr = obj.getJSONArray("alert");
        for (int i = 0; i < arr.length(); i++) {
            String name = arr.getJSONObject(i).getString("event_name");
            String message = arr.getJSONObject(i).getString("message");
            String status = arr.getJSONObject(i).getString("status");
            Double timestamp = arr.getJSONObject(i).getDouble("timestamp");

            Alert alert = new Alert(name, message, status, timestamp);
            alerts.put(name, alert);
        }

        return alerts;
    }

    static public TreeMap<String, Security> getSecurity(JSONObject obj) throws JSONException {
        TreeMap<String, Security> securityTreeMap = new TreeMap<>();

        JSONArray arr = obj.getJSONArray("security");
        for (int i = 0; i < arr.length(); i++) {
            String id = arr.getJSONObject(i).getString("id");
            String type = arr.getJSONObject(i).getString("type");
            String name = arr.getJSONObject(i).getString("name");
            String icon = arr.getJSONObject(i).getString("icon");
            Boolean active = arr.getJSONObject(i).getBoolean("active");

            Security security = new Security(id, type, name, icon, active);
            securityTreeMap.put(name, security);
        }

        return securityTreeMap;
    }

    static public String getStreamUrl(String jsonString) throws JSONException {
        JSONObject obj = new JSONObject(jsonString);

        return obj.getString("value");
    }

    static public ArrayList<Activity> parseActivities(String jsonString) throws JSONException {
        ArrayList<Activity> result = new ArrayList<>();
        JSONObject obj = new JSONObject(jsonString);

        if (obj.has("type")) {
            if (obj.get("type").equals("command_response") && obj.has("value")) {
                JSONArray arr = obj.getJSONArray("value");

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject arrObj =  arr.getJSONObject(i);

                    double timestamp = 0;
                    if (arrObj.has("timestamp")) {
                        timestamp = arrObj.getDouble("timestamp");
                    }

                    String message = "";
                    if (arrObj.has("message")) {
                        message = arrObj.getString("message");
                    }

                    result.add(new Activity(timestamp, message));
                }
            }
        }

        return result;
    }

    public static String parseEventType(String message) {
        try {
            JSONObject obj = new JSONObject(message);
            double timestamp = obj.getDouble("timestamp");
            if (obj.has("type")) {
                if (obj.getString("type").equals("event_report") && obj.has("value")) {
                    return "event_report";
                } else if (obj.getString("type").equals("alert") && obj.has("value")) {
                    return "alert";
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Activity parseEvent(String message) {
        try {
            JSONObject obj = new JSONObject(message);
            double timestamp = obj.getDouble("timestamp");
            if (timestamp > 9999999999.0) {
                timestamp /= 100;
            }

            if (obj.has("type")) {
                if (obj.getString("type").equals("event_report") && obj.has("value")) {
                    JSONObject value = obj.getJSONObject("value");
                    if (value.has("message")) {
                        return new Activity(timestamp, value.getString("message"));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> parseAlert(String message) {
        Map<String, String> result = new HashMap<String, String>();
        try {
            JSONObject obj = new JSONObject(message);
            double timestamp = obj.getDouble("timestamp");
            if (obj.has("type")) {
                if (obj.getString("type").equals("alert") && obj.has("value")) {
                    JSONObject value = obj.getJSONObject("value");
                    if (value.has("message") && value.has("event_name") && value.has("status")) {
                        result.put("message", value.getString("message"));
                        result.put("event_name", value.getString("event_name"));
                        result.put("status", value.getString("status"));
                        result.put("timestamp", timestamp + "");
                        return result;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
