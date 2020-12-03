package com.example.homeassistant.helpers;

import android.util.Log;

import com.example.homeassistant.devices.CameraDevice;
import com.example.homeassistant.model.Alert;
import com.example.homeassistant.model.BrokerData;
import com.example.homeassistant.model.DeviceModel;
import com.example.homeassistant.model.Room;
import com.example.homeassistant.model.Scene;
import com.example.homeassistant.model.Security;

import org.json.*;

import java.util.TreeMap;

public class JsonHelper {
    public JsonHelper() {
    }

    static public BrokerData brokerData(String jsonString) throws JSONException {
        JSONObject obj = new JSONObject(jsonString);
//        int timestamp = Integer.parseInt(obj.getString("timestamp"));
        int timestamp = 0;

        String type = obj.getString("type");
        JSONObject values = obj.getJSONObject("value");
        Log.w("value", values.toString());
        return new BrokerData(timestamp, type, getRooms(values), getScenes(values), getAlerts(values), getSecurity(values));
    }

    static public TreeMap<String, Room> getRooms(JSONObject obj) throws JSONException {
        TreeMap<String, Room> rooms = new TreeMap<String, Room>();

        JSONArray arr = obj.getJSONArray("rooms");
        for (int i = 0; i < arr.length(); i++) {
            String roomId = arr.getJSONObject(i).getString("id");
            JSONArray devicesArr = arr.getJSONObject(i).getJSONArray("devices");
            TreeMap<String, DeviceModel> devices = new TreeMap<String, DeviceModel>();
            for (int j = 0; j < devicesArr.length(); j++){
                String deviceId = devicesArr.getJSONObject(j).getString("id");
                String deviceName = devicesArr.getJSONObject(j).getString("name");
                String deviceType = devicesArr.getJSONObject(j).getString("type");
                switch(deviceType) {
                    case "camera":
                        String image = devicesArr.getJSONObject(j).getString("image");
                        String message = devicesArr.getJSONObject(j).getJSONObject("events").getString("message");
                        int timestamp = Integer.parseInt(devicesArr.getJSONObject(j).getJSONObject("events").getString("timestamp"));
                        CameraDevice camera = new CameraDevice(deviceId, deviceName, deviceType, image, message, timestamp);
                        devices.put(deviceId, camera);
                        break;
                    default:
                        Log.w("JSON parser", "Unknow device type");
                }
            }
            Room room = new Room(roomId, devices);
            rooms.put(roomId, room);
        }

        return rooms;
    }

    static public TreeMap<String, Scene> getScenes(JSONObject obj) throws JSONException {
        TreeMap<String, Scene> scenes = new TreeMap<String, Scene>();

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
        TreeMap<String, Alert> alerts = new TreeMap<String, Alert>();

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
        TreeMap<String, Security> securityTreeMap = new TreeMap<String, Security>();

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
}
