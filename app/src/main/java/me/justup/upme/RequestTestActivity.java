package me.justup.upme;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import me.justup.upme.api_rpc.request_model.service.RequestServiceCallbackListener;
import me.justup.upme.api_rpc.request_model.service.RequestServiceHelper;

import static me.justup.upme.utils.LogUtils.LOGI;

public class RequestTestActivity extends Activity implements RequestServiceCallbackListener {
    private static final String TAG = RequestTestActivity.class.getSimpleName();

    private RequestServiceHelper mRequestServiceHelper = null;

    public RequestServiceHelper getRequestServiceHelper() {
        return mRequestServiceHelper;
    }

    private OnRequestServiceListener mOnRequestServiceListener = null;

    public void setOnRequestServiceListener(OnRequestServiceListener listener) {
        this.mOnRequestServiceListener = listener;
    }

    public interface OnRequestServiceListener {
        void onServiceCallback(int requestId, Intent requestIntent, int resultCode, Bundle data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_test_activity);

        mRequestServiceHelper = JustUpApplication.getApplication().getApiHelper();

    }

    @Override
    protected void onResume() {
        super.onResume();

        mRequestServiceHelper.addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mRequestServiceHelper.removeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRequestServiceHelper.removeListener(this);
    }

    @Override
    public void onServiceCallback(int requestId, Intent requestIntent, int resultCode, Bundle data) {
        LOGI(TAG, "Request id : " + requestId + ", Intent : " + requestIntent + ", Result Code : " + resultCode + ", Bundle : " + data);

        if (mOnRequestServiceListener != null) {
            mOnRequestServiceListener.onServiceCallback(requestId, requestIntent, resultCode, data);
        }
    }
}
