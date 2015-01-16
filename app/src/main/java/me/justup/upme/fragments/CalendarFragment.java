package me.justup.upme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import me.justup.upme.R;

import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class CalendarFragment extends Fragment {
    private static final String TAG = makeLogTag(CalendarFragment.class);
    // public static final String URL = "http://justup.me/calendar/";
    public static final String URL = "http://justup.me/";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        WebView mWebView = (WebView) v.findViewById(R.id.root_webview);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.loadUrl(URL);

        return v;
    }

}
