package com.example.homeassistant.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeassistant.R;
import com.example.homeassistant.model.Room;
import com.example.homeassistant.views.RoomsFragmentDirections;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.ViewHolder> {
    private ArrayList<Room> rooms;
    private final Fragment fragment;
    private final RoomDevicesAdapter.OnItemClickListener listener;
    private final RoomDevicesAdapter.OnItemLongClickListener longListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        String[] backgrounds = new String[] {"bg_blue_gradient", "bg_red_gradient", "bg_green_gradient", "bg_purple_gradient"};

        private final TextView tvRoomsName;
        private final RecyclerView rvRoomsDevices;
        private final ImageButton ibSettings;
        private final Fragment fragment;
        private RecyclerView.Adapter mAdapter;
        private final RoomDevicesAdapter.OnItemClickListener listener;
        private final RoomDevicesAdapter.OnItemLongClickListener longListener;
        private View view;

        public ViewHolder(View view, RoomDevicesAdapter.OnItemClickListener listener, RoomDevicesAdapter.OnItemLongClickListener longListener, Fragment fragment, ArrayList<Room> rooms) {
            super(view);

            this.view = view;
            ibSettings = view.findViewById(R.id.ibSettings);
            tvRoomsName = view.findViewById(R.id.tvRoomName);
            rvRoomsDevices = view.findViewById(R.id.rvRoomsDevices);
            this.fragment = fragment;
            this.listener = listener;
            this.longListener = longListener;
        }

        public void bind(final Room item, int position) {
            tvRoomsName.setText(item.getName());
            view.setBackgroundResource(fragment.getResources().getIdentifier("@drawable/" + backgrounds[position % 4], "drawable", fragment.getActivity().getPackageName()));
//            ibSettings.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    RoomsFragmentDirections.ActionRoomsFragmentToEditRoomFragment action =
//                            RoomsFragmentDirections.actionRoomsFragmentToEditRoomFragment(item.getId());
//                    Navigation.findNavController(fragment.getView()).navigate(action);
//                }
//            });
            ImageButton ibSettings = view.findViewById(R.id.ibSettings);
            ibSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(fragment.getContext(), fragment.getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
                }
            });

            initRecyclerView(item);
        }

        private void initRecyclerView(Room item){
            mAdapter = new RoomDevicesAdapter(item.getDevices().stream().filter(device -> !device.getType().equals("sensor")).collect(Collectors.toList()), listener, longListener, fragment);
            rvRoomsDevices.setAdapter(mAdapter);
        }
    }

    public RoomsAdapter(ArrayList<Room> dataSet, RoomDevicesAdapter.OnItemClickListener listener, RoomDevicesAdapter.OnItemLongClickListener longListener, Fragment fragment) {
        rooms = dataSet;
        this.fragment = fragment;
        this.listener = listener;
        this.longListener = longListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.room_item, viewGroup, false);

        return new ViewHolder(view, listener, longListener, fragment, rooms);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.bind(rooms.get(position), position);
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }
}

