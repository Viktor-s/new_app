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
import me.justup.upme.entity.EducationGetModulesByProgramIdResponse;

import static me.justup.upme.db.DBHelper.EDUCATION_PRODUCT_MODULE_CREATED_AT;
import static me.justup.upme.db.DBHelper.EDUCATION_PRODUCT_MODULE_DESCRIPTION;
import static me.justup.upme.db.DBHelper.EDUCATION_PRODUCT_MODULE_NAME;
import static me.justup.upme.db.DBHelper.EDUCATION_PRODUCT_MODULE_PROGRAM_ID;
import static me.justup.upme.db.DBHelper.EDUCATION_PRODUCT_MODULE_SERVER_ID;
import static me.justup.upme.db.DBHelper.EDUCATION_PRODUCT_MODULE_UPDATED_AT;
import static me.justup.upme.utils.LogUtils.LOGD;

public class TransferActionEducationProductModule implements SyncAdapterMetaData {
    private static final String TAG = TransferActionEducationProductModule.class.getSimpleName();

    private Uri uriEducationProductModule = null;
    private Uri uriOneEducationProductModule = null;
    private Uri uriListEducationProductModule = null;

    public TransferActionEducationProductModule(CharSequence name) {
        uriEducationProductModule = Uri.parse("content://" + MetaData.AUTHORITY_EDUCATION_PRODUCT_MODULE + "/" + EducationProductModuleConstance.URI_PATH2_EDUCATION_PRODUCT_MODULE.replace("#", name));
        uriOneEducationProductModule = Uri.parse("content://" + MetaData.AUTHORITY_EDUCATION_PRODUCT_MODULE + "/" + EducationProductModuleConstance.URI_PATH2_EDUCATION_PRODUCT_MODULE.replace("#", name) + "/" + 2);
        uriListEducationProductModule = Uri.parse("content://" + MetaData.AUTHORITY_EDUCATION_PRODUCT_MODULE + "/" + EducationProductModuleConstance.URI_PATH2_EDUCATION_PRODUCT_MODULE.replace("#", name) + "/" + 1);
    }

    public void insertEducationProductModule(Activity activity, EducationObject educationObject) {
        ContentValues contentValues = educationObjectToContentValues(educationObject);

        if(contentValues!=null) {
            activity.getContentResolver().insert(uriOneEducationProductModule, contentValues);
        }
    }

    public void insertEducationProductModuleOld(Context context, EducationGetModulesByProgramIdResponse.Result educationObject) {
        ContentValues contentValues = educationObjectToContentValues(educationObject);

        if(contentValues!=null) {
            context.getContentResolver().insert(uriOneEducationProductModule, contentValues);
        }
    }

    public void insertListEducationProductModule(Activity activity, ArrayList<EducationObject> educationObjectArrayList) {
        ContentValues[] contentValues = educationObjectListToContentValues(educationObjectArrayList);

        if(contentValues!=null) {
            activity.getContentResolver().bulkInsert(uriListEducationProductModule, contentValues);
        }
    }

    public Cursor getCursorOfEducationProductModuleFromProgramId(Context context, int programId){
        String sql = "SELECT * FROM " + EducationProductModuleConstance.TABLE_EDUCATION_PRODUCT_MODULE + " WHERE program_id = " + programId;

        return context.getContentResolver().query(uriEducationProductModule, null, sql, null, null);
    }

    public Cursor getCursorOfEducationProductModuleFromModuleId(Context context, int moduleId){
        String sql = "SELECT * FROM " + EducationProductModuleConstance.TABLE_EDUCATION_PRODUCT_MODULE + " WHERE module_id = " + moduleId;

        return context.getContentResolver().query(uriEducationProductModule, null, sql, null, null);
    }

    /**
     * Convert EducationObject to ContentValues
     */
    public ContentValues educationObjectToContentValues(@NonNull EducationObject educationObject) {
        ContentValues cv = new ContentValues();

        cv.put(EDUCATION_PRODUCT_MODULE_SERVER_ID, educationObject.getId());
        cv.put(EDUCATION_PRODUCT_MODULE_PROGRAM_ID, educationObject.getProgram_id());
        cv.put(EDUCATION_PRODUCT_MODULE_NAME, educationObject.getName());
        cv.put(EDUCATION_PRODUCT_MODULE_DESCRIPTION, educationObject.getDescription());
        cv.put(EDUCATION_PRODUCT_MODULE_CREATED_AT, educationObject.getCreated_at());
        cv.put(EDUCATION_PRODUCT_MODULE_UPDATED_AT, educationObject.getUpdated_at());

        LOGD(TAG, "ContentValues : " + cv.toString());

        return cv;
    }

    /**
     * Convert EducationGetModulesByProgramIdResponse.Result to ContentValues
     */
    public ContentValues educationObjectToContentValues(@NonNull EducationGetModulesByProgramIdResponse.Result educationObject) {
        ContentValues cv = new ContentValues();

        cv.put(EDUCATION_PRODUCT_MODULE_SERVER_ID, educationObject.id);
        cv.put(EDUCATION_PRODUCT_MODULE_PROGRAM_ID, educationObject.program_id);
        cv.put(EDUCATION_PRODUCT_MODULE_NAME, educationObject.name);
        cv.put(EDUCATION_PRODUCT_MODULE_DESCRIPTION, educationObject.description);
        cv.put(EDUCATION_PRODUCT_MODULE_CREATED_AT, educationObject.created_at);
        cv.put(EDUCATION_PRODUCT_MODULE_UPDATED_AT, educationObject.updated_at);

        LOGD(TAG, "ContentValues : " + cv.toString());

        return cv;
    }

    /**
     * Convert ArrayList<Material> to ContentValues[]
     */
    public ContentValues[] educationObjectListToContentValues(@NonNull ArrayList<EducationObject> educationObjectArrayList) {
        // Init List ContentValues
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<ContentValues>(educationObjectArrayList.size());

        for (int i = 0; i < educationObjectArrayList.size(); i++) {
            // Get One Object
            EducationObject educationObject = educationObjectArrayList.get(i);

            ContentValues cv = new ContentValues();
            cv.put(EDUCATION_PRODUCT_MODULE_SERVER_ID, educationObject.getId());
            cv.put(EDUCATION_PRODUCT_MODULE_PROGRAM_ID, educationObject.getProgram_id());
            cv.put(EDUCATION_PRODUCT_MODULE_NAME, educationObject.getName());
            cv.put(EDUCATION_PRODUCT_MODULE_DESCRIPTION, educationObject.getDescription());
            cv.put(EDUCATION_PRODUCT_MODULE_CREATED_AT, educationObject.getCreated_at());
            cv.put(EDUCATION_PRODUCT_MODULE_UPDATED_AT, educationObject.getUpdated_at());

            // Add to List
            contentValuesArrayList.add(cv);
        }

        ContentValues[] contentValues = contentValuesArrayList.toArray(new ContentValues[contentValuesArrayList.size()]);
        LOGD(TAG, "ContentValues : " + Arrays.toString(contentValues));

        return contentValues;
    }
}
