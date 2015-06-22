package me.justup.upme.api_rpc.request_model.handler;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import me.justup.upme.api_rpc.jsonrpclibrary.JSONRPCClient;
import me.justup.upme.api_rpc.jsonrpclibrary.JSONRPCException;
import me.justup.upme.api_rpc.response_object.AccountObject;
import me.justup.upme.api_rpc.response_object.AuthorizationObject;
import me.justup.upme.api_rpc.response_object.CalendarObject;
import me.justup.upme.api_rpc.response_object.EducationObject;
import me.justup.upme.api_rpc.response_object.FileObject;
import me.justup.upme.api_rpc.response_object.NewsObject;
import me.justup.upme.api_rpc.response_object.OrdersObject;
import me.justup.upme.api_rpc.response_object.ProductsObject;
import me.justup.upme.api_rpc.response_object.PushObject;
import me.justup.upme.api_rpc.response_object.RPCError;
import me.justup.upme.api_rpc.response_object.UniversalPushResult;
import me.justup.upme.api_rpc.utils.Constants;
import me.justup.upme.api_rpc.utils.RequestParamBuilder;

import static me.justup.upme.utils.LogUtils.LOGI;

public class AppIntentHandler extends BasicIntentHandler {
    private static final String TAG = AppIntentHandler.class.getSimpleName();

    // Request Parameters
    public static final String EXTRA_PARAM_REQUEST = "EXTRA_PARAM_REQUEST";
    public static final String EXTRA_PARAM_REQUEST_TYPE = "EXTRA_PARAM_REQUEST_TYPE";
    // Response Parameters
    public static final String EXTRA_PARAM_MAIL_RESPONSE = "EXTRA_PARAM_MAIL_RESPONSE";

    public enum RequestType implements Serializable {
        OBJECT, LIST, STRING
    }

    @Override
    public void doExecute(Intent intent, Application application, ResultReceiver callback) {
        // Get type return Object
        RequestType requestType = (RequestType) intent.getSerializableExtra(EXTRA_PARAM_REQUEST_TYPE);
        // Init Action
        String action = intent.getAction();
        // Get Sender Data Object
        Bundle data = getData(action, requestType, intent);

        if(data!=null && !data.isEmpty() && !(data.getSerializable(AppIntentHandler.EXTRA_PARAM_MAIL_RESPONSE) instanceof RPCError)){
            sendUpdate(SUCCESS_RESPONSE, data);
        }else{
            sendUpdate(FAILURE_RESPONSE, data);
        }
    }
    
    private Bundle getData(String action, RequestType requestType, Intent intent){
        Bundle data = new Bundle();
        
        // Check Type of Object Action
        if(Constants.isActionVerification(action)){
            // Get Verification Data
            return getVerificationData(intent, data, requestType);
        }else if(Constants.isActionEducation(action)){
            // Get Education Data
            return getEducationData(intent, data, requestType);
        }else if(Constants.isActionArticles(action)){
            // Get Article(News) Data
            return getArticleData(intent, data, requestType);
        }else if(Constants.isActionCalendar(action)){
            // Get Calendar Data
            return getCalendarData(intent, data, requestType);
        }else if(Constants.isActionAccount(action)){
            // Get Account Data
            return getAccountData(intent, data, requestType);
        }else if(Constants.isActionSocial(action)){
            // Get Social Data
            return getSocialData(intent, data, requestType);
        }else if(Constants.isActionFile(action)){
            // Get File Data
            return getFileData(intent, data, requestType);
        }else if(Constants.isActionProducts(action)){
            // Get Product Data
            return getProductData(intent, data, requestType);
        }else if(Constants.isActionOrder(action)){
            // Get Order Data
            return getOrderData(intent, data, requestType);
        }else if(Constants.isActionWebRTC(action)){
            // Get WebRTC Data
            return getUniversalData(intent, data);
        }else if(Constants.isActionJabber(action)){
            // Get Jabber Data
            return getUniversalData(intent, data);
        }else if(Constants.isActionGooglePush(action)){
            // Get Push Data
            return getPushData(intent, data);
        }else if(Constants.isActionFileStorage(action)){
            // Get File Storage Data
            return getFileStorageData(intent, data, requestType);
        }
        
        return null;
    }

    private Bundle getVerificationData(Intent intent, Bundle data, RequestType requestType){
        Object responseObject = null;

        AuthorizationObject authorizationObject = null;
        JSONObject jsonObject = null;
        String strokeObject = null;

        switch (requestType){
            case OBJECT :
                try {
                    jsonObject = sendRequestObject((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonObject!=null){
                    authorizationObject = new Gson().fromJson(jsonObject.toString().trim(), AuthorizationObject.class);
                }

                if(authorizationObject!=null){
                    responseObject = authorizationObject;
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (AuthorizationObject) responseObject);
                }

                break;
            case STRING :
                try {
                    jsonObject = sendRequestObject((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonObject!=null){
                    strokeObject = jsonObject.toString().trim();
                }

                if(strokeObject!=null){
                    responseObject = strokeObject;
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (String) responseObject);
                }

                break;
            case LIST :
                // TODO Not exist now.

                break;
        }

        return data;
    }

    private Bundle getAccountData(Intent intent, Bundle data, RequestType requestType){
        Object responseObject = null;

        AccountObject accountObject = null;
        ArrayList<AccountObject> accountObjectArrayList = null;

        JSONObject jsonObject = null;
        JSONArray jsonArray = null;

        String strokeObject = null;

        switch (requestType){
            case OBJECT :
                try {
                    jsonObject = sendRequestObject((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonObject!=null){
                    accountObject = new Gson().fromJson(jsonObject.toString().trim(), AccountObject.class);
                }

                if(accountObject!=null){
                    responseObject = accountObject;
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (AccountObject) responseObject);
                }

                break;
            case STRING :
                try {
                    jsonObject = sendRequestObject((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonObject!=null){
                    strokeObject = jsonObject.toString().trim();
                }

                if(strokeObject!=null){
                    responseObject = strokeObject;
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (String) responseObject);
                }

                break;
            case LIST :
                try {
                    jsonArray = sendRequestArray((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonArray!=null){
                    accountObjectArrayList = new Gson().fromJson(jsonArray.toString().trim(), new TypeToken<ArrayList<AccountObject>>() { }.getType());
                }

                if (accountObjectArrayList != null) {
                    responseObject = new AccountObject();
                    ((AccountObject)responseObject).setAccountObjectArrayList(accountObjectArrayList);

                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, ((AccountObject)responseObject).getAccountObjectArrayList());
                }

                break;
        }

        return data;
    }

    private Bundle getEducationData(Intent intent, Bundle data, RequestType requestType){
        Object responseObject = null;

        EducationObject educationObject = null;
        ArrayList<EducationObject> educationObjectArrayList = null;

        JSONObject jsonObject = null;
        JSONArray jsonArray = null;

        String strokeObject = null;

        switch (requestType){
            case OBJECT :
                try {
                    jsonObject = sendRequestObject((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonObject!=null){
                    educationObject = new Gson().fromJson(jsonObject.toString().trim(), EducationObject.class);
                }

                if(educationObject!=null){
                    responseObject = educationObject;
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (EducationObject) responseObject);
                }

                break;
            case STRING :
                try {
                    jsonObject = sendRequestObject((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonObject!=null){
                    strokeObject = jsonObject.toString().trim();
                }

                if(strokeObject!=null){
                    responseObject = strokeObject;
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (String) responseObject);
                }

                break;
            case LIST :
                try {
                    jsonArray = sendRequestArray((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonArray!=null){
                    educationObjectArrayList = new Gson().fromJson(jsonArray.toString().trim(), new TypeToken<ArrayList<EducationObject>>() { }.getType());
                }

                if (educationObjectArrayList != null) {
                    responseObject = new EducationObject();
                    ((EducationObject)responseObject).setEducationObjectArrayList(educationObjectArrayList);

                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, ((EducationObject)responseObject).getEducationObjectArrayList());
                }

                break;
        }

        return data;
    }

    private Bundle getArticleData(Intent intent, Bundle data, RequestType requestType){
        Object responseObject = null;

        NewsObject newsObject = null;
        ArrayList<NewsObject> newsObjectArrayList = null;

        JSONObject jsonObject = null;
        JSONArray jsonArray = null;

        String strokeObject = null;

        switch (requestType){
            case OBJECT :
                try {
                    jsonObject = sendRequestObject((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonObject!=null){
                    newsObject = new Gson().fromJson(jsonObject.toString().trim(), NewsObject.class);
                }

                if(newsObject!=null){
                    responseObject = newsObject;
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (NewsObject) responseObject);
                }

                break;
            case STRING :
                try {
                    jsonObject = sendRequestObject((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonObject!=null){
                    strokeObject = jsonObject.toString().trim();
                }

                if(strokeObject!=null){
                    responseObject = strokeObject;
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (String) responseObject);
                }

                break;
            case LIST :
                try {
                    jsonArray = sendRequestArray((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonArray!=null){
                    newsObjectArrayList = new Gson().fromJson(jsonArray.toString().trim(), new TypeToken<ArrayList<NewsObject>>() { }.getType());
                }

                if (newsObjectArrayList != null) {
                    responseObject = new NewsObject();
                    ((NewsObject)responseObject).setNewsObjectArrayList(newsObjectArrayList);

                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, ((NewsObject)responseObject).getNewsObjectArrayList());
                }

                break;
        }

        return data;
    }

    private Bundle getCalendarData(Intent intent, Bundle data, RequestType requestType){
        Object responseObject = null;

        CalendarObject calendarObject = null;
        ArrayList<CalendarObject> calendarObjectArrayList = null;

        JSONObject jsonObject = null;
        JSONArray jsonArray = null;

        String strokeObject = null;

        switch (requestType){
            case OBJECT :
                try {
                    jsonObject = sendRequestObject((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonObject!=null){
                    calendarObject = new Gson().fromJson(jsonObject.toString().trim(), CalendarObject.class);
                }

                if(calendarObject!=null){
                    responseObject = calendarObject;
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (NewsObject) responseObject);
                }

                break;
            case STRING :
                try {
                    jsonObject = sendRequestObject((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonObject!=null){
                    strokeObject = jsonObject.toString().trim();
                }

                if(strokeObject!=null){
                    responseObject = strokeObject;
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (String) responseObject);
                }

                break;
            case LIST :
                try {
                    jsonArray = sendRequestArray((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonArray!=null){
                    calendarObjectArrayList = new Gson().fromJson(jsonArray.toString().trim(), new TypeToken<ArrayList<CalendarObject>>() { }.getType());
                }

                if (calendarObjectArrayList != null) {
                    responseObject = new CalendarObject();
                    ((CalendarObject)responseObject).setCalendarObjectArrayList(calendarObjectArrayList);

                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, ((CalendarObject)responseObject).getCalendarObjectArrayList());
                }

                break;
        }

        return data;
    }

    private Bundle getSocialData(Intent intent, Bundle data, RequestType requestType){
        Object responseObject = null;

        JSONObject jsonObject = null;
        String strokeObject = null;

        switch (requestType){
            case OBJECT :
                // TODO Not exist now.

                break;
            case STRING :
                try {
                    jsonObject = sendRequestObject((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonObject!=null){
                    strokeObject = jsonObject.toString().trim();
                }

                if(strokeObject!=null){
                    responseObject = strokeObject;
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (String) responseObject);
                }

                break;
            case LIST :
                // TODO Not exist now.

                break;
        }

        return data;
    }

    private Bundle getFileData(Intent intent, Bundle data, RequestType requestType){
        Object responseObject = null;

        FileObject fileObject = null;
        ArrayList<FileObject> fileObjectArrayList = null;

        JSONObject jsonObject = null;
        JSONArray jsonArray = null;

        String strokeObject = null;

        switch (requestType){
            case OBJECT :
                try {
                    jsonObject = sendRequestObject((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonObject!=null){
                    fileObject = new Gson().fromJson(jsonObject.toString().trim(), FileObject.class);
                }

                if(fileObject!=null){
                    responseObject = fileObject;
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (FileObject) responseObject);
                }

                break;
            case STRING :
                try {
                    jsonObject = sendRequestObject((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonObject!=null){
                    strokeObject = jsonObject.toString().trim();
                }

                if(strokeObject!=null){
                    responseObject = strokeObject;
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (String) responseObject);
                }

                break;
            case LIST :
                try {
                    jsonArray = sendRequestArray((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonArray!=null){
                    fileObjectArrayList = new Gson().fromJson(jsonArray.toString().trim(), new TypeToken<ArrayList<FileObject>>() { }.getType());
                }

                if (fileObjectArrayList != null) {
                    responseObject = new FileObject();
                    ((FileObject)responseObject).setFileObjectArrayList(fileObjectArrayList);

                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, ((FileObject)responseObject).getFileObjectArrayList());
                }

                break;
        }

        return data;
    }

    private Bundle getProductData(Intent intent, Bundle data, RequestType requestType){
        Object responseObject = null;

        ProductsObject productsObject = null;
        ArrayList<ProductsObject> productObjectArrayList = null;

        JSONObject jsonObject = null;
        JSONArray jsonArray = null;

        String strokeObject = null;

        switch (requestType){
            case OBJECT :
                try {
                    jsonObject = sendRequestObject((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonObject!=null){
                    productsObject = new Gson().fromJson(jsonObject.toString().trim(), ProductsObject.class);
                }

                if(productsObject!=null){
                    responseObject = productsObject;
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (ProductsObject) responseObject);
                }

                break;
            case STRING :
                try {
                    jsonObject = sendRequestObject((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonObject!=null){
                    strokeObject = jsonObject.toString().trim();
                }

                if(strokeObject!=null){
                    responseObject = strokeObject;
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (String) responseObject);
                }

                break;
            case LIST :
                try {
                    jsonArray = sendRequestArray((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonArray!=null){
                    productObjectArrayList = new Gson().fromJson(jsonArray.toString().trim(), new TypeToken<ArrayList<ProductsObject>>() { }.getType());
                }

                if (productObjectArrayList != null) {
                    responseObject = new ProductsObject();
                    ((ProductsObject)responseObject).setProductsObjectArrayList(productObjectArrayList);

                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, ((ProductsObject)responseObject).getProductsObjectArrayList());
                }

                break;
        }

        return data;
    }

    private Bundle getOrderData(Intent intent, Bundle data, RequestType requestType){
        Object responseObject = null;

        OrdersObject ordersObject = null;
        ArrayList<OrdersObject> orderObjectArrayList = null;

        JSONObject jsonObject = null;
        JSONArray jsonArray = null;

        String strokeObject = null;

        switch (requestType){
            case OBJECT :
                try {
                    jsonObject = sendRequestObject((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonObject!=null){
                    ordersObject = new Gson().fromJson(jsonObject.toString().trim(), OrdersObject.class);
                }

                if(ordersObject!=null){
                    responseObject = ordersObject;
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (OrdersObject) responseObject);
                }

                break;
            case STRING :
                try {
                    jsonObject = sendRequestObject((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonObject!=null){
                    strokeObject = jsonObject.toString().trim();
                }

                if(strokeObject!=null){
                    responseObject = strokeObject;
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (String) responseObject);
                }

                break;
            case LIST :
                try {
                    jsonArray = sendRequestArray((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonArray!=null){
                    orderObjectArrayList = new Gson().fromJson(jsonArray.toString().trim(), new TypeToken<ArrayList<OrdersObject>>() { }.getType());
                }

                if (orderObjectArrayList != null) {
                    responseObject = new OrdersObject();
                    ((OrdersObject)responseObject).setOrdersObjectList(orderObjectArrayList);

                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, ((OrdersObject)responseObject).getOrdersObjectList());
                }

                break;
        }

        return data;
    }

    private Bundle getUniversalData(Intent intent, Bundle data){
        Object responseObject = null;

        UniversalPushResult universalPushResult = null;

        JSONObject jsonObject = null;

        try {
            jsonObject = sendRequestObject((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
        }catch (JSONRPCException e){
            LOGI(TAG, e.getMessage());

            responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
            data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
        }

        if(jsonObject!=null){
            universalPushResult = new Gson().fromJson(jsonObject.toString().trim(), UniversalPushResult.class);
        }

        if(universalPushResult!=null){
            responseObject = universalPushResult;
            data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (UniversalPushResult) responseObject);
        }

        return data;
    }

    private Bundle getPushData(Intent intent, Bundle data){
        Object responseObject = null;

        PushObject pushObject = null;

        JSONObject jsonObject = null;

        try {
            jsonObject = sendRequestObject((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
        }catch (JSONRPCException e){
            LOGI(TAG, e.getMessage());

            responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
            data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
        }

        if(jsonObject!=null){
            pushObject = new Gson().fromJson(jsonObject.toString().trim(), PushObject.class);
        }

        if(pushObject!=null){
            responseObject = pushObject;
            data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (PushObject) responseObject);
        }

        return data;
    }

    private Bundle getFileStorageData(Intent intent, Bundle data, RequestType requestType){
        Object responseObject = null;

        JSONObject jsonObject = null;

        String strokeObject = null;

        switch (requestType){
            case OBJECT :
                // TODO Not exist now.

                break;
            case STRING :
                try {
                    jsonObject = sendRequestObject((RequestParamBuilder) intent.getSerializableExtra(EXTRA_PARAM_REQUEST));
                }catch (JSONRPCException e){
                    LOGI(TAG, e.getMessage());

                    responseObject = new Gson().fromJson(e.getMessage().trim(), RPCError.class);
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (RPCError) responseObject);
                }

                if(jsonObject!=null){
                    strokeObject = jsonObject.toString().trim();
                }

                if(strokeObject!=null){
                    responseObject = strokeObject;
                    data.putSerializable(EXTRA_PARAM_MAIL_RESPONSE, (String) responseObject);
                }

                break;
            case LIST :
                // TODO Not exist now.

                break;
        }

        return data;
    }

    private JSONObject sendRequestObject(RequestParamBuilder requestParamBuilder) throws JSONRPCException {
        JSONRPCClient client = JSONRPCClient.create(requestParamBuilder.getUrl(), requestParamBuilder.getJSONRPCVersion());
        // Set Param to Client
        LOGI(TAG, "Request Token : " + requestParamBuilder.getToken());
        client.setToken(requestParamBuilder.getToken());
        client.setConnectionTimeout(requestParamBuilder.getConnectionTimeout());
        client.setSoTimeout(requestParamBuilder.getSoTimeout());

        return client.callJSONObject(requestParamBuilder.getMethod(), requestParamBuilder.getJsonObject());
    }

    private JSONArray sendRequestArray(RequestParamBuilder requestParamBuilder) throws JSONRPCException {
        JSONRPCClient client = JSONRPCClient.create(requestParamBuilder.getUrl(), requestParamBuilder.getJSONRPCVersion());
        // Set Param to Client
        LOGI(TAG, "Request Token : " + requestParamBuilder.getToken());
        client.setToken(requestParamBuilder.getToken());
        client.setConnectionTimeout(requestParamBuilder.getConnectionTimeout());
        client.setSoTimeout(requestParamBuilder.getSoTimeout());

        return client.callJSONArray(requestParamBuilder.getMethod(), requestParamBuilder.getJsonObject());
    }
}
