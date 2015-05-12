package me.justup.upme.apprtc.util;

import android.os.Build;

import static me.justup.upme.utils.LogUtils.LOGD;

/**
 * AppRTCUtils provides helper functions for managing thread safety.
 */
public final class AppRTCUtils {

    private AppRTCUtils() {
    }

    /**
     * NonThreadSafe is a helper class used to help verify that methods of a
     * class are called from the same thread.
     */
    public static class NonThreadSafe {
        private final Long threadId;

        public NonThreadSafe() {
            // Store thread ID of the creating thread.
            threadId = Thread.currentThread().getId();
        }

        /** Checks if the method is called on the valid/creating thread. */
        public boolean calledOnValidThread() {
            return threadId.equals(Thread.currentThread().getId());
        }
    }

    /** Helper method which throws an exception  when an assertion has failed. */
    public static void assertIsTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected condition to be true");
        }
    }

    /** Helper method for building a string of thread information.*/
    public static String getThreadInfo() {
        return "@[name=" + Thread.currentThread().getName()
                + ", id=" + Thread.currentThread().getId() + "]";
    }

    /** Information about the current build, taken from system properties. */
    public static void logDeviceInfo(String tag) {
        LOGD(tag, "Android SDK: " + Build.VERSION.SDK_INT + ", "
                + "Release: " + Build.VERSION.RELEASE + ", "
                + "Brand: " + Build.BRAND + ", "
                + "Device: " + Build.DEVICE + ", "
                + "Id: " + Build.ID + ", "
                + "Hardware: " + Build.HARDWARE + ", "
                + "Manufacturer: " + Build.MANUFACTURER + ", "
                + "Model: " + Build.MODEL + ", "
                + "Product: " + Build.PRODUCT);
    }
}

