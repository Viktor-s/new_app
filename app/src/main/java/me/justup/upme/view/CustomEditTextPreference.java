package me.justup.upme.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.widget.EditText;

import static me.justup.upme.utils.LogUtils.LOGD;

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
        final EditText mEditText = this.getEditText();
        LOGD("TAG_dialog", " ---> onDialogClosed");
    }

}
