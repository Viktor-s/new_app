package me.justup.upme;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import me.justup.upme.db_upme.Constants;
import me.justup.upme.db_upme.MetaData;
import me.justup.upme.dialogs.WarningDialog;
import me.justup.upme.entity.LoginPhoneQueryEntity;
import me.justup.upme.entity.LoginPinCodeQueryEntity;
import me.justup.upme.entity.LoginResponseEntity;
import me.justup.upme.fragments.ProgressDialog;
import me.justup.upme.fragments.SettingsFragment;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.services.ApplicationSupervisorService;
import me.justup.upme.services.StatusBarService;
import me.justup.upme.utils.Constance;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;

public class LoginActivity extends BaseActivity {
    private static final String TAG = makeLogTag(LoginActivity.class);

    // Is From Auth Activity
    public static final String INTENT_ACTION_ACCOUNT_AUTH = "me.justup.upme.account.auth";
    public boolean mIsFromAuthActivity = false;

    private static final String IS_SHOW_PIN_PANEL = "is_show_pin_panel";
    private static final String ENTER_PIN_CODE_LIMIT = "ATTEMPTS_TO_LIMIT_EXCEEDED";
    private static final String SETTINGS_FRAGMENT_TAG = "SETTINGS_FRAGMENT";

    private TextView mPhoneField;
    private TextView mPinCodeField;
    private TextView mPinCodeLimit;
    private LinearLayout mLoginPhonePanel;
    private LinearLayout mLoginPinCodePanel;

    private StringBuilder mNumberString = new StringBuilder("+");

    private static final int phoneNumberLength = 12;
    private static final int minPhoneNumberLength = 11;
    private static final int pinNumberLength = 4;
    private static final int phoneCountryNumberLength = 1;
    private boolean isPhoneVerification = true;
    private String mPhoneNumber;
    private Animation mNumberPanelDown;
    private Animation mNumberPanelFromTop;

    private Button mPhoneLoginButton;
    private Button mPinLoginButton;
    private boolean isShowSettings;

    private Account mAccount = null;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.FLAVOR.equals(Constance.APP_FLAVOR_LAUNCHER)) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        setContentView(R.layout.activity_login);

        startService(new Intent(this, StatusBarService.class));

        mLoginPhonePanel = (LinearLayout) findViewById(R.id.login_phone_layout);
        mLoginPinCodePanel = (LinearLayout) findViewById(R.id.login_pin_layout);

        mPhoneField = (TextView) findViewById(R.id.phone_number_textView);
        mPinCodeField = (TextView) findViewById(R.id.pin_number_textView);
        mPinCodeLimit = (TextView) findViewById(R.id.pin_code_limit_textView);

        mPhoneLoginButton = (Button) findViewById(R.id.login_button);
        mPhoneLoginButton.setOnClickListener(new OnLoginPhoneListener());

        mPinLoginButton = (Button) findViewById(R.id.login_pin_button);
        mPinLoginButton.setOnClickListener(new OnLoginPinCodeListener());

        mNumberPanelDown = AnimationUtils.loadAnimation(this, R.anim.login_number_panel_down);
        mNumberPanelFromTop = AnimationUtils.loadAnimation(this, R.anim.login_number_panel_from_top);

        Button mAppSettings = (Button) findViewById(R.id.login_settings_button);
        mAppSettings.setOnClickListener(new OnLoadSettings());

        if (savedInstanceState != null) {
            isPhoneVerification = savedInstanceState.getBoolean(IS_SHOW_PIN_PANEL, false);

            if (!isPhoneVerification) {
                mLoginPhonePanel.setVisibility(View.GONE);
                mLoginPinCodePanel.setVisibility(View.VISIBLE);

                mNumberString.setLength(0);
                mPhoneNumber = JustUpApplication.getApplication().getAppPreferences().getPhoneNumber();
            } else {
                loadSavedPhoneNumber();
            }
        } else {
            loadSavedPhoneNumber();
        }

        if (JustUpApplication.getApplication().getAppPreferences().isMonitoring()) {
            startService(new Intent(JustUpApplication.getApplication().getApplicationContext(), ApplicationSupervisorService.class));
        }

        ImageView mFastEnter = (ImageView) findViewById(R.id.upme_corner_button);
        mFastEnter.setOnClickListener(new OnFastEnterListener());


        // Delete! Only for debug!
        TextView appVersion = (TextView) findViewById(R.id.app_version_textView);
        String versionName = "";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        appVersion.setText("UPME v" + versionName);

        // Registration Receiver
        registerReceiver(syncAdapterStatusReceiver, new IntentFilter(Constants.SYNC_STATUS_RECEIVER));

        if(INTENT_ACTION_ACCOUNT_AUTH.equals(getIntent().getAction())){
            mIsFromAuthActivity = true;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onPinCodeButtonClickHandler(View view) {
        switch (view.getId()) {
            case R.id.button1:
                putDigit(1);
                break;
            case R.id.button2:
                putDigit(2);
                break;
            case R.id.button3:
                putDigit(3);
                break;
            case R.id.button4:
                putDigit(4);
                break;
            case R.id.button5:
                putDigit(5);
                break;
            case R.id.button6:
                putDigit(6);
                break;
            case R.id.button7:
                putDigit(7);
                break;
            case R.id.button8:
                putDigit(8);
                break;
            case R.id.button9:
                putDigit(9);
                break;
            case R.id.button0:
                putDigit(0);
                break;
            default:
                erase();
                break;
        }
    }

    private void putDigit(int digit) {
        if (isPhoneVerification) {
            if (mNumberString.length() <= phoneNumberLength) {
                mNumberString.append(digit);
                updateNumberField();
            }
        } else {
            if (mNumberString.length() < pinNumberLength) {
                mNumberString.append(digit);
                updatePinCodeField();
            }
        }
    }

    private void erase() {
        if (isPhoneVerification) {
            if (mNumberString.length() > phoneCountryNumberLength) {
                mNumberString.setLength(mNumberString.length() - 1);
            }
            updateNumberField();
        } else {
            if (mNumberString.length() >= 1) {
                mNumberString.setLength(mNumberString.length() - 1);
            }
            updatePinCodeField();
        }
    }

    private void updateNumberField() {
        mPhoneField.setText(mNumberString.toString());
    }

    private void updatePinCodeField() {
        mPinCodeField.setText(mNumberString.toString());
    }

    private void showPinError() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.pin_code_shake);

        if (isPhoneVerification) {
            mPhoneField.startAnimation(shake);
        } else {
            mPinCodeField.startAnimation(shake);
        }
    }

    private class OnLoginPhoneListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mPhoneNumber = mPhoneField.getText().toString();

            if (mPhoneNumber.length() > minPhoneNumberLength) {
                mPhoneLoginButton.setEnabled(false);

                LoginPhoneQueryEntity mQueryLoginEntity = new LoginPhoneQueryEntity();
                mQueryLoginEntity.params.phone = mPhoneNumber;

                ApiWrapper.loginQuery(mQueryLoginEntity, new OnLoginResponse());
            } else {
                showPinError();
            }
        }
    }

    private class OnLoginPinCodeListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String mPinNumber = mPinCodeField.getText().toString();

            if (mPinNumber.length() == pinNumberLength) {
                mPinLoginButton.setEnabled(false);

                LoginPinCodeQueryEntity mLoginPinCodeQueryEntity = new LoginPinCodeQueryEntity();
                mLoginPinCodeQueryEntity.params.phone = mPhoneNumber;
                mLoginPinCodeQueryEntity.params.code = mPinNumber;

                JustUpApplication.getApplication().getAppPreferences().setPinCode(mPinNumber);

                ApiWrapper.loginQuery(mLoginPinCodeQueryEntity, new OnLoginResponse());
            } else {
                showPinError();
            }
        }
    }

    private class OnLoginResponse extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGD(TAG, "onSuccess() : " + content);

            LoginResponseEntity response = null;

            try {
                response = ApiWrapper.gson.fromJson(content, LoginResponseEntity.class);
            } catch (JsonSyntaxException e) {
                LOGE(TAG, "gson.fromJson:\n" + content);
            }

            if (response != null && response.result != null) {
                if (response.result.success && response.result.token == null) {
                    mLoginPhonePanel.startAnimation(mNumberPanelDown);
                    mLoginPhonePanel.setVisibility(View.GONE);

                    mLoginPinCodePanel.setVisibility(View.VISIBLE);
                    mLoginPinCodePanel.startAnimation(mNumberPanelFromTop);

                    mNumberString.setLength(0);
                    isPhoneVerification = false;

                    JustUpApplication.getApplication().getAppPreferences().setPhoneNumber(mPhoneNumber);
                } else {
                    JustUpApplication.getApplication().getAppPreferences().setToken(response.result.token);
                    JustUpApplication.getApplication().getAppPreferences().setTokenLife(System.currentTimeMillis());

                    addAccount(mPhoneNumber, mPinCodeField.getText().toString());
                }
            } else {
                if (response != null && response.error != null) {
                    if (response.error.data.equals(ENTER_PIN_CODE_LIMIT)) {
                        mPinCodeLimit.setVisibility(View.VISIBLE);
                        mLoginPhonePanel.setVisibility(View.VISIBLE);
                        mLoginPinCodePanel.setVisibility(View.GONE);

                        isPhoneVerification = true;
                        mPinCodeField.setText("");
                    }
                }

                mPhoneLoginButton.setEnabled(true);
                mPinLoginButton.setEnabled(true);
                showPinError();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGE(TAG, "onFailure(): " + content);

            mPhoneLoginButton.setEnabled(true);
            mPinLoginButton.setEnabled(true);

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

    private void showWarningDialog(String title, String message) {
        WarningDialog dialog = WarningDialog.newInstance(title, message);
        dialog.show(getFragmentManager(), WarningDialog.WARNING_DIALOG);
    }

    private class OnLoadSettings implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (!isShowSettings) {
                getFragmentManager().beginTransaction().add(R.id.login_settings_container, new SettingsFragment(), SETTINGS_FRAGMENT_TAG).commit();
                isShowSettings = true;
            } else {
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag(SETTINGS_FRAGMENT_TAG)).commit();
                isShowSettings = false;
            }
        }
    }

    private void loadSavedPhoneNumber() {
        String number = JustUpApplication.getApplication().getAppPreferences().getPhoneNumber();
        if (number == null) {
            return;
        }

        mNumberString.setLength(0);
        mNumberString.append(number);
        updateNumberField();
    }

    @Override
    public void onBackPressed() {
        if (BuildConfig.FLAVOR.equals(Constance.APP_FLAVOR_APP)) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(IS_SHOW_PIN_PANEL, isPhoneVerification);
    }

    private class OnFastEnterListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (BuildConfig.FLAVOR.equals(Constance.APP_FLAVOR_APP)) {
                if (JustUpApplication.getApplication().getAppPreferences().isTokenLive()) {
                    startActivity(new Intent(LoginActivity.this, SplashActivity.class));
                    LoginActivity.this.finish();
                } else {
                    showWarningDialog(getString(R.string.warning), getString(R.string.token_life_warning));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unreg.Receiver
        unregisterReceiver(syncAdapterStatusReceiver);

        if(mIsFromAuthActivity){
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    private BroadcastReceiver syncAdapterStatusReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                int statusText = intent.getIntExtra(Constants.SYNC_STATUS_TEXT, R.string.sync_adapter_dummy_data_sync);
                String syncTag = intent.getStringExtra(Constants.SYNC_STATUS_TAG);

                LOGD(TAG, "Sync msg : \nSync State : " + getString(statusText) + "\nKey : " + syncTag);

                switch (statusText){
                    case R.string.sync_adapter_product_categories_sync:
                        if(syncTag.equals(Constants.SYNC_STATUS_START)){
                            changeDialogText(statusText, ProgressDialog.TAG_WAIT_DIALOG);
                        }else if(syncTag.equals(Constants.SYNC_STATUS_END)){
                            // Start Sync Mail Account
                            ContentResolver.setSyncAutomatically(mAccount, MetaData.AUTHORITY_MAIL_CONTACT, true);
                        }

                        break;
                    case R.string.sync_adapter_mail_contact_sync:
                        if(syncTag.equals(Constants.SYNC_STATUS_START)){
                            changeDialogText(statusText, ProgressDialog.TAG_WAIT_DIALOG);
                        }else if(syncTag.equals(Constants.SYNC_STATUS_END)){
                            // Start Sync Calendar
                            ContentResolver.setSyncAutomatically(mAccount, MetaData.AUTHORITY_SHORT_NEWS, true);
                        }

                        break;
                    case R.string.sync_adapter_news_sync:

                        if(syncTag.equals(Constants.SYNC_STATUS_START)){
                            changeDialogText(statusText, ProgressDialog.TAG_WAIT_DIALOG);
                        }else if(syncTag.equals(Constants.SYNC_STATUS_END)){
                            changeDialogText(syncTag, ProgressDialog.TAG_WAIT_DIALOG);
                            // Start Sync Contact
                            ContentResolver.setSyncAutomatically(mAccount, MetaData.AUTHORITY_EVENT_CALENDAR, true);
                        }

                        break;
                    case R.string.sync_adapter_calendar_sync:
                        if(syncTag.equals(Constants.SYNC_STATUS_START)){
                            changeDialogText(statusText, ProgressDialog.TAG_WAIT_DIALOG);
                        }else if(syncTag.equals(Constants.SYNC_STATUS_END)){
                            changeDialogText(syncTag, ProgressDialog.TAG_WAIT_DIALOG);

                            dismissDialog(ProgressDialog.TAG_WAIT_DIALOG);
                            goToSplashActivity(mAccount.name);
                        }

                        break;
                }
            }
        }
    };

    public void addAccount(String user, String code){
        // Check if account consist
        Account account = JustUpApplication.getApplication().getAccount();
        if(account!=null) {
            LOGI(TAG, "Account Name : " + account.name + ", Login : " + user);
        }else{
            LOGI(TAG, "Account is NULL !");
        }

        // If Account consist update him
        if(account!=null && account.name.equals(user)){
            // Update password
            Account newAccount = new Account(user, Constants.ACCOUNT_TYPE);
            // Start Sync Account Data and show processing loading windows
            mAccount = newAccount;

            AccountManager.get(getApplicationContext()).setPassword(newAccount, code);
        }else{ // If Account first added
            // Add data to Android Account Manager
            Account newAccount = new Account(user, Constants.ACCOUNT_TYPE);
            AccountManager.get(getApplicationContext()).addAccountExplicitly(newAccount, code, null);

            // Start Sync Account Data and show processing loading windows
            mAccount = newAccount;

            showWaitDialog(getResources().getString(R.string.sync_adapter_dummy_data_sync), ProgressDialog.TAG_WAIT_DIALOG);

            // Start Sync Main App Data
            ContentResolver.setSyncAutomatically(newAccount, MetaData.AUTHORITY_PRODUCT_CATEGORIES, true);
            // ContentResolver.setSyncAutomatically(newAccount, MetaData.AUTHORITY_MAIL_CONTACT, true);
            // ContentResolver.setSyncAutomatically(newAccount, MetaData.AUTHORITY_SHORT_NEWS, true);
            // ContentResolver.setSyncAutomatically(newAccount, MetaData.AUTHORITY_EVENT_CALENDAR, true);

            // Set Sync Period to all Sync Services
            ContentResolver.addPeriodicSync(newAccount, MetaData.AUTHORITY_PRODUCT_CATEGORIES, new Bundle(), Constants.ONE_DAY);
            ContentResolver.addPeriodicSync(newAccount, MetaData.AUTHORITY_MAIL_CONTACT, new Bundle(), Constants.ONE_DAY);
            ContentResolver.addPeriodicSync(newAccount, MetaData.AUTHORITY_SHORT_NEWS, new Bundle(), Constants.ONE_DAY);
            ContentResolver.addPeriodicSync(newAccount, MetaData.AUTHORITY_EVENT_CALENDAR, new Bundle(), Constants.ONE_DAY);

            // Set Account to Application
            (JustUpApplication.getApplication()).setAccount(newAccount);
        }
    }

    public void goToSplashActivity(String login){
        // Check from what Activity call
        if(mIsFromAuthActivity){
            Intent returnIntent = new Intent();
            returnIntent.putExtra(AccountManager.KEY_ACCOUNT_NAME, login);
            setResult(RESULT_OK, returnIntent);

            LoginActivity.this.finish();
        }else {
            // Go to Splash Activity
            startActivity(new Intent(LoginActivity.this, SplashActivity.class));
            LoginActivity.this.finish();
        }
    }
}
