package me.justup.upme.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.justup.upme.R;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.fragments.StatusBarFragment;
import me.justup.upme.utils.AppLocale;

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

    private static final String USER_ID = "owner_id";
    private static final String USER_NAME = "owner_name";
    private static final String CONNECTION_TYPE = "connection_type";
    private static final String ROOM = "room";

    private static final String TIME_FORMAT = "HH:mm - dd MMMM yyyy";

    private Handler mHandler;
    private DBAdapter mDBAdapter;


    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new Handler();

        mDBAdapter = new DBAdapter(this);
        mDBAdapter.open();
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

                    int userId = 0;
                    int connectionType = 0;
                    int room = 0;

                    try {
                        userId = Integer.parseInt((String) extras.get(USER_ID));
                        connectionType = Integer.parseInt((String) extras.get(CONNECTION_TYPE));
                        room = Integer.parseInt((String) extras.get(ROOM));
                    } catch (Exception e) {
                        LOGE(TAG, "Parse push extras", e);
                    }

                    String userName = (String) extras.get(USER_NAME);

                    // Post notification of received message.
                    if (userId != 0 && userName != null) {
                        sendNotification(userId, userName, connectionType, room);
                    }
                    break;

                default:
                    break;
            }
        }

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mDBAdapter.close();
    }

    private void sendNotification(int userId, String userName, int connectionType, int room) {
        LOGD(TAG, "sendNotification: userId:" + userId + " userName:" + userName + " connectionType:" + connectionType + " room:" + room);
        makeToast(getString(R.string.push_received));

        Intent i = new Intent(StatusBarFragment.BROADCAST_ACTION_PUSH);
        i.putExtra(StatusBarFragment.BROADCAST_EXTRA_IS_NEW_MESSAGE, true); // for clear image - send false
        sendBroadcast(i);

        Date date = new Date();
        SimpleDateFormat mTimeFormat = new SimpleDateFormat(TIME_FORMAT, AppLocale.getAppLocale());
        String pushTime = mTimeFormat.format(date);

        mDBAdapter.savePush(connectionType, userId, userName, room, pushTime);
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
