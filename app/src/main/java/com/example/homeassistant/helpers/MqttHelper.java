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
     *
     * @param topic
     * @param qos
     * @param context
     * @param actionListener
     * @param messageListener
     */
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

    /**
     *
     * @param topic
     * @param payload
     * @param qos
     */
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

    /**
     *
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
     *
     * @param topic
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
     * @param connectionModel
     * @return
     */
    private String buildServerUri(ConnectionModel connectionModel) {
        return "ssl://" + connectionModel.getUrl() + ":" + connectionModel.getPort();
    }
}
