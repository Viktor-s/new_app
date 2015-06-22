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
import me.justup.upme.entity.ProductsGetAllCategoriesResponse;

import static me.justup.upme.db.DBHelper.PRODUCTS_CATEGORIES_NAME;
import static me.justup.upme.db.DBHelper.PRODUCTS_CATEGORIES_SERVER_ID;
import static me.justup.upme.utils.LogUtils.LOGD;

public class TransferActionProductsCategories implements SyncAdapterMetaData {
    private static final String TAG = TransferActionProductsCategories.class.getSimpleName();

    private Uri uriProductsCategories = null;
    private Uri uriOneProductsCategories = null;
    private Uri uriListProductCategories = null;

    public TransferActionProductsCategories(CharSequence name) {
        uriProductsCategories = Uri.parse("content://" + MetaData.AUTHORITY_PRODUCT_CATEGORIES + "/" + ProductCategoriesConstance.URI_PATH2_PRODUCT_CATEGORIES.replace("#", name));
        uriOneProductsCategories = Uri.parse("content://" + MetaData.AUTHORITY_PRODUCT_CATEGORIES + "/" + ProductCategoriesConstance.URI_PATH2_PRODUCT_CATEGORIES.replace("#", name) + "/" + 2);
        uriListProductCategories = Uri.parse("content://" + MetaData.AUTHORITY_PRODUCT_CATEGORIES + "/" + ProductCategoriesConstance.URI_PATH2_PRODUCT_CATEGORIES.replace("#", name) + "/" + 1);
    }

    public void insertProductCategories(Activity activity, ProductsObject productsObject) {
        ContentValues contentValues = productCategoriesToContentValues(productsObject);

        if(contentValues!=null) {
            activity.getContentResolver().insert(uriOneProductsCategories, contentValues);
        }
    }

    public void insertProductCategoriesOld(Context context, ProductsGetAllCategoriesResponse.Result result) {
        ContentValues contentValues = productCategoriesToContentValuesOld(result);

        if(contentValues!=null) {
            context.getContentResolver().insert(uriOneProductsCategories, contentValues);
        }
    }

    public void insertListProductCategories(Activity activity, ArrayList<ProductsObject> productsObjectArrayList) {
        ContentValues[] contentValues = productCategoriesListToContentValues(productsObjectArrayList);

        if(contentValues!=null) {
            activity.getContentResolver().bulkInsert(uriListProductCategories, contentValues);
        }
    }

    public Cursor getCursorOfProductsCategories(Context context){
        String sql = "SELECT * FROM " + ProductCategoriesConstance.TABLE_PRODUCT_CATEGORIES;

        return context.getContentResolver().query(uriProductsCategories, null, sql, null, null);
    }

    /**
     * Convert ProductsObject to ContentValues
     */
    public ContentValues productCategoriesToContentValues(@NonNull ProductsObject productsObject) {
        ContentValues cv = new ContentValues();

        cv.put(PRODUCTS_CATEGORIES_SERVER_ID, productsObject.getId());
        cv.put(PRODUCTS_CATEGORIES_NAME, productsObject.getName());

        LOGD(TAG, "ContentValues : " + cv.toString());

        return cv;
    }

    /**
     * Convert ProductsGetAllCategoriesResponse.Result to ContentValues
     */
    public ContentValues productCategoriesToContentValuesOld(@NonNull ProductsGetAllCategoriesResponse.Result result) {
        ContentValues cv = new ContentValues();

        cv.put(PRODUCTS_CATEGORIES_SERVER_ID, result.id);
        cv.put(PRODUCTS_CATEGORIES_NAME, result.name);

        LOGD(TAG, "ContentValues : " + cv.toString());

        return cv;
    }

    /**
     * Convert ArrayList<ProductsObject> to ContentValues[]
     */
    public ContentValues[] productCategoriesListToContentValues(@NonNull ArrayList<ProductsObject> productsObjectArrayList) {
        // Init List ContentValues
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<ContentValues>(productsObjectArrayList.size());

        for (int i = 0; i < productsObjectArrayList.size(); i++) {
            // Get One Object
            ProductsObject productsObject = productsObjectArrayList.get(i);

            ContentValues cv = new ContentValues();
            cv.put(PRODUCTS_CATEGORIES_SERVER_ID, productsObject.getId());
            cv.put(PRODUCTS_CATEGORIES_NAME, productsObject.getName());

            // Add to List
            contentValuesArrayList.add(cv);
        }

        ContentValues[] contentValues = contentValuesArrayList.toArray(new ContentValues[contentValuesArrayList.size()]);
        LOGD(TAG, "ContentValues : " + Arrays.toString(contentValues));

        return contentValues;
    }
}
