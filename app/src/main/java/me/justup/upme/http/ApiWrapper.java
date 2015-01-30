package me.justup.upme.http;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;

import me.justup.upme.entity.BaseHttpQueryEntity;
import me.justup.upme.entity.OnHttpFailureResponse;
import me.justup.upme.utils.AppContext;
import me.justup.upme.utils.AppPreferences;

import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class ApiWrapper {
    private static final String TAG = makeLogTag(ApiWrapper.class);

    private static final String JSON = "application/json";
    private static final String AUTHORIZATION_HEADER = "X-AUTH-UPMETOKEN";
    private static final String UTF_8 = "UTF-8";
    private static final String URL = "http://test.justup.me/uptabinterface/jsonrpc/";

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static SyncHttpClient syncClient = new SyncHttpClient();
    public static Gson gson = new Gson();

    // API methods constants
    public static final String AUTH_GET_VERIFICATION = "Auth.getVerificationPhoneCode";
    public static final String AUTH_CHECK_VERIFICATION = "Auth.checkVerificationPhoneCode";
    public static final String AUTH_GET_LOGGED_USER_INFO = "Auth.getLoggedUserInfo";
    public static final String ARTICLES_GET = "Articles.get";
    public static final String ARTICLES_GET_SHORT_DESCRIPTION = "Articles.getShortDescription";
    public static final String ACCOUNT_GET_PEOPLE_NETWORK = "Account.getPeopleNetwork";


    private static void post(final StringEntity se, AsyncHttpResponseHandler responseHandler) {
        client.addHeader(AUTHORIZATION_HEADER, getToken());
        client.post(null, URL, se, null, responseHandler);
    }

    private static void syncPost(final StringEntity se, AsyncHttpResponseHandler responseHandler) {
        syncClient.addHeader(AUTHORIZATION_HEADER, getToken());
        syncClient.post(null, URL, se, null, responseHandler);
    }

    private static void loginPost(final StringEntity se, AsyncHttpResponseHandler responseHandler) {
        client.removeAllHeaders();
        client.post(null, URL, se, null, responseHandler);
    }

    private static String getToken() {
        return new AppPreferences(AppContext.getAppContext()).getToken();
    }

    private static StringEntity queryBuilder(BaseHttpQueryEntity obj) {
        StringEntity mStringEntity;

        try {
            mStringEntity = new StringEntity(gson.toJson(obj));
        } catch (Exception e) {
            LOGE(TAG, "mStringEntity = new StringEntity(gson.toJson(obj))\n", e);
            return null;
        }

        mStringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, JSON));

        return mStringEntity;
    }

    public static void query(BaseHttpQueryEntity obj, AsyncHttpResponseHandler responseHandler) {
        StringEntity se = queryBuilder(obj);

        if (se != null)
            post(se, responseHandler);
    }

    /**
     * For services
     */
    public static void syncQuery(BaseHttpQueryEntity obj, AsyncHttpResponseHandler responseHandler) {
        StringEntity se = queryBuilder(obj);

        if (se != null)
            syncPost(se, responseHandler);
    }

    /**
     * Only for login!
     */
    public static void loginQuery(BaseHttpQueryEntity obj, AsyncHttpResponseHandler responseHandler) {
        StringEntity se = queryBuilder(obj);

        if (se != null)
            loginPost(se, responseHandler);
    }

    public static String responseBodyToString(byte[] responseBody) {
        String responseString = "";

        if (responseBody != null) {
            try {
                responseString = new String(responseBody, UTF_8);
            } catch (UnsupportedEncodingException e) {
                LOGE(TAG, "responseString = new String(responseBody, UTF-8)\n", e);
            }
        }

        return responseString;
    }

    public static String getResponseError(String content) {
        OnHttpFailureResponse errorResponse = null;

        try {
            errorResponse = gson.fromJson(content, OnHttpFailureResponse.class);
        } catch (Exception e) {
            LOGE(TAG, "gson.fromJson:\n" + content);
        }

        if (errorResponse != null && errorResponse.Error != null) {
            return errorResponse.Error;
        } else {
            return "Server error!";
        }
    }

}
