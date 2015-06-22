package me.justup.upme.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import me.justup.upme.JustUpApplication;
import me.justup.upme.R;
import me.justup.upme.SplashActivity;
import me.justup.upme.db.DBHelper;


public class CommonUtils {

    public static int convertDpToPixels(final Context context, final int dp) {
        if (context == null) return dp;

        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void hideKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String convertToUTF8(String s) {
        String out;
        try {
            out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
        } catch (java.io.UnsupportedEncodingException e) {
            return "";
        }
        return out;
    }

    public static String convertFromUTF8(String s) {
        String out;
        try {
            out = new String(s.getBytes("ISO-8859-1"), "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    public static void showWarningToast(Activity activity, String message) {
        LayoutInflater inflater = LayoutInflater.from(JustUpApplication.getApplication());
        View layout = inflater.inflate(R.layout.toast_warning, (ViewGroup) activity.findViewById(R.id.warning_toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.toast_text_textView);
        text.setText(message);

        Toast toast = new Toast(JustUpApplication.getApplication());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    @SuppressLint("InflateParams")
    public static void showWarningToast(String message) {
        LayoutInflater inflater = LayoutInflater.from(JustUpApplication.getApplication());
        View layout = inflater.inflate(R.layout.toast_warning, null);

        TextView text = (TextView) layout.findViewById(R.id.toast_text_textView);
        text.setText(message);

        Toast toast = new Toast(JustUpApplication.getApplication());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public static void clearAllAppData() {
        JustUpApplication.getApplication().getAppPreferences().clearPreferences();
        JustUpApplication.getApplication().deleteDatabase(DBHelper.DATABASE_NAME);
        JustUpApplication.getApplication().getSharedPreferences(SplashActivity.class.getSimpleName(), Context.MODE_PRIVATE).edit().clear().apply();
    }
}
