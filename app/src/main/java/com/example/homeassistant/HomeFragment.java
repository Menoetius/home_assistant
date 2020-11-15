package com.example.homeassistant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class HomeFragment extends Fragment implements MainActivity.DataFromActivityToFragment {
    TextView dataReceived;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        dataReceived = (TextView)view.findViewById(R.id.dataRecieved);
//        textView.setText(strtext);


        return view;
    }

    @Override
    public void sendData(String data) {
        if (data != null) {
            dataReceived.setText(data);
        }
    }
}