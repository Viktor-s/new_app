package me.justup.upme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import me.justup.upme.utils.AppContext;

import static me.justup.upme.utils.LogUtils.*;


public class LoginActivity extends Activity {
    private static final String TAG = makeLogTag(LoginActivity.class);

    private TextView mPhoneField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mPhoneField = (TextView) findViewById(R.id.phone_number_textView);

        Button mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPinError();
            }
        });

        // TODO for debug
        ImageView mLoginDebug = (ImageView) findViewById(R.id.upme_corner_button);
        mLoginDebug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
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

    }

    private void erase() {

    }

    private void showPinError() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.pin_code_shake);
        mPhoneField.startAnimation(shake);
    }

}
