package me.justup.upme.api_rpc.response_object.sub_object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import me.justup.upme.api_rpc.utils.Constants;

public class Question implements Serializable {

    @SerializedName(Constants.QUESTION_TEXT)
    private String question_text;

    @SerializedName(Constants.QUESTION_HASH)
    private String question_hash;

    @SerializedName(Constants.ANSWERS)
    private List<Answer> answers;

    public String getQuestion_text() {
        return question_text;
    }

    public String getQuestion_hash() {
        return question_hash;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    @Override
    public String toString() {
        return "Question{" +
                "question_text='" + question_text + '\'' +
                ", question_hash='" + question_hash + '\'' +
                ", answers=" + answers +
                '}';
    }
}
