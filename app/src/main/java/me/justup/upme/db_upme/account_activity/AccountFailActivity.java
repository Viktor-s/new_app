package me.justup.upme.db_upme.account_activity;

import android.accounts.AccountAuthenticatorActivity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Show Toast with Error
 */
public class AccountFailActivity extends AccountAuthenticatorActivity {
    private static final String TAG = AccountFailActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Toast.makeText(this, "Возможен только один пользователь", Toast.LENGTH_LONG).show();
        finish();
    }
}
