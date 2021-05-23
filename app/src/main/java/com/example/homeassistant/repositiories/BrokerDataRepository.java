package com.example.homeassistant.repositiories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.homeassistant.helpers.JsonHelper;
import com.example.homeassistant.helpers.MqttHelper;
import com.example.homeassistant.model.Activity;
import com.example.homeassistant.model.Alert;
import com.example.homeassistant.model.BrokerData;
import com.example.homeassistant.viewmodels.MainViewModel;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class BrokerDataRepository {
    private static BrokerDataRepository instance;
    private MutableLiveData<BrokerData> brokerData = new MutableLiveData<>();
    private final MainViewModel mModel;
    private ArrayList<Activity> activities;

    public BrokerDataRepository(MainViewModel viewModel) {
        this.mModel = viewModel;
    }

    public static BrokerDataRepository getInstance(MainViewModel viewModel){
        if(instance == null){
            instance = new BrokerDataRepository(viewModel);
        }
        return instance;
    }

    public MutableLiveData<BrokerData> getBrokerData(){
        setBrokerData();
        return brokerData;
    }

    public ArrayList<Activity> getActivities() {
        return activities;
    }

    public void setActivities(ArrayList<Activity> activities) {
        this.activities = activities;
    }

    public void setBrokerData() {
        final MqttHelper mqttHelper = mModel.getBinder().getValue().getService().getMqttHelper();
        mqttHelper.subscribeToTopic("BRQ/BUT/devices/out", 0, null, null, new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if (topic.equals("BRQ/BUT/devices/out")) {
                    String messageString = message.toString();
                    try {
                        Log.i("BROKER DATA", messageString);
                        BrokerData data = JsonHelper.brokerData(messageString);
                        if (activities != null) {
                            data.setActivities(getActivities());
                        }
                        brokerData.postValue(data);
                        mqttHelper.unSubscribe("BRQ/BUT/devices/out");
                    } catch (Exception e){
                        Log.w("ERROR", e); //@todo nedostal som data od brokeru daj nejaku peknu hlasku
                    }
                }
            }
        });

        mqttHelper.subscribeToTopic("BRQ/BUT/activities/out", 0, null, null, new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if (topic.equals("BRQ/BUT/activities/out")) {
                    String messageString = message.toString();
                    try {
                        Log.i("ACTIVITIES", messageString);
                        if (brokerData.getValue() != null) {
                            brokerData.getValue().setActivities(JsonHelper.parseActivities(messageString));
                        } else {
                            setActivities(JsonHelper.parseActivities(messageString));
                        }
                        mqttHelper.unSubscribe("BRQ/BUT/activities/out");
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });


        long timestamp = System.currentTimeMillis();
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", "query_gui_dev");
            obj.put("timestamp", timestamp);
            obj.put("priority_level", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mqttHelper.publishToTopic("BRQ/BUT/in", obj.toString(), 2);

        JSONObject obj2 = new JSONObject();
        try {
            obj2.put("type", "query_activities");
            obj2.put("timestamp", timestamp);
            obj2.put("priority_level", 1);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }

        mqttHelper.publishToTopic("BRQ/BUT/in", obj2.toString(), 1);

        mqttHelper.subscribeToTopic("BRQ/BUT/events/in", 0, null, null, new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if (topic.equals("BRQ/BUT/events/in")) {
                    String messageString = message.toString();
                    try {
                        if (brokerData.getValue() != null) {
                            String type = JsonHelper.parseEventType(messageString);
                            switch(type){
                                case "event_report":
                                    Activity activity = JsonHelper.parseEvent(messageString);
                                    if (activity != null) {
                                        brokerData.getValue().addActivity(activity);
                                        mModel.setRefresh(mModel.getRefresh().getValue() + 1);
                                    } else {
                                        Log.w("event_report", "Event report of unknown format.");
                                    }
                                    break;
                                case "alert":
                                    Map<String, String> alertValues = JsonHelper.parseAlert(messageString);
                                    if (alertValues != null) {
                                        String status = alertValues.get("status");
                                        if (status != null) {
                                            Alert alert = brokerData.getValue().getAlertByName(alertValues.get("event_name"));
                                            if (alert != null) {
                                                alert.setStatus(status);
                                                alert.setMessage(alertValues.get("message"));
                                                double timestamp = Double.parseDouble(alertValues.get("timestamp"));
                                                if (timestamp > 9999999999.0) {
                                                    timestamp /= 1000;
                                                }
                                                brokerData.getValue().addActivity(new Activity(timestamp, alertValues.get("message")));
                                                mModel.setRefresh(mModel.getRefresh().getValue() + 1);

                                                if (status.equals("alert")) {
                                                    mModel.getBinder().getValue().getService().pushNotification("Alert", alertValues.get("message"), "SecurityFragment");
                                                    mModel.setPendingAlert(alertValues.get("message"));
                                                }
                                            }
                                        }
                                    }
                                    break;
                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
