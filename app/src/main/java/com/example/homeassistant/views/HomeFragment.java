package com.example.homeassistant.views;

import android.os.Bundle;
import android.system.ErrnoException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeassistant.R;
import com.example.homeassistant.adapters.ActivitiesAdapter;
import com.example.homeassistant.adapters.CameraAdapter;
import com.example.homeassistant.adapters.ScenesAdapter;
import com.example.homeassistant.devices.CameraDevice;
import com.example.homeassistant.helpers.DividerItemDecorator;
import com.example.homeassistant.helpers.JsonHelper;
import com.example.homeassistant.helpers.MqttHelper;
import com.example.homeassistant.model.Activity;
import com.example.homeassistant.model.BrokerData;
import com.example.homeassistant.services.MqttService;
import com.example.homeassistant.viewmodels.MainViewModel;
import com.example.homeassistant.model.Scene;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class HomeFragment extends Fragment {
    LinearLayoutManager HorizontalLayout;
    ScenesAdapter scenesAdapter;
    ActivitiesAdapter activitiesAdapter;
    CameraAdapter cameraAdapter;
    MqttService mService;
    MainViewModel model;
    View view;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ((MainActivity) getActivity()).setBottomNavigationVisibility(View.VISIBLE);

        model = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        mService = model.getBinder().getValue().getService();
        BrokerData data = model.getBrokerData().getValue();

        initActivities(view, data);
        initScenes(view, data);
        initCameras(view, data);

        return view;
    }

    private void initActivities(View view, BrokerData data) {
        RecyclerView rvActivities = view.findViewById(R.id.rvActivities);
        rvActivities.setClickable(true);
        activitiesAdapter = new ActivitiesAdapter(data.getActivities(), new ActivitiesAdapter.OnItemClickListener() {
            @Override public void onItemClick(Activity item, View view) {
                MqttHelper mqttHelper = model.getBinder().getValue().getService().getMqttHelper();
                mqttHelper.subscribeToTopic("BRQ/BUT/out", 2,null, new IMqttMessageListener(){
                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        model.getBrokerData().getValue().setActivities(JsonHelper.parseActivities(message.toString()));
                        if (model.getActualDevice().getValue().equals("activities")) {
                            model.setRefresh(model.getRefresh().getValue() + 1);
                        }
                    }
                });
                JSONObject obj = new JSONObject();
                try {
                    obj.put("type", "query_log");
                    obj.put("timestamp", Long.toString(System.currentTimeMillis()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mqttHelper.publishToTopic("BRQ/BUT/in", obj.toString(), 2);

                Navigation.findNavController(HomeFragment.this.view).navigate(HomeFragmentDirections.actionHomeFragmentToActivitiesFragment());
            }
        },this, 3);
        rvActivities.addItemDecoration(new DividerItemDecorator(ContextCompat.getDrawable(getActivity(), R.drawable.divider)));
        rvActivities.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvActivities.setAdapter(activitiesAdapter);

        model.getRefresh().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer data) {
                if (activitiesAdapter != null && !model.getBrokerData().getValue().getActivities().isEmpty()) {
                    activitiesAdapter.setDataSet(model.getBrokerData().getValue().getActivities());
                    activitiesAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void initScenes(View view, BrokerData data) {
        ArrayList<Scene> scenesDataSet = data.getScenes();
        RecyclerView sceneRecyclerView = view.findViewById(R.id.rvPresets);
        HorizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        sceneRecyclerView.setClickable(true);
        sceneRecyclerView.setLayoutManager(HorizontalLayout);
        scenesAdapter = new ScenesAdapter(scenesDataSet, new ScenesAdapter.OnItemClickListener() {
            @Override public void onItemClick(Scene item, View view) {
                model.getBinder().getValue().getService().getMqttHelper().publishToTopic("BRQ/BUT/in", item.getSetMessage(), 2);
                model.getBrokerData().getValue().setActiveScene(item.getId());
                scenesAdapter.notifyDataSetChanged();
            }
        },HomeFragment.this);
        sceneRecyclerView.setAdapter(scenesAdapter);
    }

    private void initCameras(View view, BrokerData data) {
        ArrayList<CameraDevice> camerasDataSet = data.getRoomCameras();
        RecyclerView roomRecyclerView = view.findViewById(R.id.rvRooms);
        roomRecyclerView.setClickable(true);
        HorizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        roomRecyclerView.setLayoutManager(HorizontalLayout);
        cameraAdapter = new CameraAdapter(camerasDataSet, new CameraAdapter.OnItemClickListener() {
            @Override public void onItemClick(final CameraDevice item, View view) {
                Map<String, String> map = item.getStream();

                if (map == null) {
                    Log.w("ERROR", getString(R.string.not_implemented));
                } else {
                    MqttHelper mqttHelper = model.getBinder().getValue().getService().getMqttHelper();
                    mqttHelper.subscribeToTopic(map.get("topicOut"), 2, null, new IMqttMessageListener(){
                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            HomeFragmentDirections.ActionHomeFragmentToFullscreenCameraFragment action =
                                    HomeFragmentDirections.actionHomeFragmentToFullscreenCameraFragment(item.getImage(), JsonHelper.getStreamUrl(message.toString()));
                            Navigation.findNavController(HomeFragment.this.view).navigate(action);
                        }
                    });
                    mqttHelper.publishToTopic(map.get("topicIn"), map.get("message"), 2);
                }
            }
        }, HomeFragment.this);
        roomRecyclerView.setAdapter(cameraAdapter);
    }
}