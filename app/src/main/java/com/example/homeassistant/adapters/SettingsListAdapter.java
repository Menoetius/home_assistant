package com.example.homeassistant.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeassistant.R;
import com.example.homeassistant.SettingsItem;

import java.util.List;

public class SettingsListAdapter extends RecyclerView.Adapter<SettingsRecycleViewHolder> {
    private final List<SettingsItem> dataSet;
    private final SettingsItemOnClickListener settingsItemOnClickListener;

    public SettingsListAdapter(List<SettingsItem> data, SettingsItemOnClickListener listener) {
        dataSet = data;
        settingsItemOnClickListener = listener;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.settings_item;
    }

    @NonNull
    @Override
    public SettingsRecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new SettingsRecycleViewHolder(view, settingsItemOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsRecycleViewHolder holder, int position) {
        holder.bind(dataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface SettingsItemOnClickListener {
        void onItemClick(int position);
    }
}