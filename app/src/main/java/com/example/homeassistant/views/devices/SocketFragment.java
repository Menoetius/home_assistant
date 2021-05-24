package com.example.homeassistant.views.devices;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homeassistant.R;
import com.example.homeassistant.adapters.SocketParametersAdapter;
import com.example.homeassistant.model.DeviceModel;
import com.example.homeassistant.services.MqttService;
import com.example.homeassistant.viewmodels.MainViewModel;

import java.util.Map;

public class SocketFragment extends Fragment {
    MqttService mService;
    MainViewModel model;
    View view;
    DeviceModel device;
    TextView tvState;
    ImageView ivState;
    SocketParametersAdapter mAdapter;

    public SocketFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_socket, container, false);
        TextView tvDeviceName = view.findViewById(R.id.tvSecurityName);

        ImageButton ibBack = view.findViewById(R.id.ibBack);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketFragment.this.getActivity().onBackPressed();
            }
        });

        ImageButton ibSettings = view.findViewById(R.id.ibSettings);
        ibSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
            }
        });

        tvState = view.findViewById(R.id.tvState);
        ivState = view.findViewById(R.id.ivState);

        model = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        mService = model.getBinder().getValue().getService();
        device = model.getBrokerData().getValue().getDeviceById(model.getActualDevice().getValue());

        tvDeviceName.setText(device.getName());
        initParameters();
        initState();

        ivState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = device.getSwitchMessage(!device.isStateOn());

                if (map == null) {
                    Log.w("ERROR", getString(R.string.not_implemented));
                } else {
                    mService.getMqttHelper().publishToTopic(map.get("topicIn"), map.get("message"), 2);
                }
            }
        });

        model.getRefresh().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer data) {
                if (mAdapter != null) {
                    mAdapter.setDataSet(device.getAttributesListForRv());
                    mAdapter.notifyDataSetChanged();
                    initState();
                }
            }
        });

        return view;
    }

    private void initState() {
        if (device.isStateOn()) {
            tvState.setText(R.string.state_on);
            ivState.setImageResource(R.drawable.ic_power_on);
        } else {
            tvState.setText(R.string.state_off);
            ivState.setImageResource(R.drawable.ic_power_off);
        }
    }

    private void initParameters() {
        RecyclerView rvSocketAttributes = view.findViewById(R.id.rvSocketAttributes);
        rvSocketAttributes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new SocketParametersAdapter(device.getAttributesListForRv(), this);
        rvSocketAttributes.setAdapter(mAdapter);
    }
}