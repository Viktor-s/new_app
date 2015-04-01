package me.justup.upme.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Map;

import me.justup.upme.R;
import me.justup.upme.entity.ProductsJSQuery;
import me.justup.upme.entity.ProductsJSQueryKey;
import me.justup.upme.fragments.NewsItemFragment;
import me.justup.upme.http.ApiWrapper;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class OrderDialog extends DialogFragment {
    public static final String ORDER_DIALOG = "order_dialog";
    private static final String ORDER_DIALOG_HTML_FORM = "order_dialog_html_form";
    private static final String TAG = makeLogTag(OrderDialog.class);

    private WebView webView;

    public static OrderDialog newInstance(final String htmlString) {
        Bundle args = new Bundle();
        args.putString(ORDER_DIALOG_HTML_FORM, htmlString);

        OrderDialog fragment = new OrderDialog();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String htmlString = (String) getArguments().getSerializable(ORDER_DIALOG_HTML_FORM);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_order, null);

        setCancelable(false);

        webView = (WebView) dialogView.findViewById(R.id.dialog_product_html_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new AndroidBridge(getActivity()), "android");
        webView.loadData(htmlString, "text/html", null);

        builder.setView(dialogView).setTitle("Ордер").setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    public class AndroidBridge {
        private static final String TAG = "AndroidBridge";
        private final Handler handler = new Handler();
        private Activity activity;

        public AndroidBridge(Activity activity) {
            this.activity = activity;
        }

        public Activity getActivity() {
            return activity;
        }

        public void setActivity(Activity activity) {
            this.activity = activity;
        }

        @JavascriptInterface
        public void submit(final String json, final String method) {
            JSONObject jsonObject = null;
            String strData = "";
            String strKey = "";
            try {
                jsonObject = new JSONObject(json);
                strData = jsonObject.getString("data");
                strKey = jsonObject.getString("key");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Gson gson = new Gson();
            Type stringStringMap = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> map = gson.fromJson(strData, stringStringMap);

            ProductsJSQueryKey productsJSQueryKey = new ProductsJSQueryKey();
            productsJSQueryKey.method = method;
            productsJSQueryKey.params.key = strKey;
            productsJSQueryKey.params.data = map;

            LOGI("TAG1", " --------- productsJSQueryKey " + productsJSQueryKey.toString());
            ApiWrapper.query(productsJSQueryKey, new OnQueryResponse());
        }

        @JavascriptInterface
        public void closePage() {
            getFragmentManager().popBackStack();
        }
    }

    private class OnQueryResponse extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGD("TAG1", "onSuccess(): " + content);

//            JSONObject jsonObject = null;
//            String hashStr = "";
//            try {
//                jsonObject = new JSONObject(content);
//                JSONObject jsonResult = (JSONObject) jsonObject.get("result");
//                hashStr = jsonResult.getString("hash");
//                LOGD("TAG1", "hashStr --- " + hashStr);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            callJavaScriptFunctionBack(hashStr);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(content)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getDialog().dismiss();
                        }
                    }).create().show();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGE(TAG, "onFailure(): " + content);
        }
    }

    public void callJavaScriptFunctionBack(final String str) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                LOGD("TAG1", "callJavaScriptFunctionBack: " + str);
                webView.loadUrl("javascript:jsCallback(\""+str+"\")");
            }
        });
    }

}
