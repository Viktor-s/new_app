package me.justup.upme.view.cwvm;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.webkit.WebSettings;

public class CWVUtils {

    // HTMLConverter
    public static final String MAIL_CSS_CLASS = "UpMe";

    public static String getCSSStylePreferences(boolean textStyle, int textSize){
        return HTMLConverter.cssStylePre(textStyle, textSize);
    }

    public static boolean isSingleColumnLayoutSupported(){
        return HTMLConverter.isSingleColumnLayoutSupported();
    }

    public static WebSettings.TextSize getScreenDensity(Context context){
        return HTMLConverter.getScreenDensity(context);
    }

    public static Handler getMainThreadHandler(){
        return HTMLConverter.getMainThreadHandler();
    }

    public static boolean isSupportZoomControlButton(Context context){
        return HTMLConverter.isSupportZoomControlButton(context);
    }

    static class HTMLConverter{

        /**
         * Dynamically generate a CSS style for {@code <pre>} elements.
         *
         *  <p>
         *  The style incorporates the user's current preference
         *  setting for the font family used for plain text messages.
         *  </p>
         *
         * @return
         *      A {@code <style>} element that can be dynamically included in the HTML
         *      {@code <head>} element when messages are displayed.
         */
        private static String cssStylePre(boolean textStyle, int textSize) {
            String font;

            if(textStyle){
                font = "monospace";
            }else {
                font = "sans-serif";
            }

            return "<style type=\"text/css\"> pre." + MAIL_CSS_CLASS +
                    "{white-space: pre-wrap; " +
                    "word-wrap:break-word; " +
                    "font-family: " + font + "; " +
                    "font-size:" + textSize + "em; " +
                    "position: absolute; " +
                    "margin-left: auto; " +
                    "margin-right: auto; " +
                    "margin-top:0px}" +
                    "</style>";
        }

        // Link :
        // http://asherson.wordpress.com/2013/03/06/android-webview-scaling/
        // http://www.w3schools.com/css/css3_fonts.asp
        // https://developer.mozilla.org/en-US/docs/Web/CSS/font
        // http://www.html5rocks.com/en/mobile/responsivedesign/#toc-theneed
        // https://developers.google.com/chrome/mobile/docs/webview/pixelperfect --- Google Documentation !

        /**
         * Check whether the single column layout algorithm can be used on this version of Android.
         *
         * <p>
         * Single column layout was broken on Android < 2.2 (see
         * <a href="http://code.google.com/p/android/issues/detail?id=5024">issue 5024</a>).
         * </p>
         *
         * <p>
         * Android versions >= 3.0 have problems with unclickable links when single column layout is
         * enabled (see
         * <a href="http://code.google.com/p/android/issues/detail?id=34886">issue 34886</a>
         * in Android's bug tracker, and
         * <a href="http://code.google.com/p/k9mail/issues/detail?id=3820">issue 3820</a>
         * in K-9 Mail's bug tracker).
         */
        private static boolean isSingleColumnLayoutSupported() {
            return (Build.VERSION.SDK_INT > 7 && Build.VERSION.SDK_INT < 11);
        }

        private static WebSettings.TextSize getScreenDensity(Context context){
            WebSettings.TextSize textSize = null;

            switch (context.getResources().getDisplayMetrics().densityDpi) {
                case DisplayMetrics.DENSITY_LOW:
                    textSize = WebSettings.TextSize.SMALLER;
                case DisplayMetrics.DENSITY_MEDIUM:
                    textSize = WebSettings.TextSize.NORMAL;
                case DisplayMetrics.DENSITY_HIGH:
                    textSize = WebSettings.TextSize.LARGEST;
                case DisplayMetrics.DENSITY_XHIGH:
                    textSize = WebSettings.TextSize.LARGER;
            }

            return textSize;

        }

        private static Handler sMainThreadHandler;

        /**
         * @return a {@link Handler} tied to the main thread.
         */
        private static Handler getMainThreadHandler() {
            if (sMainThreadHandler == null) {
                // No need to synchronize -- it's okay to create an extra Handler, which will be used
                // only once and then thrown away.
                sMainThreadHandler = new Handler(Looper.getMainLooper());
            }

            return sMainThreadHandler;
        }

        private static boolean isSupportZoomControlButton(Context context){
            if (Build.VERSION.SDK_INT >= 11) {
                PackageManager pm = context.getPackageManager();

                return pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH) ||
                       pm.hasSystemFeature(PackageManager.FEATURE_FAKETOUCH_MULTITOUCH_DISTINCT);
            }else {
                return false;
            }
        }
    }
}
