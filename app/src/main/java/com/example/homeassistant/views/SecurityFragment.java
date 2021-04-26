package com.example.homeassistant.views;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.homeassistant.R;
import com.example.homeassistant.adapters.ActivitiesAdapter;
import com.example.homeassistant.adapters.AlertsAdapter;
import com.example.homeassistant.adapters.ScenesAdapter;
import com.example.homeassistant.adapters.SecurityAdapter;
import com.example.homeassistant.adapters.SocketParametersAdapter;
import com.example.homeassistant.helpers.DividerItemDecorator;
import com.example.homeassistant.model.BrokerData;
import com.example.homeassistant.model.Scene;
import com.example.homeassistant.model.Security;
import com.example.homeassistant.services.MqttService;
import com.example.homeassistant.viewmodels.MainViewModel;

public class SecurityFragment extends Fragment {
    SecurityAdapter securityAdapter;
    AlertsAdapter alertsAdapter;
    MqttService mService;
    MainViewModel model;
    View view;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        view = inflater.inflate(R.layout.fragment_security, container, false);

        model = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        mService = model.getBinder().getValue().getService();
        BrokerData data = model.getBrokerData().getValue();

        initSecurity(data);
        initAlerts(data);

        return view;
    }

    private void initSecurity(BrokerData data) {
        RecyclerView rvSecurity = view.findViewById(R.id.rvSecurity);
        securityAdapter = new SecurityAdapter(data.getSecurityList(), new SecurityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Security item, View view) {
                if (!item.getActive()) {
                    model.getBinder().getValue().getService().getMqttHelper().publishToTopic("BRQ/BUT/security/in", item.getSetMessage(true), 2);
                    model.getBrokerData().getValue().setActiveSecurity(item.getId());
                    securityAdapter.notifyDataSetChanged();
                }
            }
        },null, this);
        rvSecurity.setAdapter(securityAdapter);
    }

    private void initAlerts(BrokerData data) {
        RecyclerView rvAlerts = view.findViewById(R.id.rvAlerts);
        rvAlerts.setClickable(true);
        rvAlerts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvAlerts.addItemDecoration(new DividerItemDecorator(ContextCompat.getDrawable(getActivity(), R.drawable.divider)));
        alertsAdapter = new AlertsAdapter(data.getAlertsList(), this);
        rvAlerts.setAdapter(alertsAdapter);

        model.getRefresh().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer data) {
                if (alertsAdapter != null) {
                    alertsAdapter.setDataSet(model.getBrokerData().getValue().getAlertsList());
                    alertsAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}