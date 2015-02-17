package me.justup.upme.utils;

import android.content.Context;

import java.util.Locale;


public class CommonUtils {

    public static int convertDpToPixels(Context context, int dp) {
        int padding_in_dp = dp;
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (padding_in_dp * scale + 0.5f);
    }

}
