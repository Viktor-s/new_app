package me.justup.upme.services;

import android.app.IntentService;
import android.content.Intent;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import me.justup.upme.entity.BaseHttpQueryEntity;
import me.justup.upme.http.ApiWrapper;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class PushIntentService extends IntentService {
    private static final String TAG = makeLogTag(PushIntentService.class);

    public static final String PUSH_INTENT_QUERY_EXTRA = "push_intent_query_extra";

    public PushIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        BaseHttpQueryEntity push = (BaseHttpQueryEntity) intent.getSerializableExtra(PUSH_INTENT_QUERY_EXTRA);
        ApiWrapper.syncQuery(push, new OnQueryResponse());
    }

    private class OnQueryResponse extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGD(TAG, "onSuccess() : " + content);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGE(TAG, "onFailure() : " + content);
        }
    }

}
