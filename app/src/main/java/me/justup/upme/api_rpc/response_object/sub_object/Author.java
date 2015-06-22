package me.justup.upme.api_rpc.response_object.sub_object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import me.justup.upme.api_rpc.utils.Constants;

public class Author implements Serializable{

    @SerializedName(Constants.NAME)
    private String name;

    @SerializedName(Constants.IMG)
    private String img;

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    @Override
    public String toString() {
        return "Author{" +
                "name='" + name + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
