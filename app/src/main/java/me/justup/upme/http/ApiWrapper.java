package me.justup.upme.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import me.justup.upme.JustUpApplication;
import me.justup.upme.R;
import me.justup.upme.entity.BaseHttpQueryEntity;
import me.justup.upme.entity.OnHttpFailureResponse;
import me.justup.upme.utils.ServerSwitcher;

import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;

public class ApiWrapper {
    private static final String TAG = makeLogTag(ApiWrapper.class);

    private static final String JSON = "application/json";
    private static final String AUTHORIZATION_HEADER = "X-AUTH-UPMETOKEN";
    private static final String UTF_8 = "UTF-8";

    // private static final String URL = "http://test.justup.me/uptabinterface/jsonrpc/";
    // private static final String CLOUD_STORAGE_URL = "http://test.justup.me/CloudStorage";
    private static ServerSwitcher serverSwitcher = ServerSwitcher.getInstance();

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static AsyncHttpClient syncClient = new SyncHttpClient();
    public static final Gson gson = new Gson();

    private static final int SOCKET_TIMEOUT = 1000 * 30; // 30 sec

    // API methods constants
    public static final String AUTH_GET_VERIFICATION = "Auth.getVerificationPhoneCode";
    public static final String AUTH_CHECK_VERIFICATION = "Auth.checkVerificationPhoneCode";
    public static final String AUTH_GET_LOGGED_USER_INFO = "Auth.getLoggedUserInfo";

    public static final String ARTICLES_GET_SHORT_DESCRIPTION = "Articles.getShortDescription";
    public static final String ARTICLE_GET_FULL_DESCRIPTION = "Articles.find";
    public static final String ARTICLE_FULL_GET_COMMENTS = "ArticleComments.getByArticleId";
    public static final String ARTICLE_ADD_COMMENT = "ArticleComments.add";

    public static final String ACCOUNT_GET_ALL_CONTACTS = "Account.getAllAllowedContacts";
    public static final String ACCOUNT_GET_REFERRALS_BY_ID = "Account.getReferralsById";
    public static final String ACCOUNT_ADD_REFERRAL = "Account.addReferral";
    public static final String ACCOUNT_GET_USER_PANEL_INFO = "Account.getUserPanelInfo";
    public static final String ACCOUNT_ADD_USER_LOCATION = "Account.addUserLocation";
    public static final String ACCOUNT_SET_AVATAR_FILE = "Account.setAvatarFile";

    public static final String PUSH_SET_GOOGLE_PUSH_ID = "Push.setGooglePushId";
    public static final String JABBER_START_CHAT = "Jabber.startChat";
    public static final String WEBRTC_START_CALL = "WebRtc.startCall";
    public static final String WEBRTC_STOP_CALL = "WebRtc.stopCall";

    public static final String CALENDAR_GET_EVENTS = "Calendar.getEvents";
    public static final String CALENDAR_ADD_EVENT = "Calendar.addEvent";
    public static final String CALENDAR_REMOVE_EVENT = "Calendar.removeEvent";
    public static final String CALENDAR_UPDATE_EVENT = "Calendar.updateEvent";

    public static final String PRODUCTS_GET_ALL_CATEGORIES = "Products.getProductCategories";
    public static final String PRODUCTS_GET_HTML_BY_ID = "Products.getProductById";
    public static final String PRODUCTS_ORDER_CREATE = "Order.create";
    public static final String PRODUCTS_ORDER_GET_FORM = "Order.getForm";

    private static final String CALL_CLOUD_UPLOAD = "/upload";
    private static final String CALL_CLOUD_FILE = "/file/";

    public static final String FILE_ADD_SHARE_WITH = "File.addShareWith";
    public static final String FILE_GET_MY_FILES = "File.getAllOwn";
    public static final String FILE_GET_ALL_SHARED_WITH_ME = "File.getAllSharedWithMe";
    public static final String FILE_DELETE = "File.delete";
    public static final String FILE_COPY_SHARED_TO_ME = "File.copySharedToMe";
    public static final String FILE_GET_PROPERTIES_BY_HASH = "File.getByHash";
    public static final String FILE_GET_SHARE_WITH = "File.getShareWith";
    public static final String FILE_UNLINK_SHARED_FILE = "File.unlinkSharedFile";
    public static final String FILE_DROP_SHARE_WITH = "File.dropShareWith";

    public static final String EDUCATION_GET_PROGRAMS = "Education.getPrograms";
    public static final String EDUCATION_GET_MODULES_BY_PROGRAM_ID = "Education.getModulesByProgramId";
    public static final String EDUCATION_GET_TESTS = "Education.getTestsByModuleId";


    private static void post(final StringEntity se, AsyncHttpResponseHandler responseHandler) {
        addClientHeader();
        // client.post(null, URL, se, null, responseHandler);
        client.post(null, serverSwitcher.getUrl(), se, null, responseHandler);
    }

    private static void syncPost(final StringEntity se, AsyncHttpResponseHandler responseHandler) {
        addSyncClientHeader();
        // syncClient.post(null, URL, se, null, responseHandler);
        syncClient.post(null, serverSwitcher.getUrl(), se, null, responseHandler);
    }

    private static void loginPost(final StringEntity se, AsyncHttpResponseHandler responseHandler) {
        client.removeAllHeaders();
        // client.post(null, URL, se, null, responseHandler);
        client.post(null, serverSwitcher.getUrl(), se, null, responseHandler);
    }

    private static String getToken() {
        return JustUpApplication.getApplication().getAppPreferences().getToken();
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

    public static void sendFileToCloud(final File file, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();

        try {
            params.put("file", file);
            params.put("file_name", file.getName());
        } catch (FileNotFoundException e) {
            LOGE(TAG, "sendFileToCloud()\n", e);
        }

        addClientHeader();
        // client.post(CLOUD_STORAGE_URL + CALL_CLOUD_UPLOAD, params, responseHandler);
        client.post(serverSwitcher.getCloudStorageUrl() + CALL_CLOUD_UPLOAD, params, responseHandler);
    }

    public static void downloadFileFromUrl(String url, FileAsyncHttpResponseHandler fileResponseHandler) {
        // AsyncHttpClient localClient = new AsyncHttpClient();
        addClientHeader();
        client.get(url, fileResponseHandler);
    }

    public static void syncDownloadFileFromCloud(String fileHash, FileAsyncHttpResponseHandler fileResponseHandler) {
        // downloads bug
        // AsyncHttpClient localClient = new SyncHttpClient();
        addSyncClientHeader();
        // localClient.get(CLOUD_STORAGE_URL + CALL_CLOUD_FILE + fileHash, fileResponseHandler);
        syncClient.get(serverSwitcher.getCloudStorageUrl() + CALL_CLOUD_FILE + fileHash, fileResponseHandler);
    }

    public static void syncSendFileToCloud(final File file, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();

        try {
            params.put("file", file);
            params.put("file_name", file.getName());
        } catch (FileNotFoundException e) {
            LOGE(TAG, "syncSendFileToCloud()\n", e);
        }

        addClientHeader();
        // syncClient.post(CLOUD_STORAGE_URL + CALL_CLOUD_UPLOAD, params, responseHandler);
        syncClient.post(serverSwitcher.getCloudStorageUrl() + CALL_CLOUD_UPLOAD, params, responseHandler);
    }

    private static void addClientHeader() {
        client.setTimeout(SOCKET_TIMEOUT);
        client.addHeader(AUTHORIZATION_HEADER, getToken());
    }

    private static void addSyncClientHeader() {
        syncClient.setTimeout(SOCKET_TIMEOUT);
        syncClient.addHeader(AUTHORIZATION_HEADER, getToken());
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
        String error = JustUpApplication.getApplication().getApplicationContext().getString(R.string.network_error);

        try {
            errorResponse = gson.fromJson(content, OnHttpFailureResponse.class);
        } catch (Exception e) {
            LOGE(TAG, "gson.fromJson:\n" + content);
        }

        if (errorResponse != null && errorResponse.Error != null) {
            error = errorResponse.Error;
        } else {
            if (content.length() > 280) {
                error = content.substring(0, 280) + "...";
            }
        }

        return error;
    }

    public static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) JustUpApplication.getApplication().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void loadImage(String url, ImageView imageView) {
        Picasso.Builder builder = new Picasso.Builder(JustUpApplication.getApplication());
        Picasso picasso = builder.downloader(new OkHttpDownloader(JustUpApplication.getApplication()) {
            @Override
            protected HttpURLConnection openConnection(Uri uri) throws IOException {
                HttpURLConnection connection = super.openConnection(uri);
                connection.setRequestProperty(AUTHORIZATION_HEADER, getToken());
                return connection;
            }
        }).build();

        picasso.load(serverSwitcher.getAvatarUrl() + url).into(imageView);
    }
}
