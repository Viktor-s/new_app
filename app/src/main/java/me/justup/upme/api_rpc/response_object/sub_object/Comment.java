package me.justup.upme.api_rpc.response_object.sub_object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import me.justup.upme.api_rpc.utils.Constants;

public class Comment implements Serializable{

    @SerializedName(Constants.ID)
    private int id;

    @SerializedName(Constants.CONTENT)
    private String content;

    @SerializedName(Constants.POSTED_AT)
    private String posted_at;

    @SerializedName(Constants.AUTHOR_ID)
    private int author_id;

    @SerializedName(Constants.AUTHOR)
    private Author author;

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getPosted_at() {
        return posted_at;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public Author getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", posted_at='" + posted_at + '\'' +
                ", author_id=" + author_id +
                ", author=" + author +
                '}';
    }
}
