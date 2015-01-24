package me.justup.upme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import me.justup.upme.entity.LoginPhoneQueryEntity;
import me.justup.upme.entity.LoginPinCodeQueryEntity;
import me.justup.upme.entity.LoginResponseEntity;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.utils.AppContext;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class LoginActivity extends Activity {
    private static final String TAG = makeLogTag(LoginActivity.class);

    private TextView mPhoneField;
    private TextView mPinCodeField;
    private LinearLayout mLoginPhonePanel;
    private LinearLayout mLoginPinCodePanel;

    private StringBuilder mNumberString = new StringBuilder("+380");
    private static final int phoneNumberLength = 12;
    private static final int pinNumberLength = 4;
    private static final int phoneCountryNumberLength = 4;
    private boolean isPhoneVerification = true;
    private String mPhoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mLoginPhonePanel = (LinearLayout) findViewById(R.id.login_phone_layout);
        mLoginPinCodePanel = (LinearLayout) findViewById(R.id.login_pin_layout);

        mPhoneField = (TextView) findViewById(R.id.phone_number_textView);
        mPinCodeField = (TextView) findViewById(R.id.pin_number_textView);

        Button mPhoneLoginButton = (Button) findViewById(R.id.login_button);
        mPhoneLoginButton.setOnClickListener(new OnLoginPhoneListener());

        Button mPinLoginButton = (Button) findViewById(R.id.login_pin_button);
        mPinLoginButton.setOnClickListener(new OnLoginPinCodeListener());


        // Delete! Only for debug!
        ImageView mLoginDebug = (ImageView) findViewById(R.id.upme_corner_button);
        mLoginDebug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
                LOGD(TAG, AppContext.getAppContext().toString());
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

            if (mPhoneNumber.length() > phoneNumberLength) {
                LoginPhoneQueryEntity mQueryLoginEntity = new LoginPhoneQueryEntity();
                mQueryLoginEntity.method = ApiWrapper.AUTH_GET_VERIFICATION;
                mQueryLoginEntity.params.phone = mPhoneNumber;

                ApiWrapper.query(mQueryLoginEntity, new OnLoginResponse());
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
                mLoginPinCodeQueryEntity.method = ApiWrapper.AUTH_CHECK_VERIFICATION;
                mLoginPinCodeQueryEntity.params.phone = mPhoneNumber;
                mLoginPinCodeQueryEntity.params.code = mPinNumber;

                ApiWrapper.query(mLoginPinCodeQueryEntity, new OnLoginResponse());
            } else {
                showPinError();
            }
        }
    }

    private class OnLoginResponse extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String content = (responseBody != null) ? new String(responseBody) : "";
            LOGD(TAG, "onSuccess(): " + content);

            LoginResponseEntity result = ApiWrapper.gson.fromJson(content, LoginResponseEntity.class);

            if (result.result != null) {
                mLoginPhonePanel.setVisibility(View.GONE);
                mLoginPinCodePanel.setVisibility(View.VISIBLE);

                mNumberString.setLength(0);
                isPhoneVerification = false;
            } else {
                showPinError();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = (responseBody != null) ? new String(responseBody) : "";
            LOGE(TAG, "onFailure(): " + content);
        }
    }

}
