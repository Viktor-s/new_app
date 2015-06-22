package me.justup.upme.api_rpc.request_model.service;

import android.content.Intent;
import android.os.Bundle;

public interface RequestServiceCallbackListener {
    public void onServiceCallback(int requestId, Intent requestIntent, int resultCode, Bundle data);
}
