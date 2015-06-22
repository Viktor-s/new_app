package me.justup.upme.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import me.justup.upme.JustUpApplication;
import me.justup.upme.R;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.entity.BaseHttpQueryEntity;
import me.justup.upme.entity.ProductHtmlEntity;
import me.justup.upme.entity.ProductsJSQuery;
import me.justup.upme.entity.ProductsOrderCreateQuery;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.http.HttpIntentService;
import me.justup.upme.view.CustomWebView;

import static me.justup.upme.db.DBHelper.PRODUCTS_HTML_CONTENT;
import static me.justup.upme.db.DBHelper.PRODUCTS_HTML_SERVER_ID;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;

public class ProductHTMLFragment extends Fragment {
    private static final String TAG = makeLogTag(NewsItemFragment.class);

    private static final String ARG_PRODUCT_ID = "product_id";
    private static final String ARG_PRODUCT_NAME = "product_name";
    private static final String ARG_PRODUCT_PATH = "product_path";

    private BroadcastReceiver mProductHtmlReceiver = null;
    private int mCurrentProductId;
    private String mCurrentProductName;
    private String mCurrentProductPath;
    private ProductHtmlEntity mProductHtmlEntity = null;
    private CustomWebView mWebView = null;
    private FrameLayout mProgressBar = null;

    private View mContentView = null;

    // Instance
    public static ProductHTMLFragment newInstance() {
        return new ProductHTMLFragment();
    }

    public static ProductHTMLFragment newInstance(int productId, String nameProduct, String namePath) {
        ProductHTMLFragment fragment = new ProductHTMLFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT_ID, productId);
        args.putString(ARG_PRODUCT_NAME, nameProduct);
        args.putSerializable(ARG_PRODUCT_PATH, namePath);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = super.onCreateView(inflater, container, savedInstanceState);

        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_product_html, container, false);
        }

        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mCurrentProductId = bundle.getInt(ARG_PRODUCT_ID);
            mCurrentProductName = bundle.getString(ARG_PRODUCT_NAME);
            mCurrentProductPath = bundle.getString(ARG_PRODUCT_PATH);
        }

        // Init UI
        if (getActivity() != null) {
            initUI();
        }
    }

    private void initUI(){
        ((TextView) mContentView.findViewById(R.id.web_prod_category_top_title_main_textView)).setText(mCurrentProductPath);
        ((TextView) mContentView.findViewById(R.id.web_prod_category_top_title_textView)).setText(mCurrentProductName);
        mProgressBar = (FrameLayout) mContentView.findViewById(R.id.base_progressBar);

        mWebView = (CustomWebView) mContentView.findViewById(R.id.product_html_webview);
        mWebView.addJavascriptInterface(new AndroidBridge(getActivity()), "android");
        mWebView.getSettings().setJavaScriptEnabled(true);

        // Other options
//        mWebView.getSettings().setLoadWithOverviewMode(true);
//        mWebView.getSettings().setUseWideViewPort(true);
//        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
//        mWebView.setScrollbarFadingEnabled(false);
//        mWebView.getSettings().setBuiltInZoomControls(true);

        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onLoadResource(WebView view, String url) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.GONE);
            }
        });

        Button mCloseButton = (Button) mContentView.findViewById(R.id.product_html_close_button);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        updateProduct();
    }

    @Override
    public void onResume() {
        super.onResume();
        mProductHtmlReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                updateProduct();
            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mProductHtmlReceiver, new IntentFilter(DBAdapter.PRODUCT_HTML_SQL_BROADCAST_INTENT));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mProductHtmlReceiver);
    }

    private void updateProduct() {
        mProductHtmlEntity = fillProductHtmlFromDB(mCurrentProductId);
        LOGE(TAG, mProductHtmlEntity.getHtmlContent() + " " + mProductHtmlEntity.getId());
        if (mProductHtmlEntity != null) {
            updateWebView(mProductHtmlEntity.getHtmlContent());
        }

    }

    private void updateWebView(String content) {
        mWebView.loadDataWithBaseURL("", content, "text/html", "UTF-8", "");
    }

    private ProductHtmlEntity fillProductHtmlFromDB(int id) {
        Cursor cursorProductHtml = JustUpApplication.getApplication().getTransferActionProductHTML().getCursorOfProductHTMLByServerId(getActivity().getApplicationContext(), id);
        ProductHtmlEntity productHtmlEntity = new ProductHtmlEntity();
        if (cursorProductHtml != null && cursorProductHtml.moveToFirst()) {
            productHtmlEntity.setId(cursorProductHtml.getInt(cursorProductHtml.getColumnIndex(PRODUCTS_HTML_SERVER_ID)));
            productHtmlEntity.setHtmlContent(cursorProductHtml.getString(cursorProductHtml.getColumnIndex(PRODUCTS_HTML_CONTENT)));
        }

        return productHtmlEntity;
    }

    private void sendOrderQuery(String paramColor, String paramQty) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("color", paramColor);
        data.put("qty", paramQty);
        ProductsOrderCreateQuery productsOrderCreateQuery = new ProductsOrderCreateQuery();
        productsOrderCreateQuery.params.product_id = mCurrentProductId;
        productsOrderCreateQuery.params.data = data;
        startHttpIntent(productsOrderCreateQuery, HttpIntentService.PRODUCTS_CREATE_ORDER);
    }

    public void startHttpIntent(BaseHttpQueryEntity entity, int dbTable) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(HttpIntentService.HTTP_INTENT_QUERY_EXTRA, entity);
        bundle.putInt(HttpIntentService.HTTP_INTENT_PART_EXTRA, dbTable);
        Intent intent = new Intent(getActivity().getApplicationContext(), HttpIntentService.class);
        getActivity().startService(intent.putExtras(bundle));
    }

    public class AndroidBridge {
        private final String TAG = AndroidBridge.class.getSimpleName();

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
            Log.i(TAG, "Json : " + json + ", Method : " + method);

            JSONObject jsonObject = null;
            String strData = "";
            String strProductId = "";
            try {
                jsonObject = new JSONObject(json);
                strData = jsonObject.getString("data");
                strProductId = jsonObject.getString("product_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Gson gson = new Gson();
            Type stringStringMap = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> map = gson.fromJson(strData, stringStringMap);

            Log.i(TAG, "Data : " + strData + ", Product Id : " + strProductId);

            ProductsJSQuery productsJSQuery = new ProductsJSQuery();
            productsJSQuery.method = method;
            productsJSQuery.params.product_id = strProductId;
            productsJSQuery.params.data = map;

            Log.i(TAG, "ProductsJSQuery : " + productsJSQuery.toString());

            ApiWrapper.query(productsJSQuery, new OnQueryResponse());

        }

        @JavascriptInterface
        public void closePage() {
            getFragmentManager().popBackStack();
        }
    }

    private class OnQueryResponse extends AsyncHttpResponseHandler {
        private final String TAG = OnQueryResponse.class.getSimpleName();

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            Log.i(TAG, "Content : " + content);

            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONObject jsonResult = (JSONObject) jsonObject.get("result");
                String hashStr = jsonResult.getString("hash");

                callJavaScriptFunctionBack(hashStr);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Заказ № " + hashStr + " успешно создан")
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        }).create().show();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONObject jsonResult = (JSONObject) jsonObject.get("error");
                String code = jsonResult.getString("code");
                String data = jsonResult.getString("data");

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Error code : " + code + ". Data : " + data)
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        }).create().show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
        }
    }

    public void callJavaScriptFunctionBack(final String str) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                mWebView.loadUrl("javascript:jsCallback(\"" + str + "\")");
            }
        });
    }

}