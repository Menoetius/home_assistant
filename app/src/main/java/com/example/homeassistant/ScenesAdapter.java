package com.example.homeassistant;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homeassistant.model.Scene;

import java.util.ArrayList;

public class ScenesAdapter extends RecyclerView.Adapter<ScenesAdapter.ViewHolder> {

    private ArrayList<Scene> scenes;
    private final Fragment fragment;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView icon;
        private final TextView sceneName;

        public ViewHolder(View view) {
            super(view);

            sceneName = view.findViewById(R.id.tvSceneName);
            icon = view.findViewById(R.id.ivIcon);
        }

        public TextView getSceneName() {
            return sceneName;
        }

        public ImageView getIcon() {
            return icon;
        }
    }

    public ScenesAdapter(ArrayList<Scene> dataSet, Fragment fragment) {
        scenes = dataSet;
        this.fragment = fragment;
    }

    @Override
    public ScenesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.scene_item, viewGroup, false);

        return new ScenesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ScenesAdapter.ViewHolder viewHolder, final int position) {
        Log.w("SCENES", "" + fragment.getResources().getIdentifier("ic_moon" + scenes.get(position).getIcon(), "drawable", fragment.getContext().getPackageName()));
        int imgId = fragment.getResources().getIdentifier("@drawable/ic_" + scenes.get(position).getIcon(), null, null);
        viewHolder.getIcon().setImageResource(imgId);
//        Glide.with(fragment).load(fragment.getResources().getIdentifier("app_icon", null, null)).into(viewHolder.getIcon());
        viewHolder.getSceneName().setText(scenes.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return scenes.size();
    }
}
