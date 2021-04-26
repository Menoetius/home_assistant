package com.example.homeassistant.views;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
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

import com.example.homeassistant.R;
import com.example.homeassistant.adapters.ActivitiesAdapter;
import com.example.homeassistant.helpers.DividerItemDecorator;
import com.example.homeassistant.model.Activity;
import com.example.homeassistant.model.BrokerData;
import com.example.homeassistant.services.MqttService;
import com.example.homeassistant.viewmodels.MainViewModel;
import com.example.homeassistant.views.devices.SocketFragment;

import java.util.ArrayList;

public class ActivitiesFragment extends Fragment {
    View view;
    MqttService mService;
    MainViewModel model;
    ActivitiesAdapter activitiesAdapter;

    public ActivitiesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_activities, container, false);

        ImageButton ibBack = view.findViewById(R.id.ibBack);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitiesFragment.this.getActivity().onBackPressed();
            }
        });

        model = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        model.setActualDevice("activities");
        mService = model.getBinder().getValue().getService();
        BrokerData data = model.getBrokerData().getValue();

        initActivities(view, data);

        return view;
    }

    private void initActivities(View view, BrokerData data) {
        RecyclerView rvActivities = view.findViewById(R.id.rvActivities);
        rvActivities.setClickable(true);
        activitiesAdapter = new ActivitiesAdapter(data.getActivities(), new ActivitiesAdapter.OnItemClickListener() {
            @Override public void onItemClick(Activity item, View view) {
            }
        },this, 0);
        rvActivities.addItemDecoration(new DividerItemDecorator(ContextCompat.getDrawable(getActivity(), R.drawable.divider)));
        rvActivities.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvActivities.setAdapter(activitiesAdapter);

        model.getRefresh().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer data) {
                if (activitiesAdapter != null) {
                    Log.i("REFRESH", "!!!refresh activities!!!");
                    activitiesAdapter.setDataSet(model.getBrokerData().getValue().getActivities());
                    activitiesAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}