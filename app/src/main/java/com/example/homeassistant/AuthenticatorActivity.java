package com.example.homeassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AuthenticatorActivity extends AppCompatActivity {

    public final static String ARG_ACCOUNT_TYPE = "smartHomeAssistant";
    public final static String ARG_AUTH_TYPE = "smartHomeAssistantUser";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

    public final static String PARAM_USER_PASS = "USER_PASS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}