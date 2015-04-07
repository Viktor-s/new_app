package me.justup.upme;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.webkit.URLUtil;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.util.Random;

import me.justup.upme.db.DBAdapter;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;

@ReportsCrashes
     (
        formKey = "",
        mailTo = "initrod@gmail.com",
        customReportContent = {ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION,
                               ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.USER_COMMENT},
        mode = ReportingInteractionMode.DIALOG,
        resToastText = R.string.crash_toast_text,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info,
        resDialogTitle = R.string.crash_dialog_title,
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
        resDialogOkToast = R.string.crash_dialog_ok_toast
     )

public class JustUpApplication extends Application {
    private static final String TAG = JustUpApplication.class.getSimpleName();

    private static JustUpApplication mJustUpApplication = null;
    synchronized public static JustUpApplication getApplication() {
        return mJustUpApplication;
    }

    // Room random
    private static final int min = 1000000000;
    private static final int max = 2147483647;

    private SharedPreferences mSharedPref = null;
    private String keyprefResolution = null;
    private String keyprefFps = null;
    private String keyprefBitrateType = null;
    private String keyprefBitrateValue = null;
    private String keyprefVideoCodec = null;
    private String keyprefHwCodecAcceleration = null;
    private String keyprefCpuUsageDetection = null;
    private String keyprefDisplayHud = null;
    private String keyprefRoomServerUrl = null;

    public static final String EXTRA_ROOM_URL = "org.appspot.apprtc.ROOM_URL";
    public static final String EXTRA_ROOMID = "org.appspot.apprtc.ROOMID";
    public static final String EXTRA_LOOPBACK = "org.appspot.apprtc.LOOPBACK";
    public static final String EXTRA_HWCODEC = "org.appspot.apprtc.HWCODEC";
    public static final String EXTRA_VIDEO_BITRATE = "org.appspot.apprtc.VIDEO_BITRATE";
    public static final String EXTRA_VIDEO_WIDTH = "org.appspot.apprtc.VIDEO_WIDTH";
    public static final String EXTRA_VIDEO_HEIGHT = "org.appspot.apprtc.VIDEO_HEIGHT";
    public static final String EXTRA_VIDEO_FPS = "org.appspot.apprtc.VIDEO_FPS";
    public static final String EXTRA_VIDEOCODEC = "org.appspot.apprtc.VIDEOCODEC";
    public static final String EXTRA_CPUOVERUSE_DETECTION = "org.appspot.apprtc.CPUOVERUSE_DETECTION";
    public static final String EXTRA_DISPLAY_HUD = "org.appspot.apprtc.DISPLAY_HUD";
    public static final String EXTRA_CMDLINE = "org.appspot.apprtc.CMDLINE";
    public static final String EXTRA_RUNTIME = "org.appspot.apprtc.RUNTIME";
    public static final String EXTRA_IDPERSON = "org.appspot.apprtc.IDPERSON";

    @Override
    public void onCreate() {
        // Bug report systems
        ACRA.init(this);

        super.onCreate();

        new Runnable() {
            @Override
            public void run() {
                // Logic Application
                initApplication();
            }
        }.run();

    }

    private void initApplication(){
        mJustUpApplication = JustUpApplication.this;

        // Init Pref
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        keyprefResolution = getString(R.string.pref_resolution_key);
        keyprefFps = getString(R.string.pref_fps_key);
        keyprefBitrateType = getString(R.string.pref_startbitrate_key);
        keyprefBitrateValue = getString(R.string.pref_startbitratevalue_key);
        keyprefVideoCodec = getString(R.string.pref_videocodec_key);
        keyprefHwCodecAcceleration = getString(R.string.pref_hwcodec_key);
        keyprefCpuUsageDetection = getString(R.string.pref_cpu_usage_detection_key);
        keyprefDisplayHud = getString(R.string.pref_displayhud_key);
        keyprefRoomServerUrl = getString(R.string.pref_room_server_url_key);

        // Main App DB Singleton
        DBAdapter.initInstance();
    }

    public Bundle prepareCallParam(String roomId, Boolean loopback, Boolean commandLineRun, int runTimeMs, int idPerson){

        String roomUrl = mSharedPref.getString(keyprefRoomServerUrl, getString(R.string.pref_room_server_url_default));

        // Get default video codec.
        String videoCodec = mSharedPref.getString(keyprefVideoCodec, getString(R.string.pref_videocodec_default));

        // Check HW codec flag.
        boolean hwCodec = mSharedPref.getBoolean(keyprefHwCodecAcceleration, Boolean.valueOf(getString(R.string.pref_hwcodec_default)));

        // Get video resolution from settings.
        int videoWidth = 0;
        int videoHeight = 0;
        String resolution = mSharedPref.getString(keyprefResolution, getString(R.string.pref_resolution_default));
        String[] dimensions = resolution.split("[ x]+");
        if (dimensions.length == 2) {
            try {
                videoWidth = Integer.parseInt(dimensions[0]);
                videoHeight = Integer.parseInt(dimensions[1]);
            } catch (NumberFormatException e) {
                videoWidth = 0;
                videoHeight = 0;

                LOGE(TAG, "Wrong video resolution setting : " + resolution);
            }
        }

        // Get camera fps from settings.
        int cameraFps = 0;
        String fps = mSharedPref.getString(keyprefFps, getString(R.string.pref_fps_default));
        String[] fpsValues = fps.split("[ x]+");
        if (fpsValues.length == 2) {
            try {
                cameraFps = Integer.parseInt(fpsValues[0]);
            } catch (NumberFormatException e) {
                LOGE(TAG, "Wrong camera fps setting : " + fps);
            }
        }

        // Get start bitrate.
        int startBitrate = 0;
        String bitrateTypeDefault = getString(R.string.pref_startbitrate_default);
        String bitrateType = mSharedPref.getString(keyprefBitrateType, bitrateTypeDefault);
        if (!bitrateType.equals(bitrateTypeDefault)) {
            String bitrateValue = mSharedPref.getString(keyprefBitrateValue, getString(R.string.pref_startbitratevalue_default));
            startBitrate = Integer.parseInt(bitrateValue);
        }

        // Test if CpuOveruseDetection should be disabled. By default is on.
        boolean cpuOveruseDetection = mSharedPref.getBoolean(keyprefCpuUsageDetection, Boolean.valueOf(getString(R.string.pref_cpu_usage_detection_default)));

        // Check statistics display option.
        boolean displayHud = mSharedPref.getBoolean(keyprefDisplayHud, Boolean.valueOf(getString(R.string.pref_displayhud_default)));

        // Prepare Bundle.
        LOGD(TAG, "Connecting to room " + roomId + " at URL " + roomUrl);

        if (validateUrl(roomUrl)) {
            Bundle args = new Bundle();

            args.putString(EXTRA_ROOM_URL, roomUrl);
            args.putString(EXTRA_ROOMID, roomId);
            args.putBoolean(EXTRA_LOOPBACK, loopback);
            args.putString(EXTRA_VIDEOCODEC, videoCodec);
            args.putBoolean(EXTRA_HWCODEC, hwCodec);
            args.putInt(EXTRA_VIDEO_BITRATE, startBitrate);
            args.putInt(EXTRA_VIDEO_WIDTH, videoWidth);
            args.putInt(EXTRA_VIDEO_HEIGHT, videoHeight);
            args.putInt(EXTRA_VIDEO_FPS, cameraFps);
            args.putBoolean(EXTRA_CPUOVERUSE_DETECTION, cpuOveruseDetection);
            args.putBoolean(EXTRA_DISPLAY_HUD, displayHud);
            args.putBoolean(EXTRA_CMDLINE, commandLineRun);
            args.putInt(EXTRA_RUNTIME, runTimeMs);

            args.putInt(EXTRA_IDPERSON, idPerson);

            return args;
        }

        return null;
    }

    private boolean validateUrl(String url) {
        if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
            return true;
        }

        new AlertDialog.Builder(this)
                .setTitle(getText(R.string.invalid_url_title))
                .setMessage(getString(R.string.invalid_url_text, url))
                .setCancelable(false)
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).create().show();

        return false;
    }

    public int getRandomNum(){
        return new Random().nextInt((max - min) + 1) + min;
    }

}
