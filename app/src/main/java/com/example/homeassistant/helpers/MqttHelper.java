package com.example.homeassistant.helpers;

import android.content.Context;
import android.util.Log;

import com.example.homeassistant.R;
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


import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class MqttHelper {

    public static final String TAG = "MqttHelper";

    public MqttAndroidClient mqttAndroidClient;
    public final Context mContext;
    private DatabaseHelper db;
    private ConnectionModel connectionModel;
    final String deviceId = MqttClient.generateClientId();

    /**
     * Mqtt helper constructor
     * @param context - activity context
     */
    public MqttHelper(Context context) {
        mContext = context;

        db = new DatabaseHelper(context.getApplicationContext());
        connectionModel = db.getLast();

        mqttAndroidClient = new MqttAndroidClient(context, buildServerUri(connectionModel), deviceId);
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    /**
     * Sets up connection options and connects to broker
     * @param listener - callback of connect
     */
    public void connect(IMqttActionListener listener) {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setUserName(connectionModel.getUserName());
        mqttConnectOptions.setPassword(connectionModel.getPassword().toCharArray());

        if (connectionModel.getProtocol().equals("ssl")) {
            try {
                final KeyStore ks = KeyStore.getInstance("BKS");

                final InputStream in = mContext.getResources().openRawResource(R.raw.mystore);
                try {
                    ks.load(in, connectionModel.getPassword().toCharArray());
                } finally {
                    in.close();
                }

                mqttConnectOptions.setSocketFactory(new CustomKeyStores(ks));
            } catch( Exception e ) {
                throw new RuntimeException(e);
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

    /**
     * Subscribes to provided topic with given parameters
     * @param topic - String topic name
     * @param qos - Integer QoS [0, 1, 2]
     * @param actionListener - information on success/failure from server
     * @param messageListener - message callback listener
     */
    public void subscribeToTopic(String topic, Integer qos, IMqttActionListener actionListener, IMqttMessageListener messageListener) {
        if (actionListener == null) {
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

    /**
     * Publish given message to given topic
     * @param topic - String topic name
     * @param payload - String payload of message - in MQTT2GO should be json format
     * @param qos - Integer QoS [0, 1, 2]
     */
    public void publishToTopic(String topic, String payload, Integer qos) {
        byte[] encodedPayload;
        try {
            encodedPayload = payload.getBytes(StandardCharsets.UTF_8);
            MqttMessage message = new MqttMessage(encodedPayload);
            message.setQos(qos != null ? qos : 0);
            mqttAndroidClient.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return mqttAndroidClient.isConnected();
    }

    /**
     * Sets options for unexpected disconnect from server
     */
    public void setDisconnectOptions() {
        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
        disconnectedBufferOptions.setBufferEnabled(true);
        disconnectedBufferOptions.setBufferSize(100);
        disconnectedBufferOptions.setPersistBuffer(false);
        disconnectedBufferOptions.setDeleteOldestMessages(false);
        mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
    }

    /**
     * Unsubscribe form topic
     * @param topic - string topic name
     */
    public void unSubscribe(String topic) {
        try {
            mqttAndroidClient.unsubscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param connectionModel - object containing connection information
     * @return returns URL address with port number based on connection model
     */
    private String buildServerUri(ConnectionModel connectionModel) {
        return "ssl://" + connectionModel.getUrl() + ":" + connectionModel.getPort();
    }

    /**
     * Disconnect from server
     */
    public void disconnect() {
        try {
            mqttAndroidClient.disconnect();
        } catch (MqttException err) {
            Log.d(TAG, err.getMessage());
        }
    }
}
