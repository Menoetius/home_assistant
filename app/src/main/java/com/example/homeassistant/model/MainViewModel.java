package com.example.homeassistant.model;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.homeassistant.helpers.DatabaseHelper;
import com.example.homeassistant.helpers.JsonHelper;
import com.example.homeassistant.helpers.MqttHelper;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainViewModel extends ViewModel {
    private BrokerData brokerData;
    MqttHelper mqttHelper;
    Context appContext;
    MutableLiveData<Boolean> loaded;

    public MutableLiveData<Boolean> getLoaded() {
        return loaded;
    }

    public void startViewModel(Context context) {
        loaded = new MutableLiveData<Boolean>();
        loaded.setValue(false);
        appContext = context;
        startMqtt();
    }

    public BrokerData getBrokerData() {
        if (brokerData == null) {
            Log.w("ERROR", "No broker data in MainViewModel");
            return null;
        }

        return brokerData;
    }

    private void startMqtt() {
        mqttHelper = new MqttHelper(appContext);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("mqtt", "connectComplete "+s.toString());
            }

            @Override
            public void connectionLost(Throwable throwable) {
                Log.w("mqtt", "connectionLost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                if (topic.equals("BRQ/BUT/devices/out")) {
                    String messageString = mqttMessage.toString();
                    try {
                        brokerData = JsonHelper.brokerData(messageString);
                    } catch (Exception e){
                        Log.w("ERROR", e);
                    }
                    Log.w("BROKER DATA", brokerData.toString());
                    loaded.setValue(true);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                try {
                    Log.w("Mqtt", "delivery completed " + iMqttDeliveryToken.getMessage().toString());
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
