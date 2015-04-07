package me.justup.upme.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import me.justup.upme.R;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.entity.BaseHttpQueryEntity;
import me.justup.upme.entity.ProductHtmlEntity;
import me.justup.upme.entity.ProductsJSQuery;
import me.justup.upme.entity.ProductsOrderCreateQuery;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.http.HttpIntentService;
import me.justup.upme.view.CustomWebView;
import me.justup.upme.view.LiveWebView;

import static me.justup.upme.db.DBHelper.PRODUCTS_HTML_CONTENT;
import static me.justup.upme.db.DBHelper.PRODUCTS_HTML_SERVER_ID;
import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;

public class ProductHTMLFragment extends Fragment {

    private static final String TAG = makeLogTag(NewsItemFragment.class);
    private SQLiteDatabase database;
    private BroadcastReceiver mProductHtmlReceiver;
    private static final String ARG_PRODUCT_ID = "product_id";
    private static final String ARG_PRODUCT_NAME = "product_name";
    private static final String ARG_PRODUCT_PATH = "product_path";
    private int currentProductId;
    private String currentProductName;
    private String currentProductPath;
    private ProductHtmlEntity mProductHtmlEntity;
    private CustomWebView webView;
    private FrameLayout mProgressBar;

    public static ProductHTMLFragment newInstance(int prodyctId, String nameProduct, String namePath) {
        ProductHTMLFragment fragment = new ProductHTMLFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT_ID, prodyctId);
        args.putString(ARG_PRODUCT_NAME, nameProduct);
        args.putSerializable(ARG_PRODUCT_PATH, namePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = DBAdapter.getInstance().openDatabase();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            currentProductId = bundle.getInt(ARG_PRODUCT_ID);
            currentProductName = bundle.getString(ARG_PRODUCT_NAME);
            currentProductPath = bundle.getString(ARG_PRODUCT_PATH);
        }

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
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mProductHtmlReceiver, new IntentFilter(DBAdapter.PRODUCT_HTML_SQL_BROADCAST_INTENT));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mProductHtmlReceiver);
    }

    @Override
    public void onDestroy() {
        DBAdapter.getInstance().closeDatabase();
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_html, container, false);

        ((TextView) view.findViewById(R.id.web_prod_category_top_title_main_textView)).setText(currentProductPath);
        ((TextView) view.findViewById(R.id.web_prod_category_top_title_textView)).setText(currentProductName);
        mProgressBar = (FrameLayout) view.findViewById(R.id.base_progressBar);

        webView = (CustomWebView) view.findViewById(R.id.product_html_webview);
        webView.addJavascriptInterface(new AndroidBridge(getActivity()), "android");
        webView.getSettings().setJavaScriptEnabled(true);

        // Other options
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setUseWideViewPort(true);
//        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
//        webView.setScrollbarFadingEnabled(false);
//        webView.getSettings().setBuiltInZoomControls(true);

        webView.setWebViewClient(new WebViewClient() {
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

        Button mCloseButton = (Button) view.findViewById(R.id.product_html_close_button);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });
        updateProduct();

        return view;
    }


    private void updateProduct() {
        mProductHtmlEntity = fillProductHtmlFromDB(currentProductId);
        LOGE("pavel", mProductHtmlEntity.getHtmlContent() + " " + mProductHtmlEntity.getId());
        if (mProductHtmlEntity != null) {
            updateWebView(mProductHtmlEntity.getHtmlContent());
        }

    }

    private void updateWebView(String content) {
        webView.loadDataWithBaseURL("", content, "text/html", "UTF-8", "");
    }

    private ProductHtmlEntity fillProductHtmlFromDB(int id) {
        String selectQueryBrands = "SELECT * FROM products_html_table WHERE server_id=" + id;
        Cursor cursorProductHtml = database.rawQuery(selectQueryBrands, null);
        ProductHtmlEntity productHtmlEntity = new ProductHtmlEntity();
        if (cursorProductHtml != null && cursorProductHtml.moveToFirst()) {
            productHtmlEntity.setId(cursorProductHtml.getInt(cursorProductHtml.getColumnIndex(PRODUCTS_HTML_SERVER_ID)));
            productHtmlEntity.setHtmlContent(cursorProductHtml.getString(cursorProductHtml.getColumnIndex(PRODUCTS_HTML_CONTENT)));
        }
        if (cursorProductHtml != null) {
            cursorProductHtml.close();
        }
        return productHtmlEntity;
    }

    private void sendOrderQuery(String paramColor, String paramQty) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("color", paramColor);
        data.put("qty", paramQty);
        ProductsOrderCreateQuery productsOrderCreateQuery = new ProductsOrderCreateQuery();
        productsOrderCreateQuery.params.product_id = currentProductId;
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

            ProductsJSQuery productsJSQuery = new ProductsJSQuery();
            productsJSQuery.method = method;
            productsJSQuery.params.product_id = strProductId;
            productsJSQuery.params.data = map;
            ApiWrapper.query(productsJSQuery, new OnQueryResponse());

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
            LOGD(TAG, "content --> " + content);
            JSONObject jsonObject = null;
            String hashStr = "";
            try {
                jsonObject = new JSONObject(content);
                JSONObject jsonResult = (JSONObject) jsonObject.get("result");
                hashStr = jsonResult.getString("hash");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            callJavaScriptFunctionBack(hashStr);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Заказ № " + hashStr + " успешно создан")
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    }).create().show();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
        }
    }

    public void callJavaScriptFunctionBack(final String str) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                webView.loadUrl("javascript:jsCallback(\"" + str + "\")");
            }
        });
    }


}