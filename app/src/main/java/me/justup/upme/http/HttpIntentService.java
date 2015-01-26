package me.justup.upme.http;

import android.app.IntentService;
import android.content.Intent;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import me.justup.upme.entity.BaseHttpQueryEntity;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;

/**
 * <h3>Example (for fragment):</h3>
 * <code>
 * Bundle mBundle = new Bundle();<br>
 * mBundle.putSerializable(HttpIntentService.HTTP_INTENT_SERVICE_EXTRA, myEntity);<br>
 * Intent intent = new Intent(getActivity(), HttpIntentService.class);<br>
 * getActivity().startService(intent.putExtras(mBundle));<br>
 * </code>
 */
public class HttpIntentService extends IntentService {
    private static final String TAG = makeLogTag(HttpIntentService.class);
    public static final String HTTP_INTENT_SERVICE_EXTRA = "http_intent_service_extra";

    public HttpIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        BaseHttpQueryEntity mQueryEntity = (BaseHttpQueryEntity) intent.getSerializableExtra(HTTP_INTENT_SERVICE_EXTRA);
        ApiWrapper.syncQuery(mQueryEntity, new OnQueryResponse());
    }

    private class OnQueryResponse extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGD(TAG, "onSuccess(): " + content);

            // write to db, maybe send broadcast
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGE(TAG, "onFailure(): " + content);
        }
    }

}
