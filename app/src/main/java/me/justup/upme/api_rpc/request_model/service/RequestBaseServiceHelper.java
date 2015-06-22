package me.justup.upme.api_rpc.request_model.service;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import me.justup.upme.api_rpc.request_model.RequestService;

import static me.justup.upme.utils.LogUtils.LOGD;

public class RequestBaseServiceHelper {
    private static final String TAG = RequestBaseServiceHelper.class.getSimpleName();

    public ArrayList<RequestServiceCallbackListener> mCurrentListeners = new ArrayList<RequestServiceCallbackListener>();
    public SparseArray<Intent> mPendingActivities = new SparseArray<Intent>();
    public AtomicInteger mIdCounter = new AtomicInteger();
    public Application mApplication = null;

    public RequestBaseServiceHelper(Application app) {
        this.mApplication = app;
    }

    protected int runRequest(final int requestId, Intent intent) {
        LOGD(TAG, "Request id : " + String.valueOf(requestId) + "\nIntent Action : " + intent.getAction());

        mPendingActivities.append(requestId, intent);
        LOGD(TAG, "Request started Services : " + mPendingActivities.toString());
        mApplication.startService(intent); // TODO. This loop same time.

        return requestId;
    }

    protected Intent createIntent(final Context context, String action, final int requestId) {
        Intent intent = new Intent(context, RequestService.class);
        intent.setAction(action);
        intent.putExtra(RequestService.EXTRA_STATUS_RECEIVER, new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                Intent originalIntent = mPendingActivities.get(requestId);
                if (isPending(requestId)) {
                    mPendingActivities.remove(requestId);

                    for (RequestServiceCallbackListener currentListener : mCurrentListeners) {
                        if (currentListener != null) {
                            currentListener.onServiceCallback(requestId, originalIntent, resultCode, resultData);
                        }
                    }
                }
            }
        });

        return intent;
    }

    protected int createId() {
        return mIdCounter.getAndIncrement();
    }

    public void addListener(RequestServiceCallbackListener listener) {
        mCurrentListeners.add(listener);
    }

    public void removeListener(RequestServiceCallbackListener listener) {
        mCurrentListeners.remove(listener);
    }

    public boolean isPending(int requestId) {
        return mPendingActivities.get(requestId) != null;
    }
}
