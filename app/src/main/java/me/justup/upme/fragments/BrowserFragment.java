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
import android.widget.Button;
import android.widget.EditText;

import me.justup.upme.R;


public class BrowserFragment extends Fragment {
    private static final String HOME_URL = "http://www.google.com/";
    private static final String HTTP = "http://";

    private WebView mWebView;
    private EditText mUrlField;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browser, container, false);

        mWebView = (WebView) view.findViewById(R.id.browser_webView);
        mUrlField = (EditText) view.findViewById(R.id.browser_url_editText);

        Button mGoButton = (Button) view.findViewById(R.id.browser_go_button);
        mGoButton.setOnClickListener(new OnLoadUrlListener());
        Button mBackButton = (Button) view.findViewById(R.id.browser_back_button);
        mBackButton.setOnClickListener(new OnGoBackListener());
        Button mHomeButton = (Button) view.findViewById(R.id.browser_home_button);
        mHomeButton.setOnClickListener(new OnGoHomeListener());
        Button mForwardButton = (Button) view.findViewById(R.id.browser_forward_button);
        mForwardButton.setOnClickListener(new OnGoForwardListener());

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

        mWebView.loadUrl(HOME_URL);

        return view;
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

    private class OnGoHomeListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mWebView.loadUrl(HOME_URL);
        }
    }

    private class OnGoBackListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            }
        }
    }

    private class OnGoForwardListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mWebView.canGoForward()) {
                mWebView.goForward();
            }
        }
    }

}
