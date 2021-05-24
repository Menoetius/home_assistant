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
import com.example.homeassistant.model.Scene;

import java.util.ArrayList;

public class ScenesAdapter extends RecyclerView.Adapter<ScenesAdapter.ViewHolder> {
    private final ScenesAdapter.OnItemClickListener listener;
    private final ArrayList<Scene> scenes;
    private final Fragment fragment;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView icon;
        private final TextView sceneName;
        private final View dIsSelected;

        public ViewHolder(View view) {
            super(view);
            sceneName = view.findViewById(R.id.tvSecurityName);
            icon = view.findViewById(R.id.ivIcon);
            dIsSelected = view.findViewById(R.id.dIsSelected);
        }

        public void bind(final Scene item, final ScenesAdapter.OnItemClickListener listener, Fragment fragment) {
            sceneName.setText(item.getName());
            icon.setImageResource(fragment.getResources().getIdentifier("@drawable/ic_" + item.getIcon(), "drawable", fragment.getActivity().getPackageName()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item, v);
                }
            });
            if (item.getActive()) {
                dIsSelected.setVisibility(View.VISIBLE);
            } else {
                dIsSelected.setVisibility(View.INVISIBLE);
            }
        }
    }

    public ScenesAdapter(ArrayList<Scene> dataSet, ScenesAdapter.OnItemClickListener listener, Fragment fragment) {
        this.scenes = dataSet;
        this.fragment = fragment;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScenesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.scene_item, viewGroup, false);

        return new ScenesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ScenesAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.bind(scenes.get(position), listener, fragment);
    }

    @Override
    public int getItemCount() {
        return scenes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    public interface OnItemClickListener {
        void onItemClick(Scene item, View view);
    }
}