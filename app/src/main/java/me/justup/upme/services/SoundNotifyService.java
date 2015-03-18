package me.justup.upme.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;

import me.justup.upme.R;


public class SoundNotifyService extends Service implements MediaPlayer.OnCompletionListener {
    public static final String SOUND_NOTIFY_TYPE_EXTRA = "sound_notify_type_extra";

    public static final int TYPE_CHAT = 1;
    public static final int TYPE_CALL = 2;

    private MediaPlayer mMediaPlayer = null;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int type = intent.getIntExtra(SOUND_NOTIFY_TYPE_EXTRA, 0);

        int sound_chat = R.raw.chat;
        int sound_call = R.raw.call;

        if (type == TYPE_CHAT) {
            playNotify(sound_chat);
        } else {
            playNotify(sound_call);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void playNotify(int soundResource) {
        mMediaPlayer = MediaPlayer.create(this, soundResource);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopSelf();
    }

}
