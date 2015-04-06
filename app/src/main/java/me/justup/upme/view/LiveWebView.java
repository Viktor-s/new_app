package me.justup.upme.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class LiveWebView extends WebView {
    Context mContext;

    public LiveWebView(Context context) {
        super(context);
    }

    public LiveWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LiveWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LiveWebView(Context context, String URL) {
        super(context);
//        mContext = context;
//        setWebViewClient(URL);
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    @SuppressLint("SetJavaScriptEnabled")
    public boolean setWebViewClient(Context context, String URL) {
        setScrollBarStyle(SCROLLBARS_INSIDE_OVERLAY);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus(View.FOCUS_DOWN);

        WebSettings webSettings = getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setUseWideViewPort(false);

        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });

        this.setWebViewClient(new WebViewClient() {
            ProgressDialog dialog = new ProgressDialog(mContext);

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                loadUrl(url);

                return true;
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(mContext, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (dialog != null) {
                    dialog.setMessage("Loading...");
                    dialog.setIndeterminate(true);
                    dialog.setCancelable(true);
                    dialog.show();
                }

            }

            public void onPageFinished(WebView view, String url) {
                if (dialog != null) {
                    dialog.cancel();
                }
            }
        });

        this.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.confirm();
                return true;
            }
        });

        loadUrl(URL);

        return true;
    }
}
