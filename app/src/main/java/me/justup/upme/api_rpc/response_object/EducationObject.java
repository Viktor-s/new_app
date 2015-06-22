package me.justup.upme.api_rpc.response_object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.justup.upme.api_rpc.response_object.sub_object.Extradata;
import me.justup.upme.api_rpc.response_object.sub_object.Material;
import me.justup.upme.api_rpc.response_object.sub_object.Question;
import me.justup.upme.api_rpc.utils.Constants;

public class EducationObject implements Serializable {

    @SerializedName(Constants.ID)
    private int id;

    @SerializedName(Constants.NAME)
    private String name;

    @SerializedName(Constants.PROGRAM_ID)
    private int program_id;

    @SerializedName(Constants.DESCRIPTION)
    private String description;

    @SerializedName(Constants.CREATE_AT)
    private String created_at;

    @SerializedName(Constants.UPDATE_AT)
    private String updated_at;

    @SerializedName(Constants.MATERIALS)
    private List<Material> materials;

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

    @SerializedName(Constants.PASS_LIMIT)
    private int pass_limit;

    @SerializedName(Constants.QUESTIONS)
    private List<Question> questions;

    @SerializedName(Constants.PASS_RESULT)
    private int pass_result;

    @SerializedName(Constants.PASSED)
    private boolean passed;

    private ArrayList<EducationObject> educationObjectArrayList;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getProgram_id() {
        return program_id;
    }

    public String getDescription() {
        return description;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public int getModule_id() {
        return module_id;
    }

    public String getContent_type() {
        return content_type;
    }

    public String getPriority_type() {
        return priority_type;
    }

    public Extradata getExtradata() {
        return extradata;
    }

    public int getSort_weight() {
        return sort_weight;
    }

    public int getPass_limit() {
        return pass_limit;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public int getPass_result() {
        return pass_result;
    }

    public boolean isPassed() {
        return passed;
    }

    public ArrayList<EducationObject> getEducationObjectArrayList() {
        return educationObjectArrayList;
    }

    public void setEducationObjectArrayList(ArrayList<EducationObject> educationObjectArrayList) {
        this.educationObjectArrayList = educationObjectArrayList;
    }

    @Override
    public String toString() {
        return "EducationObject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", program_id=" + program_id +
                ", description='" + description + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", materials=" + materials +
                ", module_id=" + module_id +
                ", content_type='" + content_type + '\'' +
                ", priority_type='" + priority_type + '\'' +
                ", extradata=" + extradata +
                ", sort_weight=" + sort_weight +
                ", pass_limit=" + pass_limit +
                ", questions=" + questions +
                ", pass_result=" + pass_result +
                ", passed=" + passed +
                '}';
    }
}
