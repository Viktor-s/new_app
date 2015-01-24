package me.justup.upme.http;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import me.justup.upme.entity.BaseHttpQueryEntity;
import me.justup.upme.utils.AppContext;
import me.justup.upme.utils.AppPreferences;

import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class ApiWrapper {
    private static final String TAG = makeLogTag(ApiWrapper.class);
    private static final String JSON = "application/json";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String URL = "http://test.justup.me/uptabinterface/jsonrpc/";
    private static AsyncHttpClient client = new AsyncHttpClient();
    public static Gson gson = new Gson();

    // API methods constants
    public static final String AUTH_GET_VERIFICATION = "Auth.getVerificationPhoneCode";
    public static final String AUTH_CHECK_VERIFICATION = "Auth.checkVerificationPhoneCode";
    public static final String AUTH_GET_USER_INFO = "Auth.getLoggedUserInfo";
    public static final String ARTICLES_GET = "Articles.get";
    public static final String ARTICLES_GET_SHORT_DESCRIPTION = "Articles.getShortDescription";


    private static void post(final StringEntity se, AsyncHttpResponseHandler responseHandler) {
        String token = new AppPreferences(AppContext.getAppContext()).getToken();

        client.addHeader(AUTHORIZATION_HEADER, token);
        client.post(null, URL, se, null, responseHandler);
    }

    public static void query(BaseHttpQueryEntity obj, AsyncHttpResponseHandler responseHandler) {
        StringEntity mStringEntity;

        try {
            mStringEntity = new StringEntity(gson.toJson(obj));
        } catch (Exception e) {
            LOGE(TAG, "mStringEntity = new StringEntity(gson.toJson(obj))\n", e);
            return;
        }

        mStringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, JSON));
        post(mStringEntity, responseHandler);
    }

}
