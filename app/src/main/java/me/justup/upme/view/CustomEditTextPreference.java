package me.justup.upme.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

import static me.justup.upme.utils.LogUtils.LOGD;

/**
 * Created by bogdan on 03.04.15.
 */
public class CustomEditTextPreference extends EditTextPreference {


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CustomEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditTextPreference(Context context) {
        super(context);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        LOGD("TAG_dialog", " ---> onDialogClosed");
    }
}
