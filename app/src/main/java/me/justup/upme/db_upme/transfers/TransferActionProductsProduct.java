package me.justup.upme.db_upme.transfers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

import me.justup.upme.api_rpc.response_object.sub_object.Product;
import me.justup.upme.db_upme.MetaData;
import me.justup.upme.db_upme.SyncAdapterMetaData;
import me.justup.upme.entity.ProductsGetAllCategoriesResponse;

import static me.justup.upme.db.DBHelper.PRODUCTS_PRODUCT_BRAND_ID;
import static me.justup.upme.db.DBHelper.PRODUCTS_PRODUCT_IMAGE;
import static me.justup.upme.db.DBHelper.PRODUCTS_PRODUCT_NAME;
import static me.justup.upme.db.DBHelper.PRODUCTS_PRODUCT_SERVER_ID;
import static me.justup.upme.db.DBHelper.PRODUCTS_PRODUCT_SHORT_DESCRIPTION;
import static me.justup.upme.utils.LogUtils.LOGD;

public class TransferActionProductsProduct implements SyncAdapterMetaData {
    private static final String TAG = TransferActionProductsProduct.class.getSimpleName();

    private Uri uriProductsProduct = null;
    private Uri uriOneProductsProduct = null;
    private Uri uriListProductsProduct = null;

    public TransferActionProductsProduct(CharSequence name) {
        uriProductsProduct = Uri.parse("content://" + MetaData.AUTHORITY_PRODUCTS_PRODUCT + "/" + ProductsProductConstance.URI_PATH2_PRODUCTS_PRODUCT.replace("#", name));
        uriOneProductsProduct = Uri.parse("content://" + MetaData.AUTHORITY_PRODUCTS_PRODUCT + "/" + ProductsProductConstance.URI_PATH2_PRODUCTS_PRODUCT.replace("#", name) + "/" + 2);
        uriListProductsProduct = Uri.parse("content://" + MetaData.AUTHORITY_PRODUCTS_PRODUCT + "/" + ProductsProductConstance.URI_PATH2_PRODUCTS_PRODUCT.replace("#", name) + "/" + 1);
    }

    public void insertProductsProduct(Activity activity, Product product, Integer brandId) {
        ContentValues contentValues = productToContentValues(product, brandId);

        if(contentValues!=null) {
            activity.getContentResolver().insert(uriOneProductsProduct, contentValues);
        }
    }

    public void insertProductsProductOld(Context context, ProductsGetAllCategoriesResponse.Result.BrandCategory.Product product, Integer brandId) {
        ContentValues contentValues = productToContentValues(product, brandId);

        if(contentValues!=null) {
            context.getContentResolver().insert(uriOneProductsProduct, contentValues);
        }
    }

    public void insertListProductsProduct(Activity activity, ArrayList<Product> productsArrayList, Integer brandId) {
        ContentValues[] contentValues = productListToContentValues(productsArrayList, brandId);

        if(contentValues!=null) {
            activity.getContentResolver().bulkInsert(uriListProductsProduct, contentValues);
        }
    }

    public Cursor getCursorOfProductsProductByBrandId(Context context, int productBrandId){
        String sql = "SELECT * FROM " + ProductsProductConstance.TABLE_PRODUCTS_PRODUCT + " WHERE product_brand_id = " + productBrandId;

        return context.getContentResolver().query(uriProductsProduct, null, sql, null, null);
    }

    /**
     * Convert ProductsGetAllCategoriesResponse.Result.BrandCategory.Product product to ContentValues
     */
    public ContentValues productToContentValues(@NonNull ProductsGetAllCategoriesResponse.Result.BrandCategory.Product product, Integer brandId) {
        ContentValues cv = new ContentValues();

        cv.put(PRODUCTS_PRODUCT_SERVER_ID, product.id);
        if(brandId!=null) {
            cv.put(PRODUCTS_PRODUCT_BRAND_ID, String.valueOf(brandId));
        }

        cv.put(PRODUCTS_PRODUCT_NAME, product.name);
        cv.put(PRODUCTS_PRODUCT_SHORT_DESCRIPTION, product.short_description);
        cv.put(PRODUCTS_PRODUCT_IMAGE, product.img);

        LOGD(TAG, "ContentValues : " + cv.toString());

        return cv;
    }

    /**
     * Convert Product to ContentValues
     */
    public ContentValues productToContentValues(@NonNull Product product, Integer brandId) {
        ContentValues cv = new ContentValues();

        cv.put(PRODUCTS_PRODUCT_SERVER_ID, product.getId());
        if(brandId!=null) {
            cv.put(PRODUCTS_PRODUCT_BRAND_ID, String.valueOf(brandId));
        }

        cv.put(PRODUCTS_PRODUCT_NAME, product.getName());
        cv.put(PRODUCTS_PRODUCT_SHORT_DESCRIPTION, product.getShort_description());
        cv.put(PRODUCTS_PRODUCT_IMAGE, product.getImg());

        LOGD(TAG, "ContentValues : " + cv.toString());

        return cv;
    }

    /**
     * Convert ArrayList<Product> to ContentValues[]
     */
    public ContentValues[] productListToContentValues(@NonNull ArrayList<Product> productObjectArrayList, Integer brandId) {
        // Init List ContentValues
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<ContentValues>(productObjectArrayList.size());

        for (int i = 0; i < productObjectArrayList.size(); i++) {
            // Get One Object
            Product product = productObjectArrayList.get(i);

            ContentValues cv = new ContentValues();
            cv.put(PRODUCTS_PRODUCT_SERVER_ID, product.getId());
            if(brandId!=null) {
                cv.put(PRODUCTS_PRODUCT_BRAND_ID, String.valueOf(brandId));
            }

            cv.put(PRODUCTS_PRODUCT_NAME, product.getName());
            cv.put(PRODUCTS_PRODUCT_SHORT_DESCRIPTION, product.getShort_description());
            cv.put(PRODUCTS_PRODUCT_IMAGE, product.getImg());

            // Add to List
            contentValuesArrayList.add(cv);
        }

        ContentValues[] contentValues = contentValuesArrayList.toArray(new ContentValues[contentValuesArrayList.size()]);
        LOGD(TAG, "ContentValues : " + Arrays.toString(contentValues));

        return contentValues;
    }
}
