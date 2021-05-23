package com.example.homeassistant.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.homeassistant.R;

public class WaitingDialog {

    private final Activity activity;
    private AlertDialog dialog;

    public WaitingDialog(Activity activity) {
        this.activity = activity;
    }

    void startWaitingDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.waiting_dialog, null);
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
