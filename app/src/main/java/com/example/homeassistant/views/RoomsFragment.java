package com.example.homeassistant.views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.homeassistant.R;
import com.example.homeassistant.adapters.RoomDevicesAdapter;
import com.example.homeassistant.adapters.RoomsAdapter;
import com.example.homeassistant.adapters.ScenesAdapter;
import com.example.homeassistant.helpers.JsonHelper;
import com.example.homeassistant.model.BrokerData;
import com.example.homeassistant.model.DeviceModel;
import com.example.homeassistant.viewmodels.MainViewModel;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class RoomsFragment extends Fragment {
    private RecyclerView rvRoom;
    private MainViewModel model;
    RoomsAdapter roomsAdapter;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_rooms, container, false);
        model = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        model.setActualDevice("all");

        rvRoom = view.findViewById(R.id.rvRoom);
        initFragment();
        SnapHelper helper = new LinearSnapHelper(){
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY){
                if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
                    return RecyclerView.NO_POSITION;
                }

                final View currentItemView = findSnapView(layoutManager);
                if(currentItemView == null) {
                    return RecyclerView.NO_POSITION;
                }

                return layoutManager.getPosition(currentItemView);
            }
        };
        helper.attachToRecyclerView(rvRoom);
        return view;
    }

    private void initFragment(){
        LinearLayoutManager horizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvRoom.setLayoutManager(horizontalLayout);
        roomsAdapter = new RoomsAdapter(model.getBrokerData().getValue().getRoomsList(), new RoomDevicesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DeviceModel item, View view) {
                Map<String, String> map = item.getSwitchMessage(!item.isStateOn());

                if (map == null) {
                    Log.w("ERROR", "Device getSetMessage not working"); // @todo error
                } else {
                    ViewModelProviders.of(getActivity()).get(MainViewModel.class).getBinder().getValue().getService().getMqttHelper().publishToTopic(map.get("topicIn"), map.get("message"), 2);
                }
            }
        }, new RoomDevicesAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(DeviceModel item, View view) {
                ViewModelProviders.of(getActivity()).get(MainViewModel.class).setActualDevice(item.getId());
                switch(item.getType()) {
                    case "socket":
                    case "switch":
                        Navigation.findNavController(RoomsFragment.this.view).navigate(RoomsFragmentDirections.actionRoomsFragmentToSocketFragment());
                        break;
                    case "light_level":
                    case "light_rgb":
                        Navigation.findNavController(RoomsFragment.this.view).navigate(RoomsFragmentDirections.actionRoomsFragmentToLightFragment());
                        break;
                    case "blinds":
                        Navigation.findNavController(RoomsFragment.this.view).navigate(RoomsFragmentDirections.actionRoomsFragmentToBlindsFragment());
                        break;
                    case "sensor":
                        break;
                }
            }
        }, RoomsFragment.this);
        rvRoom.setAdapter(roomsAdapter);

        model.getRefresh().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer data) {
                if (roomsAdapter != null) {
                    roomsAdapter.notifyDataSetChanged();
                }
            }
        });
        model.setActualDevice("all");
    }

    public MainViewModel getModel() {
        return model;
    }
}