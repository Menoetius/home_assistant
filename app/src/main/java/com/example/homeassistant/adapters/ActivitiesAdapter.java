package com.example.homeassistant.adapters;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeassistant.R;
import com.example.homeassistant.model.Activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.ViewHolder> {
    private final ActivitiesAdapter.OnItemClickListener listener;
    private ArrayList<Activity> activities;
    private final Fragment fragment;
    private final int limit;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        private final TextView tvTime;
        private final TextView tvMessage;

        public ViewHolder(View view) {
            super(view);
            tvTime = view.findViewById(R.id.tvTime);
            tvMessage = view.findViewById(R.id.tvMessage);
        }

        public void bind(final Activity item, final ActivitiesAdapter.OnItemClickListener listener, Fragment fragment) {
            cal.setTimeInMillis(Math.round(item.getTimestamp() * 1000));
            if (DateFormat.format("dd MMM", cal).toString().equals(DateFormat.format("dd MMM", new Date()).toString())) {
                tvTime.setText(DateFormat.format("hh:mm a", cal).toString());
            } else {
                tvTime.setText(DateFormat.format("dd MMM", cal).toString());
            }
            tvMessage.setText(item.getMessage());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item, v);
                }
            });
        }
    }

    public ActivitiesAdapter(ArrayList<Activity> dataSet, ActivitiesAdapter.OnItemClickListener listener, Fragment fragment, int limit) {
        if (dataSet.isEmpty()) {
            this.activities = new ArrayList<Activity> (Collections.singletonList(new Activity(System.currentTimeMillis() / 1000, fragment.getString(R.string.no_activities))));;
        } else {
            this.activities = dataSet;
        }
        this.fragment = fragment;
        this.listener = listener;
        this.limit = limit;
    }

    public void setDataSet(ArrayList<Activity> data) {
        this.activities = data;
    }

    @NonNull
    @Override
    public ActivitiesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.activity_item, viewGroup, false);

        return new ActivitiesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ActivitiesAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.bind(activities.get(position), listener, fragment);
    }

    @Override
    public int getItemCount() {
        if(limit != 0 && activities.size() > limit){
            return limit;
        } else {
            return activities.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    public interface OnItemClickListener {
        void onItemClick(Activity item, View view);
    }
}