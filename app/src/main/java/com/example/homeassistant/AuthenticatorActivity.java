package com.example.homeassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.AccountManager;
import android.os.Bundle;
import android.widget.EditText;

public class AuthenticatorActivity extends AppCompatActivity {

    public final static String ARG_ACCOUNT_TYPE = "smartHomeAssistant";
    public final static String ARG_AUTH_TYPE = "smartHomeAssistantUser";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    private AccountManager mAccountManager;

    private String login;
    private String password;

    private EditText etLogin;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}