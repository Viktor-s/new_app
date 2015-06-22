package me.justup.upme.db_upme.sync_service;

import android.accounts.Account;
import android.accounts.OperationCanceledException;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.justup.upme.BuildConfig;
import me.justup.upme.JustUpApplication;
import me.justup.upme.R;
import me.justup.upme.api_rpc.request_model.handler.AppIntentHandler;
import me.justup.upme.api_rpc.request_model.service.RequestServiceCallbackListener;
import me.justup.upme.api_rpc.request_model.service.RequestServiceHelper;
import me.justup.upme.api_rpc.response_object.NewsObject;
import me.justup.upme.api_rpc.response_object.RPCError;
import me.justup.upme.api_rpc.utils.JSONObjectBuilder;
import me.justup.upme.api_rpc.utils.RequestParamBuilder;
import me.justup.upme.db_upme.Constants;
import me.justup.upme.db_upme.SyncAdapterMetaData;
import me.justup.upme.db_upme.providers.ShortNewsProvider;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;

public class ShortNewsSyncAdapterService extends Service implements SyncAdapterMetaData, RequestServiceCallbackListener {
    private static final String TAG = ShortNewsSyncAdapterService.class.getSimpleName();

    // Service Helper
    private static RequestServiceHelper mRequestServiceHelper = null;

    private static SyncAdapterImplementation mSyncAdapterImplementation = null;
    private static JustUpApplication mJustUpApplication = null;

    public ShortNewsSyncAdapterService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Init Application
        mJustUpApplication = JustUpApplication.getApplication();

        if (mSyncAdapterImplementation == null) {
            mSyncAdapterImplementation = new SyncAdapterImplementation(mJustUpApplication.getApplicationContext());
        }

        mRequestServiceHelper = JustUpApplication.getApplication().getApiHelper();
        LOGI(TAG, "RequestServiceHelper : " + mRequestServiceHelper);

        mRequestServiceHelper.addListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return getSyncAdapter().getSyncAdapterBinder();
    }

    private SyncAdapterImplementation getSyncAdapter() {
        if (mSyncAdapterImplementation == null)
            mSyncAdapterImplementation = new SyncAdapterImplementation(mJustUpApplication.getApplicationContext());

        return mSyncAdapterImplementation;
    }

    private static class SyncAdapterImplementation extends AbstractThreadedSyncAdapter {

        private Context mContext = null;

        public SyncAdapterImplementation(Context context) {
            super(context, true);

            mContext = context;
        }

        public SyncAdapterImplementation(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
            super(context, autoInitialize, allowParallelSyncs);

            mContext = context;
        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
            try {
                ShortNewsSyncAdapterService.performSync(mContext, account, extras, authority, provider, syncResult);
            } catch (OperationCanceledException e) {
                LOGE(TAG, e.getMessage());
            }
        }
    }

    private static void performSync(Context context, Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) throws OperationCanceledException {
        LOGD(TAG, "ContentProviderClient : " + provider.toString());

        if (provider.getLocalContentProvider() instanceof ShortNewsProvider) {

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Account Name = " + account.name + "\n" +
                        "Account toString() = " + account.toString() + "\n" +
                        "Bundle = " + extras.toString() + "\n" +
                        "String = " + authority + "\n" +
                        "CPClient = " + provider.toString() +
                        "SResult = " + syncResult.toString());
            }

            if (checkConnectingToInternet(context)) {
                // Send Start Broadcast to Activity
                Intent startIntent = new Intent(Constants.SYNC_STATUS_RECEIVER);
                startIntent.putExtra(Constants.SYNC_STATUS_TEXT, R.string.sync_adapter_news_sync);
                startIntent.putExtra(Constants.SYNC_STATUS_TAG, Constants.SYNC_STATUS_START);
                context.sendBroadcast(startIntent);

                // Check is Application is started
                if(mJustUpApplication!=null) {
                    if (mJustUpApplication.getTransferActionMailContact() == null) {
                        mJustUpApplication.setTransferActionMailContact(account.name);
                    }

                    if (mJustUpApplication.getTransferActionMailContact() == null) {
                        mJustUpApplication.setTransferActionMailContact(account.name);
                    }
                }

                new Handler(Looper.getMainLooper()).post(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mRequestServiceHelper.sendRequest(new RequestParamBuilder.ParamBuilder(me.justup.upme.api_rpc.utils.Constants.ARTICLES_GET, new JSONObjectBuilder(new JSONObject()).limit("100").offset("0").order("ASC").build(), me.justup.upme.api_rpc.utils.Constants.ACTION_ARTICLES_GET, AppIntentHandler.RequestType.LIST).token(JustUpApplication.getApplication().getAppPreferences().getToken()).build());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
            }
        }
    }

    @Override
    public void onServiceCallback(int requestId, Intent requestIntent, int resultCode, Bundle data) {
        // Get Action
        String action = requestIntent.getAction();

        LOGI(TAG, "Request id : " + requestId + ", Intent : " + requestIntent + ", Result Code : " + resultCode + ", Bundle : " + data);

        switch (action){
            case me.justup.upme.api_rpc.utils.Constants.ACTION_ARTICLES_GET :
                if(resultCode== AppIntentHandler.SUCCESS_RESPONSE){
                    ArrayList<NewsObject> newsObjectArrayList = (ArrayList<NewsObject>) data.getSerializable(AppIntentHandler.EXTRA_PARAM_MAIL_RESPONSE);
                    LOGD(TAG, "ArrayList<NewsObject> : " + newsObjectArrayList.toString());

                    JustUpApplication.getApplication().getTransferActionShortNews().insertShortNewsList(getApplicationContext(), newsObjectArrayList);
                }else{
                    RPCError rpcError = (RPCError) data.getSerializable(AppIntentHandler.EXTRA_PARAM_MAIL_RESPONSE);
                    LOGI(TAG, "RPCError : " + rpcError.toString());
                }

                break;
            default :
                break;
        }

        // Send End Broadcast to Activity
        Intent intentEnd = new Intent(Constants.SYNC_STATUS_RECEIVER);
        intentEnd.putExtra(Constants.SYNC_STATUS_TEXT, R.string.sync_adapter_news_sync);
        intentEnd.putExtra(Constants.SYNC_STATUS_TAG, Constants.SYNC_STATUS_END);
        getApplicationContext().sendBroadcast(intentEnd);
    }

    /**
     * true - on / false - off
     */
    public static boolean checkConnectingToInternet(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
