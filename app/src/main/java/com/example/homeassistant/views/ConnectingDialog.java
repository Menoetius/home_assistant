package com.example.homeassistant.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.homeassistant.R;
import com.example.homeassistant.adapters.CameraAdapter;

public class ConnectingDialog {

    private final Activity activity;
    private AlertDialog dialog;

    public ConnectingDialog(Activity activity) {
        this.activity = activity;
    }

    void startConnectingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.connecting_dialog, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    void dismissDialog() {
        dialog.dismiss();
    }
}
