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

import java.util.ArrayList;
import java.util.Map;

public class SocketParametersAdapter extends RecyclerView.Adapter<SocketParametersAdapter.ViewHolder> {
    private ArrayList<Map<String, String>> dataSet;
    private final Fragment fragment;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivIcon;
        private final TextView tvName;
        private final TextView tvValue;

        public ViewHolder(View view) {
            super(view);
            ivIcon = view.findViewById(R.id.ivIcon);
            tvName = view.findViewById(R.id.tvName);
            tvValue = view.findViewById(R.id.tvValue);
        }

        public void bind(final Map<String, String> item, Fragment fragment) {
            ivIcon.setImageResource(fragment.getResources().getIdentifier("@drawable/" + item.get("icon"), "drawable", fragment.getActivity().getPackageName()));
            tvName.setText(fragment.getResources().getIdentifier("@string/" + item.get("name"), "string", fragment.getActivity().getPackageName()));
            tvValue.setText(item.get("value"));
        }
    }

    public SocketParametersAdapter(ArrayList<Map<String, String>> dataSet, Fragment fragment) {
        this.dataSet = dataSet;
        this.fragment = fragment;
    }

    public void setDataSet(ArrayList<Map<String, String>> data) {
        this.dataSet = data;
    }

    @NonNull
    @Override
    public SocketParametersAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.rv_item_socket, viewGroup, false);

        return new SocketParametersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SocketParametersAdapter.ViewHolder viewHolder, final int position) {
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