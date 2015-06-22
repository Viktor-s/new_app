package me.justup.upme.api_rpc.response_object.sub_object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import me.justup.upme.api_rpc.utils.Constants;

public class Answer implements Serializable {

    @SerializedName(Constants.ANSWERS_HASH)
    private String answer_hash;

    @SerializedName(Constants.ANSWER_TEXT)
    private String answer_text;

    public String getAnswer_hash() {
        return answer_hash;
    }

    public String getAnswer_text() {
        return answer_text;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "answer_hash='" + answer_hash + '\'' +
                ", answer_text='" + answer_text + '\'' +
                '}';
    }
}
