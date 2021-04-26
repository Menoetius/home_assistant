package com.example.homeassistant.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeassistant.R;
import com.example.homeassistant.model.Alert;
import java.util.ArrayList;

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.ViewHolder> {
    private ArrayList<Alert> dataSet;
    private final Fragment fragment;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivIcon;
        private final TextView tvMessage;

        public ViewHolder(View view) {
            super(view);
            ivIcon = view.findViewById(R.id.ivIcon);
            tvMessage = view.findViewById(R.id.tvName);
        }

        public void bind(final Alert item, Fragment fragment) {
            ivIcon.setImageResource(fragment.getResources().getIdentifier("@drawable/" + (item.getStatus().equals("ok") ? "ic_checkmark" : "ic_error"), "drawable", fragment.getActivity().getPackageName()));
            tvMessage.setText(item.getMessage());
        }
    }

    public AlertsAdapter(ArrayList<Alert> dataSet, Fragment fragment) {
        this.dataSet = dataSet;
        this.fragment = fragment;
    }

    public void setDataSet(ArrayList<Alert> data) {
        this.dataSet = data;
    }

    @NonNull
    @Override
    public AlertsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.rv_item_alert, viewGroup, false);

        return new AlertsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlertsAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.bind(dataSet.get(position), fragment);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }
}
