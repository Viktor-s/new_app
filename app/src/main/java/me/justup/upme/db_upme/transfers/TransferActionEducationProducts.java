package me.justup.upme.db_upme.transfers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

import me.justup.upme.api_rpc.response_object.EducationObject;
import me.justup.upme.db_upme.MetaData;
import me.justup.upme.db_upme.SyncAdapterMetaData;
import me.justup.upme.entity.EducationGetProgramsResponse;

import static me.justup.upme.db.DBHelper.EDUCATION_PRODUCTS_NAME;
import static me.justup.upme.db.DBHelper.EDUCATION_PRODUCTS_NAME_LOWER_CASE;
import static me.justup.upme.db.DBHelper.EDUCATION_PRODUCTS_SERVER_ID;
import static me.justup.upme.utils.LogUtils.LOGD;

public class TransferActionEducationProducts implements SyncAdapterMetaData {
    private static final String TAG = TransferActionProductsCategories.class.getSimpleName();

    private Uri uriEducationProducts = null;
    private Uri uriOneEducationProduct = null;
    private Uri uriListEducationProducts = null;

    public TransferActionEducationProducts(CharSequence name) {
        uriEducationProducts = Uri.parse("content://" + MetaData.AUTHORITY_EDUCATION_PRODUCT + "/" + EducationProductModuleConstance.URI_PATH2_EDUCATION_PRODUCT_MODULE.replace("#", name));
        uriOneEducationProduct = Uri.parse("content://" + MetaData.AUTHORITY_EDUCATION_PRODUCT + "/" + EducationProductModuleConstance.URI_PATH2_EDUCATION_PRODUCT_MODULE.replace("#", name) + "/" + 2);
        uriListEducationProducts = Uri.parse("content://" + MetaData.AUTHORITY_EDUCATION_PRODUCT + "/" + EducationProductModuleConstance.URI_PATH2_EDUCATION_PRODUCT_MODULE.replace("#", name) + "/" + 1);
    }

    public void insertEducationProduct(Activity activity, EducationObject educationObject) {
        ContentValues contentValues = educationObjectToContentValues(educationObject);

        if(contentValues!=null) {
            activity.getContentResolver().insert(uriOneEducationProduct, contentValues);
        }
    }

    public void insertEducationProductOld(Context context, EducationGetProgramsResponse.Result educationObject) {
        ContentValues contentValues = educationObjectToContentValues(educationObject);

        if(contentValues!=null) {
            context.getContentResolver().insert(uriOneEducationProduct, contentValues);
        }
    }

    public void insertListEducationProducts(Activity activity, ArrayList<EducationObject> educationObjectArrayList) {
        ContentValues[] contentValues = educationObjectListToContentValues(educationObjectArrayList);

        if(contentValues!=null) {
            activity.getContentResolver().bulkInsert(uriListEducationProducts, contentValues);
        }
    }

    public Cursor getCursorOfEducationProducts(Context context){
        String sql = "SELECT * FROM " + EducationProductConstance.TABLE_EDUCATION_PRODUCT;

        return context.getContentResolver().query(uriEducationProducts, null, sql, null, null);
    }

    public Cursor getCursorOfEducationProductsWithSearch(Context context, String search){
        String sql = "SELECT * FROM "
                + EducationProductConstance.TABLE_EDUCATION_PRODUCT + " where " + "name_lc" + " like '%" + search
                + "%'";

        return context.getContentResolver().query(uriEducationProducts, null, sql, null, null);
    }

    /**
     * Convert EducationObject to ContentValues
     */
    public ContentValues educationObjectToContentValues(@NonNull EducationObject educationObject) {
        ContentValues cv = new ContentValues();

        cv.put(EDUCATION_PRODUCTS_SERVER_ID, educationObject.getId());
        cv.put(EDUCATION_PRODUCTS_NAME, educationObject.getName());
        cv.put(EDUCATION_PRODUCTS_NAME_LOWER_CASE, educationObject.getName().toLowerCase());

        LOGD(TAG, "ContentValues : " + cv.toString());

        return cv;
    }

    /**
     * Convert EducationGetProgramsResponse.Result to ContentValues
     */
    public ContentValues educationObjectToContentValues(@NonNull EducationGetProgramsResponse.Result educationObject) {
        ContentValues cv = new ContentValues();

        cv.put(EDUCATION_PRODUCTS_SERVER_ID, educationObject.id);
        cv.put(EDUCATION_PRODUCTS_NAME, educationObject.name);
        cv.put(EDUCATION_PRODUCTS_NAME_LOWER_CASE, educationObject.name.toLowerCase());

        LOGD(TAG, "ContentValues : " + cv.toString());

        return cv;
    }

    /**
     * Convert ArrayList<EducationObject> to ContentValues[]
     */
    public ContentValues[] educationObjectListToContentValues(@NonNull ArrayList<EducationObject> educationObjectArrayList) {
        // Init List ContentValues
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<ContentValues>(educationObjectArrayList.size());

        for (int i = 0; i < educationObjectArrayList.size(); i++) {
            // Get One Object
            EducationObject educationObject = educationObjectArrayList.get(i);

            ContentValues cv = new ContentValues();
            cv.put(EDUCATION_PRODUCTS_SERVER_ID, educationObject.getId());
            cv.put(EDUCATION_PRODUCTS_NAME, educationObject.getName());
            cv.put(EDUCATION_PRODUCTS_NAME_LOWER_CASE, educationObject.getName().toLowerCase());

            // Add to List
            contentValuesArrayList.add(cv);
        }

        ContentValues[] contentValues = contentValuesArrayList.toArray(new ContentValues[contentValuesArrayList.size()]);
        LOGD(TAG, "ContentValues : " + Arrays.toString(contentValues));

        return contentValues;
    }
}
