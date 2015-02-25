package me.justup.upme.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    private static final String TAG = makeLogTag(GcmIntentService.class);

    private static final String PUSH_TITLE = "title";
    private static final String PUSH_MESSAGE = "message";

    private Handler mHandler;


    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            switch (messageType) {
                case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR:
                    LOGE(TAG, "Send error: " + extras.toString());
                    break;

                case GoogleCloudMessaging.MESSAGE_TYPE_DELETED:
                    LOGD(TAG, "Deleted messages on server: " + extras.toString());
                    break;

                // If it's a regular GCM message, do some work.
                case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE:
                    LOGI(TAG, "PUSH received: " + extras.toString());

                    String title = (String) extras.get(PUSH_TITLE);
                    String message = (String) extras.get(PUSH_MESSAGE);

                    // Post notification of received message.
                    if (title != null && message != null) {
                        sendNotification(title, message);
                    }
                    break;

                default:
                    break;
            }
        }

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String title, String message) {
        LOGD(TAG, "sendNotification: " + title + " " + message);

        makeToast("Принято сообщение: " + title + " " + message);

        /*
        Intent i = new Intent();
                i.setAction("GCM_NOTIFY");
                i.putExtra("message", message);
                sendBroadcast(i);
        */
    }

    private void makeToast(final String message) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(GcmIntentService.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
