package me.justup.upme;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
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

import me.justup.upme.api_rpc.response_object.PushObject;
import me.justup.upme.dialogs.BreakCallDialog;
import me.justup.upme.dialogs.CallDialog;
import me.justup.upme.dialogs.OrderDialog;
import me.justup.upme.dialogs.StatusBarSliderDialog;
import me.justup.upme.dialogs.WarningDialog;
import me.justup.upme.entity.ArticlesGetShortDescriptionQuery;
import me.justup.upme.entity.BaseHttpQueryEntity;
import me.justup.upme.entity.BaseMethodEmptyQuery;
import me.justup.upme.entity.CalendarGetEventsQuery;
import me.justup.upme.entity.EducationGetProgramsQuery;
import me.justup.upme.entity.ErrorResponse;
import me.justup.upme.entity.GetLoggedUserInfoQuery;
import me.justup.upme.entity.GetLoggedUserInfoResponse;
import me.justup.upme.entity.ProductsGetAllCategoriesQuery;
import me.justup.upme.entity.ProductsOrderGetFormQuery;
import me.justup.upme.entity.ProductsOrderGetFormResponse;
import me.justup.upme.entity.Push;
import me.justup.upme.fragments.AccountSettingsFragment;
import me.justup.upme.fragments.BriefcaseFragment;
import me.justup.upme.fragments.BrowserFragment;
import me.justup.upme.fragments.CalendarFragment;
import me.justup.upme.fragments.DocumentsFragment;
import me.justup.upme.fragments.EducationFragment;
import me.justup.upme.fragments.MailFragment;
import me.justup.upme.fragments.NewsFeedFragmentNew;
import me.justup.upme.fragments.ProductsFragment;
import me.justup.upme.fragments.SettingsFragment;
import me.justup.upme.fragments.TiledMenuFragment;
import me.justup.upme.fragments.UserFragment;
import me.justup.upme.fragments.WebRtcFragment;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.http.HttpIntentService;
import me.justup.upme.interfaces.OnDownloadCloudFile;
import me.justup.upme.interfaces.OnLoadMailFragment;
import me.justup.upme.services.GPSTracker;
import me.justup.upme.utils.CircularImageView;
import me.justup.upme.utils.CommonUtils;
import me.justup.upme.utils.Constance;
import me.justup.upme.view.dashboard.TileUtils;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;

public class MainActivity extends LauncherActivity implements View.OnClickListener,
        OnLoadMailFragment,
        OnDownloadCloudFile {

    public static final String TAG = makeLogTag(MainActivity.class);

    // SavedInstanceState Constance
    private static final String SAVE_FRAGMENT_STATE = "save_fragment_state";
    private static final String IS_SHOW_FRAGMENT_CONTAINER = "is_show_fragment_container";
    private static final String IS_SHOW_USER_INFO_CONTAINER = "is_show_user_info_container";

    private static final int SELECTED_FRAGMENT_NEWS = 1;
    private static final int SELECTED_FRAGMENT_MAIL = 2;
    private static final int SELECTED_FRAGMENT_CALENDAR = 3;
    private static final int SELECTED_FRAGMENT_PRODUCTS = 4;
    private static final int SELECTED_FRAGMENT_BRIEFCASE = 5;
    private static final int SELECTED_FRAGMENT_DOCS = 6;
    private static final int SELECTED_FRAGMENT_STUDY = 7;
    private static final int SELECTED_FRAGMENT_BROWSER = 8;
    private static final int SELECTED_FRAGMENT_SETTINGS = 9;
    private int currentlySelectedFragment;

    private FrameLayout mMainFragmentContainer = null;

    private ArrayList<Button> mButtonList = new ArrayList<>();
    private Button mNewsButton, mMailButton, mCalendarButton, mProductsButton, mBriefcaseButton, mDocsButton, mStudyButton, mBrowserButton, mSettingsButton;
    private PushObject push;
    private String shareFileName;
    private Button mExitButton = null;

    private FrameLayout.LayoutParams mLogoParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
    private ImageView mUPMELogo;
    private TextView mUserName;
    private TextView mUserInSystem;
    private FrameLayout mCornerButton;

    private WebRtcFragment mWebRtcFragment = null;
    private Fragment mUserFragment = null;
    private Fragment mNewsFeedFragmentNew = null;
    private Fragment mProductsFragment = null;

    private Bundle mCallParam = null;

    // broadcast push
    public static final String BROADCAST_ACTION_CALL = "me.justup.upme.broadcast.call.call";
    public static final String BROADCAST_EXTRA_PUSH = "me.justup.upme.broadcast.call.extra.push";
    public static final String BROADCAST_EXTRA_CHANGE_AVATAR = "me.justup.upme.broadcast.call.change.avatar";
    private BroadcastReceiver mCallPushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            final Push push = (Push) intent.getSerializableExtra(BROADCAST_EXTRA_PUSH);
            final boolean isChangeAvatar = intent.getBooleanExtra(BROADCAST_EXTRA_CHANGE_AVATAR, false);

            if (push != null)
                showCallDialog(push);

            if (isChangeAvatar) {
                LOGI(TAG, "isChangeAvatar");
                ApiWrapper.query(new GetLoggedUserInfoQuery(), new OnGetLoggedUserInfoResponse());
            }
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

    private boolean isAccountSettingsLoad;
    private CircularImageView mLoadAccountSettings = null;

    @Override
    protected void onResume() {
        super.onResume();
        LOGI(TAG, "onResume");

        // Launcher
        onResumeLA();

        startService(new Intent(this, GPSTracker.class));
        registerReceiver(mCallPushReceiver, new IntentFilter(BROADCAST_ACTION_CALL));
        registerReceiver(mBreakCallReceiver, new IntentFilter(BROADCAST_ACTION_BREAK_CALL));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LOGI(TAG, "onCreate");

        hideNavBar();
        setContentView(R.layout.activity_main);

        // Launcher Setup
        onCreateLA(savedInstanceState);

//        new CountDownTimer(10000, 1000) {
//            public void onTick(long millisUntilFinished) {
//            }
//            public void onFinish() {
//                Toast.makeText(getBaseContext(), "Сработал таймер", Toast.LENGTH_SHORT).show();
//                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "My Tag");
//                wl.acquire();
//                wl.release();
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
//            }
//        }.start();

        mMainFragmentContainer = (FrameLayout) findViewById(R.id.main_fragment_container);
        mCornerButton = (FrameLayout) findViewById(R.id.include_corner);
        mCornerButton.setOnClickListener(new OnCornerButtonListener());

//        Button mSettingButton = (Button) findViewById(R.id.settings_menu_item);
//        mSettingButton.setOnClickListener(new OnLoadSettingsListener());

        mUPMELogo = (ImageView) findViewById(R.id.upme_brick_logo);
        mUserName = (TextView) findViewById(R.id.ab_user_name_textView);
        mUserInSystem = (TextView) findViewById(R.id.ab_user_in_system_textView);

        mExitButton = (Button) findViewById(R.id.demo_menu_item);
        mExitButton.setTag(getResources().getDrawable(R.drawable.ic_main_demo));
        mExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.clearAllAppData();
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
            isOrderingPanelOpen = savedInstanceState.getBoolean(IS_SHOW_USER_INFO_CONTAINER, false);

            reopenFragment(currentlySelectedFragment);
        }

        initTiledMenuFragment();

        mLoadAccountSettings = (CircularImageView) findViewById(R.id.ab_user_image_imageView);
        mLoadAccountSettings.setOnClickListener(new LoadAccountSettingsFragment());
        Button mOrderingButton = (Button) findViewById(R.id.main_screen_ordering_button);
        mOrderingButton.setOnClickListener(new OpenOrderingPanel());

        // Init User Fragment
        mUserFragment = UserFragment.newInstance(new GetLoggedUserInfoQuery(), true);
        getFragmentManager().beginTransaction().replace(R.id.mapAndUserFragment, mUserFragment).commit();

        // Init News Fragment
        mNewsFeedFragmentNew = NewsFeedFragmentNew.newInstance();
        // Init Product Panel
        mProductsFragment = new ProductsFragment();

        setAnimationOpenFragmentListener(new AnimationOpenFragmentListener() {
            @Override
            public void onStartAnim() {
                if (currentlySelectedFragment == SELECTED_FRAGMENT_NEWS) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            ((NewsFeedFragmentNew) mNewsFeedFragmentNew).showFullNews();
                        }
                    }, 500);
                }
            }

            @Override
            public void onEndAnim() {

            }
        });

        setAnimationCloseFragmentListener(new AnimationCloseFragmentListener() {
            @Override
            public void onStartAnim() {
                if (currentlySelectedFragment == SELECTED_FRAGMENT_NEWS) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            ((NewsFeedFragmentNew) mNewsFeedFragmentNew).closeFullNews();
                        }
                    }, 300);
                }
            }

            @Override
            public void onEndAnim() {

            }
        });
    }

    /**
     * Init Tiled Menu Fragment
     */
    private void initTiledMenuFragment() {
        removeCurrentFragment(R.id.main_tiled_fragment_container);
        replaceFragment(TiledMenuFragment.newInstance(), R.id.main_tiled_fragment_container);
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
                    changeButtonState(mNewsButton, (Drawable) getResources().getDrawable(R.drawable.ic_main_news_pink));

                    fragment = mNewsFeedFragmentNew;
                    currentlySelectedFragment = SELECTED_FRAGMENT_NEWS;
                }

                break;

            case R.id.mail_menu_item:
                if (currentlySelectedFragment != SELECTED_FRAGMENT_MAIL) {
                    BaseMethodEmptyQuery query = new BaseMethodEmptyQuery();
                    query.method = ApiWrapper.ACCOUNT_GET_ALL_CONTACTS;
                    startHttpIntent(query, HttpIntentService.MAIL_CONTACT_PART);
                    changeButtonState(mMailButton, (Drawable) getResources().getDrawable(R.drawable.ic_main_mail_pink));

                    fragment = new MailFragment();
                    currentlySelectedFragment = SELECTED_FRAGMENT_MAIL;
                }

                break;

            case R.id.calendar_menu_item:
                if (currentlySelectedFragment != SELECTED_FRAGMENT_CALENDAR) {
                    LocalDateTime firstDayCurrentWeek = new LocalDateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withDayOfWeek(DateTimeConstants.MONDAY);
                    startHttpIntent(getEventCalendarQuery(firstDayCurrentWeek), HttpIntentService.CALENDAR_PART);
                    changeButtonState(mCalendarButton, (Drawable) getResources().getDrawable(R.drawable.ic_main_cal_pink));

                    fragment = new CalendarFragment();
                    currentlySelectedFragment = SELECTED_FRAGMENT_CALENDAR;
                }

                break;

            case R.id.products_menu_item:
                if (currentlySelectedFragment != SELECTED_FRAGMENT_PRODUCTS) {
                    startHttpIntent(new ProductsGetAllCategoriesQuery(), HttpIntentService.PRODUCTS_PART);
                    changeButtonState(mProductsButton, (Drawable) getResources().getDrawable(R.drawable.ic_main_prod_pink));

                    fragment = mProductsFragment;
                    currentlySelectedFragment = SELECTED_FRAGMENT_PRODUCTS;
                }

                break;

            case R.id.briefcase_menu_item:
                if (currentlySelectedFragment != SELECTED_FRAGMENT_BRIEFCASE) {
                    BaseMethodEmptyQuery query = new BaseMethodEmptyQuery();
                    query.method = ApiWrapper.ACCOUNT_GET_ALL_CONTACTS;
                    startHttpIntent(query, HttpIntentService.MAIL_CONTACT_PART);
                    changeButtonState(mBriefcaseButton, (Drawable) getResources().getDrawable(R.drawable.ic_main_brief_pink));

                    fragment = new BriefcaseFragment();
                    currentlySelectedFragment = SELECTED_FRAGMENT_BRIEFCASE;
                }

                break;

            case R.id.docs_menu_item:
                if (currentlySelectedFragment != SELECTED_FRAGMENT_DOCS) {
                    changeButtonState(mDocsButton, (Drawable) getResources().getDrawable(R.drawable.ic_main_docs_pink));

                    fragment = new DocumentsFragment();
                    currentlySelectedFragment = SELECTED_FRAGMENT_DOCS;
                }

                break;

            case R.id.study_menu_item:
                if (currentlySelectedFragment != SELECTED_FRAGMENT_STUDY) {
                    startHttpIntent(new EducationGetProgramsQuery(), HttpIntentService.EDUCATION_GET_PRODUCTS);
                    changeButtonState(mStudyButton, (Drawable) getResources().getDrawable(R.drawable.ic_main_study_pink));

                    fragment = new EducationFragment();
                    currentlySelectedFragment = SELECTED_FRAGMENT_STUDY;
                }

                break;

            case R.id.browser_menu_item:
                if (currentlySelectedFragment != SELECTED_FRAGMENT_BROWSER) {
                    changeButtonState(mBrowserButton, (Drawable) getResources().getDrawable(R.drawable.ic_main_browser_pink));

                    fragment = new BrowserFragment();
                    currentlySelectedFragment = SELECTED_FRAGMENT_BROWSER;
                }

                break;

            case R.id.settings_menu_item:
                if (currentlySelectedFragment != SELECTED_FRAGMENT_SETTINGS) {
                    changeButtonState(mSettingsButton, (Drawable) getResources().getDrawable(R.drawable.ic_main_settings_pink));

                    fragment = new SettingsFragment();
                    currentlySelectedFragment = SELECTED_FRAGMENT_SETTINGS;
                }

                break;

            default:
                break;
        }

        if (fragment != null) {
            getFragmentManager().beginTransaction().replace(R.id.main_fragment_container, fragment).commit();
        }

        if (isShowMainFragmentContainer != null && !isShowMainFragmentContainer) {
            showMainFragmentContainer();
        }
    }

    private void reopenFragment(int currentNumberFragment) {
        switch (currentNumberFragment) {
            case SELECTED_FRAGMENT_NEWS:
                changeButtonState(mNewsButton, null);
                break;

            case SELECTED_FRAGMENT_MAIL:
                changeButtonState(mMailButton, null);
                break;

            case SELECTED_FRAGMENT_CALENDAR:
                changeButtonState(mCalendarButton, null);
                break;

            case SELECTED_FRAGMENT_PRODUCTS:
                changeButtonState(mProductsButton, null);
                break;

            case SELECTED_FRAGMENT_BRIEFCASE:
                changeButtonState(mBriefcaseButton, null);
                break;

            case SELECTED_FRAGMENT_DOCS:
                changeButtonState(mDocsButton, null);
                break;

            case SELECTED_FRAGMENT_STUDY:
                changeButtonState(mStudyButton, null);
                break;

            case SELECTED_FRAGMENT_BROWSER:
                changeButtonState(mBrowserButton, null);
                break;

            case SELECTED_FRAGMENT_SETTINGS:
                changeButtonState(mSettingsButton, null);
                break;

            default:
                break;
        }

        if (isShowMainFragmentContainer != null && isShowMainFragmentContainer) {
            showMainFragmentContainer();
        }
    }

    private void makeButtonSelector() {
        mNewsButton = (Button) findViewById(R.id.news_menu_item);
        mNewsButton.setTag(getResources().getDrawable(R.drawable.ic_main_news));

        mMailButton = (Button) findViewById(R.id.mail_menu_item);
        mNewsButton.setTag(getResources().getDrawable(R.drawable.ic_main_mail));

        mCalendarButton = (Button) findViewById(R.id.calendar_menu_item);
        mNewsButton.setTag(getResources().getDrawable(R.drawable.ic_main_cal));

        mProductsButton = (Button) findViewById(R.id.products_menu_item);
        mNewsButton.setTag(getResources().getDrawable(R.drawable.ic_main_prod));

        mBriefcaseButton = (Button) findViewById(R.id.briefcase_menu_item);
        mNewsButton.setTag(getResources().getDrawable(R.drawable.ic_main_brief));

        mDocsButton = (Button) findViewById(R.id.docs_menu_item);
        mNewsButton.setTag(getResources().getDrawable(R.drawable.ic_main_docs));

        mStudyButton = (Button) findViewById(R.id.study_menu_item);
        mNewsButton.setTag(getResources().getDrawable(R.drawable.ic_main_study));

        mBrowserButton = (Button) findViewById(R.id.browser_menu_item);
        mNewsButton.setTag(getResources().getDrawable(R.drawable.ic_main_browser));

        mSettingsButton = (Button) findViewById(R.id.settings_menu_item);
        mNewsButton.setTag(getResources().getDrawable(R.drawable.ic_main_settings));

        mNewsButton.setOnClickListener(this);
        mMailButton.setOnClickListener(this);
        mCalendarButton.setOnClickListener(this);
        mProductsButton.setOnClickListener(this);
        mBriefcaseButton.setOnClickListener(this);
        mDocsButton.setOnClickListener(this);
        mStudyButton.setOnClickListener(this);
        mBrowserButton.setOnClickListener(this);
        mSettingsButton.setOnClickListener(this);

        // Add all menu button to List
        if (mButtonList != null) {
            mButtonList.add(mNewsButton);
            mButtonList.add(mMailButton);
            mButtonList.add(mCalendarButton);
            mButtonList.add(mProductsButton);
            mButtonList.add(mBriefcaseButton);
            mButtonList.add(mDocsButton);
            mButtonList.add(mStudyButton);
            mButtonList.add(mBrowserButton);
            mButtonList.add(mSettingsButton);
            mButtonList.add(mExitButton);
        }

        // Set Drawable Padding
        if (mButtonList != null) {
            for (Button button : mButtonList) {
                if(JustUpApplication.getScreenDensityDpi()!=240){
                    button.setCompoundDrawablePadding(TileUtils.dpToPx( (int) getResources().getDimension(R.dimen.base10dp720sw), getApplicationContext()));
                }
            }
        }
    }

    private void changeButtonState(Button activeButton, Drawable drawable) {
        if (mButtonList != null) {
            for (Button button : mButtonList) {
                if (!button.getText().toString().equals("Демо - режим")) {
                    button.setBackground(getResources().getDrawable(R.drawable.main_menu_background));
                }

                if(JustUpApplication.getScreenDensityDpi()!=240){
                    button.setCompoundDrawablePadding(TileUtils.dpToPx( (int) getResources().getDimension(R.dimen.base10dp720sw), getApplicationContext()));
                }

                if(button.getText().toString().equals("Новости")){
                    button.setCompoundDrawablesWithIntrinsicBounds((Drawable) getResources().getDrawable(R.drawable.ic_main_news), null, null, null);
                }else{
                    button.setCompoundDrawablesWithIntrinsicBounds((Drawable) button.getTag(), null, null, null);
                }
            }
        }

        activeButton.setBackground(getResources().getDrawable(R.drawable.main_menu_pressed_background));
        if(drawable!=null) {
            if(JustUpApplication.getScreenDensityDpi()!=240){
                activeButton.setCompoundDrawablePadding(TileUtils.dpToPx( (int) getResources().getDimension(R.dimen.base10dp720sw), getApplicationContext()));
            }

            activeButton.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }
    }

    private class OnCornerButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (currentlySelectedFragment != 0) {
                if (isShowMainFragmentContainer != null && isShowMainFragmentContainer) {
                    mMainFragmentContainer.startAnimation(mFragmentSliderOut);
                    mMainFragmentContainer.setVisibility(View.GONE);

                    mLogoParams.gravity = Gravity.CENTER;
                    // mUPMELogo.setLayoutParams(mLogoParams);

                    // Set Anim to Logo
                    mUPMELogo.startAnimation(mAnimCloseLogo);
                } else if (isShowMainFragmentContainer != null && !isShowMainFragmentContainer) {
                    showMainFragmentContainer();
                }
            }
        }
    }

    private void showMainFragmentContainer() {
        mLogoParams.gravity = Gravity.CENTER | Gravity.START;
        // mUPMELogo.setLayoutParams(mLogoParams);

        // Set Anim to Logo
        mUPMELogo.startAnimation(mAnimOpenLogo);

        mMainFragmentContainer.setVisibility(View.VISIBLE);
        mMainFragmentContainer.startAnimation(mFragmentSliderIn);
    }

//    private class OnLoadSettingsListener implements View.OnClickListener {
//        @Override
//        public void onClick(View v) {
//            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
//        }
//    }

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
        LOGI(TAG, "onStop");

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
        // Launcher
        outState.putInt(RUNTIME_STATE_CURRENT_SCREEN, mWorkspace.getNextPage());
        super.onSaveInstanceState(outState);

        // Launcher
        onSaveInstanceStateLA(outState);

        outState.putInt(SAVE_FRAGMENT_STATE, currentlySelectedFragment);
        if (isShowMainFragmentContainer != null) {
            outState.putBoolean(IS_SHOW_FRAGMENT_CONTAINER, isShowMainFragmentContainer);
        }

        if (isOrderingPanelOpen != null) {
            outState.putBoolean(IS_SHOW_USER_INFO_CONTAINER, isOrderingPanelOpen);
        }
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
    public void onLoadMailFragment(final PushObject push) {
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

        changeButtonState(mMailButton, getResources().getDrawable(R.drawable.ic_main_docs_pink));
        if (isShowMainFragmentContainer != null && !isShowMainFragmentContainer) {
            showMainFragmentContainer();
        }
    }

    public PushObject getPush() {
        return push;
    }

    public void setPush(PushObject push) {
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
        changeButtonState(mDocsButton, getResources().getDrawable(R.drawable.ic_main_docs_pink));

        if (isShowMainFragmentContainer != null && !isShowMainFragmentContainer) {
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

                JustUpApplication.getApplication().getAppPreferences().setUserName(userName);
                JustUpApplication.getApplication().getAppPreferences().setUserId(response.result.id);
                JustUpApplication.getApplication().getAppPreferences().setJabberId(response.result.jabber_id);

                if (response.result.img != null && !response.result.img.equals("")) {
                    ApiWrapper.loadImage(response.result.img, mLoadAccountSettings);
                    JustUpApplication.getApplication().getAppPreferences().setUserAvatarUrl(response.result.img);
                }

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
        try {
            dialog.show(getFragmentManager(), WarningDialog.WARNING_DIALOG);
        } catch (IllegalStateException e) {
            LOGE(TAG, e.getMessage());
        }
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
        LOGI(WebRtcFragment.TAG, "prepareAndCallRTC");

        mCallParam = JustUpApplication.getApplication().prepareCallParam(roomId.getClass().equals(String.class) ? (String) roomId : String.valueOf(roomId), loopback, commandLineRun, runTimeMs, idPerson, contactName);
        findViewById(R.id.container_video_chat).setVisibility(View.VISIBLE);
        mWebRtcFragment = WebRtcFragment.newInstance(mCallParam);
        getFragmentManager().beginTransaction().replace(R.id.container_video_chat, mWebRtcFragment).commit();
    }

    public void reCall(){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                LOGI(WebRtcFragment.TAG, "Handler reCall");

                findViewById(R.id.container_video_chat).setVisibility(View.VISIBLE);
                mWebRtcFragment = WebRtcFragment.newInstance(mCallParam);
                getFragmentManager().beginTransaction().replace(R.id.container_video_chat, mWebRtcFragment).commit();
            }
        }, 1500);
    }

    public void clearDataAfterCallRTC() {
        Fragment videoFragment = getFragmentManager().findFragmentById(R.id.container_video_chat);
        if (videoFragment != null) {
            getFragmentManager().beginTransaction().remove(videoFragment).commit();
        }

        findViewById(R.id.container_video_chat).setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (BuildConfig.FLAVOR.equals(Constance.APP_FLAVOR_APP)) {
            // Launcher
            onBackPressedLA();

            if (getFragmentManager().getBackStackEntryCount() >= 1) {
                finish();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        try {
            super.onConfigurationChanged(newConfig);

            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                LOGI(TAG, "ORIENTATION_LANDSCAPE");

                initTiledMenuFragment();
            } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                LOGI(TAG, "ORIENTATION_PORTRAIT");

                initTiledMenuFragment();
            }
        } catch (Exception e) {
            LOGE(TAG, e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        LOGI(TAG, "onPause");

        // Launcher
        // NOTE: We want all transitions from launcher to act as if the
        // wallpaper were enabled
        // to be consistent. So re-enable the flag here, and we will re-disable
        // it as necessary
        // when Launcher resumes and we are still in AllApps.
        updateWallpaperVisibility(true);
        super.onPause();

        // Launcher
        onPauseLA();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Launcher
        onActivityResultLA(requestCode, resultCode, data);
    }

    public void closeSettingsFragment() {
        mCornerButton.performClick();
    }

    private class OpenOrderingPanel implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (isOrderingPanelOpen != null && !isOrderingPanelOpen) {

                findViewById(R.id.mapAndUserFragment).setVisibility(View.VISIBLE);
                findViewById(R.id.mapAndUserFragment).startAnimation(mAnimCloseUserPanel);

            } else if (isOrderingPanelOpen != null && isOrderingPanelOpen) {

                findViewById(R.id.mapAndUserFragment).startAnimation(mAnimOpenUserPanel);
                findViewById(R.id.mapAndUserFragment).setVisibility(View.GONE);
            }
        }
    }

    private class LoadAccountSettingsFragment implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (!isAccountSettingsLoad) {
                getFragmentManager().beginTransaction().add(R.id.account_settings_fragment_container, new AccountSettingsFragment()).commit();
                isAccountSettingsLoad = true;
            } else {
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.account_settings_fragment_container)).commit();
                isAccountSettingsLoad = false;
            }
        }
    }

    @Override
    public void onServiceCallback(int requestId, Intent requestIntent, int resultCode, Bundle data) {

    }

}