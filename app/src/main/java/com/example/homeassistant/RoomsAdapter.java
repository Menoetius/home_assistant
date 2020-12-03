package com.example.homeassistant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homeassistant.devices.CameraDevice;

import java.util.ArrayList;


public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.ViewHolder> {

    private ArrayList<CameraDevice> deviceModels;
    private final Fragment fragment;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView roomName;
        private final ImageView roomImage;
        private final TextView movement;

        public ViewHolder(View view) {
            super(view);

            roomName = view.findViewById(R.id.tvRoomName);
            roomImage = view.findViewById(R.id.ivIcon);
            movement = view.findViewById(R.id.tvSceneName);
        }

        public TextView getRoomName() {
            return roomName;
        }

        public ImageView getImageView() {
            return roomImage;
        }

        public TextView getMovement() {
            return movement;
        }
    }

    public RoomsAdapter(ArrayList<CameraDevice> dataSet, Fragment fragment) {
        deviceModels = dataSet;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.room_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getRoomName().setText(deviceModels.get(position).getName());
        Glide.with(fragment).load(deviceModels.get(position).getImage()).into(viewHolder.getImageView());
        viewHolder.getMovement().setText(deviceModels.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return deviceModels.size();
    }
}