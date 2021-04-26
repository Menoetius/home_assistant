package com.example.homeassistant.adapters;

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

import java.util.ArrayList;


public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.ViewHolder> {
    private final ArrayList<CameraDevice> deviceModels;
    private final OnItemClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView roomName;
        private final ImageView roomImage;
        private final TextView movement;

        public ViewHolder(View view) {
            super(view);

            roomName = view.findViewById(R.id.tvRoomName);
            roomImage = view.findViewById(R.id.ivIcon);
            movement = view.findViewById(R.id.tvSecurityName);
        }

        public void bind(final CameraDevice item, final OnItemClickListener listener) {
            roomName.setText(item.getName());
            Glide.with(itemView.getContext())
                    .load(item.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(
                            Glide.with(itemView.getContext())
                                    .load(item.getImage())
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .override(100))
                    .signature(new GlideSignature((int) System.currentTimeMillis() / 10000))
                    .into(roomImage);
            movement.setText(item.getMessage());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item, v);
                }
            });
        }
    }

    public CameraAdapter(ArrayList<CameraDevice> dataSet, OnItemClickListener listener) {
        deviceModels = dataSet;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.camera_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.bind(deviceModels.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return deviceModels.size();
    }

    public interface OnItemClickListener {
        void onItemClick(CameraDevice item, View view);
    }
}