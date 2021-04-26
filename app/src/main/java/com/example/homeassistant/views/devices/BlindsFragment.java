package com.example.homeassistant.views.devices;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.homeassistant.R;
import com.example.homeassistant.model.DeviceModel;
import com.example.homeassistant.services.MqttService;
import com.example.homeassistant.viewmodels.MainViewModel;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;

import java.util.Map;


public class BlindsFragment extends Fragment {
    MqttService mService;
    MainViewModel model;
    View view;
    DeviceModel device;
    TextView tvState;
    ImageView ivState;
    Slider slider;

    public BlindsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_blinds, container, false);
        TextView tvDeviceName = view.findViewById(R.id.tvSecurityName);

        ImageButton ibBack = view.findViewById(R.id.ibBack);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlindsFragment.this.getActivity().onBackPressed();
            }
        });

        tvState = view.findViewById(R.id.tvState);
        ivState = view.findViewById(R.id.ivState);
        slider = view.findViewById(R.id.slider);

        model = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        mService = model.getBinder().getValue().getService();
        device = model.getBrokerData().getValue().getDeviceById(model.getActualDevice().getValue());

        tvDeviceName.setText(device.getName());
        initState();

        slider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                return Math.round(value) + " %";
            }
        });
        slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                Map<String, String> map = device.getSetMessage("position", (int) (100 - slider.getValue()));

                if (map == null) {
                    Log.w("ERROR", "Device getSetMessage not working"); // @todo error
                } else {
                    model.getBinder().getValue().getService().getMqttHelper().publishToTopic(map.get("topicIn"), map.get("message"), 2);
                }
            }
        });

        model.getRefresh().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer data) {
                initState();
            }
        });

        return view;
    }

    private void initState() {
        tvState.setText(this.getResources().getString(R.string.window_position, device.getFeaturedValue()));
        String position = device.getFeaturedValue();
        if (position.equals("Up")) {
            slider.setValue(0);
            ivState.setImageResource(R.drawable.ic_blinds_100);
        } else if (position.equals("Down")){
            slider.setValue(100);
            ivState.setImageResource(R.drawable.ic_blinds_0);
        } else {
            int number = Integer.parseInt(position.substring(0, position.length()-1));
            slider.setValue(100-number);
            if (number <= 35) {
                ivState.setImageResource(R.drawable.ic_blinds_25);
            } else if (number <= 65) {
                ivState.setImageResource(R.drawable.ic_blinds_50);
            }  else {
                ivState.setImageResource(R.drawable.ic_blinds_75);
            }
        }
    }
}