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
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import me.justup.upme.R;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.entity.BaseHttpQueryEntity;
import me.justup.upme.entity.ProductHtmlEntity;
import me.justup.upme.entity.ProductsOrderCreateQuery;
import me.justup.upme.http.HttpIntentService;
import me.justup.upme.utils.AppContext;

import static me.justup.upme.db.DBHelper.PRODUCTS_HTML_CONTENT;
import static me.justup.upme.db.DBHelper.PRODUCTS_HTML_SERVER_ID;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;

public class ProductHTMLFragment extends Fragment {

    private static final String TAG = makeLogTag(NewsItemFragment.class);
    private SQLiteDatabase database;
    private BroadcastReceiver mProductHtmlReceiver;
    private static final String ARG_PRODUCT_ID = "product_id";
    private int currentProductId;
    private ProductHtmlEntity mProductHtmlEntity;
    private WebView webView;

    public static ProductHTMLFragment newInstance(int prodyctId) {
        ProductHTMLFragment fragment = new ProductHTMLFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT_ID, prodyctId);
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
        LocalBroadcastManager.getInstance(ProductHTMLFragment.this.getActivity())
                .registerReceiver(mProductHtmlReceiver, new IntentFilter(DBAdapter.PRODUCT_HTML_SQL_BROADCAST_INTENT));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(ProductHTMLFragment.this.getActivity()).unregisterReceiver(mProductHtmlReceiver);
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

        webView = (WebView) view.findViewById(R.id.product_html_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new AndroidBridge(getActivity()), "android");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        Button mCloseButton = (Button) view.findViewById(R.id.product_html_close_button);
        //mCloseButton.setVisibility(View.INVISIBLE);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View view) {
                LocalBroadcastManager.getInstance(ProductHTMLFragment.this.getActivity()).unregisterReceiver(mProductHtmlReceiver);
                getActivity().getFragmentManager().beginTransaction().remove(ProductHTMLFragment.this).commit();
                //sendOrderQuery();
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
        Intent intent = new Intent(AppContext.getAppContext(), HttpIntentService.class);
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
        public void submit(final String json) { // must be final
            handler.post(new Runnable() {
                public void run() {
                    try {
                        JSONObject jsonObject = new JSONObject(json.toString());

                        String strColor = jsonObject.getString("color");
                        String strQty = jsonObject.getString("qty");
                        Log.d("TAG_submit", "strColor " + strColor + " strQty " + strQty);
                        sendOrderQuery(strColor, strQty);

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Заказ принят")
                                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) { }
                                }).create().show();
                        
                        LocalBroadcastManager.getInstance(ProductHTMLFragment.this.getActivity()).unregisterReceiver(mProductHtmlReceiver);
                        getActivity().getFragmentManager().beginTransaction().remove(ProductHTMLFragment.this).commit();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}