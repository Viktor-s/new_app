package me.justup.upme.http;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import me.justup.upme.db.DBAdapter;
import me.justup.upme.entity.ArticleFullResponse;
import me.justup.upme.entity.ArticlesGetShortDescriptionResponse;
import me.justup.upme.entity.BaseHttpQueryEntity;
import me.justup.upme.entity.CommentsArticleFullResponse;
import me.justup.upme.entity.EventsCalendarResponse;
import me.justup.upme.entity.GetMailContactQuery;
import me.justup.upme.entity.GetMailContactResponse;
import me.justup.upme.fragments.NewsItemFragment;
import me.justup.upme.utils.AppContext;
import me.justup.upme.utils.AppPreferences;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class HttpIntentService extends IntentService {
    private static final String TAG = makeLogTag(HttpIntentService.class);

    public static final String HTTP_INTENT_QUERY_EXTRA = "http_intent_query_extra";
    public static final String HTTP_INTENT_PART_EXTRA = "http_intent_part_extra";

    public static final int NEWS_PART_SHORT = 1;
    public static final int NEWS_PART_FULL = 2;
    public static final int PRODUCTS_PART = 3;
    public static final int BRIEFCASE_PART = 4;
    public static final int MAIL_CONTACT_PART = 5;
    public static final int ADD_COMMENT = 6;
    public static final int GET_COMMENTS_FULL_ARTICLE = 7;
    public static final int ADD_REFERAL = 8;
    public static final int CALENDAR_PART = 9;


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
                case NEWS_PART_SHORT:
                    fillNewsShortDB(content);
                    break;
                case NEWS_PART_FULL:
                    fillNewsFullDB(content);
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

                case ADD_COMMENT:
                    mDBAdapter.sendBroadcast(DBAdapter.NEWS_ITEM_SQL_BROADCAST_INTENT);
                    break;

                case GET_COMMENTS_FULL_ARTICLE:
                    fillCommentsFullDB(content, NewsItemFragment.mNewsFeedEntity.getId());
                    break;

                case ADD_REFERAL:
                    startHttpIntent(new GetMailContactQuery(), HttpIntentService.MAIL_CONTACT_PART);
                    break;

                case CALENDAR_PART:
                    fillEventsCalendarDB(content);
                    break;

                default:
                    break;
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT).show();
            LOGE(TAG, "onFailure(): " + content);

        }
    }


    public void startHttpIntent(BaseHttpQueryEntity entity, int dbTable) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(HTTP_INTENT_QUERY_EXTRA, entity);
        bundle.putInt(HTTP_INTENT_PART_EXTRA, dbTable);

        Intent intent = new Intent(this, HttpIntentService.class);
        startService(intent.putExtras(bundle));
    }

    private void fillNewsShortDB(String content) {
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

    private void fillNewsFullDB(String content) {
        ArticleFullResponse response = null;
        try {
            response = ApiWrapper.gson.fromJson(content, ArticleFullResponse.class);
        } catch (JsonSyntaxException e) {
            LOGE(TAG, "gson.fromJson:\n" + content);
        }

        if (response != null && response.result != null) {
            mDBAdapter.saveFullNews(response);
        }
    }

    private void fillCommentsFullDB(String content, int article_id) {
        CommentsArticleFullResponse response = null;
        try {
            response = ApiWrapper.gson.fromJson(content, CommentsArticleFullResponse.class);
        } catch (JsonSyntaxException e) {
            LOGE(TAG, "gson.fromJson:\n" + content);
        }

        if (response != null && response.result != null) {
            mDBAdapter.saveArticleFullComments(response, article_id);
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
        int userId = new AppPreferences(AppContext.getAppContext()).getUserId();


        // fake
        String contents = "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"result\": [\n" +
                "        {\n" +
                "            \"id\": \"5\",\n" +
                "            \"parentId\":" + "\"" + userId + "\"" + ",\n" +
                "            \"id\": \"1\",\n" +
                "            \"name\": \"test0\",\n" +
                "            \"login\": \"Mr.Android0\",\n" +
                "            \"dateAdd\": \"45554\",\n" +
                "            \"phone\": \"+380934262276\",\n" +
                "            \"img\": \"http://www.edigames.net/images/callcenter.png\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"71\",\n" +
                "            \"parentId\":" + "\"" + userId + "\"" + ",\n" +
                "            \"id\": \"2\",\n" +
                "            \"name\": \"test1\",\n" +
                "            \"login\": \"Mr.Android1\",\n" +
                "            \"dateAdd\": \"45554\",\n" +
                "            \"phone\": \"+380934262276\",\n" +
                "            \"img\": \"https://www.leadflash.com/Content/v3/images/call_center.png\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"3\",\n" +
                "            \"id\": \"72\",\n" +
                "            \"parentId\":" + "\"" + userId + "\"" + ",\n" +
                "            \"name\": \"test2\",\n" +
                "            \"login\": \"Mr.Android2\",\n" +
                "            \"dateAdd\": \"45554\",\n" +
                "            \"phone\": \"+380934262276\",\n" +
                "            \"img\": \"http://telemosa.mx/wp-content/uploads/2014/10/call-center1-300x231.png\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"4\",\n" +
                "            \"id\": \"73\",\n" +
                "            \"parentId\":" + "\"" + userId + "\"" + ",\n" +
                "            \"name\": \"test3\",\n" +
                "            \"login\": \"Mr.Android3\",\n" +
                "            \"dateAdd\": \"45554\",\n" +
                "            \"phone\": \"+380934262276\",\n" +
                "            \"img\": \"http://www.eppsinsurance.com/wp-content/uploads/2013/09/call-center-300x300.jpg\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"5\",\n" +
                "            \"id\": \"74\",\n" +
                "            \"parentId\":" + "\"" + userId + "\"" + ",\n" +
                "            \"name\": \"test4\",\n" +
                "            \"login\": \"Mr.Android4\",\n" +
                "            \"dateAdd\": \"45554\",\n" +
                "            \"phone\": \"+380934262276\",\n" +
                "            \"img\": \"http://initrod.com/game/img/write.png\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"6\",\n" +
                "            \"id\": \"75\",\n" +
                "            \"parentId\":" + "\"" + userId + "\"" + ",\n" +
                "            \"name\": \"test5\",\n" +
                "            \"login\": \"Mr.Android5\",\n" +
                "            \"dateAdd\": \"45554\",\n" +
                "            \"phone\": \"+380934262276\",\n" +
                "            \"img\": \"http://bulletproofhvac.ca/wp-content/uploads/2013/05/call-agent1-276x300.png\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"7\",\n" +
                "            \"id\": \"76\",\n" +
                "            \"parentId\":" + "\"" + userId + "\"" + ",\n" +
                "            \"name\": \"test6\",\n" +
                "            \"login\": \"Mr.Android6\",\n" +
                "            \"dateAdd\": \"45554\",\n" +
                "            \"phone\": \"+380934262276\",\n" +
                "            \"img\": \"http://www.universal-promotions.com/images/icon-callcenter.png\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"8\",\n" +
                "            \"id\": 9,\n" +
                "            \"parentId\":" + "\"" + 5 + "\"" + ",\n" +
                "            \"name\": \"test7\",\n" +
                "            \"login\": \"mr_ctd\",\n" +
                "            \"dateAdd\": \"5543543\",\n" +
                "            \"phone\": \"+380111111111\",\n" +
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


    private void fillEventsCalendarDB(String content) {
        LOGD("TAG_", content);
        EventsCalendarResponse response = null;
        try {
            response = ApiWrapper.gson.fromJson(content, EventsCalendarResponse.class);
        } catch (JsonSyntaxException e) {
            LOGE(TAG, "gson.fromJson:\n" + content);
        }

        if (response != null && response.result != null) {
          mDBAdapter.saveEventsCalendar(response);
        }
    }
}
