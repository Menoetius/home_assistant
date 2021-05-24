package com.example.homeassistant.views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.homeassistant.R;
import com.example.homeassistant.devices.CameraDevice;
import com.example.homeassistant.model.SettingsItem;
import com.example.homeassistant.adapters.SettingsListAdapter;
import com.example.homeassistant.viewmodels.MainViewModel;

import java.util.LinkedList;
import java.util.List;

public class SettingsFragment extends Fragment {
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    MainViewModel model;
    View view;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rvItems);
        recyclerView.setHasFixedSize(true);
        recyclerView.setClickable(true);
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mAdapter = new SettingsListAdapter(getListData(), new SettingsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SettingsItem item, View view) {
                if (item.getDestinationFragment() == null) {
                    Toast.makeText(getContext(), getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                } else {
                    if (item.getTitle().equals(getString(R.string.logout))) {
                        model.getBinder().getValue().getService().getMqttHelper().disconnect();
                        Toast.makeText(getContext(), getString(R.string.disconnected), Toast.LENGTH_SHORT).show();
                    }
                    Navigation.findNavController(SettingsFragment.this.view).navigate(SettingsFragment.this.getResources().getIdentifier(item.getDestinationFragment(), "id", getActivity().getPackageName()));
                }
            }
        });
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

        item = new SettingsItem();
        item.setIcon(R.drawable.ic_logout);
        item.setTitle(getString(R.string.logout));
        item.setDestinationFragment("action_SettingsFragment_to_loginFragment");

        items.add(item);
        return items;
    }
}