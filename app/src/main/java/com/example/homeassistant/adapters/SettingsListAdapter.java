package com.example.homeassistant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.homeassistant.R;
import com.example.homeassistant.devices.CameraDevice;
import com.example.homeassistant.helpers.GlideSignature;
import com.example.homeassistant.model.SettingsItem;

import java.util.ArrayList;
import java.util.List;

public class SettingsListAdapter extends RecyclerView.Adapter<SettingsListAdapter.ViewHolder>  {
    private final List<SettingsItem> dataSet;
    private final OnItemClickListener listener;

    public SettingsListAdapter(List<SettingsItem> data, SettingsListAdapter.OnItemClickListener listener) {
        dataSet = data;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.settings_item;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.settings_item, viewGroup, false);

        return new SettingsListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(dataSet.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void onItemClick(SettingsItem item, View view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private TextView itemTitle;
        private ImageView itemIcon;
        private OnItemClickListener onClickListener;
        private Context context;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            this.itemView = itemView;
            this.itemTitle = itemView.findViewById(R.id.tvSettingsItem);
            this.itemIcon = itemView.findViewById(R.id.ivSettingsItem);
        }

        public void bind(SettingsItem item, final SettingsListAdapter.OnItemClickListener listener) {
            this.itemIcon.setImageResource(item.getIcon());
            this.itemTitle.setText(item.getTitle());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item, v);
                }
            });
        }
    }
}