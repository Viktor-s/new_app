package me.justup.upme;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.io.IOException;
import java.util.ArrayList;

import me.justup.upme.dialogs.BreakCallDialog;
import me.justup.upme.dialogs.CallDialog;
import me.justup.upme.dialogs.StatusBarSliderDialog;
import me.justup.upme.dialogs.WarningDialog;
import me.justup.upme.entity.ArticlesGetShortDescriptionQuery;
import me.justup.upme.entity.BaseHttpQueryEntity;
import me.justup.upme.entity.BaseMethodEmptyQuery;
import me.justup.upme.entity.CalendarGetEventsQuery;
import me.justup.upme.entity.GetLoggedUserInfoQuery;
import me.justup.upme.entity.GetLoggedUserInfoResponse;
import me.justup.upme.entity.GetMailContactQuery;
import me.justup.upme.entity.ProductsGetAllCategoriesQuery;
import me.justup.upme.entity.Push;
import me.justup.upme.entity.SetGooglePushIdQuery;
import me.justup.upme.fragments.BriefcaseFragment;
import me.justup.upme.fragments.BrowserFragment;
import me.justup.upme.fragments.CalendarFragment;
import me.justup.upme.fragments.DocumentsFragment;
import me.justup.upme.fragments.MailFragment;
import me.justup.upme.fragments.NewsFeedFragment;
import me.justup.upme.fragments.ProductsFragment;
import me.justup.upme.fragments.StudyFragment;
import me.justup.upme.fragments.UserFragment;
import me.justup.upme.fragments.WebRtcFragment;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.http.HttpIntentService;
import me.justup.upme.interfaces.OnDownloadCloudFile;
import me.justup.upme.interfaces.OnLoadMailFragment;
import me.justup.upme.services.GPSTracker;
import me.justup.upme.utils.AppContext;
import me.justup.upme.utils.AppPreferences;
import me.justup.upme.utils.CommonUtils;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class MainActivity extends BaseActivity implements View.OnClickListener, OnLoadMailFragment, OnDownloadCloudFile {
    private static final String TAG = makeLogTag(MainActivity.class);

    private static final int SELECTED_FRAGMENT_NEWS = 1;
    private static final int SELECTED_FRAGMENT_MAIL = 2;
    private static final int SELECTED_FRAGMENT_CALENDAR = 3;
    private static final int SELECTED_FRAGMENT_PRODUCTS = 4;
    private static final int SELECTED_FRAGMENT_BRIEFCASE = 5;
    private static final int SELECTED_FRAGMENT_DOCS = 6;
    private static final int SELECTED_FRAGMENT_STUDY = 7;
    private static final int SELECTED_FRAGMENT_BROWSER = 8;
    private int currentlySelectedFragment;


    private FrameLayout mMainFragmentContainer;
    private Animation mFragmentSliderOut;
    private Animation mFragmentSliderIn;
    private boolean isShowMainFragmentContainer;

    private ArrayList<Button> mButtonList = new ArrayList<>();
    private Button mNewsButton, mMailButton, mCalendarButton, mProductsButton, mBriefcaseButton, mDocsButton, mStudyButton, mBrowserButton;
    private Push push;
    private String shareFileName;

    private FrameLayout.LayoutParams mLogoParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
    private ImageView mUPMELogo;
    private TextView mUserName;
    private TextView mUserInSystem;

    //GCM
    private GoogleCloudMessaging gcm;
    private String regid;
    private static final String SENDER_ID = "896253211448";
    private Context context = AppContext.getAppContext();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    // broadcast push
    public static final String BROADCAST_ACTION_CALL = "me.justup.upme.broadcast.call.call";
    public static final String BROADCAST_EXTRA_PUSH = "me.justup.upme.broadcast.call.extra.push";
    private BroadcastReceiver mCallPushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            final Push push = (Push) intent.getSerializableExtra(BROADCAST_EXTRA_PUSH);
            showCallDialog(push);
        }
    };

    public static final String BROADCAST_ACTION_BREAK_CALL = "me.justup.upme.broadcast.call.break.call";
    public static final String BROADCAST_EXTRA_BREAK_CALL = "me.justup.upme.broadcast.call.extra.break.user";
    private BroadcastReceiver mBreakCallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            String userName = intent.getStringExtra(BROADCAST_EXTRA_BREAK_CALL);
            showBreakCallDialog(userName);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();

        startService(new Intent(this, GPSTracker.class));
        registerReceiver(mCallPushReceiver, new IntentFilter(BROADCAST_ACTION_CALL));
        registerReceiver(mBreakCallReceiver, new IntentFilter(BROADCAST_ACTION_BREAK_CALL));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainFragmentContainer = (FrameLayout) findViewById(R.id.main_fragment_container);
        FrameLayout mCornerButton = (FrameLayout) findViewById(R.id.include_corner);
        mCornerButton.setOnClickListener(new OnCornerButtonListener());

        mFragmentSliderOut = AnimationUtils.loadAnimation(this, R.anim.fragment_slider_out);
        mFragmentSliderIn = AnimationUtils.loadAnimation(this, R.anim.fragment_slider_in);

        Button mSettingButton = (Button) findViewById(R.id.settings_menu_item);
        mSettingButton.setOnClickListener(new OnLoadSettingsListener());

        mUPMELogo = (ImageView) findViewById(R.id.upme_brick_logo);
        mUserName = (TextView) findViewById(R.id.ab_user_name_textView);
        mUserInSystem = (TextView) findViewById(R.id.ab_user_in_system_textView);

        Button mExitButton = (Button) findViewById(R.id.demo_menu_item);
        mExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        makeButtonSelector();

        ApiWrapper.query(new GetLoggedUserInfoQuery(), new OnGetLoggedUserInfoResponse());

        Fragment fragment = UserFragment.newInstance(new GetLoggedUserInfoQuery(), true);
        getFragmentManager().beginTransaction().add(R.id.mapAndUserFragment, fragment).commit();


        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            } else {
                sendAsyncRegistrationIdToBackend(regid);
            }

        } else {
            LOGE(TAG, "No valid Google Play Services APK found.");
        }

        View mOpenStatusBar = findViewById(R.id.status_bar_fragment);
        mOpenStatusBar.setOnClickListener(new OnOpenStatusBarListener());
    }

    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        CommonUtils.hideKeyboard(this);
        switch (view.getId()) {
            case R.id.news_menu_item:
                if (currentlySelectedFragment != SELECTED_FRAGMENT_NEWS) {
                    startHttpIntent(getShortDescriptionQuery(20, 0), HttpIntentService.NEWS_PART_SHORT);
                    startHttpIntent(getShortDescriptionQuery(500, 20), HttpIntentService.NEWS_PART_SHORT);
                    changeButtonState(mNewsButton);
                    fragment = new NewsFeedFragment();
                    currentlySelectedFragment = SELECTED_FRAGMENT_NEWS;
                }
                break;

            case R.id.mail_menu_item:
                if (currentlySelectedFragment != SELECTED_FRAGMENT_MAIL) {
                    changeButtonState(mMailButton);
                    fragment = new MailFragment();
                    currentlySelectedFragment = SELECTED_FRAGMENT_MAIL;
                }
                break;

            case R.id.calendar_menu_item:
                if (currentlySelectedFragment != SELECTED_FRAGMENT_CALENDAR) {
                    LocalDateTime firstDayCurrentWeek = new LocalDateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withDayOfWeek(DateTimeConstants.MONDAY);
                    startHttpIntent(getEventCalendarQuery(firstDayCurrentWeek), HttpIntentService.CALENDAR_PART);
                    changeButtonState(mCalendarButton);
                    fragment = new CalendarFragment();
                    currentlySelectedFragment = SELECTED_FRAGMENT_CALENDAR;
                }
                break;

            case R.id.products_menu_item:
                if (currentlySelectedFragment != SELECTED_FRAGMENT_PRODUCTS) {
                    startHttpIntent(new ProductsGetAllCategoriesQuery(), HttpIntentService.PRODUCTS_PART);
                    changeButtonState(mProductsButton);
                    fragment = new ProductsFragment();
                    currentlySelectedFragment = SELECTED_FRAGMENT_PRODUCTS;
                }
                break;

            case R.id.briefcase_menu_item:
                if (currentlySelectedFragment != SELECTED_FRAGMENT_BRIEFCASE) {
                    changeButtonState(mBriefcaseButton);
                    fragment = new BriefcaseFragment();
                    currentlySelectedFragment = SELECTED_FRAGMENT_BRIEFCASE;
                }
                break;

            case R.id.docs_menu_item:
                if (currentlySelectedFragment != SELECTED_FRAGMENT_DOCS) {
                    changeButtonState(mDocsButton);
                    fragment = new DocumentsFragment();
                    currentlySelectedFragment = SELECTED_FRAGMENT_DOCS;
                }
                break;

            case R.id.study_menu_item:
                if (currentlySelectedFragment != SELECTED_FRAGMENT_STUDY) {
                    changeButtonState(mStudyButton);
                    fragment = new StudyFragment();
                    currentlySelectedFragment = SELECTED_FRAGMENT_STUDY;
                }
                break;

            case R.id.browser_menu_item:
                if (currentlySelectedFragment != SELECTED_FRAGMENT_BROWSER) {
                    changeButtonState(mBrowserButton);
                    fragment = new BrowserFragment();
                    currentlySelectedFragment = SELECTED_FRAGMENT_BROWSER;
                }
                break;

            default:
                break;
        }

        if (fragment != null) {
            getFragmentManager().beginTransaction().replace(R.id.main_fragment_container, fragment).commit();
        }
        if (!isShowMainFragmentContainer) {
            showMainFragmentContainer();
        }
    }

    private void makeButtonSelector() {
        mNewsButton = (Button) findViewById(R.id.news_menu_item);
        mMailButton = (Button) findViewById(R.id.mail_menu_item);
        mCalendarButton = (Button) findViewById(R.id.calendar_menu_item);
        mProductsButton = (Button) findViewById(R.id.products_menu_item);
        mBriefcaseButton = (Button) findViewById(R.id.briefcase_menu_item);
        mDocsButton = (Button) findViewById(R.id.docs_menu_item);
        mStudyButton = (Button) findViewById(R.id.study_menu_item);
        mBrowserButton = (Button) findViewById(R.id.browser_menu_item);

        mNewsButton.setOnClickListener(this);
        mMailButton.setOnClickListener(this);
        mCalendarButton.setOnClickListener(this);
        mProductsButton.setOnClickListener(this);
        mBriefcaseButton.setOnClickListener(this);
        mDocsButton.setOnClickListener(this);
        mStudyButton.setOnClickListener(this);
        mBrowserButton.setOnClickListener(this);

        mButtonList.add(mNewsButton);
        mButtonList.add(mMailButton);
        mButtonList.add(mCalendarButton);
        mButtonList.add(mProductsButton);
        mButtonList.add(mBriefcaseButton);
        mButtonList.add(mDocsButton);
        mButtonList.add(mStudyButton);
        mButtonList.add(mBrowserButton);
    }

    private void changeButtonState(Button activeButton) {
        for (Button button : mButtonList) {
            button.setBackground(getResources().getDrawable(R.drawable.main_menu_background));
        }

        activeButton.setBackground(getResources().getDrawable(R.drawable.pay_button_pressed));
    }

    private class OnCornerButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (isShowMainFragmentContainer) {
                mMainFragmentContainer.startAnimation(mFragmentSliderOut);
                mMainFragmentContainer.setVisibility(View.GONE);

                isShowMainFragmentContainer = false;

                mLogoParams.gravity = Gravity.CENTER;
                mUPMELogo.setLayoutParams(mLogoParams);
            } else {
                showMainFragmentContainer();
            }
        }
    }

    private void showMainFragmentContainer() {
        mLogoParams.gravity = Gravity.CENTER | Gravity.START;
        mUPMELogo.setLayoutParams(mLogoParams);

        mMainFragmentContainer.setVisibility(View.VISIBLE);
        mMainFragmentContainer.startAnimation(mFragmentSliderIn);

        isShowMainFragmentContainer = true;
    }

    private class OnLoadSettingsListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }
    }

    private class OnOpenStatusBarListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            StatusBarSliderDialog dialog = StatusBarSliderDialog.newInstance();
            dialog.show(getFragmentManager(), StatusBarSliderDialog.STATUS_BAR_DIALOG);
        }
    }

    public void startHttpIntent(BaseHttpQueryEntity entity, int dbTable) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(HttpIntentService.HTTP_INTENT_QUERY_EXTRA, entity);
        bundle.putInt(HttpIntentService.HTTP_INTENT_PART_EXTRA, dbTable);

        Intent intent = new Intent(this, HttpIntentService.class);
        startService(intent.putExtras(bundle));
    }


    @Override
    public void onStop() {
        super.onStop();

        stopService(new Intent(this, GPSTracker.class));
        unregisterReceiver(mCallPushReceiver);
        unregisterReceiver(mBreakCallReceiver);
    }

    public static ArticlesGetShortDescriptionQuery getShortDescriptionQuery(int limit, int offset) {
        ArticlesGetShortDescriptionQuery query = new ArticlesGetShortDescriptionQuery();
        query.params.limit = limit;
        query.params.offset = offset;
        query.params.order = "DESC";
        return query;
    }

    public static CalendarGetEventsQuery getEventCalendarQuery(LocalDateTime startWeek) {
        String startTime = Long.toString(startWeek.toDateTime(DateTimeZone.UTC).getMillis() / 1000);
        LocalDateTime lastDayCurrentWeek = startWeek.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withDayOfWeek(DateTimeConstants.SUNDAY);
        String endTime = Long.toString(lastDayCurrentWeek.toDateTime(DateTimeZone.UTC).getMillis() / 1000);
        CalendarGetEventsQuery query = new CalendarGetEventsQuery();
        query.params.start = startTime;
        query.params.end = endTime;
        Log.d("TAG333_query", query.toString());
        return query;
    }

    // GCM
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                LOGE(TAG, "This device is not supported GCM");
                // finish();
                Toast.makeText(this, "Это устройство не поддерживает GCM", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences();
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            LOGI(TAG, "Registration not found.");
            return "";
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            LOGI(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);

                    LOGD(TAG, "GCM registration ID:" + regid);
                    msg = "Device registered in GCM";

                    sendSyncRegistrationIdToBackend(regid);

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // Require the user to click a button again, or perform exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        }.execute(null, null, null);
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences();
        int appVersion = getAppVersion(context);
        LOGI(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences() {
        return getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    private SetGooglePushIdQuery createPushIdEntity(final String pushId) {
        SetGooglePushIdQuery query = new SetGooglePushIdQuery();
        query.params.google_push_id = pushId;

        return query;
    }

    // for Main thread
    private void sendAsyncRegistrationIdToBackend(final String pushId) {
        ApiWrapper.query(createPushIdEntity(pushId), new OnPushRegisterResponse());
    }

    // for works threads
    private void sendSyncRegistrationIdToBackend(final String pushId) {
        ApiWrapper.syncQuery(createPushIdEntity(pushId), new OnPushRegisterResponse());
    }

    private class OnPushRegisterResponse extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGD(TAG, "OnPushRegisterResponse onSuccess: " + content);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGE(TAG, "OnPushRegisterResponse onFailure: " + content);
        }
    }

    @Override
    public void onLoadMailFragment(final Push push) {
        setPush(push);

        if (push != null && push.getType() == MailFragment.WEBRTC) {
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.container_video_chat, WebRtcFragment.newInstance(String.valueOf(push.getRoom())));
            ft.commit();

            setPush(null);
            return;
        }

        startHttpIntent(new GetMailContactQuery(), HttpIntentService.MAIL_CONTACT_PART);
        Fragment fragment = new MailFragment();
        getFragmentManager().beginTransaction().replace(R.id.main_fragment_container, fragment).commit();

        changeButtonState(mMailButton);
        if (!isShowMainFragmentContainer) {
            showMainFragmentContainer();
        }
    }

    public Push getPush() {
        return push;
    }

    public void setPush(Push push) {
        this.push = push;
    }

    private void showCallDialog(final Push push) {
        CallDialog dialog = CallDialog.newInstance(push);
        dialog.show(getFragmentManager(), CallDialog.CALL_DIALOG);
    }

    private void showBreakCallDialog(final String userName) {
        BreakCallDialog dialog = BreakCallDialog.newInstance(userName);
        dialog.show(getFragmentManager(), BreakCallDialog.BREAK_CALL_DIALOG);
    }

    @Override
    public void onDownloadCloudFile(final String fileHash, final String fileName) {
        setShareFileName(fileName);

        getFragmentManager().beginTransaction().replace(R.id.main_fragment_container, new DocumentsFragment()).commit();

        changeButtonState(mDocsButton);
        if (!isShowMainFragmentContainer) {
            showMainFragmentContainer();
        }
    }

    public String getShareFileName() {
        return shareFileName;
    }

    public void setShareFileName(String shareFileName) {
        this.shareFileName = shareFileName;
    }

    private class OnGetLoggedUserInfoResponse extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGD(TAG, "OnGetLoggedUserInfoResponse onSuccess: " + content);

            GetLoggedUserInfoResponse response = null;
            try {
                response = ApiWrapper.gson.fromJson(content, GetLoggedUserInfoResponse.class);
            } catch (JsonSyntaxException e) {
                LOGE(TAG, "OnGetLoggedUserInfoResponse gson.fromJson:\n" + content);
            }

            if (response != null && response.result != null) {
                String userName = (response.result.name != null) ? response.result.name : "";

                mUserName.setText(userName);
                // mUserInSystem.setText(response.result.in_system);

                AppPreferences appPreferences = new AppPreferences(AppContext.getAppContext());
                appPreferences.setUserName(userName);
                appPreferences.setUserId(response.result.id);
                appPreferences.setJabberId(response.result.jabber_id);

                BaseMethodEmptyQuery query = new BaseMethodEmptyQuery();
                query.method = ApiWrapper.ACCOUNT_GET_ALL_CONTACTS;
                startHttpIntent(query, HttpIntentService.MAIL_CONTACT_PART);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGE(TAG, "OnGetLoggedUserInfoResponse onFailure: " + content);

            try {
                showWarningDialog(ApiWrapper.getResponseError(content));
            } catch (Exception e) {
                LOGE(TAG, "showWarningDialog FAIL", e);
            }
        }
    }

    private void showWarningDialog(String message) {
        WarningDialog dialog = WarningDialog.newInstance(getString(R.string.network_error), message);
        dialog.show(getFragmentManager(), WarningDialog.WARNING_DIALOG);
    }

}
