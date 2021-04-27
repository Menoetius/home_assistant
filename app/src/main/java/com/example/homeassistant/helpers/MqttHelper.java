package com.example.homeassistant.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.homeassistant.model.ConnectionModel;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MqttHelper {
    public MqttAndroidClient mqttAndroidClient;
    public final Context mContext;
    private DatabaseHelper db;
    private ConnectionModel connectionModel;

    final String deviceId = MqttClient.generateClientId();

    public MqttHelper(Context context) {
        mContext = context;

        db = new DatabaseHelper(context.getApplicationContext());
        connectionModel = db.getLast();

        mqttAndroidClient = new MqttAndroidClient(context, buildServerUri(connectionModel), deviceId);
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    public void connect(IMqttActionListener listener) {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setUserName(connectionModel.getUserName());
        mqttConnectOptions.setPassword(connectionModel.getPassword().toCharArray());

        if (connectionModel.getProtocol().equals("ssl")) {
            SocketFactory.SocketFactoryOptions socketFactoryOptions = new SocketFactory.SocketFactoryOptions();
            try {
                socketFactoryOptions.withCaInputStream(mContext.getResources().openRawResource(mContext.getResources().getIdentifier("@raw/ca", "raw", mContext.getPackageName())));
                mqttConnectOptions.setSocketFactory(new SocketFactory(socketFactoryOptions));
            } catch (NoSuchAlgorithmException | KeyManagementException | IOException | CertificateException | UnrecoverableKeyException | KeyStoreException e) {
                e.printStackTrace();
            }
        } else {
            try {
                final TrustManager[] myTrustManager = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        }
                };

                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, myTrustManager, new java.security.SecureRandom());
                mqttConnectOptions.setSocketFactory(sslContext.getSocketFactory());
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
            }
        }

        try {
            mqttAndroidClient.connect(mqttConnectOptions, mContext, listener);
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    public void subscribeToTopic(String topic, Integer qos, Context context, IMqttActionListener actionListener, IMqttMessageListener messageListener) {
        if (actionListener == null) { // todo toto pri service nebude fungovat
            actionListener = new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("Mqtt", "Subscribed successfully!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("Mqtt", "Subscribed fail!");
                }
            };
        }

        try {
            if (messageListener != null) {
                mqttAndroidClient.subscribe(topic, qos != null ? qos : 0, mContext, actionListener, messageListener);
            } else {
                mqttAndroidClient.subscribe(topic, qos != null ? qos : 0, mContext, actionListener);
            }
        } catch (MqttException ex) {
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    public void publishToTopic(String topic, String payload, Integer qos) {
        byte[] encodedPayload;
        try {
            encodedPayload = payload.getBytes(StandardCharsets.UTF_8);
            MqttMessage message = new MqttMessage(encodedPayload);
            message.setQos(qos != null ? qos : 0);
            Log.w("publish", "topic: " + topic + " message: " + payload);
            mqttAndroidClient.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return mqttAndroidClient.isConnected();
    }

    public void setDisconnectOptions() {
        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
        disconnectedBufferOptions.setBufferEnabled(true);
        disconnectedBufferOptions.setBufferSize(100);
        disconnectedBufferOptions.setPersistBuffer(false);
        disconnectedBufferOptions.setDeleteOldestMessages(false);
        mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
    }

    public void unSubscribe(String topic) {
        try {
            mqttAndroidClient.unsubscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private String buildServerUri(ConnectionModel connectionModel) {
        return "ssl://" + connectionModel.getUrl() + ":" + connectionModel.getPort();
    }
}
