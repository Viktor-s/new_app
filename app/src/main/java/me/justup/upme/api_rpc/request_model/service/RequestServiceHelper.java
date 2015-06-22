package me.justup.upme.api_rpc.request_model.service;

import android.app.Application;
import android.content.Intent;

import me.justup.upme.api_rpc.request_model.handler.AppIntentHandler;
import me.justup.upme.api_rpc.utils.RequestParamBuilder;

import static me.justup.upme.utils.LogUtils.LOGI;

public class RequestServiceHelper extends RequestBaseServiceHelper {
    private static final String TAG = RequestServiceHelper.class.getSimpleName();

    public RequestServiceHelper(Application app) {
        super(app);
    }

    public int sendRequest(RequestParamBuilder requestParamBuilder){
        int requestId = createId();
        LOGI(TAG, "SendRequest (request Id) : " + requestId);

        Intent intent = createIntent(mApplication, requestParamBuilder.getAction(), requestId);
        intent.putExtra(AppIntentHandler.EXTRA_PARAM_REQUEST, requestParamBuilder);
        intent.putExtra(AppIntentHandler.EXTRA_PARAM_REQUEST_TYPE, requestParamBuilder.getRequestType());

        return runRequest(requestId, intent);
    }

}
