package me.justup.upme.db_upme.transfers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

import me.justup.upme.api_rpc.response_object.sub_object.BrandCategories;
import me.justup.upme.db_upme.MetaData;
import me.justup.upme.db_upme.SyncAdapterMetaData;
import me.justup.upme.entity.ProductsGetAllCategoriesResponse;

import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_BRAND_ID;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_DESCRIPTION;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_ID;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_NAME;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_CATEGORY_ID;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_FULL_DESCRIPTION;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_IMAGE;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_NAME;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_SERVER_ID;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_SHORT_DESCRIPTION;
import static me.justup.upme.utils.LogUtils.LOGD;

public class TransferActionBrandCategories implements SyncAdapterMetaData {
    private static final String TAG = TransferActionBrandCategories.class.getSimpleName();

    private Uri uriBrandCategories = null;
    private Uri uriOneBrandCategories = null;
    private Uri uriListBrandCategories = null;

    public TransferActionBrandCategories(CharSequence name) {
        uriBrandCategories = Uri.parse("content://" + MetaData.AUTHORITY_PRODUCT_BRAND + "/" + ProductBrandConstance.URI_PATH2_PRODUCT_BRAND.replace("#", name));
        uriOneBrandCategories = Uri.parse("content://" + MetaData.AUTHORITY_PRODUCT_BRAND + "/" + ProductBrandConstance.URI_PATH2_PRODUCT_BRAND.replace("#", name) + "/" + 2);
        uriListBrandCategories = Uri.parse("content://" + MetaData.AUTHORITY_PRODUCT_BRAND + "/" + ProductBrandConstance.URI_PATH2_PRODUCT_BRAND.replace("#", name) + "/" + 1);
    }

    public void insertBrandCategories(Context context, BrandCategories brandCategories) {
        ContentValues contentValues = brandCategoriesToContentValues(brandCategories);

        if(contentValues!=null) {
            context.getContentResolver().insert(uriOneBrandCategories, contentValues);
        }
    }

    public void insertBrandCategoriesOld(Context context, ProductsGetAllCategoriesResponse.Result.BrandCategory brandCategories) {
        ContentValues contentValues = brandCategoriesToContentValues(brandCategories);

        if(contentValues!=null) {
            context.getContentResolver().insert(uriOneBrandCategories, contentValues);
        }
    }

    public void insertListBrandCategories(Context context, ArrayList<BrandCategories> brandCategoriesArrayList) {
        ContentValues[] contentValues = brandCategoriesListToContentValues(brandCategoriesArrayList);

        if(contentValues!=null) {
            context.getContentResolver().bulkInsert(uriListBrandCategories, contentValues);
        }
    }

    public Cursor getCursorOfProductsBrandByCategoriesId(Context context, int categoriesId){
        String sql = "SELECT * FROM " + ProductBrandConstance.TABLE_PRODUCT_BRAND + " WHERE category_id = " + categoriesId;

        return context.getContentResolver().query(uriBrandCategories, null, sql, null, null);
    }

    /**
     * Convert BrandCategories to ContentValues
     */
    public ContentValues brandCategoriesToContentValues(@NonNull BrandCategories brandCategories) {
        ContentValues cv = new ContentValues();

        cv.put(PRODUCTS_BRAND_CATEGORIES_SERVER_ID, brandCategories.getId());
        cv.put(PRODUCTS_BRAND_CATEGORIES_NAME, brandCategories.getName());
        cv.put(PRODUCTS_BRAND_CATEGORIES_IMAGE, brandCategories.getImage());
        cv.put(PRODUCTS_BRAND_CATEGORIES_SHORT_DESCRIPTION, brandCategories.getShortDescription());
        cv.put(PRODUCTS_BRAND_CATEGORIES_FULL_DESCRIPTION, brandCategories.getFullDescription());
        cv.put(PRODUCTS_BRAND_CATEGORIES_CATEGORY_ID, brandCategories.getCategoryId());
        cv.put(PRODUCTS_BRAND_CATEGORIES_BRAND_ID, brandCategories.getBrandId());
        cv.put(PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_ID, brandCategories.getBrand().getId());
        cv.put(PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_NAME, brandCategories.getBrand().getName());
        cv.put(PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_DESCRIPTION, brandCategories.getBrand().getDescription());

        LOGD(TAG, "ContentValues : " + cv.toString());

        return cv;
    }

    /**
     * Convert ProductsGetAllCategoriesResponse.Result.BrandCategory to ContentValues
     */
    public ContentValues brandCategoriesToContentValues(@NonNull ProductsGetAllCategoriesResponse.Result.BrandCategory brandCategories) {
        ContentValues cv = new ContentValues();

        cv.put(PRODUCTS_BRAND_CATEGORIES_SERVER_ID, brandCategories.id);
        cv.put(PRODUCTS_BRAND_CATEGORIES_NAME, brandCategories.name);
        cv.put(PRODUCTS_BRAND_CATEGORIES_IMAGE, brandCategories.image);
        cv.put(PRODUCTS_BRAND_CATEGORIES_SHORT_DESCRIPTION, brandCategories.shortDescription);
        cv.put(PRODUCTS_BRAND_CATEGORIES_FULL_DESCRIPTION, brandCategories.fullDescription);
        cv.put(PRODUCTS_BRAND_CATEGORIES_CATEGORY_ID, brandCategories.categoryId);
        cv.put(PRODUCTS_BRAND_CATEGORIES_BRAND_ID, brandCategories.brandId);
        cv.put(PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_ID, brandCategories.brand.id);
        cv.put(PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_NAME, brandCategories.brand.name);
        cv.put(PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_DESCRIPTION, brandCategories.brand.description);

        LOGD(TAG, "ContentValues : " + cv.toString());

        return cv;
    }

    /**
     * Convert ArrayList<BrandCategories> to ContentValues[]
     */
    public ContentValues[] brandCategoriesListToContentValues(@NonNull ArrayList<BrandCategories> brandObjectArrayList) {
        // Init List ContentValues
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<ContentValues>(brandObjectArrayList.size());

        for (int i = 0; i < brandObjectArrayList.size(); i++) {
            // Get One Object
            BrandCategories brandCategories = brandObjectArrayList.get(i);

            ContentValues cv = new ContentValues();
            cv.put(PRODUCTS_BRAND_CATEGORIES_SERVER_ID, brandCategories.getId());
            cv.put(PRODUCTS_BRAND_CATEGORIES_NAME, brandCategories.getName());
            cv.put(PRODUCTS_BRAND_CATEGORIES_IMAGE, brandCategories.getImage());
            cv.put(PRODUCTS_BRAND_CATEGORIES_SHORT_DESCRIPTION, brandCategories.getShortDescription());
            cv.put(PRODUCTS_BRAND_CATEGORIES_FULL_DESCRIPTION, brandCategories.getFullDescription());
            cv.put(PRODUCTS_BRAND_CATEGORIES_CATEGORY_ID, brandCategories.getCategoryId());
            cv.put(PRODUCTS_BRAND_CATEGORIES_BRAND_ID, brandCategories.getBrandId());
            cv.put(PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_ID, brandCategories.getBrand().getId());
            cv.put(PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_NAME, brandCategories.getBrand().getName());
            cv.put(PRODUCTS_BRAND_CATEGORIES_BRAND_ITEM_DESCRIPTION, brandCategories.getBrand().getDescription());

            // Add to List
            contentValuesArrayList.add(cv);
        }

        ContentValues[] contentValues = contentValuesArrayList.toArray(new ContentValues[contentValuesArrayList.size()]);
        LOGD(TAG, "ContentValues : " + Arrays.toString(contentValues));

        return contentValues;
    }
}
