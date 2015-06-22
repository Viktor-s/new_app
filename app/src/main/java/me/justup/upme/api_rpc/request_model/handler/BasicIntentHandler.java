package me.justup.upme.api_rpc.request_model.handler;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

public abstract class BasicIntentHandler {
    private static final String TAG = BasicIntentHandler.class.getSimpleName();

    public static final int SUCCESS_RESPONSE = 0;
    public static final int FAILURE_RESPONSE = 1;

    private ResultReceiver mCallback = null;
    private int mResult;

    public final void execute(Intent intent, Application application, ResultReceiver callback) {
        this.mCallback = callback;

        doExecute(intent, application, callback);
    }

    public abstract void doExecute(Intent intent, Application application, ResultReceiver callback);

    public int getResult() {
        return mResult;
    }

    protected void sendUpdate(int resultCode, Bundle data) {
        mResult = resultCode;
        if (mCallback != null) {
            mCallback.send(mResult, data);
        }
    }
}
