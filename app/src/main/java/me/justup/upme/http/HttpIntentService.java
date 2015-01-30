package me.justup.upme.http;

import android.app.IntentService;
import android.content.Intent;

import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import me.justup.upme.db.DBAdapter;
import me.justup.upme.entity.ArticlesGetShortDescriptionResponse;
import me.justup.upme.entity.BaseHttpQueryEntity;
import me.justup.upme.entity.GetMailContactResponse;
import me.justup.upme.utils.AppContext;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class HttpIntentService extends IntentService {
    private static final String TAG = makeLogTag(HttpIntentService.class);

    public static final String HTTP_INTENT_QUERY_EXTRA = "http_intent_query_extra";
    public static final String HTTP_INTENT_PART_EXTRA = "http_intent_part_extra";

    public static final int NEWS_PART = 1;
    public static final int PRODUCTS_PART = 2;
    public static final int BRIEFCASE_PART = 3;
    public static final int MAIL_CONTACT_PART = 4;

    private DBAdapter mDBAdapter;
    private int partNumber;


    public HttpIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mDBAdapter = new DBAdapter(AppContext.getAppContext());
        mDBAdapter.open();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        BaseHttpQueryEntity mQueryEntity = (BaseHttpQueryEntity) intent.getSerializableExtra(HTTP_INTENT_QUERY_EXTRA);
        partNumber = intent.getIntExtra(HTTP_INTENT_PART_EXTRA, 0);

        ApiWrapper.syncQuery(mQueryEntity, new OnQueryResponse());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mDBAdapter.close();
    }

    private class OnQueryResponse extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGD(TAG, "onSuccess(): " + content);

            switch (partNumber) {
                case NEWS_PART:
                    fillNewsDB(content);
                    break;

                case PRODUCTS_PART:
                    fillProductsDB(content);
                    break;

                case BRIEFCASE_PART:
                    fillBriefcaseDB(content);
                    break;

                case MAIL_CONTACT_PART:
                    fillMailContactDB(content);
                    break;

                default:
                    break;
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGE(TAG, "onFailure(): " + content);
        }
    }

    private void fillNewsDB(String content) {
        ArticlesGetShortDescriptionResponse response = null;
        try {
            response = ApiWrapper.gson.fromJson(content, ArticlesGetShortDescriptionResponse.class);
        } catch (JsonSyntaxException e) {
            LOGE(TAG, "gson.fromJson:\n" + content);
        }

        if (response != null && response.result != null) {
            mDBAdapter.saveShortNews(response);
        }
    }

    private void fillProductsDB(String content) {
        LOGI(TAG, "fillProductsDB");
    }

    private void fillBriefcaseDB(String content) {
        LOGI(TAG, "fillBriefcaseDB");
    }


    private void fillMailContactDB(String content) {
        LOGI(TAG, "fillMailContactDB");
        GetMailContactResponse response = null;

        // fake
        String contents = "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"result\": [\n" +
                "        {\n" +
                "            \"id\": \"7\",\n" +
                "            \"name\": \"alexkissel\",\n" +
                "            \"login\": \"mrctd\",\n" +
                "            \"dateAdd\": \"45554\",\n" +
                "            \"phone\": \"+380934262276\",\n" +
                "            \"img\": \"http://initrod.com/game/img/write.png\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 9,\n" +
                "            \"name\": \"Mr.Ctd\",\n" +
                "            \"login\": \"mr_ctd\",\n" +
                "            \"dateAdd\": \"5543543\",\n" +
                "            \"phone\": \"+380111111111\",\n" +
                "            \"img\": \"http://initrod.com/game/img/java.png\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"10\",\n" +
                "            \"name\": \"Mr.Android\",\n" +
                "            \"login\": \"test_log\",\n" +
                "            \"dateAdd\": \"55435543\",\n" +
                "            \"phone\": \"+380111122222\",\n" +
                "            \"img\": \"http://life-in-taxi.ru/2012/07/telefone.jpg\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"id\": \"123\"\n" +
                "}";

        try {
            response = ApiWrapper.gson.fromJson(contents, GetMailContactResponse.class);
        } catch (JsonSyntaxException e) {
            LOGE(TAG, "gson.fromJson:\n" + content);
        }

        if (response != null && response.result != null) {
            LOGI(TAG, response.toString());
            mDBAdapter.saveMailContacts(response);
        }
    }

}
