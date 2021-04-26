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
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;

import java.util.Map;

public class LightFragment extends Fragment {
    MqttService mService;
    MainViewModel model;
    View view;
    DeviceModel device;
    TextView tvState;
    ImageView ivState;
    Slider slider;
    ColorPickerView colorPickerView;

    public LightFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_light, container, false);
        TextView tvDeviceName = view.findViewById(R.id.tvSecurityName);

        ImageButton ibBack = view.findViewById(R.id.ibBack);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LightFragment.this.getActivity().onBackPressed();
            }
        });

        tvState = view.findViewById(R.id.tvState);
        ivState = view.findViewById(R.id.ivState);
        slider = view.findViewById(R.id.slider);
        colorPickerView = view.findViewById(R.id.cpvColorPicker);

        model = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        mService = model.getBinder().getValue().getService();
        device = model.getBrokerData().getValue().getDeviceById(model.getActualDevice().getValue());

        tvDeviceName.setText(device.getName());
        initState();
        initSlider();
        initColorPickerView();

        ivState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = device.getSwitchMessage(!device.isStateOn());

                if (map == null) {
                    Log.w("ERROR", "Device getSetMessage not working"); // @todo error
                } else {
                    mService.getMqttHelper().publishToTopic(map.get("topicIn"), map.get("message"), 2);
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
        String brightness = device.getStringValue("brightness");
        String color = device.getStringValue("color");
        if (device.isStateOn()) {
            tvState.setText(R.string.state_on);
            ivState.setImageResource(R.drawable.ic_power_on);
        } else {
            tvState.setText(R.string.state_off);
            ivState.setImageResource(R.drawable.ic_power_off);
        }
        int number = Integer.parseInt(brightness);
        slider.setValue(number);
        if (!color.equals("") && !color.equals("0")) {
            colorPickerView.setColor(Integer.parseInt(color), false);
        }
    }

    private void initSlider() {
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
                Map<String, String> map = device.getSetMessage("brightness", (int) (slider.getValue()));

                if (map == null) {
                    Log.w("ERROR", "Device getSetMessage not working"); // @todo error
                } else {
                    model.getBinder().getValue().getService().getMqttHelper().publishToTopic(map.get("topicIn"), map.get("message"), 2);
                }
            }
        });
    }

    private void initColorPickerView() {
        colorPickerView.setShowBorder(true);
        colorPickerView.addOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void onColorChanged(int selectedColor) {
                Map<String, String> map = device.getSetMessage("color", selectedColor);

                if (map == null) {
                    Log.w("ERROR", "Device getSetMessage not working"); // @todo error
                } else {
                    model.getBinder().getValue().getService().getMqttHelper().publishToTopic(map.get("topicIn"), map.get("message"), 2);
                }
            }
        });
    }
}