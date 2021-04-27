package com.example.homeassistant.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.homeassistant.R;
import com.example.homeassistant.adapters.CameraAdapter;

public class ConnectingDialog {

    private final Activity activity;
    private AlertDialog dialog;

    public ConnectingDialog(Activity activity) {
        this.activity = activity;
    }

    void startConnectingDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.connecting_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);

        if (message != null && !message.equals("")) {
            TextView tvMessage = view.findViewById(R.id.tvMessage);
            tvMessage.setText(message);
        }

        dialog = builder.create();
        dialog.show();
    }

    void dismissDialog() {
        dialog.dismiss();
    }
}
