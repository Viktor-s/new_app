package me.justup.upme.api_rpc.response_object.sub_object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import me.justup.upme.api_rpc.utils.Constants;

public class Extradata implements Serializable {

    @SerializedName(Constants.SOURCE)
    private String source;

    @SerializedName(Constants.LINK)
    private String link;

    public String getSource() {
        return source;
    }

    public String getLink() {
        return link;
    }

    @Override
    public String toString() {
        return "Extradata{" +
                "source='" + source + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
