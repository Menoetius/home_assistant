package com.example.homeassistant.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.homeassistant.R;
import com.example.homeassistant.viewmodels.MainViewModel;

public class BrokerAlertDialog {

    private final Activity activity;
    private AlertDialog dialog;
    private final String alertText;

    public BrokerAlertDialog(Activity activity, String alertText) {
        this.activity = activity;
        this.alertText = alertText;
    }

    void startBrokerAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.broker_alert_dialog, null);
        builder.setView(view);
        builder.setCancelable(true);

        dialog = builder.create();

        Button bCancel = view.findViewById(R.id.bCancel);
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                ViewModelProviders.of((FragmentActivity) activity).get(MainViewModel.class).getBinder().getValue().getService().getNotificationManager().cancelAll();
            }
        });

        TextView tvAlertText = view.findViewById(R.id.tvAlertText);
        tvAlertText.setText(alertText);

        dialog.show();
    }

    void dismissDialog() {
        dialog.dismiss();
    }
}
