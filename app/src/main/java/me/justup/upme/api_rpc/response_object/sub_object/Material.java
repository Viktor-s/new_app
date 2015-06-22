package me.justup.upme.api_rpc.response_object.sub_object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import me.justup.upme.api_rpc.utils.Constants;

public class Material implements Serializable {

    @SerializedName(Constants.ID)
    private int id;

    @SerializedName(Constants.MODULE_ID)
    private int module_id;

    @SerializedName(Constants.CONTENT_TYPE)
    private String content_type;

    @SerializedName(Constants.PRIORITY_TYPE)
    private String priority_type;

    @SerializedName(Constants.EXTRADATA)
    private Extradata extradata;

    @SerializedName(Constants.SORT_WEIGHT)
    private int sort_weight;

    @SerializedName(Constants.CREATE_AT)
    private String created_at;

    @SerializedName(Constants.UPDATE_AT)
    private String updated_at;

    @SerializedName(Constants.NAME)
    private String name;

    @SerializedName(Constants.DESCRIPTION)
    private String description;

    public int getId() {
        return id;
    }

    public int getModuleId() {
        return module_id;
    }

    public String getContentType() {
        return content_type;
    }

    public String getPriorityType() {
        return priority_type;
    }

    public Extradata getExtradata() {
        return extradata;
    }

    public int getSortWeight() {
        return sort_weight;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public String getUpdatedAt() {
        return updated_at;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Material{" +
                "id=" + id +
                ", module_id=" + module_id +
                ", content_type='" + content_type + '\'' +
                ", priority_type='" + priority_type + '\'' +
                ", extradata=" + extradata +
                ", sort_weight=" + sort_weight +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
