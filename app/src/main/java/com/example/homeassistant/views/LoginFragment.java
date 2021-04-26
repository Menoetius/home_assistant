package com.example.homeassistant.views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.example.homeassistant.R;
import com.example.homeassistant.helpers.DatabaseHelper;
import com.example.homeassistant.model.ConnectionModel;
import com.example.homeassistant.viewmodels.MainViewModel;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class LoginFragment extends Fragment {
    private View view;
    private DatabaseHelper db;
    private EditText etUrl;
    private EditText etPort;
    private EditText etUsername;
    private EditText etPassword;
    private SwitchMaterial sSSL;
    private ConnectingDialog connectingDialog;
    MainViewModel model;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        db = new DatabaseHelper(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        ((MainActivity) getActivity()).setBottomNavigationVisibility(View.INVISIBLE);

        Button bConnect = view.findViewById(R.id.btnConnect);
        etUrl = view.findViewById(R.id.etUrl);
        etPort = view.findViewById(R.id.etPort);
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        sSSL = view.findViewById(R.id.sSSL);

        setForm();

        bConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.addConnection(new ConnectionModel(-1, "", etUsername.getText().toString(), etPassword.getText().toString(), Integer.parseInt(etPort.getText().toString()), etUrl.getText().toString(), sSSL.isChecked() ? "ssl" : ""));

                model.getBinder().getValue().getService().startMqtt();
            }
        });

        return view;
    }

    private void connect() {
        connectingDialog = new ConnectingDialog(getActivity());
        connectingDialog.startConnectingDialog();
    }

    private void setForm() {
        ConnectionModel connectionModel = db.getLast();

        if (connectionModel != null) {
            etUrl.setText(connectionModel.getUrl());
            etPort.setText(connectionModel.getPort()+"");
            etUsername.setText(connectionModel.getUserName());
            etPassword.setText(connectionModel.getPassword());
            sSSL.setChecked(connectionModel.getProtocol().equals("ssl"));
        }
    }
}