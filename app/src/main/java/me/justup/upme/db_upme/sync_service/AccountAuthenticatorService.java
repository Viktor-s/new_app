package me.justup.upme.db_upme.sync_service;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.provider.ContactsContract;

import java.util.Arrays;

import me.justup.upme.db_upme.MD5;
import me.justup.upme.db_upme.account_activity.AccountAuthActivity;
import me.justup.upme.db_upme.account_activity.AccountFailActivity;

import static me.justup.upme.utils.LogUtils.LOGD;

public class AccountAuthenticatorService extends Service {
    private static final String TAG = AccountAuthenticatorService.class.getSimpleName();

    public AccountAuthenticatorService() {
        super();
    }

    public static final String ACTION_ADD_ACCOUNT = me.justup.upme.db_upme.Constants.PACKAGE.concat(".account.ADD_ACCOUNT");
    public static final String ACTION_EDIT_ACCOUNT = me.justup.upme.db_upme.Constants.PACKAGE.concat(".account.EDIT_ACCOUNT");
    public static final String ACTION_REMOVE_ACCOUNT = me.justup.upme.db_upme.Constants.PACKAGE.concat(".account.REMOVE_ACCOUNT");

    private static AccountAuthenticatorImpl authenticator = null;

    @Override
    public IBinder onBind(Intent intent) {
        IBinder binder = null;

        if (intent.getAction().equals(android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT)) {
            binder = getAuthenticator().getIBinder();
        }

        return binder;
    }

    private AccountAuthenticatorImpl getAuthenticator() {
        return authenticator == null ? new AccountAuthenticatorImpl(this) : authenticator;
    }

    public static void addAccount(Context ctx, String username, String password, Parcelable response) {
        AccountAuthenticatorResponse authResponse = (AccountAuthenticatorResponse)response;
        Bundle result = AccountAuthenticatorImpl.addAccount(ctx, username, password);
        if(authResponse != null) {
            authResponse.onResult(result);
        }
    }

    public static Boolean hasLastDITFLyAccount(Context ctx) {
        return AccountAuthenticatorImpl.hasLastDITFLyAccount(ctx);
    }

    public static void removeLastDITFLyAccount(Context ctx) {
        AccountAuthenticatorImpl.removeLastDITFLyAccount(ctx);
    }

    public static void reSyncAccount(Context context) {

        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType(me.justup.upme.db_upme.Constants.ACCOUNT_TYPE);

        if(ContentResolver.getSyncAutomatically(accounts[0], ContactsContract.AUTHORITY)) {
            // Try turning it off and on again
            ContentResolver.setSyncAutomatically(accounts[0], ContactsContract.AUTHORITY, false);
            ContentResolver.setSyncAutomatically(accounts[0], ContactsContract.AUTHORITY, true);

        }

    }

    /**
     * Class account management. Abstract base class for creating AccountAuthenticators.
     * In order to be an authenticator one must extend this class, provider implementations for the abstract methods and write a service that returns the result of getIBinder() in the service's onBind(android.content.Intent) when invoked with an intent with action ACTION_AUTHENTICATOR_INTENT.
     * This service must specify the following intent filter and metadata tags in its AndroidManifest.xml file.
     *
     * Link : http://developer.android.com/reference/android/accounts/AbstractAccountAuthenticator.html
     */
    private static class AccountAuthenticatorImpl extends AbstractAccountAuthenticator {
        private Context mContext;

        public AccountAuthenticatorImpl(Context context) {
            super(context);

            this.mContext = context;
        }

        public static Bundle addAccount(Context context, String username, String password) {
            Bundle result = null;

            Account account = new Account(username, me.justup.upme.db_upme.Constants.ACCOUNT_TYPE);
            AccountManager am = AccountManager.get(context);

            if (am.addAccountExplicitly(account, MD5.getInstance().hash(password), null)) {
                result = new Bundle();
                result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            }

            return result;
        }

        public static Boolean hasLastDITFLyAccount(Context context) {
            AccountManager am = AccountManager.get(context);
            Account[] accounts = am.getAccountsByType(me.justup.upme.db_upme.Constants.ACCOUNT_TYPE);

            return accounts != null && accounts.length > 0;
        }

        public static void removeLastDITFLyAccount(Context ctx) {
            AccountManager am = AccountManager.get(ctx);
            Account[] accounts = am.getAccountsByType(me.justup.upme.db_upme.Constants.ACCOUNT_TYPE);

            for(Account account : accounts) {
                am.removeAccount(account, null, null);
            }
        }

        @Override
        public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
            Bundle result;

            if(hasLastDITFLyAccount(mContext)) {
                result = new Bundle();
                Intent i = new Intent(mContext, AccountFailActivity.class);
                i.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
                result.putParcelable(AccountManager.KEY_INTENT, i);

                return result;
            } else {
                result = new Bundle();
                Intent i = new Intent(mContext, AccountAuthActivity.class);
                i.setAction(ACTION_ADD_ACCOUNT);
                i.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
                result.putParcelable(AccountManager.KEY_INTENT, i);
            }

            return result;
        }

        @Override
        public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException { return null; }

        @Override
        public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) { throw new UnsupportedOperationException(); }

        @Override
        public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
            LOGD(TAG, "GetAuthToken");

            Bundle result = new Bundle();
            Intent i = new Intent(mContext, AccountAuthActivity.class);
            i.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

            result.putParcelable(AccountManager.KEY_INTENT, i);

            return result;
        }
        @Override
        public String getAuthTokenLabel(String authTokenType) {
            LOGD(TAG, "GetAuthTokenLabel : " + authTokenType);

            return null;
        }

        @Override
        public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
            LOGD(TAG, "HasFeatures : " + Arrays.toString(features));

            Bundle result = new Bundle();
            result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);

            return result;
        }

        @Override
        public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) { return null; }

    }
}
