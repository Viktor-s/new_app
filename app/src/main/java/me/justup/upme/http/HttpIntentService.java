package me.justup.upme.http;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.Arrays;
import java.util.List;

import me.justup.upme.JustUpApplication;
import me.justup.upme.MainActivity;
import me.justup.upme.entity.ArticleFullResponse;
import me.justup.upme.entity.ArticlesGetShortDescriptionResponse;
import me.justup.upme.entity.BaseHttpQueryEntity;
import me.justup.upme.entity.BaseMethodEmptyQuery;
import me.justup.upme.entity.CalendarGetEventsResponse;
import me.justup.upme.entity.CommentsArticleFullResponse;
import me.justup.upme.entity.EducationGetModulesByProgramIdResponse;
import me.justup.upme.entity.EducationGetProgramsResponse;
import me.justup.upme.entity.GetAllContactsResponse;
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
    public static final String BROADCAST_INTENT_EDUCATION_MODULE_SERVER_ERROR = "broadcast_intent_education_module_server_error";

    public static final int NEWS_PART_SHORT = 1;
    public static final int NEWS_PART_FULL = 2;
    public static final int PRODUCTS_PART = 3;
    public static final int BRIEFCASE_PART = 4;
    public static final int MAIL_CONTACT_PART = 5;
    public static final int ADD_COMMENT = 6;
    public static final int GET_COMMENTS_FULL_ARTICLE = 7;
    public static final int ADD_REFERRAL = 8;
    public static final int CALENDAR_PART = 9;
    public static final int CALENDAR_ADD_EVENT = 10;
    public static final int CALENDAR_REMOVE_EVENT = 11;
    public static final int CALENDAR_UPDATE_EVENT = 12;
    public static final int PRODUCTS_GET_ALL_CATEGORIES = 13;
    public static final int PRODUCTS_GET_HTML_BY_ID = 14;
    public static final int PRODUCTS_CREATE_ORDER = 15;
    public static final int EDUCATION_GET_PRODUCTS = 16;
    public static final int EDUCATION_GET_PRODUCT_MODULES = 17;

    private int mPartNumber;

    public HttpIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        BaseHttpQueryEntity mQueryEntity = (BaseHttpQueryEntity) intent.getSerializableExtra(HTTP_INTENT_QUERY_EXTRA);
        mPartNumber = intent.getIntExtra(HTTP_INTENT_PART_EXTRA, 0);
        ApiWrapper.syncQuery(mQueryEntity, new OnQueryResponse());
    }

    private class OnQueryResponse extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGD(TAG, "OnSuccess() : " + content);
            LOGI(TAG, "OnSuccess() : headers : " + Arrays.toString(headers) + ", status code : " + statusCode);

            switch (mPartNumber) {
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
                    // TODO 22.06.15 DBAdapter.getInstance().sendBroadcast(DBAdapter.NEWS_ITEM_SQL_BROADCAST_INTENT);
                    break;

                case GET_COMMENTS_FULL_ARTICLE:
                    fillCommentsFullDB(content, NewsItemFragment.mNewsFeedEntityId);
                    break;

                case ADD_REFERRAL:
                    BaseMethodEmptyQuery query = new BaseMethodEmptyQuery();
                    query.method = ApiWrapper.ACCOUNT_GET_ALL_CONTACTS;
                    startHttpIntent(query, HttpIntentService.MAIL_CONTACT_PART);
                    break;

                case CALENDAR_PART:
                    LOGD(TAG, "OnQueryResponse");
                    fillEventsCalendarDB(content);
                    break;

                case CALENDAR_ADD_EVENT:
                    startHttpIntent(MainActivity.getEventCalendarQuery(CalendarFragment.mFirstDayCurrentWeek), HttpIntentService.CALENDAR_PART);
                    break;

                case CALENDAR_REMOVE_EVENT:
                    startHttpIntent(MainActivity.getEventCalendarQuery(CalendarFragment.mFirstDayCurrentWeek), HttpIntentService.CALENDAR_PART);
                    break;

                case CALENDAR_UPDATE_EVENT:

                    startHttpIntent(MainActivity.getEventCalendarQuery(CalendarFragment.mFirstDayCurrentWeek), HttpIntentService.CALENDAR_PART);
                    break;

                case PRODUCTS_GET_ALL_CATEGORIES:
                    fillProductsDB(content);
                    break;

                case PRODUCTS_GET_HTML_BY_ID:
                    fillProductHTML(content);
                    break;
                case PRODUCTS_CREATE_ORDER:

                    break;

                case EDUCATION_GET_PRODUCTS:
                    fillEducationProductsDB(content);
                    break;

                case EDUCATION_GET_PRODUCT_MODULES:
                    fillEducationProductModulesDB(content);
                    break;

                default:
                    break;
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);

            LOGE(TAG, "OnFailure / StatusCode : " + statusCode);

            if (headers != null) {
                LOGE(TAG, "Headers: " + Arrays.toString(headers));
            }else{
                LOGE(TAG, "Headers is NULL");
            }

            LOGE(TAG, "ResponseBody: " + content);

            if (error != null) {
                LOGE(TAG, "Error: ", error);
            }else {
                LOGE(TAG, "Error is NULL");
            }

            // mDBAdapter.sendBroadcast(BROADCAST_INTENT_NEWS_FEED_SERVER_ERROR);
            switch (mPartNumber) {
                case NEWS_PART_SHORT:
                    // TODO 22.06.15 DBAdapter.getInstance().sendBroadcast(BROADCAST_INTENT_NEWS_FEED_SERVER_ERROR);
                    break;
                case EDUCATION_GET_PRODUCT_MODULES:
                    // TODO 22.06.15 DBAdapter.getInstance().sendBroadcast(BROADCAST_INTENT_EDUCATION_MODULE_SERVER_ERROR);
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
            LOGE(TAG, "Gson.fromJson:\n" + content);
        }

        if (response != null && response.result != null) {
            for (int i = 0; i < response.result.size(); i++) {
                JustUpApplication.getApplication().getTransferActionShortNews().insertShortNewsOld(getApplicationContext(), response.result.get(i));

                for (int j = 0; j < response.result.get(i).comments.size(); j++) {
                    JustUpApplication.getApplication().getTransferActionNewsComments().insertNewsCommentsOld(getApplicationContext(), response.result.get(i), j);
                }
            }
        } else {
            // TODO 22.06.15 DBAdapter.getInstance().sendBroadcast(BROADCAST_INTENT_NEWS_FEED_SERVER_ERROR);
        }
    }

    private void fillNewsFullDB(String content) {
        ArticleFullResponse response = null;
        try {
            response = ApiWrapper.gson.fromJson(content, ArticleFullResponse.class);
        } catch (JsonSyntaxException e) {
            LOGE(TAG, "Gson.fromJson:\n" + content);
        }

        if (response != null && response.result != null) {
            JustUpApplication.getApplication().getTransferActionFullNews().insertFullNewsOld(getApplicationContext(), response);
            for (int j = 0; j < response.result.comments.size(); j++) {
                JustUpApplication.getApplication().getTransferActionNewsComments().insertNewsCommentsOld(getApplicationContext(), response.result, j);
            }
        }
    }

    private void fillCommentsFullDB(String content, int article_id) {
        CommentsArticleFullResponse response = null;
        try {
            response = ApiWrapper.gson.fromJson(content, CommentsArticleFullResponse.class);
        } catch (JsonSyntaxException e) {
            LOGE(TAG, "Gson.fromJson:\n" + content);
        }

        if (response != null && response.result != null) {
            for (int j = 0; j < response.result.size(); j++) {
                JustUpApplication.getApplication().getTransferActionNewsComments().insertNewsCommentsOldFullWithArticleId(getApplicationContext(), response.result.get(j), article_id);
            }
        }
    }

    private void fillBriefcaseDB(String content) {
        LOGI(TAG, "FillBriefcase DB");
    }

    private void fillMailContactDB(String content) {
        LOGI(TAG, "FillMailContact DB");

        GetAllContactsResponse response = null;

        try {
            response = ApiWrapper.gson.fromJson(content, GetAllContactsResponse.class);
        } catch (JsonSyntaxException e) {
            LOGE(TAG, "Gson.fromJson : \n" + content);
        }

        if (response != null && response.result != null) {
            LOGI(TAG, "Response : " + response.toString());

            final List<GetAllContactsResponse.Result.Parents> allUsers = response.result.getAllUsers();
            if (allUsers != null) {
                JustUpApplication.getApplication().getTransferActionMailContact().insertContactListOld(getApplicationContext(), allUsers);
            }
        }

        //  DBAdapter.getInstance().sendBroadcast(DBAdapter.MAIL_SQL_BROADCAST_INTENT);
    }

    private void fillEventsCalendarDB(String content) {
        CalendarGetEventsResponse response = null;
        try {
            response = ApiWrapper.gson.fromJson(content, CalendarGetEventsResponse.class);
        } catch (JsonSyntaxException e) {
            LOGE(TAG, "Gson.fromJson:\n" + content);
        }

        if (response != null && response.result != null) {
            for (int i = 0; i < response.result.size(); i++) {
                JustUpApplication.getApplication().getTransferActionEventCalendar().insertEventCalendarOld(getApplicationContext(), response.result.get(i));
            }
        }
    }

    private void fillProductsDB(String content) {
        ProductsGetAllCategoriesResponse response = null;
        try {
            response = ApiWrapper.gson.fromJson(content, ProductsGetAllCategoriesResponse.class);
        } catch (JsonSyntaxException e) {
            LOGE(TAG, "Gson.fromJson:\n" + content);
        }

        if (response != null && response.result != null) {
            for (int i = 0; i < response.result.size(); i++) {
                JustUpApplication.getApplication().getTransferActionProductsCategories().insertProductCategoriesOld(getApplicationContext(), response.result.get(i));

                for (int j = 0; j < response.result.get(i).brandCategories.size(); j++) {
                    JustUpApplication.getApplication().getTransferActionBrandCategories().insertBrandCategoriesOld(getApplicationContext(), response.result.get(i).brandCategories.get(j));

                    for (int k = 0; k < response.result.get(i).brandCategories.get(j).products.size(); k++) {
                        JustUpApplication.getApplication().getTransferActionProductsProduct().insertProductsProductOld(getApplicationContext(), response.result.get(i).brandCategories.get(j).products.get(k), response.result.get(i).brandCategories.get(j).brandId);
                    }
                }
            }
        }
    }

    private void fillProductHTML(String content) {
        GetProductHtmlByIdResponse response = null;
        try {
            response = ApiWrapper.gson.fromJson(content, GetProductHtmlByIdResponse.class);
        } catch (JsonSyntaxException e) {
            LOGE(TAG, "Gson.fromJson:\n" + content);
        }

        if (response != null && response.result != null) {
            JustUpApplication.getApplication().getTransferActionProductHTML().insertProductHtmlOld(getApplicationContext(), response);
        }
    }

    private void fillEducationProductsDB(String content) {
        EducationGetProgramsResponse response = null;
        try {
            response = ApiWrapper.gson.fromJson(content, EducationGetProgramsResponse.class);
        } catch (JsonSyntaxException e) {
            LOGE(TAG, "Gson.fromJson:\n" + content);
        }

        if (response != null && response.result != null) {
            for (int i = 0; i < response.result.size(); i++) {
                JustUpApplication.getApplication().getTransferActionEducationProducts().insertEducationProductOld(getApplicationContext(), response.result.get(i));
            }
        }
    }

    private void fillEducationProductModulesDB(String content) {
        EducationGetModulesByProgramIdResponse response = null;
        try {
            response = ApiWrapper.gson.fromJson(content, EducationGetModulesByProgramIdResponse.class);
        } catch (JsonSyntaxException e) {
            LOGE(TAG, "gson.fromJson:\n" + content);
        }

        if (response != null && response.result != null) {
            for (int i = 0; i < response.result.size(); i++) {
                JustUpApplication.getApplication().getTransferActionEducationProductModule().insertEducationProductModuleOld(getApplicationContext(), response.result.get(i));

                for (int j = 0; j < response.result.get(i).materials.size(); j++) {
                    JustUpApplication.getApplication().getTransferActionEducationModulesMaterial().insertEducationModulesMaterialOld(getApplicationContext(), response.result.get(i).materials.get(j));
                }
            }
        } else {
            // TODO 22.06.15 DBAdapter.getInstance().sendBroadcast(BROADCAST_INTENT_EDUCATION_MODULE_SERVER_ERROR);
        }
    }

}
