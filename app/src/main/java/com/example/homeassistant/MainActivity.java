package com.example.homeassistant;

import android.os.Bundle;

import com.example.homeassistant.helpers.DatabaseHelper;
import com.example.homeassistant.helpers.JsonHelper;
import com.example.homeassistant.helpers.MqttHelper;
import com.example.homeassistant.model.BrokerData;
import com.example.homeassistant.model.MainViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    BottomNavigationView bottomNav;
    NavHostFragment navHostFragment;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.INVISIBLE);
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setVisibility(View.INVISIBLE);
        setUpNavigation();

        db = new DatabaseHelper(MainActivity.this);

        MainViewModel model = new ViewModelProvider(MainActivity.this).get(MainViewModel.class);
        final Observer<Boolean> obsLoaded = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    toolbar.setVisibility(View.VISIBLE);
                    bottomNav.setVisibility(View.VISIBLE);
                    navHostFragment.getNavController().navigate(R.id.action_loadingFragment_to_HomeFragment);
                }
            }
        };

        model.startViewModel(getApplicationContext());
        model.getLoaded().observe(this, obsLoaded);
    }

    public void setUpNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNav, navHostFragment.getNavController());
    }
}