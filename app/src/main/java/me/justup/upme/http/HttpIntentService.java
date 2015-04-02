package me.justup.upme.http;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.List;

import me.justup.upme.MainActivity;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.entity.ArticleFullResponse;
import me.justup.upme.entity.ArticlesGetShortDescriptionResponse;
import me.justup.upme.entity.BaseHttpQueryEntity;
import me.justup.upme.entity.CalendarGetEventsResponse;
import me.justup.upme.entity.CommentsArticleFullResponse;
import me.justup.upme.entity.GetAllContactsResponse;
import me.justup.upme.entity.GetMailContactQuery;
import me.justup.upme.entity.GetProductHtmlByIdResponse;
import me.justup.upme.entity.ProductsGetAllCategoriesResponse;
import me.justup.upme.fragments.CalendarFragment;
import me.justup.upme.fragments.NewsItemFragment;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class HttpIntentService extends IntentService {
    private static final String TAG = makeLogTag(HttpIntentService.class);

    public static final String HTTP_INTENT_QUERY_EXTRA = "http_intent_query_extra";
    public static final String HTTP_INTENT_PART_EXTRA = "http_intent_part_extra";
    public static final String BROADCAST_INTENT_NEWS_FEED_SERVER_ERROR = "broadcast_intent_news_feed_server_error";

    public static final int NEWS_PART_SHORT = 1;
    public static final int NEWS_PART_FULL = 2;
    public static final int PRODUCTS_PART = 3;
    public static final int BRIEFCASE_PART = 4;
    public static final int MAIL_CONTACT_PART = 5;
    public static final int ADD_COMMENT = 6;
    public static final int GET_COMMENTS_FULL_ARTICLE = 7;
    public static final int ADD_REFERAL = 8;
    public static final int CALENDAR_PART = 9;
    public static final int CALENDAR_ADD_EVENT = 10;
    public static final int CALENDAR_REMOVE_EVENT = 11;
    public static final int CALENDAR_UPDATE_EVENT = 12;
    public static final int PRODUCTS_GET_ALL_CATEGORIES = 13;
    public static final int PRODUCTS_GET_HTML_BY_ID = 14;
    public static final int PRODUCTS_CREATE_ORDER = 15;


    //private DBAdapter mDBAdapter;
    private int partNumber;


    public HttpIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        mDBAdapter = new DBAdapter(AppContext.getAppContext());
//        mDBAdapter.open();
        DBAdapter.getInstance().openDatabase();
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

        //mDBAdapter.close();
        DBAdapter.getInstance().closeDatabase();
    }

    private class OnQueryResponse extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGD(TAG, "onSuccess(): " + content);
            // LOGI(TAG, "onSuccess(): headers:" + Arrays.toString(headers) + " status code:" + statusCode);

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
                    DBAdapter.getInstance().sendBroadcast(DBAdapter.NEWS_ITEM_SQL_BROADCAST_INTENT);
                    break;

                case GET_COMMENTS_FULL_ARTICLE:
                    fillCommentsFullDB(content, NewsItemFragment.newsFeedEntityId);
                    break;

                case ADD_REFERAL:
                    startHttpIntent(new GetMailContactQuery(), HttpIntentService.MAIL_CONTACT_PART);
                    break;

                case CALENDAR_PART:
                    Log.d("TAG333_selectQuery", " OnQueryResponse ---------------------------");
                    fillEventsCalendarDB(content);
                    break;

                case CALENDAR_ADD_EVENT:
                    startHttpIntent(MainActivity.getEventCalendarQuery(CalendarFragment.firstDayCurrentWeek), HttpIntentService.CALENDAR_PART);
                    break;

                case CALENDAR_REMOVE_EVENT:
                    startHttpIntent(MainActivity.getEventCalendarQuery(CalendarFragment.firstDayCurrentWeek), HttpIntentService.CALENDAR_PART);
                    break;

                case CALENDAR_UPDATE_EVENT:

                    startHttpIntent(MainActivity.getEventCalendarQuery(CalendarFragment.firstDayCurrentWeek), HttpIntentService.CALENDAR_PART);
                    break;

                case PRODUCTS_GET_ALL_CATEGORIES:
                    fillProductsDB(content);
                    break;

                case PRODUCTS_GET_HTML_BY_ID:
                    fillProductHtmlDB(content);
                    break;
                case PRODUCTS_CREATE_ORDER:

                    break;
                default:
                    break;
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGE(TAG, "onFailure(): " + content);
            //mDBAdapter.sendBroadcast(BROADCAST_INTENT_NEWS_FEED_SERVER_ERROR);
            switch (partNumber) {
                case NEWS_PART_SHORT:
                    DBAdapter.getInstance().sendBroadcast(BROADCAST_INTENT_NEWS_FEED_SERVER_ERROR);
                    break;
                default:
                    break;
            }
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
            DBAdapter.getInstance().saveShortNews(response);
        } else {
            DBAdapter.getInstance().sendBroadcast(BROADCAST_INTENT_NEWS_FEED_SERVER_ERROR);
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
            DBAdapter.getInstance().saveFullNews(response);
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
            DBAdapter.getInstance().saveArticleFullComments(response, article_id);
        }
    }

    private void fillBriefcaseDB(String content) {
        LOGI(TAG, "fillBriefcaseDB");
    }


    private void fillMailContactDB(String content) {
        LOGI(TAG, "fillMailContactDB");
        GetAllContactsResponse response = null;

        // int userId = new AppPreferences(AppContext.getAppContext()).getUserId();

        // fake
        /*
        String contents = "{\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"result\": [\n" +
                "        {\n" +
                "            \"id\": \"1\",\n" +
                "            \"parentId\":" + "\"" + userId + "\"" + ",\n" +
                "            \"name\": \"test0\",\n" +
                "            \"login\": \"Mr.Android0\",\n" +
                "            \"dateAdd\": \"45554\",\n" +
                "            \"phone\": \"+380934262276\",\n" +
                "            \"img\": \"http://www.edigames.net/images/callcenter.png\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"2\",\n" +
                "            \"parentId\":" + "\"" + userId + "\"" + ",\n" +
                "            \"name\": \"test1\",\n" +
                "            \"login\": \"Mr.Android1\",\n" +
                "            \"dateAdd\": \"45554\",\n" +
                "            \"phone\": \"+380934262276\",\n" +
                "            \"img\": \"https://www.leadflash.com/Content/v3/images/call_center.png\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"3\",\n" +
                "            \"parentId\":" + "\"" + userId + "\"" + ",\n" +
                "            \"name\": \"test2\",\n" +
                "            \"login\": \"Mr.Android2\",\n" +
                "            \"dateAdd\": \"45554\",\n" +
                "            \"phone\": \"+380934262276\",\n" +
                "            \"img\": \"http://telemosa.mx/wp-content/uploads/2014/10/call-center1-300x231.png\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"4\",\n" +
                "            \"parentId\":" + "\"" + userId + "\"" + ",\n" +
                "            \"name\": \"test3\",\n" +
                "            \"login\": \"Mr.Android3\",\n" +
                "            \"dateAdd\": \"45554\",\n" +
                "            \"phone\": \"+380934262276\",\n" +
                "            \"img\": \"http://www.eppsinsurance.com/wp-content/uploads/2013/09/call-center-300x300.jpg\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"5\",\n" +
                "            \"parentId\":" + "\"" + userId + "\"" + ",\n" +
                "            \"name\": \"test4\",\n" +
                "            \"login\": \"Mr.Android4\",\n" +
                "            \"dateAdd\": \"45554\",\n" +
                "            \"phone\": \"+380934262276\",\n" +
                "            \"img\": \"http://initrod.com/game/img/write.png\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"6\",\n" +
                "            \"parentId\":" + "\"" + userId + "\"" + ",\n" +
                "            \"name\": \"test5\",\n" +
                "            \"login\": \"Mr.Android5\",\n" +
                "            \"dateAdd\": \"45554\",\n" +
                "            \"phone\": \"+380934262276\",\n" +
                "            \"img\": \"http://bulletproofhvac.ca/wp-content/uploads/2013/05/call-agent1-276x300.png\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"7\",\n" +
                "            \"parentId\":" + "\"" + userId + "\"" + ",\n" +
                "            \"name\": \"test6\",\n" +
                "            \"login\": \"Mr.Android6\",\n" +
                "            \"dateAdd\": \"45554\",\n" +
                "            \"phone\": \"+380934262276\",\n" +
                "            \"img\": \"http://www.universal-promotions.com/images/icon-callcenter.png\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"8\",\n" +
                "            \"parentId\":" + "\"" + userId + "\"" + ",\n" +
                "            \"name\": \"test7\",\n" +
                "            \"login\": \"Mr.Android6\",\n" +
                "            \"dateAdd\": \"45554\",\n" +
                "            \"phone\": \"+380934262276\",\n" +
                "            \"img\": \"http://telemosa.mx/wp-content/uploads/2014/10/call-center1-300x231.png\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"9\",\n" +
                "            \"parentId\":" + "\"" + userId + "\"" + ",\n" +
                "            \"name\": \"test8\",\n" +
                "            \"login\": \"Mr.Android6\",\n" +
                "            \"dateAdd\": \"45554\",\n" +
                "            \"phone\": \"+380934262276\",\n" +
                "            \"img\": \"http://www.universal-promotions.com/images/icon-callcenter.png\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"10\",\n" +
                "            \"parentId\":" + "\"" + userId + "\"" + ",\n" +
                "            \"name\": \"test9\",\n" +
                "            \"login\": \"Mr.Android6\",\n" +
                "            \"dateAdd\": \"45554\",\n" +
                "            \"phone\": \"+380934262276\",\n" +
                "            \"img\": \"http://flagmantelecom.ru/upload/iblock/callcentre.png\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"11\",\n" +
                "            \"parentId\":" + "\"" + 5 + "\"" + ",\n" +
                "            \"name\": \"test10\",\n" +
                "            \"login\": \"mr_ctd\",\n" +
                "            \"dateAdd\": \"5543543\",\n" +
                "            \"phone\": \"+380111111111\",\n" +
                "            \"img\": \"http://life-in-taxi.ru/2012/07/telefone.jpg\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"id\": \"123\"\n" +
                "}";
    */

        try {
            response = ApiWrapper.gson.fromJson(content, GetAllContactsResponse.class);
        } catch (JsonSyntaxException e) {
            LOGE(TAG, "gson.fromJson:\n" + content);
        }

        if (response != null && response.result != null) {
            LOGI(TAG, response.toString());

            final List<GetAllContactsResponse.Result.Parents> allUsers = response.result.getAllUsers();
            if (allUsers != null) {
                DBAdapter.getInstance().saveContactsArray(allUsers);
            }
        }
    }


    private void fillEventsCalendarDB(String content) {
        CalendarGetEventsResponse response = null;
        try {
            response = ApiWrapper.gson.fromJson(content, CalendarGetEventsResponse.class);
        } catch (JsonSyntaxException e) {
            LOGE(TAG, "gson.fromJson:\n" + content);
        }

        if (response != null && response.result != null) {
            DBAdapter.getInstance().saveEventsCalendar(response);
        }
    }


    private void fillProductsDB(String content) {
        ProductsGetAllCategoriesResponse response = null;
        try {
            response = ApiWrapper.gson.fromJson(content, ProductsGetAllCategoriesResponse.class);
        } catch (JsonSyntaxException e) {
            LOGE(TAG, "gson.fromJson:\n" + content);
        }

        if (response != null && response.result != null) {
            DBAdapter.getInstance().saveAllProducts(response);
        }
    }

    private void fillProductHtmlDB(String content) {
        GetProductHtmlByIdResponse response = null;
        try {
            response = ApiWrapper.gson.fromJson(content, GetProductHtmlByIdResponse.class);
        } catch (JsonSyntaxException e) {
            LOGE(TAG, "gson.fromJson:\n" + content);
        }

        if (response != null && response.result != null) {
            DBAdapter.getInstance().saveProductHtml(response);
        }
    }


}
