package me.justup.upme.utils;

import android.util.Log;

import me.justup.upme.BuildConfig;

public class LogUtils {
    public static final boolean DEVELOPER_MODE = BuildConfig.DEBUG;

    private static final String LOG_PREFIX = "upme_";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;

    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }

        return LOG_PREFIX + str;
    }

    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }

    public static void LOGD(final String tag, String message) {
        if(DEVELOPER_MODE && message!=null) {
            Log.d(tag, message);
        }
    }

    public static void LOGD(final String tag, String message, Throwable cause) {
        if(DEVELOPER_MODE && message!=null) {
            Log.d(tag, message, cause);
        }
    }

    public static void LOGI(final String tag, String message) {
        if(DEVELOPER_MODE && message!=null) {
            Log.i(tag, message);
        }
    }

    public static void LOGI(final String tag, String message, Throwable cause) {
        if(DEVELOPER_MODE && message!=null) {
            Log.i(tag, message, cause);
        }
    }

    public static void LOGE(final String tag, String message) {
        if(DEVELOPER_MODE && message!=null) {
            Log.e(tag, message);
        }
    }

    public static void LOGE(final String tag, String message, Throwable cause) {
        if(DEVELOPER_MODE && message!=null) {
            Log.e(tag, message, cause);
        }
    }

    public static void LOGW(final String tag, String message) {
        if(DEVELOPER_MODE && message!=null) {
            Log.w(tag, message);
        }
    }

    public static void LOGW(final String tag, String message, Throwable cause) {
        if(DEVELOPER_MODE && message!=null) {
            Log.w(tag, message, cause);
        }
    }
}
