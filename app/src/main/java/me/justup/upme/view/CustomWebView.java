package me.justup.upme.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.WebView;

public class CustomWebView extends WebView {

    public CustomWebView(Context context) {
        super(context);
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection inputConnection = super.onCreateInputConnection(outAttrs);
        if (outAttrs != null) {
            // remove other IME_ACTION_*
            outAttrs.imeOptions &= ~EditorInfo.IME_ACTION_GO;
            outAttrs.imeOptions &= ~EditorInfo.IME_ACTION_SEARCH;
            outAttrs.imeOptions &= ~EditorInfo.IME_ACTION_SEND;
            outAttrs.imeOptions &= ~EditorInfo.IME_ACTION_DONE;
            outAttrs.imeOptions &= ~EditorInfo.IME_ACTION_NONE;
            // add IME_ACTION_NEXT instead
            outAttrs.imeOptions |= EditorInfo.IME_ACTION_NEXT;
        }
        return inputConnection;
    }
}