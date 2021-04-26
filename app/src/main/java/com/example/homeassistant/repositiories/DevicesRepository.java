package com.example.homeassistant.repositiories;

import android.util.Log;

import com.bumptech.glide.gifdecoder.StandardGifDecoder;
import com.example.homeassistant.helpers.JsonHelper;
import com.example.homeassistant.helpers.MqttHelper;
import com.example.homeassistant.model.BrokerData;
import com.example.homeassistant.model.DeviceModel;
import com.example.homeassistant.viewmodels.MainViewModel;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;

public class DevicesRepository {
    private static final String TAG = DevicesRepository.class.getSimpleName();

    private static DevicesRepository instance;
    private final MainViewModel mModel;

    public DevicesRepository(MainViewModel mModel) {
        this.mModel = mModel;
    }

    public static DevicesRepository getInstance(MainViewModel viewModel){
        if(instance == null){
            instance = new DevicesRepository(viewModel);
        }
        return instance;
    }

    public void subscribeToDevices() {
        ArrayList<DeviceModel> devices = mModel.getBrokerData().getValue().getAllDevices();

        MqttHelper mqttHelper = mModel.getBinder().getValue().getService().getMqttHelper();

        for (DeviceModel device : devices) {
            mqttHelper.subscribeToTopic("BRQ/BUT/"+device.getId()+"/+/out", 0, null, null, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    DeviceModel device = mModel.getBrokerData().getValue().getDeviceById(topic.split("/")[2]);
                    if (device.processMessage(topic, message.toString(), mModel.getActualDevice().getValue())) {
                        mModel.setRefresh(mModel.getRefresh().getValue() + 1);
                    }
                }
            });
        }
    }
}
