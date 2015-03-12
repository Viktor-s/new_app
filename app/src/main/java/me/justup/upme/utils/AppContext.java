package me.justup.upme.utils;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import me.justup.upme.R;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.db.DBHelper;


@ReportsCrashes(formKey = "",
        mailTo = "initrod@gmail.com",
        customReportContent = {ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION,
                ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.USER_COMMENT},
        mode = ReportingInteractionMode.DIALOG,
        resToastText = R.string.crash_toast_text,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info,
        resDialogTitle = R.string.crash_dialog_title,
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
        resDialogOkToast = R.string.crash_dialog_ok_toast

)
public class AppContext extends Application {
    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        AppContext.context = getApplicationContext();
        DBAdapter.initializeInstance(new DBHelper(context));

        ACRA.init(this);
    }

    public static Context getAppContext() {
        return AppContext.context;
    }

}
