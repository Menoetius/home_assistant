package com.example.homeassistant.views;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.homeassistant.R;
import com.example.homeassistant.helpers.DatabaseHelper;
import com.example.homeassistant.model.ConnectionModel;
import com.example.homeassistant.services.MqttService;
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
    private WaitingDialog waitingDialog;
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
                closeKeyboard();

                db.addConnection(new ConnectionModel(-1, "", etUsername.getText().toString(), etPassword.getText().toString(), etPort.getText().toString().equals("") ? 8883 : Integer.parseInt(etPort.getText().toString()), etUrl.getText().toString(), sSSL.isChecked() ? "ssl" : ""));

                model.getBinder().getValue().getService().startMqtt();

                setObserver();
                connect();
            }
        });

        return view;
    }

    private void connect() {
        waitingDialog = new WaitingDialog(getActivity());
        waitingDialog.startWaitingDialog("");
    }

    private void setForm() {
        ConnectionModel connectionModel = db.getLast();

        if (connectionModel != null && !model.getIsLoggedIn()) {
            etUrl.setText(connectionModel.getUrl());
            etPort.setText(connectionModel.getPort()+"");
            etUsername.setText(connectionModel.getUserName());
            etPassword.setText(connectionModel.getPassword());
            sSSL.setChecked(connectionModel.getProtocol().equals("ssl"));

            setObserver();
            connect();
        }
    }

    private void setObserver() {
        model.getBinder().observe(getActivity(), new Observer<MqttService.MyBinder>() {
            @Override
            public void onChanged(@Nullable MqttService.MyBinder myBinder) {
                if(myBinder != null) {
                    if (myBinder.getService() != null) {
                        model.getBinder().getValue().getService().getConnected().observe(getActivity(), new Observer<String>() {
                            @Override
                            public void onChanged(String connected) {
                                if (connected.equals("connected") || connected.equals("failure")) {
                                    if (connected.equals("connected")) {
                                        model.setIsLoggedIn(true);
                                    }
                                    if (waitingDialog != null) {
                                        waitingDialog.dismissDialog();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        if (waitingDialog != null) {
            waitingDialog.dismissDialog();
        }
    }

    private void closeKeyboard() {
        View view = getView();
        if (view != null) {
            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}