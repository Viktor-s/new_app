package me.justup.upme.api_rpc.request_model;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Process;
import android.os.ResultReceiver;
import android.text.TextUtils;

import java.util.ArrayList;

import me.justup.upme.BuildConfig;
import me.justup.upme.api_rpc.request_model.handler.AppIntentHandler;
import me.justup.upme.api_rpc.utils.Constants;

import static me.justup.upme.utils.LogUtils.LOGD;

/**
 * About this technology : http://habrahabr.ru/post/167679/
 */
public class RequestService extends IntentService {
    private static final String TAG = RequestService.class.getSimpleName();

    public static final String EXTRA_STATUS_RECEIVER = BuildConfig.APPLICATION_ID.concat(".STATUS_RECEIVER");

    private ArrayList<String> mActionList = new ArrayList<String>(); // List Incoming Action
    private ArrayList<Intent> mActionIntent = new ArrayList<Intent>(); // List Incoming Intent

    public RequestService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Set height priority
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        String action = intent.getAction();
        LOGD(TAG, "Action : " + action);

        // If Connection
        if (checkConnectingToInternet()) {
            // Re-send requests to the server
            resendData();
            // Send requests
            doAction(action, intent);
        } else {
            // Add to poll request
            mActionList.add(action);
            mActionIntent.add(intent);
        }

    }

    /**
     * Resend data, used this method only not main thread
     */
    private void resendData() {
        if (mActionList!=null && mActionIntent!=null && !mActionList.isEmpty() && !mActionIntent.isEmpty() && mActionList.size()==mActionIntent.size()) {
            for (int i = 0; i < mActionList.size(); i++) {
                if(checkConnectingToInternet()) {
                    doAction(mActionList.get(i), mActionIntent.get(i));  // Do Action from List
                }
            }
        }
    }

    /**
     * Do Incoming Action
     */
    private void doAction(String action, Intent intent) {

        if (!TextUtils.isEmpty(action)) {
            ResultReceiver receiver = getReceiver(intent);

            if (Constants.isActionOwn(action)) {
                new AppIntentHandler().execute(intent, getApplication(), receiver);
            }
        }
    }

    /**
     * true - on / false - off
     */
    public boolean checkConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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

    private ResultReceiver getReceiver(Intent intent) {
        return intent.getParcelableExtra(EXTRA_STATUS_RECEIVER);
    }

}