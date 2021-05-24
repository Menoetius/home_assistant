package com.example.homeassistant.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.homeassistant.R;
import com.example.homeassistant.helpers.DatabaseHelper;
import com.example.homeassistant.helpers.MqttHelper;
import com.example.homeassistant.model.BrokerData;
import com.example.homeassistant.services.MqttService;
import com.example.homeassistant.viewmodels.MainViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.View;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private BottomNavigationView bottomNav;
    private NavHostFragment navHostFragment;
    private DatabaseHelper db;
    private MainViewModel model;
    private MqttService mService;
    private WaitingDialog waitingDialog;
    private BrokerAlertDialog brokerAlertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottom_navigation);
        setUpNavigation();
        bottomNav.setVisibility(View.INVISIBLE);

        db = new DatabaseHelper(getApplicationContext());
        model = ViewModelProviders.of(MainActivity.this).get(MainViewModel.class);

        setObservers();
    }

    public void setUpNavigation() {
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNav, navHostFragment.getNavController());
    }

    public void setBottomNavigationVisibility(Integer visibility) {
        bottomNav.setVisibility(visibility);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startMqttService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(model.getBinder() != null){
            unbindService(model.getServiceConnection());
        }
    }

    private void startMqttService(){
        Intent serviceIntent = new Intent(this, MqttService.class);
        startService(serviceIntent);

        bindService();
    }

    private void bindService() {
        Intent serviceBindIntent =  new Intent(this, MqttService.class);
        bindService(serviceBindIntent, model.getServiceConnection(), Context.BIND_AUTO_CREATE);
    }

    private void setObservers(){
        model.getBinder().observe(MainActivity.this, new Observer<MqttService.MyBinder>() {
            @Override
            public void onChanged(@Nullable MqttService.MyBinder myBinder) {
                if(myBinder == null) {
                    Log.d(TAG, "Unbound from service");
                } else {
                    Log.d(TAG, "Bound to service.");
                    mService = myBinder.getService();
                    if (mService != null) {
                        mService.getConnected().observe(MainActivity.this, new Observer<String>() {
                            @Override
                            public void onChanged(String connected) {
                                if (connected.equals("connected")) {
                                    connect();

                                    model.initBrokerData();

                                    model.getBrokerData().observe(MainActivity.this, new Observer<BrokerData>() {
                                        @Override
                                        public void onChanged(BrokerData data) {
                                            if (data != null) {
                                                setUpHomeScreen();
                                                model.getBrokerData().removeObservers(MainActivity.this);
                                                model.initDevicesSubscription();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });

        model.getPendingAlert().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                if (!message.equals("")) {
                    brokerAlertDialog = new BrokerAlertDialog(MainActivity.this, message);
                    brokerAlertDialog.startBrokerAlertDialog();
                    model.setPendingAlert("");
                }
            }
        });
    }

    private void setUpHomeScreen() {
        if (navHostFragment.getChildFragmentManager().getFragments().get(0) instanceof LoginFragment) {
            navHostFragment.getNavController().navigate(R.id.action_loginFragment_to_HomeFragment);
            bottomNav.setVisibility(View.VISIBLE);
        }
        if (waitingDialog != null) {
            waitingDialog.dismissDialog();
        }
    }

    public void connect() {
        waitingDialog = new WaitingDialog(MainActivity.this);
        waitingDialog.startWaitingDialog(getString(R.string.fetching_data_from_server));
    }

    @Override
    public void onBackPressed() {
        if (!(navHostFragment.getChildFragmentManager().getFragments().get(0) instanceof LoginFragment)) {
            bottomNav.setVisibility(View.VISIBLE);
            super.onBackPressed();
        }
    }
}