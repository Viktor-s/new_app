package me.justup.upme.api_rpc.response_object.sub_object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import me.justup.upme.api_rpc.utils.Constants;

public class PushResult implements Serializable {

    @SerializedName(Constants.HEADERS)
    private List<String> headers;

    @SerializedName(Constants.CONTENT)
    private String content;

    public List<String> getHeaders() {
        return headers;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "PushResult{" +
                "headers=" + headers +
                ", content='" + content + '\'' +
                '}';
    }
}
