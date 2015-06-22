package me.justup.upme.api_rpc.response_object.sub_object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import me.justup.upme.api_rpc.utils.Constants;

public class Product implements Serializable {

    @SerializedName(Constants.ID)
    private int id;

    @SerializedName(Constants.NAME)
    private String name;

    @SerializedName(Constants.SHORT_DESCRIPTION_UNDERLINE)
    private String short_description;

    @SerializedName(Constants.DESCRIPTION)
    private String description;

    @SerializedName(Constants.IMG)
    private String img;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShort_description() {
        return short_description;
    }

    public String getDescription() {
        return description;
    }

    public String getImg() {
        return img;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", short_description='" + short_description + '\'' +
                ", description='" + description + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
