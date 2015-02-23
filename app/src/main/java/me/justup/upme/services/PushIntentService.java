package me.justup.upme.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import me.justup.upme.entity.SendNotificationQuery;
import me.justup.upme.http.ApiWrapper;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class PushIntentService extends IntentService {
    private static final String TAG = makeLogTag(PushIntentService.class);

    public static final String PUSH_INTENT_QUERY_EXTRA = "push_intent_query_extra";
    private Handler mHandler;


    public PushIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SendNotificationQuery push = (SendNotificationQuery) intent.getSerializableExtra(PUSH_INTENT_QUERY_EXTRA);
        ApiWrapper.syncQuery(push, new OnQueryResponse());
    }

    private class OnQueryResponse extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGD(TAG, "onSuccess(): " + content);

            makeToast(content);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGE(TAG, "onFailure(): " + content);
        }
    }

    private void makeToast(final String message) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PushIntentService.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
