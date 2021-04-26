package com.example.homeassistant;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.homeassistant.adapters.SettingsListAdapter;

import java.util.LinkedList;
import java.util.List;


public class SettingsFragment extends Fragment implements SettingsListAdapter.SettingsItemOnClickListener {
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Add the following lines to create RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.rvItems);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setClickable(true);
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mAdapter = new SettingsListAdapter(getListData(), this);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    private List<SettingsItem> getListData(){
        List<SettingsItem> items = new LinkedList<SettingsItem>();

        SettingsItem item = new SettingsItem();
        item.setIcon(R.drawable.ic_devices);
        item.setTitle(getString(R.string.devices));

        items.add(item);

        item = new SettingsItem();
        item.setIcon(R.drawable.ic_family);
        item.setTitle(getString(R.string.family));

        items.add(item);

        item = new SettingsItem();
        item.setIcon(R.drawable.ic_presets);
        item.setTitle(getString(R.string.presets));

        items.add(item);
        return items;
    }

    @Override
    public void onItemClick(int position) {

    }
}