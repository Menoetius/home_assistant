package com.example.homeassistant;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsRecycleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private View itemView;
    private TextView itemTitle;
    private ImageView itemIcon;
    private SettingsListAdapter.SettingsItemOnClickListener onClickListener;
    private Context context;

    public SettingsRecycleViewHolder(@NonNull View itemView, SettingsListAdapter.SettingsItemOnClickListener listener) {
        super(itemView);
        context = itemView.getContext();
        this.itemView = itemView;
        this.itemTitle = itemView.findViewById(R.id.tvSettingsItem);
        this.itemIcon = itemView.findViewById(R.id.ivSettingsItem);
        this.onClickListener = listener;

        itemView.setOnClickListener(this);
    }

    public void bind(SettingsItem item) {
        this.itemIcon.setImageResource(item.getIcon());
        this.itemTitle.setText(item.getTitle());
    }

    @Override
    public void onClick(View view) {
        onClickListener.onItemClick(getAdapterPosition());

        final Intent intent;
        if (getAdapterPosition() == 0){
            intent =  new Intent(context, DevicesActivity.class);
        } else {
            intent =  new Intent(context, DevicesActivity.class);
        }
        context.startActivity(intent);
    }
}
