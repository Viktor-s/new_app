package me.justup.upme.db_upme.transfers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

import me.justup.upme.api_rpc.response_object.ProductsObject;
import me.justup.upme.db_upme.MetaData;
import me.justup.upme.db_upme.SyncAdapterMetaData;
import me.justup.upme.entity.GetProductHtmlByIdResponse;

import static me.justup.upme.db.DBHelper.PRODUCTS_HTML_CONTENT;
import static me.justup.upme.db.DBHelper.PRODUCTS_HTML_SERVER_ID;
import static me.justup.upme.utils.LogUtils.LOGD;

public class TransferActionProductHTML implements SyncAdapterMetaData {
    private static final String TAG = TransferActionProductsCategories.class.getSimpleName();

    private Uri uriProductHTML = null;
    private Uri uriOneProductHTML = null;
    private Uri uriListProductHTML = null;

    public TransferActionProductHTML(CharSequence name) {
        uriProductHTML = Uri.parse("content://" + MetaData.AUTHORITY_PRODUCT_HTML + "/" + ProductHTMLConstance.URI_PATH2_PRODUCT_HTML.replace("#", name));
        uriOneProductHTML = Uri.parse("content://" + MetaData.AUTHORITY_PRODUCT_HTML + "/" + ProductHTMLConstance.URI_PATH2_PRODUCT_HTML.replace("#", name) + "/" + 2);
        uriListProductHTML = Uri.parse("content://" + MetaData.AUTHORITY_PRODUCT_HTML + "/" + ProductHTMLConstance.URI_PATH2_PRODUCT_HTML.replace("#", name) + "/" + 1);
    }

    public void insertProductHtml(Activity activity, ProductsObject productsObject) {
        ContentValues contentValues = productHTMLToContentValues(productsObject);

        if(contentValues!=null) {
            activity.getContentResolver().insert(uriOneProductHTML, contentValues);
        }
    }

    public void insertProductHtmlOld(Context context, GetProductHtmlByIdResponse productsObject) {
        ContentValues contentValues = productHTMLToContentValues(productsObject);

        if(contentValues!=null) {
            context.getContentResolver().insert(uriOneProductHTML, contentValues);
        }
    }

    public void insertListProductHtml(Activity activity, ArrayList<ProductsObject> productsObjectArrayList) {
        ContentValues[] contentValues = productHTMLListToContentValues(productsObjectArrayList);

        if(contentValues!=null) {
            activity.getContentResolver().bulkInsert(uriListProductHTML, contentValues);
        }
    }

    public Cursor getCursorOfProductHTMLByServerId(Context context, int serverId){
        String sql = "SELECT * FROM " + ProductHTMLConstance.TABLE_PRODUCT_HTML + " WHERE server_id = " + serverId;

        return context.getContentResolver().query(uriProductHTML, null, sql, null, null);
    }

    /**
     * Convert ProductsObject to ContentValues
     */
    public ContentValues productHTMLToContentValues(@NonNull ProductsObject productsObject) {
        ContentValues cv = new ContentValues();

        cv.put(PRODUCTS_HTML_SERVER_ID, productsObject.getId());
        cv.put(PRODUCTS_HTML_CONTENT, productsObject.getHtml());

        LOGD(TAG, "ContentValues : " + cv.toString());

        return cv;
    }

    /**
     * Convert GetProductHtmlByIdResponse to ContentValues
     */
    public ContentValues productHTMLToContentValues(@NonNull GetProductHtmlByIdResponse productsObject) {
        ContentValues cv = new ContentValues();

        cv.put(PRODUCTS_HTML_SERVER_ID, productsObject.result.id);
        cv.put(PRODUCTS_HTML_CONTENT, productsObject.result.html);

        LOGD(TAG, "ContentValues : " + cv.toString());

        return cv;
    }

    /**
     * Convert ArrayList<ProductsObject> to ContentValues[]
     */
    public ContentValues[] productHTMLListToContentValues(@NonNull ArrayList<ProductsObject> productsObjectArrayList) {
        // Init List ContentValues
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<ContentValues>(productsObjectArrayList.size());

        for (int i = 0; i < productsObjectArrayList.size(); i++) {
            // Get One Object
            ProductsObject productsObject = productsObjectArrayList.get(i);

            ContentValues cv = new ContentValues();
            cv.put(PRODUCTS_HTML_SERVER_ID, productsObject.getId());
            cv.put(PRODUCTS_HTML_CONTENT, productsObject.getHtml());

            // Add to List
            contentValuesArrayList.add(cv);
        }

        ContentValues[] contentValues = contentValuesArrayList.toArray(new ContentValues[contentValuesArrayList.size()]);
        LOGD(TAG, "ContentValues : " + Arrays.toString(contentValues));

        return contentValues;
    }
}
