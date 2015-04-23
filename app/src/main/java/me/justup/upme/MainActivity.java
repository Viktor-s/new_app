package me.justup.upme;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;

import me.justup.upme.dialogs.BreakCallDialog;
import me.justup.upme.dialogs.CallDialog;
import me.justup.upme.dialogs.OrderDialog;
import me.justup.upme.dialogs.StatusBarSliderDialog;
import me.justup.upme.dialogs.WarningDialog;
import me.justup.upme.entity.ArticlesGetShortDescriptionQuery;
import me.justup.upme.entity.BaseHttpQueryEntity;
import me.justup.upme.entity.BaseMethodEmptyQuery;
import me.justup.upme.entity.CalendarGetEventsQuery;
import me.justup.upme.entity.ErrorResponse;
import me.justup.upme.entity.GetLoggedUserInfoQuery;
import me.justup.upme.entity.GetLoggedUserInfoResponse;
import me.justup.upme.entity.ProductsGetAllCategoriesQuery;
import me.justup.upme.entity.ProductsOrderGetFormQuery;
import me.justup.upme.entity.ProductsOrderGetFormResponse;
import me.justup.upme.entity.Push;
import me.justup.upme.fragments.BriefcaseFragment;
import me.justup.upme.fragments.BrowserFragment;
import me.justup.upme.fragments.CalendarFragment;
import me.justup.upme.fragments.DocumentsFragment;
import me.justup.upme.fragments.MailFragment;
import me.justup.upme.fragments.NewsFeedFragmentNew;
import me.justup.upme.fragments.ProductsFragment;
import me.justup.upme.fragments.StudyFragment;
import me.justup.upme.fragments.WebRtcFragment;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.http.HttpIntentService;
import me.justup.upme.interfaces.OnDownloadCloudFile;
import me.justup.upme.interfaces.OnLoadMailFragment;
import me.justup.upme.services.GPSTracker;
import me.justup.upme.utils.AppPreferences;
import me.justup.upme.utils.CommonUtils;
import me.justup.upme.utils.Constance;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class MainActivity extends BaseActivity implements View.OnClickListener, OnLoadMailFragment, OnDownloadCloudFile {
    private static final String TAG = makeLogTag(MainActivity.class);

    private static final String SAVE_FRAGMENT_STATE = "save_fragment_state";
    private static final String IS_SHOW_FRAGMENT_CONTAINER = "is_show_fragment_container";

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

    private WebRtcFragment mWebRtcFragment = null;

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

        View mOpenStatusBar = findViewById(R.id.status_bar_fragment);
        mOpenStatusBar.setOnClickListener(new OnOpenStatusBarListener());

        if (savedInstanceState != null) {
            currentlySelectedFragment = savedInstanceState.getInt(SAVE_FRAGMENT_STATE, 0);
            isShowMainFragmentContainer = savedInstanceState.getBoolean(IS_SHOW_FRAGMENT_CONTAINER, false);
            reopenFragment(currentlySelectedFragment);
        }
    }

    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        CommonUtils.hideKeyboard(this);

        switch (view.getId()) {
            case R.id.news_menu_item:
                if (currentlySelectedFragment != SELECTED_FRAGMENT_NEWS) {
                    startHttpIntent(getShortDescriptionQuery(100, 0), HttpIntentService.NEWS_PART_SHORT);
                    //startHttpIntent(getShortDescriptionQuery(500, 20), HttpIntentService.NEWS_PART_SHORT);
                    changeButtonState(mNewsButton);
                    fragment = new NewsFeedFragmentNew();
                    currentlySelectedFragment = SELECTED_FRAGMENT_NEWS;
                }
                break;

            case R.id.mail_menu_item:
                if (currentlySelectedFragment != SELECTED_FRAGMENT_MAIL) {
                    BaseMethodEmptyQuery query = new BaseMethodEmptyQuery();
                    query.method = ApiWrapper.ACCOUNT_GET_ALL_CONTACTS;
                    startHttpIntent(query, HttpIntentService.MAIL_CONTACT_PART);
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
                    BaseMethodEmptyQuery query = new BaseMethodEmptyQuery();
                    query.method = ApiWrapper.ACCOUNT_GET_ALL_CONTACTS;
                    startHttpIntent(query, HttpIntentService.MAIL_CONTACT_PART);
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

    private void reopenFragment(int currentNumberFragment) {
        switch (currentNumberFragment) {
            case SELECTED_FRAGMENT_NEWS:
                changeButtonState(mNewsButton);
                break;

            case SELECTED_FRAGMENT_MAIL:
                changeButtonState(mMailButton);
                break;

            case SELECTED_FRAGMENT_CALENDAR:
                changeButtonState(mCalendarButton);
                break;

            case SELECTED_FRAGMENT_PRODUCTS:
                changeButtonState(mProductsButton);
                break;

            case SELECTED_FRAGMENT_BRIEFCASE:
                changeButtonState(mBriefcaseButton);
                break;

            case SELECTED_FRAGMENT_DOCS:
                changeButtonState(mDocsButton);
                break;

            case SELECTED_FRAGMENT_STUDY:
                changeButtonState(mStudyButton);
                break;

            case SELECTED_FRAGMENT_BROWSER:
                changeButtonState(mBrowserButton);
                break;

            default:
                break;
        }

        if (isShowMainFragmentContainer) {
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

        if (mButtonList != null) {
            mButtonList.add(mNewsButton);
            mButtonList.add(mMailButton);
            mButtonList.add(mCalendarButton);
            mButtonList.add(mProductsButton);
            mButtonList.add(mBriefcaseButton);
            mButtonList.add(mDocsButton);
            mButtonList.add(mStudyButton);
            mButtonList.add(mBrowserButton);
        }
    }

    private void changeButtonState(Button activeButton) {
        if (mButtonList != null) {
            for (Button button : mButtonList) {
                button.setBackground(getResources().getDrawable(R.drawable.main_menu_background));
            }
        }

        activeButton.setBackground(getResources().getDrawable(R.drawable.main_menu_pressed_background));
    }

    private class OnCornerButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (currentlySelectedFragment != 0) {
                if (isShowMainFragmentContainer) {
                    mMainFragmentContainer.startAnimation(mFragmentSliderOut);
                    mMainFragmentContainer.setVisibility(View.GONE);
                    isShowMainFragmentContainer = false;
                    mLogoParams.gravity = Gravity.CENTER;
                    // mUPMELogo.setLayoutParams(mLogoParams);
                    Animation animation = new TranslateAnimation(-100, 0, 0, 0);
                    animation.setDuration(1000);
                    animation.setFillAfter(true);
                    mUPMELogo.startAnimation(animation);
                } else {
                    showMainFragmentContainer();
                }
            }
        }
    }

    private void showMainFragmentContainer() {
        mLogoParams.gravity = Gravity.CENTER | Gravity.START;
        // mUPMELogo.setLayoutParams(mLogoParams);
        Animation animation = new TranslateAnimation(0, -100, 0, 0);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        mUPMELogo.startAnimation(animation);
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

        try {
            unregisterReceiver(mCallPushReceiver);
        } catch (Exception e) {
            LOGE(TAG, "unregisterReceiver(mCallPushReceiver)", e);
            mCallPushReceiver = null;
        }

        try {
            unregisterReceiver(mBreakCallReceiver);
        } catch (Exception e) {
            LOGE(TAG, "unregisterReceiver(mBreakCallReceiver)", e);
            mBreakCallReceiver = null;
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SAVE_FRAGMENT_STATE, currentlySelectedFragment);
        outState.putBoolean(IS_SHOW_FRAGMENT_CONTAINER, isShowMainFragmentContainer);
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
        Log.d(TAG, query.toString());
        return query;
    }

    @Override
    public void onLoadMailFragment(final Push push) {
        setPush(push);

        if (push != null && push.getType() == MailFragment.WEBRTC) {
            prepareAndCallRTC(push.getRoom(), false, false, 1000, 0, "");

            setPush(null);
            return;
        }

        if (push != null && push.getType() == MailFragment.ORDER_FORM) {
            getOrderFormQuery(push.getFormId());

            setPush(null);
            return;
        }

        BaseMethodEmptyQuery query = new BaseMethodEmptyQuery();
        query.method = ApiWrapper.ACCOUNT_GET_ALL_CONTACTS;
        startHttpIntent(query, HttpIntentService.MAIL_CONTACT_PART);
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
                mUserInSystem.setText(response.result.in_system);

                AppPreferences appPreferences = new AppPreferences(getApplicationContext());
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

            ErrorResponse response = null;
            try {
                response = ApiWrapper.gson.fromJson(content, ErrorResponse.class);
            } catch (JsonSyntaxException e) {
                LOGE(TAG, "OnGetLoggedUserInfoResponse onFailure() gson.fromJson:\n" + content);
            }

            if (response != null && response.error != null) {
                try {
                    showWarningDialog(response.error.message);
                } catch (Exception e) {
                    LOGE(TAG, "showWarningDialog FAIL", e);
                }
            } else {
                try {
                    showWarningDialog(ApiWrapper.getResponseError(content));
                } catch (Exception e) {
                    LOGE(TAG, "showWarningDialog FAIL", e);
                }
            }
        }
    }

    private void showWarningDialog(String message) {
        WarningDialog dialog = WarningDialog.newInstance(getString(R.string.network_error), message);
        dialog.show(getFragmentManager(), WarningDialog.WARNING_DIALOG);
    }

    private void getOrderFormQuery(String formId) {
        ProductsOrderGetFormQuery query = new ProductsOrderGetFormQuery();
        query.params.key = formId;

        ApiWrapper.query(query, new OrderGetFormResponse());
    }

    private class OrderGetFormResponse extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGD(TAG, "OrderGetFormResponse onSuccess: " + content);

            ProductsOrderGetFormResponse response = null;
            try {
                response = ApiWrapper.gson.fromJson(content, ProductsOrderGetFormResponse.class);
            } catch (JsonSyntaxException e) {
                LOGE(TAG, "ProductsOrderGetFormResponse gson.fromJson:\n" + content);
            }

            if (response != null && !response.result.equals("")) {
                showOrderFormDialog(response.result);
            } else if (response != null && response.error != null) {
                try {
                    showWarningDialog(response.error.data);
                } catch (Exception e) {
                    LOGE(TAG, "showWarningDialog FAIL", e);
                }
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGE(TAG, "OrderGetFormResponse onFailure: " + content);

            try {
                showWarningDialog(ApiWrapper.getResponseError(content));
            } catch (Exception e) {
                LOGE(TAG, "showWarningDialog FAIL", e);
            }
        }
    }

    private void showOrderFormDialog(String htmlString) {
        OrderDialog dialog = OrderDialog.newInstance(htmlString);
        dialog.show(getFragmentManager(), OrderDialog.ORDER_DIALOG);
    }

    public void prepareAndCallRTC(Object roomId, Boolean loopback, Boolean commandLineRun, int runTimeMs, int idPerson, String contactName) {
        final Bundle callParam = JustUpApplication.getApplication().prepareCallParam(roomId.getClass().equals(String.class) ? (String) roomId : String.valueOf(roomId), loopback, commandLineRun, runTimeMs, idPerson, contactName);
        findViewById(R.id.container_video_chat).setVisibility(View.VISIBLE);
        mWebRtcFragment = WebRtcFragment.newInstance(callParam);
        getFragmentManager().beginTransaction().replace(R.id.container_video_chat, mWebRtcFragment).commit();
    }

    public void clearDataAfterCallRTC() {
        if (mWebRtcFragment != null) {
            getFragmentManager().beginTransaction().remove(mWebRtcFragment).commit();
            mWebRtcFragment = null;
        } else {
            LOGI(TAG, "WebRTCFragment is NULL");
        }
    }

    @Override
    public void onBackPressed() {
        if (BuildConfig.FLAVOR.equals(Constance.APP_FLAVOR_APP)) {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Add change config
    }

}