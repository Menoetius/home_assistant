package com.example.homeassistant.internal;

import com.example.homeassistant.model.ReceivedMessage;

public interface IReceivedMessageListener {

    void onMessageReceived(ReceivedMessage message);
}