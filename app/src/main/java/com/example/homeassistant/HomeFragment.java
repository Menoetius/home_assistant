package com.example.homeassistant;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeassistant.devices.CameraDevice;
import com.example.homeassistant.model.BrokerData;
import com.example.homeassistant.model.MainViewModel;
import com.example.homeassistant.model.Scene;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private RecyclerView.Adapter roomAdapter;
    private RecyclerView.Adapter sceneAdapter;
    private MainViewModel model;
    private RecyclerView.LayoutManager layoutManager;
    LinearLayoutManager HorizontalLayout;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        model = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        BrokerData data = model.getBrokerData();
        ArrayList<CameraDevice> camerasDataSet = data.getRoomCameras();
        ArrayList<Scene> scenesDataSet = data.getScenes();

        roomAdapter = new RoomsAdapter(camerasDataSet, this);
        RecyclerView roomRecyclerView = view.findViewById(R.id.rvRooms);
//        recyclerView.setClickable(true);
        layoutManager = new LinearLayoutManager(view.getContext());
        HorizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        HorizontalLayout.setInitialPrefetchItemCount(5);
        roomRecyclerView.setLayoutManager(HorizontalLayout);
        roomRecyclerView.setAdapter(roomAdapter);

        sceneAdapter = new ScenesAdapter(scenesDataSet, this);
        RecyclerView sceneRecyclerView = view.findViewById(R.id.rvPresets);
//        recyclerView.seetClickable(true);
        layoutManager = new LinearLayoutManager(view.getContext());
        HorizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        HorizontalLayout.setInitialPrefetchItemCount(5);
        sceneRecyclerView.setLayoutManager(HorizontalLayout);
        sceneRecyclerView.setAdapter(sceneAdapter);

        return view;
    }
}