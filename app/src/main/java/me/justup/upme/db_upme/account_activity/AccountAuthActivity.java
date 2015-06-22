package me.justup.upme.db_upme.account_activity;

import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import me.justup.upme.LoginActivity;
import me.justup.upme.db_upme.Constants;

public class AccountAuthActivity extends AccountAuthenticatorActivity {
    private static final String TAG = AccountAuthActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Start Activity for Login Result
        Intent intent = new Intent(AccountAuthActivity.this, LoginActivity.class);
        intent.setAction(LoginActivity.INTENT_ACTION_ACCOUNT_AUTH);
        this.startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                Intent intent = new Intent();
                intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, true);
                if(data!=null) {
                    intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
                }
                intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
                setAccountAuthenticatorResult(intent.getExtras());

                setResult(RESULT_OK, intent);
                finish();
            }else{
                Intent intent = new Intent();
                intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, false);
                setAccountAuthenticatorResult(intent.getExtras());

                setResult(RESULT_CANCELED, intent);
                finish();
            }
        }
    }
}
