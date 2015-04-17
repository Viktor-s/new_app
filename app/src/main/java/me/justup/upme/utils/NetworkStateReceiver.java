package me.justup.upme.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkStateReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Network connectivity change", Toast.LENGTH_SHORT).show();
        if (intent.getExtras() != null) {
            NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                Toast.makeText(context, "Network " + ni.getTypeName() + " connected", Toast.LENGTH_SHORT).show();
            }
        }
        if (intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
            Toast.makeText(context, "There's no network connectivity", Toast.LENGTH_SHORT).show();
        }
    }

//    public static boolean hasActiveInternetConnection(Context context) {
//        if (isNetworkAvailable(context)) {
//            try {
//                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
//                urlc.setRequestProperty("User-Agent", "Test");
//                urlc.setRequestProperty("Connection", "close");
//                urlc.setConnectTimeout(1500);
//                urlc.connect();
//                return (urlc.getResponseCode() == 200);
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "Error checking internet connection", e);
//            }
//        } else {
//            Log.d(LOG_TAG, "No network available!");
//        }
//        return false;
//    }

}