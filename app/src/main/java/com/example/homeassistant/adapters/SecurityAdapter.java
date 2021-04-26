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
import com.example.homeassistant.model.Security;

import java.util.List;

public class SecurityAdapter extends RecyclerView.Adapter<SecurityAdapter.ViewHolder>{
    private final List<Security> values;
    private final SecurityAdapter.OnItemClickListener listener;
    private final SecurityAdapter.OnItemLongClickListener longListener;
    private final Fragment fragment;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivIcon;
        public TextView tvSecurityName;
        private final View dIsSelected;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            ivIcon = (ImageView) v.findViewById(R.id.ivIcon);
            tvSecurityName = (TextView) v.findViewById(R.id.tvSecurityName);
            dIsSelected = v.findViewById(R.id.dIsSelected);
        }

        public void bind(final Security item, final SecurityAdapter.OnItemClickListener listener, final SecurityAdapter.OnItemLongClickListener longListener, Fragment fragment) {
            int imageId = fragment.getResources().getIdentifier("@drawable/ic_" + item.getIcon(), "drawable", fragment.getActivity().getPackageName());
            ivIcon.setImageResource(imageId != 0 ? imageId : fragment.getResources().getIdentifier("@drawable/ic_moon", "drawable", fragment.getActivity().getPackageName()));
            tvSecurityName.setText(item.getName());
            if (item.getActive()) {
                dIsSelected.setVisibility(View.VISIBLE);
            } else {
                dIsSelected.setVisibility(View.INVISIBLE);
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

    public SecurityAdapter(List<Security> data, SecurityAdapter.OnItemClickListener listener, SecurityAdapter.OnItemLongClickListener longListener, Fragment fragment) {
        values = data;
        this.fragment = fragment;
        this.listener = listener;
        this.longListener = longListener;
    }

    @NonNull
    @Override
    public SecurityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.rv_item_security, parent, false);
        return new SecurityAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SecurityAdapter.ViewHolder holder, final int position) {
        holder.bind(values.get(position), listener, longListener, fragment);
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Security item, View view);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Security item, View view);
    }
}
