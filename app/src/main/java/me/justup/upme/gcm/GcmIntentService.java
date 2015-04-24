package me.justup.upme.gcm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.justup.upme.JustUpApplication;
import me.justup.upme.MainActivity;
import me.justup.upme.R;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.entity.Push;
import me.justup.upme.fragments.MailFragment;
import me.justup.upme.fragments.StatusBarFragment;
import me.justup.upme.services.SoundNotifyService;
import me.justup.upme.utils.AppLocale;

import static me.justup.upme.services.SoundNotifyService.SOUND_NOTIFY_TYPE_EXTRA;
import static me.justup.upme.services.SoundNotifyService.TYPE_CALL;
import static me.justup.upme.services.SoundNotifyService.TYPE_CHAT;
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

    private static final String CONNECTIONS = "connections";
    private static final String USER_ID = "user_id";
    private static final String USER_NAME = "name";
    private static final String CONNECTION_TYPE = "type";
    private static final String ROOM_ID = "room_id";
    private static final String LINK = "link";
    private static final String JABBER_ID = "jabber_id";
    private static final String FILE_NAME = "file_name";
    private static final String FORM_ID = "form_id";
    private static final String PUSH_DESCRIPTION = "push_description";

    private static final String TIME_FORMAT = "HH:mm - dd MMMM yyyy";

    private Handler mHandler = new Handler();

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        DBAdapter.getInstance().openDatabase();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "My Tag");
            wl.acquire();
            wl.release();
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

            switch (messageType) {
                case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR:
                    LOGE(TAG, "Send error : " + extras.toString());
                    break;

                case GoogleCloudMessaging.MESSAGE_TYPE_DELETED:
                    LOGD(TAG, "Deleted messages on server : " + extras.toString());
                    break;

                // If it's a regular GCM message, do some work.
                case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE:
                    LOGI(TAG, "PUSH received : " + extras.toString());

                    final Push push = createPushObject((String) extras.get(CONNECTIONS));

                    // Post notification of received message.
                    if (push != null && push.getType() != 0) {
                        sendNotification(push);
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

        DBAdapter.getInstance().closeDatabase();
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(final Push push) {
        LOGD(TAG, "Push message : " + push.toString());

        if (push.getType() == MailFragment.BREAK_CALL) {
            Intent i = new Intent(MainActivity.BROADCAST_ACTION_BREAK_CALL);
            i.putExtra(MainActivity.BROADCAST_EXTRA_BREAK_CALL, push.getUserName());
            sendBroadcast(i);

            return;
        }

        makeToast(getString(R.string.push_received));

        Intent i = new Intent(StatusBarFragment.BROADCAST_ACTION_PUSH);
        i.putExtra(StatusBarFragment.BROADCAST_EXTRA_IS_NEW_MESSAGE, true); // for clear image - send false
        sendBroadcast(i);

        if (push.getType() == MailFragment.WEBRTC) {
            Intent webRTCIntent = new Intent(MainActivity.BROADCAST_ACTION_CALL);
            webRTCIntent.putExtra(MainActivity.BROADCAST_EXTRA_PUSH, push);
            sendBroadcast(webRTCIntent);

            playNotify(TYPE_CALL);
        } else {
            playNotify(TYPE_CHAT);
        }

        Date date = new Date();
        SimpleDateFormat mTimeFormat = new SimpleDateFormat(TIME_FORMAT, AppLocale.getAppLocale());
        String pushTime = mTimeFormat.format(date);

        if (push.getType() == MailFragment.ORDER_INFO) {
            push.setPushDescription("Payment OK");
        }

        DBAdapter.getInstance().savePush(push, pushTime);
    }

    private void playNotify(int type) {
        Intent sound = new Intent(JustUpApplication.getApplication(), SoundNotifyService.class);
        sound.putExtra(SOUND_NOTIFY_TYPE_EXTRA, type);
        startService(sound);
    }

    private void makeToast(final String message) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(GcmIntentService.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Push createPushObject(String jsonString) {
        Push push = new Push();

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            if (jsonObject.has(USER_ID))
                push.setUserId(jsonObject.getInt(USER_ID));
            if (jsonObject.has(USER_NAME))
                push.setUserName(jsonObject.getString(USER_NAME));
            if (jsonObject.has(CONNECTION_TYPE))
                push.setType(jsonObject.getInt(CONNECTION_TYPE));
            if (jsonObject.has(ROOM_ID))
                push.setRoom(jsonObject.getString(ROOM_ID));
            if (jsonObject.has(LINK))
                push.setLink(jsonObject.getString(LINK));
            if (jsonObject.has(JABBER_ID))
                push.setJabberId(jsonObject.getString(JABBER_ID));
            if (jsonObject.has(FILE_NAME))
                push.setFileName(jsonObject.getString(FILE_NAME));
            if (jsonObject.has(FORM_ID))
                push.setFormId(jsonObject.getString(FORM_ID));
            if (jsonObject.has(PUSH_DESCRIPTION))
                push.setPushDescription(jsonObject.getString(PUSH_DESCRIPTION));

        } catch (JSONException e) {
            LOGE(TAG, "createPushObject() \nError : ", e);
            return null;
        }

        return push;
    }

}
