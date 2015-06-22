package me.justup.upme;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.webkit.URLUtil;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.lang.ref.WeakReference;
import java.util.Random;

import me.justup.upme.api_rpc.request_model.service.RequestServiceHelper;
import me.justup.upme.db_upme.Constants;
import me.justup.upme.db_upme.transfers.TransferActionBrandCategories;
import me.justup.upme.db_upme.transfers.TransferActionEducationModulesMaterial;
import me.justup.upme.db_upme.transfers.TransferActionEducationProductModule;
import me.justup.upme.db_upme.transfers.TransferActionEducationProducts;
import me.justup.upme.db_upme.transfers.TransferActionEventCalendar;
import me.justup.upme.db_upme.transfers.TransferActionFullNews;
import me.justup.upme.db_upme.transfers.TransferActionIsShortNewsRead;
import me.justup.upme.db_upme.transfers.TransferActionMailContact;
import me.justup.upme.db_upme.transfers.TransferActionNewsComments;
import me.justup.upme.db_upme.transfers.TransferActionProductHTML;
import me.justup.upme.db_upme.transfers.TransferActionProductsCategories;
import me.justup.upme.db_upme.transfers.TransferActionProductsProduct;
import me.justup.upme.db_upme.transfers.TransferActionShortNews;
import me.justup.upme.db_upme.transfers.TransferActionStatusBarPush;
import me.justup.upme.db_upme.transfers.TransferActionTileMenu;
import me.justup.upme.launcher.IconCache;
import me.justup.upme.launcher.LauncherModel;
import me.justup.upme.launcher.LauncherProvider;
import me.justup.upme.launcher.LauncherSettings;
import me.justup.upme.utils.AppPreferences;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;

@ReportsCrashes
     (
        formKey = "",
        mailTo = "upMeUser@gmail.com",
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PHONE_MODEL,
                ReportField.CUSTOM_DATA,
                ReportField.STACK_TRACE,
                ReportField.USER_COMMENT},
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

    // Client-Server Communication Object
    private RequestServiceHelper mRequestServiceHelper = null;

    /**
     * @return Main Api Request and Response Class
     */
    public RequestServiceHelper getApiHelper() {
        if(mRequestServiceHelper==null){
            mRequestServiceHelper = new RequestServiceHelper(this);
        }

        return mRequestServiceHelper;
    }

    // App Preferences
    private AppPreferences mAppPreferences = null;

    /**
     * @return AppPreferences Class
     */
    public AppPreferences getAppPreferences() {
        if(mAppPreferences==null){
            mAppPreferences = new AppPreferences(this);
        }

        return mAppPreferences;
    }

    // DB Transfers Object
    private TransferActionBrandCategories mTransferActionBrandCategories = null;
    private TransferActionEducationModulesMaterial mTransferActionEducationModulesMaterial = null;
    private TransferActionEducationProductModule mTransferActionEducationProductModule = null;
    private TransferActionEducationProducts mTransferActionEducationProducts = null;
    private TransferActionEventCalendar mTransferActionEventCalendar = null;
    private TransferActionFullNews mTransferActionFullNews = null;
    private TransferActionIsShortNewsRead mTransferActionIsShortNewsRead = null;
    private TransferActionMailContact mTransferActionMailContact = null;
    private TransferActionNewsComments mTransferActionNewsComments = null;
    private TransferActionProductHTML mTransferActionProductHTML = null;
    private TransferActionProductsCategories mTransferActionProductsCategories = null;
    private TransferActionProductsProduct mTransferActionProductsProduct = null;
    private TransferActionShortNews mTransferActionShortNews = null;
    private TransferActionStatusBarPush mTransferActionStatusBarPush = null;
    private TransferActionTileMenu mTransferActionTileMenu = null;

    private static AccountManager mAccountManager = null;
    private Account mAccount = null;

    private static JustUpApplication mJustUpApplication = null;
    synchronized public static JustUpApplication getApplication() {
        return mJustUpApplication;
    }

    // Launcher
    public LauncherModel mModel = null;
    private IconCache mIconCache = null;

    private static final String sSharedPreferencesKey = "com.android.launcher2.prefs";

    private static boolean sIsScreenLarge;
    private static int sLongPressTimeout = 300;
    private static float sScreenDensity;

    private WeakReference<LauncherProvider> mLauncherProvider = null;

    // Room random
    private static final int min = 1000000000;
    private static final int max = 2147483647;

    private SharedPreferences mSharedPref = null;
    private String keyprefVideoCallEnabled = null;
    private String keyprefResolution = null;
    private String keyprefFps = null;
    private String keyprefVideoBitrateType = null;
    private String keyprefBitrateType = null;
    private String keyprefBitrateValue = null;
    private String keyprefVideoCodec = null;
    private String keyprefVideoBitrateValue = null;
    private String keyprefHwCodecAcceleration = null;
    private String keyprefAudioBitrateType = null;
    private String keyprefAudioBitrateValue = null;
    private String keyprefAudioCodec = null;
    private String keyprefCpuUsageDetection = null;
    private String keyprefDisplayHud = null;
    private String keyprefRoomServerUrl = null;
    private String keyprefRoom = null;
    private String keyprefRoomList = null;

    public static final String EXTRA_ROOM_URL = "org.appspot.apprtc.ROOM_URL";
    public static final String EXTRA_ROOMID = "org.appspot.apprtc.ROOMID";
    public static final String EXTRA_LOOPBACK = "org.appspot.apprtc.LOOPBACK";
    public static final String EXTRA_HWCODEC = "org.appspot.apprtc.HWCODEC";
    public static final String EXTRA_VIDEO_CALL = "org.appspot.apprtc.VIDEO_CALL";
    public static final String EXTRA_VIDEO_BITRATE = "org.appspot.apprtc.VIDEO_BITRATE";
    public static final String EXTRA_VIDEO_WIDTH = "org.appspot.apprtc.VIDEO_WIDTH";
    public static final String EXTRA_VIDEO_HEIGHT = "org.appspot.apprtc.VIDEO_HEIGHT";
    public static final String EXTRA_VIDEO_FPS = "org.appspot.apprtc.VIDEO_FPS";
    public static final String EXTRA_VIDEOCODEC = "org.appspot.apprtc.VIDEOCODEC";
    public static final String EXTRA_HWCODEC_ENABLED = "org.appspot.apprtc.HWCODEC";
    public static final String EXTRA_CPUOVERUSE_DETECTION = "org.appspot.apprtc.CPUOVERUSE_DETECTION";
    public static final String EXTRA_AUDIO_BITRATE = "org.appspot.apprtc.AUDIO_BITRATE";
    public static final String EXTRA_AUDIOCODEC = "org.appspot.apprtc.AUDIOCODEC";
    public static final String EXTRA_DISPLAY_HUD = "org.appspot.apprtc.DISPLAY_HUD";
    public static final String EXTRA_CMDLINE = "org.appspot.apprtc.CMDLINE";
    public static final String EXTRA_RUNTIME = "org.appspot.apprtc.RUNTIME";
    public static final String EXTRA_IDPERSON = "org.appspot.apprtc.IDPERSON";
    public static final String EXTRA_CONTACT_NAME = "org.appspot.apprtc.CONTACT_NAME";

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
        // Client-Server Object init
        mRequestServiceHelper = new RequestServiceHelper(this);

        // Init App Preferences
        mAppPreferences = new AppPreferences(this);

        // Check this App for Account
        checkAppAccount();

        // Launcher
        sIsScreenLarge = getResources().getBoolean(R.bool.is_large_screen);
        sScreenDensity = getResources().getDisplayMetrics().density;

        mIconCache = new IconCache(this);
        mModel = new LauncherModel(this, mIconCache);

        // Register intent receivers
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");

        registerReceiver(mModel, filter);

        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);

        registerReceiver(mModel, filter);

        filter = new IntentFilter();
        filter.addAction(SearchManager.INTENT_GLOBAL_SEARCH_ACTIVITY_CHANGED);

        registerReceiver(mModel, filter);

        filter = new IntentFilter();
        filter.addAction(SearchManager.INTENT_ACTION_SEARCHABLES_CHANGED);

        registerReceiver(mModel, filter);

        // Register for changes to the favorites
        ContentResolver resolver = getContentResolver();
        resolver.registerContentObserver(LauncherSettings.Favorites.CONTENT_URI, true, mFavoritesObserver);

        mJustUpApplication = JustUpApplication.this;

        // Init Pref
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        keyprefVideoCallEnabled = getString(R.string.pref_videocall_key);
        keyprefResolution = getString(R.string.pref_resolution_key);
        keyprefFps = getString(R.string.pref_fps_key);
        keyprefVideoBitrateType = getString(R.string.pref_startvideobitrate_key);
        keyprefVideoBitrateValue = getString(R.string.pref_startvideobitratevalue_key);
        keyprefVideoCodec = getString(R.string.pref_videocodec_key);
        keyprefHwCodecAcceleration = getString(R.string.pref_hwcodec_key);
        keyprefAudioBitrateType = getString(R.string.pref_startaudiobitrate_key);
        keyprefAudioBitrateValue = getString(R.string.pref_startaudiobitratevalue_key);
        keyprefAudioCodec = getString(R.string.pref_audiocodec_key);
        keyprefCpuUsageDetection = getString(R.string.pref_cpu_usage_detection_key);
        keyprefDisplayHud = getString(R.string.pref_displayhud_key);
        keyprefRoomServerUrl = getString(R.string.pref_room_server_url_key);
        keyprefRoom = getString(R.string.pref_room_key);
        keyprefRoomList = getString(R.string.pref_room_list_key);
    }

    public Bundle prepareCallParam(String roomId, Boolean loopback, Boolean commandLineRun, int runTimeMs, int idPerson, String contactName){

        String roomUrl = mSharedPref.getString(keyprefRoomServerUrl, getString(R.string.pref_room_server_url_default));

        // Get default codecs.
        String videoCodec = mSharedPref.getString(keyprefVideoCodec,
                getString(R.string.pref_videocodec_default));
        String audioCodec = mSharedPref.getString(keyprefAudioCodec,
                getString(R.string.pref_audiocodec_default));

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

        // Get video and audio start bitrate.
        int videoStartBitrate = 0;
        String bitrateTypeDefault = getString(R.string.pref_startvideobitrate_default);
        String bitrateType = mSharedPref.getString(keyprefVideoBitrateType, bitrateTypeDefault);
        if (!bitrateType.equals(bitrateTypeDefault)) {
            String bitrateValue = mSharedPref.getString(keyprefVideoBitrateValue, getString(R.string.pref_startvideobitratevalue_default));
            videoStartBitrate = Integer.parseInt(bitrateValue);
        }

        int audioStartBitrate = 0;
        bitrateTypeDefault = getString(R.string.pref_startaudiobitrate_default);
        bitrateType = mSharedPref.getString(keyprefAudioBitrateType, bitrateTypeDefault);
        if (!bitrateType.equals(bitrateTypeDefault)) {
            String bitrateValue = mSharedPref.getString(keyprefAudioBitrateValue, getString(R.string.pref_startaudiobitratevalue_default));
            audioStartBitrate = Integer.parseInt(bitrateValue);
        }

        // Test if CpuOveruseDetection should be disabled. By default is on.
        boolean cpuOveruseDetection = mSharedPref.getBoolean(keyprefCpuUsageDetection, Boolean.valueOf(getString(R.string.pref_cpu_usage_detection_default)));

        // Check statistics display option.
        boolean displayHud = mSharedPref.getBoolean(keyprefDisplayHud, Boolean.valueOf(getString(R.string.pref_displayhud_default)));

        // Video call enabled flag.
        boolean videoCallEnabled = mSharedPref.getBoolean(keyprefVideoCallEnabled, Boolean.valueOf(getString(R.string.pref_videocall_default)));

        // Prepare Bundle.
        LOGD(TAG, "Connecting to room " + roomId + " at URL " + roomUrl);

        if (validateUrl(roomUrl)) {
            Bundle args = new Bundle();

            args.putString(EXTRA_ROOMID, roomId);
            args.putBoolean(EXTRA_LOOPBACK, loopback);
            args.putBoolean(EXTRA_VIDEO_CALL, videoCallEnabled);
            args.putInt(EXTRA_VIDEO_WIDTH, videoWidth);
            args.putInt(EXTRA_VIDEO_HEIGHT, videoHeight);
            args.putInt(EXTRA_VIDEO_FPS, cameraFps);
            args.putInt(EXTRA_VIDEO_BITRATE, videoStartBitrate);
            args.putString(EXTRA_VIDEOCODEC, videoCodec);
            args.putBoolean(EXTRA_HWCODEC, hwCodec);
            args.putInt(EXTRA_AUDIO_BITRATE, audioStartBitrate);
            args.putString(EXTRA_AUDIOCODEC, audioCodec);
            args.putString(EXTRA_ROOM_URL, roomUrl);
            args.putBoolean(EXTRA_CPUOVERUSE_DETECTION, cpuOveruseDetection);
            args.putBoolean(EXTRA_DISPLAY_HUD, displayHud);
            args.putBoolean(EXTRA_CMDLINE, commandLineRun);
            args.putInt(EXTRA_RUNTIME, runTimeMs);

            args.putInt(EXTRA_IDPERSON, idPerson);
            args.putString(EXTRA_CONTACT_NAME, contactName);

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

    // Launcher
    @Override
    public void onTerminate() {
        super.onTerminate();

        if(mModel!=null) {
            unregisterReceiver(mModel);
        }

        if(mFavoritesObserver!=null) {
            ContentResolver resolver = getContentResolver();
            resolver.unregisterContentObserver(mFavoritesObserver);
        }
    }

    /**
     * Receives notifications whenever the user favorites have changed.
     */
    private final ContentObserver mFavoritesObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            // If the database has ever changed, then we really need to force a
            // reload of the
            // workspace on the next load
            mModel.resetLoadedState(false, true);
            mModel.startLoaderFromBackground();
        }
    };

    public LauncherModel setLauncher(LauncherActivity launcher) {
        mModel.initialize(launcher);
        return mModel;
    }

    public IconCache getIconCache() {
        return mIconCache;
    }

    public LauncherModel getModel() {
        return mModel;
    }

    public  void setLauncherProvider(LauncherProvider provider) {
        mLauncherProvider = new WeakReference<LauncherProvider>(provider);
    }

    public LauncherProvider getLauncherProvider() {
        return mLauncherProvider.get();
    }

    public static String getSharedPreferencesKey() {
        return sSharedPreferencesKey;
    }

    public static boolean isScreenLarge() {
        return sIsScreenLarge;
    }

    public static boolean isScreenLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static float getScreenDensity() {
        return sScreenDensity;
    }

    public static int getLongPressTimeout() {
        return sLongPressTimeout;
    }

    public static int getScreenDensityDpi(){
        return (int)(sScreenDensity * 160f);
    }

    // DB
    private void checkAppAccount(){
        // Check AccountManager for NULL
        if(mAccountManager==null) {
            mAccountManager = AccountManager.get(this);
        }

        // Init Account
        mAccount = getAppAccount(this);

        if (mAccount != null) {
              mTransferActionBrandCategories = new TransferActionBrandCategories(mAccount.name);
              mTransferActionEducationModulesMaterial = new TransferActionEducationModulesMaterial(mAccount.name);
              mTransferActionEducationProductModule = new TransferActionEducationProductModule(mAccount.name);
              mTransferActionEducationProducts = new TransferActionEducationProducts(mAccount.name);
              mTransferActionEventCalendar = new TransferActionEventCalendar(mAccount.name);
              mTransferActionFullNews = new TransferActionFullNews(mAccount.name);
              mTransferActionIsShortNewsRead = new TransferActionIsShortNewsRead(mAccount.name);
              mTransferActionMailContact = new TransferActionMailContact(mAccount.name);
              mTransferActionNewsComments = new TransferActionNewsComments(mAccount.name);
              mTransferActionProductHTML = new TransferActionProductHTML(mAccount.name);
              mTransferActionProductsCategories = new TransferActionProductsCategories(mAccount.name);
              mTransferActionProductsProduct = new TransferActionProductsProduct(mAccount.name);
              mTransferActionShortNews = new TransferActionShortNews(mAccount.name);
              mTransferActionStatusBarPush = new TransferActionStatusBarPush(mAccount.name);
              mTransferActionTileMenu = new TransferActionTileMenu(mAccount.name);

            LOGD(TAG, "Account = " + mAccount + "\nName : " + mAccount.name + "\nPassword : " + mAccountManager.getPassword(mAccount));
        }
    }

    /**
     * Return My Application Main Account
     */
    public static Account getAppAccount(Context context) {
        // Check if Account Manager Init
        if(mAccountManager==null){
            mAccountManager = AccountManager.get(context);
        }

        Account[] accounts = mAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE);

        return accounts.length > 0 ? accounts[0] : null;
    }

    /**
     * Update Account Password
     */
    public void updateAppAccountPassword(String newPass) {
        mAccountManager.setPassword(getAppAccount(this), newPass);
    }

    public Account getAccount() {
        return mAccount;
    }

    public void setAccount(Account account) {
        this.mAccount = account;
    }

    public TransferActionBrandCategories getTransferActionBrandCategories() { return mTransferActionBrandCategories; }
    public TransferActionEducationModulesMaterial getTransferActionEducationModulesMaterial() { return mTransferActionEducationModulesMaterial; }
    public TransferActionEducationProductModule getTransferActionEducationProductModule() { return mTransferActionEducationProductModule; }
    public TransferActionEducationProducts getTransferActionEducationProducts() { return mTransferActionEducationProducts; }
    public TransferActionEventCalendar getTransferActionEventCalendar() { return mTransferActionEventCalendar; }
    public TransferActionFullNews getTransferActionFullNews() { return mTransferActionFullNews; }
    public TransferActionIsShortNewsRead getTransferActionIsShortNewsRead() { return mTransferActionIsShortNewsRead; }
    public TransferActionMailContact getTransferActionMailContact() { return mTransferActionMailContact; }
    public TransferActionNewsComments getTransferActionNewsComments() { return mTransferActionNewsComments; }
    public TransferActionProductHTML getTransferActionProductHTML() { return mTransferActionProductHTML; }
    public TransferActionProductsCategories getTransferActionProductsCategories() { return mTransferActionProductsCategories; }
    public TransferActionProductsProduct getTransferActionProductsProduct() { return mTransferActionProductsProduct; }
    public TransferActionShortNews getTransferActionShortNews() { return mTransferActionShortNews; }
    public TransferActionStatusBarPush getTransferActionStatusBarPush() { return mTransferActionStatusBarPush; }
    public TransferActionTileMenu getTransferActionTileMenu() { return mTransferActionTileMenu; }

    public void setTransferActionBrandCategories(CharSequence name) {
        // Init Transfer App Account DB Class
        if (mTransferActionBrandCategories == null) {
            this.mTransferActionBrandCategories = new TransferActionBrandCategories(name);
        }
    }

    public void setTransferActionEducationModulesMaterial(CharSequence name) {
        // Init Transfer App Account DB Class
        if (mTransferActionEducationModulesMaterial == null) {
            this.mTransferActionEducationModulesMaterial = new TransferActionEducationModulesMaterial(name);
        }
    }

    public void setTransferActionEducationProductModule(CharSequence name) {
        // Init Transfer App Account DB Class
        if (mTransferActionEducationProductModule == null) {
            this.mTransferActionEducationProductModule = new TransferActionEducationProductModule(name);
        }
    }

    public void setTransferActionEducationProducts(CharSequence name) {
        // Init Transfer App Account DB Class
        if (mTransferActionEducationProducts == null) {
            this.mTransferActionEducationProducts = new TransferActionEducationProducts(name);
        }
    }

    public void setTransferActionEventCalendar(CharSequence name) {
        // Init Transfer App Account DB Class
        if (mTransferActionEventCalendar == null) {
            this.mTransferActionEventCalendar = new TransferActionEventCalendar(name);
        }
    }

    public void setTransferActionFullNews(CharSequence name) {
        // Init Transfer App Account DB Class
        if (mTransferActionFullNews == null) {
            this.mTransferActionFullNews = new TransferActionFullNews(name);
        }
    }

    public void setTransferActionIsShortNewsRead(CharSequence name) {
        // Init Transfer App Account DB Class
        if (mTransferActionIsShortNewsRead == null) {
            this.mTransferActionIsShortNewsRead = new TransferActionIsShortNewsRead(name);
        }
    }

    public void setTransferActionMailContact(CharSequence name) {
        // Init Transfer App Account DB Class
        if (mTransferActionMailContact == null) {
            this.mTransferActionMailContact = new TransferActionMailContact(name);
        }
    }

    public void setTransferActionNewsComments(CharSequence name) {
        // Init Transfer App Account DB Class
        if (mTransferActionNewsComments == null) {
            this.mTransferActionNewsComments = new TransferActionNewsComments(name);
        }
    }

    public void setTransferActionProductHTML(CharSequence name) {
        // Init Transfer App Account DB Class
        if (mTransferActionProductHTML == null) {
            this.mTransferActionProductHTML = new TransferActionProductHTML(name);
        }
    }

    public void setTransferActionProductsCategories(CharSequence name) {
        // Init Transfer App Account DB Class
        if (mTransferActionProductsCategories == null) {
            this.mTransferActionProductsCategories = new TransferActionProductsCategories(name);
        }
    }

    public void setTransferActionProductsProduct(CharSequence name) {
        // Init Transfer App Account DB Class
        if (mTransferActionProductsProduct == null) {
            this.mTransferActionProductsProduct = new TransferActionProductsProduct(name);
        }
    }

    public void setTransferActionShortNews(CharSequence name) {
        // Init Transfer App Account DB Class
        if (mTransferActionShortNews == null) {
            this.mTransferActionShortNews = new TransferActionShortNews(name);
        }
    }

    public void setTransferActionStatusBarPush(CharSequence name) {
        // Init Transfer App Account DB Class
        if (mTransferActionStatusBarPush == null) {
            this.mTransferActionStatusBarPush = new TransferActionStatusBarPush(name);
        }
    }

    public void setTransferActionTileMenu(CharSequence name) {
        // Init Transfer App Account DB Class
        if (mTransferActionTileMenu == null) {
            this.mTransferActionTileMenu = new TransferActionTileMenu(name);
        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }
}
