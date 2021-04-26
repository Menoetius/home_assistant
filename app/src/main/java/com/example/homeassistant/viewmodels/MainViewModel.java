package com.example.homeassistant.viewmodels;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.homeassistant.model.BrokerData;
import com.example.homeassistant.repositiories.BrokerDataRepository;
import com.example.homeassistant.repositiories.DevicesRepository;
import com.example.homeassistant.services.MqttService;

public class MainViewModel extends ViewModel {

    public static final String TAG = "MainViewModel";

    private MutableLiveData<MqttService.MyBinder> mBinder = new MutableLiveData<>();
    private BrokerDataRepository brokerDataRepository;
    private DevicesRepository devicesRepository;
    private MutableLiveData<BrokerData> brokerData = new MutableLiveData<>();
    private MutableLiveData<Boolean> hasLogin = new MutableLiveData<>();
    private MutableLiveData<Integer> refresh = new MutableLiveData<>();
    private MutableLiveData<String> pendingAlert = new MutableLiveData<>();
    private MutableLiveData<String> actualDevice = new MutableLiveData<>();

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            Log.d(TAG, "ServiceConnection: connected to service.");
            MqttService.MyBinder binder = (MqttService.MyBinder) iBinder;
            mBinder.postValue(binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "ServiceConnection: disconnected from service.");
            mBinder.postValue(null);
        }
    };

    public void initBrokerData() {
        if (brokerData.getValue() != null) {
            return;
        }
        actualDevice.setValue("all");
        refresh.setValue(0);
        pendingAlert.setValue("");
        brokerDataRepository = BrokerDataRepository.getInstance(MainViewModel.this);
        devicesRepository = DevicesRepository.getInstance(MainViewModel.this);
//        brokerData = brokerDataRepository.getBrokerData();
    }

    public ServiceConnection getServiceConnection(){
        return serviceConnection;
    }

    public LiveData<MqttService.MyBinder> getBinder(){
        return mBinder;
    }

    public LiveData<BrokerData> getBrokerData() {
        if (brokerData.getValue() == null) {
            brokerData = brokerDataRepository.getBrokerData();
        }
        return brokerData;
    }

    public void initDevicesSubscription() {
        devicesRepository.subscribeToDevices();
    }

    public LiveData<Integer> getRefresh() {
        return refresh;
    }

    public void setRefresh(int value) {
        this.refresh.postValue(value);
    }

    public LiveData<String> getActualDevice() {
        return actualDevice;
    }

    public void setActualDevice(String value) {
        this.actualDevice.postValue(value);
    }

    public LiveData<String> getPendingAlert() {
        return pendingAlert;
    }

    public void setPendingAlert(String pendingAlert) {
        this.pendingAlert.postValue(pendingAlert);
    }
}
