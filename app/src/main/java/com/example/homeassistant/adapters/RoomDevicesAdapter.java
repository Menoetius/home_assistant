package com.example.homeassistant.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeassistant.R;
import com.example.homeassistant.model.DeviceModel;

public class RoomDevicesAdapter extends RecyclerView.Adapter<RoomDevicesAdapter.ViewHolder> {
    private final List<DeviceModel> values;
    private final OnItemClickListener listener;
    private final OnItemLongClickListener longListener;
    private final Fragment fragment;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivDeviceIcon;
        public TextView tvDeviceName;
        public TextView tvDeviceStatus;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            ivDeviceIcon = v.findViewById(R.id.ivIcon);
            tvDeviceName = v.findViewById(R.id.tvSecurityName);
            tvDeviceStatus = v.findViewById(R.id.tvDeviceStatus);
        }

        public void bind(final DeviceModel item, final RoomDevicesAdapter.OnItemClickListener listener, final RoomDevicesAdapter.OnItemLongClickListener longListener, Fragment fragment) {
            int imageId = fragment.getResources().getIdentifier("@drawable/ic_" + item.getType(), "drawable", fragment.getActivity().getPackageName());
            ivDeviceIcon.setImageResource(imageId != 0 ? imageId : fragment.getResources().getIdentifier("@drawable/ic_moon", "drawable", fragment.getActivity().getPackageName()));
            tvDeviceName.setText(item.getName());
            tvDeviceStatus.setText(item.getFeaturedValue());

            setBackgroundColor(itemView.getBackground(), item.isStateOn(), fragment.getActivity());
            if (!item.isStateOn()) {
                ColorMatrix colorMatrix = new ColorMatrix();
                colorMatrix.setSaturation(0);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
                tvDeviceName.setTextColor(ContextCompat.getColor(fragment.getContext(), R.color.colorDeviceNameLight));
                tvDeviceStatus.setTextColor(ContextCompat.getColor(fragment.getContext(), R.color.colorDeviceStateLight));
                ivDeviceIcon.setColorFilter(filter);
                ivDeviceIcon.setImageAlpha(130);
            }


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item, v);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longListener.onItemLongClick(item, v);
                    return true;
                }
            });
        }
    }

    public RoomDevicesAdapter(List<DeviceModel> myDataset, RoomDevicesAdapter.OnItemClickListener listener, RoomDevicesAdapter.OnItemLongClickListener longListener, Fragment fragment) {
        values = myDataset;
        this.fragment = fragment;
        this.listener = listener;
        this.longListener = longListener;
    }

    @NonNull
    @Override
    public RoomDevicesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.rooms_device_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.bind(values.get(position), listener, longListener, fragment);
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public interface OnItemClickListener {
        void onItemClick(DeviceModel item, View view);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(DeviceModel item, View view);
    }

    private void setBackgroundColor(Drawable background, boolean state, Context mContext) {
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable)background).getPaint().setColor(ContextCompat.getColor(mContext, state ? R.color.deviceOn : R.color.deviceOff));
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable)background).setColor(ContextCompat.getColor(mContext, state ? R.color.deviceOn : R.color.deviceOff));
        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable)background).setColor(ContextCompat.getColor(mContext, state ? R.color.deviceOn : R.color.deviceOff));
        }
    }
}