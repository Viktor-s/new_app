package me.justup.upme.db_upme.transfers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

import me.justup.upme.api_rpc.response_object.sub_object.Material;
import me.justup.upme.db_upme.MetaData;
import me.justup.upme.db_upme.SyncAdapterMetaData;
import me.justup.upme.entity.EducationGetModulesByProgramIdResponse;

import static me.justup.upme.db.DBHelper.EDUCATION_MODULES_MATERIAL_CONTENT_TYPE;
import static me.justup.upme.db.DBHelper.EDUCATION_MODULES_MATERIAL_CREATED_AT;
import static me.justup.upme.db.DBHelper.EDUCATION_MODULES_MATERIAL_DESCRIPTION;
import static me.justup.upme.db.DBHelper.EDUCATION_MODULES_MATERIAL_EXTRA_LINK;
import static me.justup.upme.db.DBHelper.EDUCATION_MODULES_MATERIAL_EXTRA_SOURCE;
import static me.justup.upme.db.DBHelper.EDUCATION_MODULES_MATERIAL_MODULE_ID;
import static me.justup.upme.db.DBHelper.EDUCATION_MODULES_MATERIAL_NAME;
import static me.justup.upme.db.DBHelper.EDUCATION_MODULES_MATERIAL_PRIORITY_TYPE;
import static me.justup.upme.db.DBHelper.EDUCATION_MODULES_MATERIAL_SERVER_ID;
import static me.justup.upme.db.DBHelper.EDUCATION_MODULES_MATERIAL_SORT_WEIGHT;
import static me.justup.upme.db.DBHelper.EDUCATION_MODULES_MATERIAL_UPDATED_AT;
import static me.justup.upme.utils.LogUtils.LOGD;

public class TransferActionEducationModulesMaterial implements SyncAdapterMetaData {
    private static final String TAG = TransferActionEducationModulesMaterial.class.getSimpleName();

    private Uri uriEducationModulesMaterial = null;
    private Uri uriOneEducationModulesMaterial = null;
    private Uri uriListEducationModulesMaterial = null;

    public TransferActionEducationModulesMaterial(CharSequence name) {
        uriEducationModulesMaterial = Uri.parse("content://" + MetaData.AUTHORITY_EDUCATION_MODULE_MATERIAL + "/" + EducationModulesMaterialConstance.URI_PATH2_EDUCATION_MODULES_MATERIAL.replace("#", name));
        uriOneEducationModulesMaterial = Uri.parse("content://" + MetaData.AUTHORITY_EDUCATION_MODULE_MATERIAL + "/" + EducationModulesMaterialConstance.URI_PATH2_EDUCATION_MODULES_MATERIAL.replace("#", name) + "/" + 2);
        uriListEducationModulesMaterial = Uri.parse("content://" + MetaData.AUTHORITY_EDUCATION_MODULE_MATERIAL + "/" + EducationModulesMaterialConstance.URI_PATH2_EDUCATION_MODULES_MATERIAL.replace("#", name) + "/" + 1);
    }

    public void insertEducationModulesMaterial(Activity activity, Material materialObject) {
        ContentValues contentValues = materialObjectToContentValues(materialObject);

        if(contentValues!=null) {
            activity.getContentResolver().insert(uriOneEducationModulesMaterial, contentValues);
        }
    }

    public void insertEducationModulesMaterialOld(Context context, EducationGetModulesByProgramIdResponse.Material materialObject) {
        ContentValues contentValues = materialObjectToContentValues(materialObject);

        if(contentValues!=null) {
            context.getContentResolver().insert(uriOneEducationModulesMaterial, contentValues);
        }
    }

    public void insertListEducationModulesMaterial(Activity activity, ArrayList<Material> materialObjectArrayList) {
        ContentValues[] contentValues = materialObjectListToContentValues(materialObjectArrayList);

        if(contentValues!=null) {
            activity.getContentResolver().bulkInsert(uriListEducationModulesMaterial, contentValues);
        }
    }

    /**
     * Convert Material to ContentValues
     */
    public ContentValues materialObjectToContentValues(@NonNull Material materialObject) {
        ContentValues cv = new ContentValues();

        cv.put(EDUCATION_MODULES_MATERIAL_SERVER_ID, materialObject.getId());
        cv.put(EDUCATION_MODULES_MATERIAL_MODULE_ID, materialObject.getModuleId());
        cv.put(EDUCATION_MODULES_MATERIAL_CONTENT_TYPE, materialObject.getContentType());
        cv.put(EDUCATION_MODULES_MATERIAL_PRIORITY_TYPE, materialObject.getPriorityType());
        cv.put(EDUCATION_MODULES_MATERIAL_EXTRA_SOURCE, materialObject.getExtradata().getSource());
        cv.put(EDUCATION_MODULES_MATERIAL_EXTRA_LINK, materialObject.getExtradata().getLink());
        cv.put(EDUCATION_MODULES_MATERIAL_SORT_WEIGHT, materialObject.getSortWeight());
        cv.put(EDUCATION_MODULES_MATERIAL_CREATED_AT, materialObject.getCreatedAt());
        cv.put(EDUCATION_MODULES_MATERIAL_UPDATED_AT, materialObject.getUpdatedAt());
        cv.put(EDUCATION_MODULES_MATERIAL_NAME, materialObject.getName());
        cv.put(EDUCATION_MODULES_MATERIAL_DESCRIPTION, materialObject.getDescription());

        LOGD(TAG, "ContentValues : " + cv.toString());

        return cv;
    }

    /**
     * Convert EducationGetModulesByProgramIdResponse.Material to ContentValues
     */
    public ContentValues materialObjectToContentValues(@NonNull EducationGetModulesByProgramIdResponse.Material materialObject) {
        ContentValues cv = new ContentValues();

        cv.put(EDUCATION_MODULES_MATERIAL_SERVER_ID, materialObject.id);
        cv.put(EDUCATION_MODULES_MATERIAL_MODULE_ID, materialObject.module_id);
        cv.put(EDUCATION_MODULES_MATERIAL_CONTENT_TYPE, materialObject.content_type);
        cv.put(EDUCATION_MODULES_MATERIAL_PRIORITY_TYPE, materialObject.priority_type);
        cv.put(EDUCATION_MODULES_MATERIAL_EXTRA_SOURCE, materialObject.extradata.source);
        cv.put(EDUCATION_MODULES_MATERIAL_EXTRA_LINK, materialObject.extradata.link);
        cv.put(EDUCATION_MODULES_MATERIAL_SORT_WEIGHT, materialObject.sort_weight);
        cv.put(EDUCATION_MODULES_MATERIAL_CREATED_AT, materialObject.created_at);
        cv.put(EDUCATION_MODULES_MATERIAL_UPDATED_AT, materialObject.updated_at);
        cv.put(EDUCATION_MODULES_MATERIAL_NAME, materialObject.name);
        cv.put(EDUCATION_MODULES_MATERIAL_DESCRIPTION, materialObject.description);

        LOGD(TAG, "ContentValues : " + cv.toString());

        return cv;
    }

    /**
     * Convert ArrayList<Material> to ContentValues[]
     */
    public ContentValues[] materialObjectListToContentValues(@NonNull ArrayList<Material> materialObjectArrayList) {
        // Init List ContentValues
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<ContentValues>(materialObjectArrayList.size());

        for (int i = 0; i < materialObjectArrayList.size(); i++) {
            // Get One Object
            Material materialObject = materialObjectArrayList.get(i);

            ContentValues cv = new ContentValues();
            cv.put(EDUCATION_MODULES_MATERIAL_SERVER_ID, materialObject.getId());
            cv.put(EDUCATION_MODULES_MATERIAL_MODULE_ID, materialObject.getModuleId());
            cv.put(EDUCATION_MODULES_MATERIAL_CONTENT_TYPE, materialObject.getContentType());
            cv.put(EDUCATION_MODULES_MATERIAL_PRIORITY_TYPE, materialObject.getPriorityType());
            cv.put(EDUCATION_MODULES_MATERIAL_EXTRA_SOURCE, materialObject.getExtradata().getSource());
            cv.put(EDUCATION_MODULES_MATERIAL_EXTRA_LINK, materialObject.getExtradata().getLink());
            cv.put(EDUCATION_MODULES_MATERIAL_SORT_WEIGHT, materialObject.getSortWeight());
            cv.put(EDUCATION_MODULES_MATERIAL_CREATED_AT, materialObject.getCreatedAt());
            cv.put(EDUCATION_MODULES_MATERIAL_UPDATED_AT, materialObject.getUpdatedAt());
            cv.put(EDUCATION_MODULES_MATERIAL_NAME, materialObject.getName());
            cv.put(EDUCATION_MODULES_MATERIAL_DESCRIPTION, materialObject.getDescription());

            // Add to List
            contentValuesArrayList.add(cv);
        }

        ContentValues[] contentValues = contentValuesArrayList.toArray(new ContentValues[contentValuesArrayList.size()]);
        LOGD(TAG, "ContentValues : " + Arrays.toString(contentValues));

        return contentValues;
    }
}
