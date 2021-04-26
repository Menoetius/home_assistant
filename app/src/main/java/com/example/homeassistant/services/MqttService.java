package com.example.homeassistant.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.MutableLiveData;

import com.example.homeassistant.R;
import com.example.homeassistant.helpers.DatabaseHelper;
import com.example.homeassistant.helpers.MqttHelper;
import com.example.homeassistant.model.ConnectionModel;
import com.example.homeassistant.views.MainActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

public class MqttService extends Service {

    private static final String TAG = "MyService";
    private final String CHANNEL_ID = "mainChannel";

    private DatabaseHelper db;

    private ConnectionModel connectionModel;
    private MqttHelper mqttHelper;
    private final IBinder mBinder = new MyBinder();
    private MutableLiveData<Boolean> hasCredentials = new MutableLiveData<>();
    private MutableLiveData<Boolean> connected = new MutableLiveData<>();

    @Override
    public void onCreate() {
        super.onCreate();
        this.hasCredentials.setValue(false);
        this.connected.setValue(false);
        createNotificationChannel();
        db = new DatabaseHelper(getApplicationContext());

        connectionModel = db.getLast();

        if (mqttHelper == null && connectionModel != null) {
            startMqtt();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder{

        public MqttService getService(){
            return MqttService.this;
        }
    }

    public void startMqtt() {
        connectionModel = db.getLast();
        mqttHelper = new MqttHelper(this);
        mqttHelper.connect(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Toast.makeText(mqttHelper.mContext, "Connected!", Toast.LENGTH_SHORT).show();
                mqttHelper.setDisconnectOptions();
                connected.setValue(true);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Toast.makeText(mqttHelper.mContext, "Failed to connect. Try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public MqttHelper getMqttHelper() {
        return mqttHelper;
    }

    public MutableLiveData<Boolean> getConnected(){
        return connected;
    }

    public void pushNotification(String title, String message, String fragmentName) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    getString(R.string.channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(getString(R.string.channel_description));
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}