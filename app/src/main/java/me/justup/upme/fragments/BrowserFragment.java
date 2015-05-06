package me.justup.upme.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;

import me.justup.upme.R;
import me.justup.upme.utils.AppPreferences;


public class BrowserFragment extends Fragment implements View.OnClickListener {
    public static final String HOME_URL = "https://www.google.com/";
    private static final String HTTP = "http://";

    private WebView mWebView = null;
    private EditText mUrlField = null;

    private View mContentView = null;

    private AppPreferences mAppPreferences = null;

    @Override
    public void onResume() {
        super.onResume();

        String url = mAppPreferences.getBrowserUrl();
        mWebView.loadUrl(url);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = super.onCreateView(inflater, container, savedInstanceState);

        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_browser, container, false);
        }

        return mContentView;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAppPreferences = new AppPreferences(getActivity().getApplicationContext());

        // Init UI
        if (getActivity() != null) {

            mWebView = (WebView) mContentView.findViewById(R.id.browser_webView);
            mUrlField = (EditText) mContentView.findViewById(R.id.browser_url_editText);

            ImageButton mGoButton = (ImageButton) mContentView.findViewById(R.id.browser_go_button);
            mGoButton.setOnClickListener(new OnLoadUrlListener());

            ImageButton mBackButton = (ImageButton) mContentView.findViewById(R.id.browser_back_button);
            mBackButton.setOnClickListener(this);
            ImageButton mHomeButton = (ImageButton) mContentView.findViewById(R.id.browser_home_button);
            mHomeButton.setOnClickListener(this);
            ImageButton mForwardButton = (ImageButton) mContentView.findViewById(R.id.browser_forward_button);
            mForwardButton.setOnClickListener(this);
            ImageButton mReloadButton = (ImageButton) mContentView.findViewById(R.id.browser_reload_button);
            mReloadButton.setOnClickListener(this);
            ImageButton mStopButton = (ImageButton) mContentView.findViewById(R.id.browser_stop_button);
            mStopButton.setOnClickListener(this);

            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.setWebChromeClient(new WebChromeClient());
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    mUrlField.setText(url);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.browser_back_button:
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                }
                break;

            case R.id.browser_forward_button:
                if (mWebView.canGoForward()) {
                    mWebView.goForward();
                }
                break;

            case R.id.browser_reload_button:
                mWebView.reload();
                break;

            case R.id.browser_home_button:
                mWebView.loadUrl(HOME_URL);
                break;

            case R.id.browser_stop_button:
                mWebView.stopLoading();
                break;

            default:
                break;
        }
    }

    private class OnLoadUrlListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String url = mUrlField.getText().toString();

            if (!url.startsWith(HTTP)) {
                url = HTTP + url;
            }

            mWebView.loadUrl(url);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        mAppPreferences.setBrowserUrl(mWebView.getUrl());

        mWebView.loadUrl(HOME_URL);
        mWebView.onPause();
    }

}
