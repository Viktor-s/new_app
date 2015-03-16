package me.justup.upme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import me.justup.upme.dialogs.WarningDialog;
import me.justup.upme.entity.LoginPhoneQueryEntity;
import me.justup.upme.entity.LoginPinCodeQueryEntity;
import me.justup.upme.entity.LoginResponseEntity;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.services.StatusBarService;
import me.justup.upme.utils.AppContext;
import me.justup.upme.utils.AppPreferences;
import me.justup.upme.utils.ServerSwitcher;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class LoginActivity extends BaseActivity {
    private static final String TAG = makeLogTag(LoginActivity.class);

    private TextView mPhoneField;
    private TextView mPinCodeField;
    private LinearLayout mLoginPhonePanel;
    private LinearLayout mLoginPinCodePanel;

    private StringBuilder mNumberString = new StringBuilder("+");
    private AppPreferences mAppPreferences = new AppPreferences(AppContext.getAppContext());

    private static final int phoneNumberLength = 12;
    private static final int minPhoneNumberLength = 11;
    private static final int pinNumberLength = 4;
    private static final int phoneCountryNumberLength = 1;
    private boolean isPhoneVerification = true;
    private String mPhoneNumber;
    private Animation mNumberPanelDown;
    private Animation mNumberPanelFromTop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        startService(new Intent(this, StatusBarService.class));

        mLoginPhonePanel = (LinearLayout) findViewById(R.id.login_phone_layout);
        mLoginPinCodePanel = (LinearLayout) findViewById(R.id.login_pin_layout);

        mPhoneField = (TextView) findViewById(R.id.phone_number_textView);
        mPinCodeField = (TextView) findViewById(R.id.pin_number_textView);

        Button mPhoneLoginButton = (Button) findViewById(R.id.login_button);
        mPhoneLoginButton.setOnClickListener(new OnLoginPhoneListener());

        Button mPinLoginButton = (Button) findViewById(R.id.login_pin_button);
        mPinLoginButton.setOnClickListener(new OnLoginPinCodeListener());

        mNumberPanelDown = AnimationUtils.loadAnimation(this, R.anim.login_number_panel_down);
        mNumberPanelFromTop = AnimationUtils.loadAnimation(this, R.anim.login_number_panel_from_top);

        Button mAppSettings = (Button) findViewById(R.id.login_settings_button);
        mAppSettings.setOnClickListener(new OnLoadSettings());

        loadSavedPhoneNumber();


        // Delete! Only for debug!
        RadioGroup radiogroup = (RadioGroup) findViewById(R.id.server_radioGroup);
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.server1_radioButton:
                        ServerSwitcher.getInstance().setUrl("http://test.justup.me/uptabinterface/jsonrpc/");
                        ServerSwitcher.getInstance().setCloudStorageUrl("http://test.justup.me/CloudStorage");
                        break;
                    case R.id.server2_radioButton:
                        ServerSwitcher.getInstance().setUrl("http://test.justup.me/uptabinterface/jsonrpc/");
                        ServerSwitcher.getInstance().setCloudStorageUrl("http://test.justup.me/CloudStorage");
                        break;
                    default:
                        break;
                }
            }
        });

        ImageView mLoginDebug = (ImageView) findViewById(R.id.upme_corner_button);
        mLoginDebug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });
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
                LoginPinCodeQueryEntity mLoginPinCodeQueryEntity = new LoginPinCodeQueryEntity();
                mLoginPinCodeQueryEntity.params.phone = mPhoneNumber;
                mLoginPinCodeQueryEntity.params.code = mPinNumber;

                mAppPreferences.setPinCode(mPinNumber);

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
            LOGD(TAG, "onSuccess(): " + content);

            LoginResponseEntity response = null;

            try {
                response = ApiWrapper.gson.fromJson(content, LoginResponseEntity.class);
            } catch (JsonSyntaxException e) {
                LOGE(TAG, "gson.fromJson:\n" + content);
            }

            if (response != null && response.result != null) {
                if (response.result.PHONE != null) {
                    mLoginPhonePanel.startAnimation(mNumberPanelDown);
                    mLoginPhonePanel.setVisibility(View.GONE);

                    mLoginPinCodePanel.setVisibility(View.VISIBLE);
                    mLoginPinCodePanel.startAnimation(mNumberPanelFromTop);

                    mNumberString.setLength(0);
                    isPhoneVerification = false;

                    mAppPreferences.setPhoneNumber(mPhoneNumber);
                } else {
                    mAppPreferences.setToken(response.result.token);

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            } else {
                showPinError();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGE(TAG, "onFailure(): " + content);

            showWarningDialog(ApiWrapper.getResponseError(content));
        }
    }

    private void showWarningDialog(String message) {
        WarningDialog dialog = WarningDialog.newInstance(getString(R.string.network_error), message);
        dialog.show(getFragmentManager(), WarningDialog.WARNING_DIALOG);
    }

    private class OnLoadSettings implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(LoginActivity.this, SettingsActivity.class));
        }
    }

    private void loadSavedPhoneNumber() {
        String number = mAppPreferences.getPhoneNumber();
        if (number == null) {
            return;
        }

        mNumberString.setLength(0);
        mNumberString.append(number);
        updateNumberField();
    }

}
