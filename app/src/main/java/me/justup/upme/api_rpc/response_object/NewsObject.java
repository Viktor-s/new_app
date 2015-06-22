package me.justup.upme.api_rpc.response_object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.justup.upme.api_rpc.response_object.sub_object.Author;
import me.justup.upme.api_rpc.response_object.sub_object.Comment;
import me.justup.upme.api_rpc.utils.Constants;

public class NewsObject implements Serializable{

    @SerializedName(Constants.ID)
    private int id;

    @SerializedName(Constants.TITLE)
    private String title;

    @SerializedName(Constants.SHORT_DESCR)
    private String short_descr;

    @SerializedName(Constants.FULL_DESCR)
    private String full_descr;

    @SerializedName(Constants.IMG)
    private String img;

    @SerializedName(Constants.THUMBNAIL)
    private String thumbnail;

    @SerializedName(Constants.IS_HIDDEN)
    private boolean is_hidden;

    @SerializedName(Constants.POSTED_AT)
    private String posted_at;

    @SerializedName(Constants.CREATE_AT)
    private String created_at;

    @SerializedName(Constants.COMMENTS)
    private List<Comment> comments;

    @SerializedName(Constants.SUCCESS)
    private boolean success;

    @SerializedName(Constants.CONTENT)
    private String content;

    @SerializedName(Constants.AUTHOR_ID)
    private int author_id;

    @SerializedName(Constants.AUTHOR)
    private Author author;

    private ArrayList<NewsObject> newsObjectArrayList;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getShort_descr() {
        return short_descr;
    }

    public String getFull_descr() {
        return full_descr;
    }

    public String getImg() {
        return img;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public boolean is_hidden() {
        return is_hidden;
    }

    public String getPosted_at() {
        return posted_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getContent() {
        return content;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public Author getAuthor() {
        return author;
    }

    public ArrayList<NewsObject> getNewsObjectArrayList() {
        return newsObjectArrayList;
    }

    public void setNewsObjectArrayList(ArrayList<NewsObject> newsObjectArrayList) {
        this.newsObjectArrayList = newsObjectArrayList;
    }

    @Override
    public String toString() {
        return "NewsObject{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", short_descr='" + short_descr + '\'' +
                ", full_descr='" + full_descr + '\'' +
                ", img='" + img + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", is_hidden=" + is_hidden +
                ", posted_at='" + posted_at + '\'' +
                ", created_at='" + created_at + '\'' +
                ", comments=" + comments +
                ", success=" + success +
                ", content='" + content + '\'' +
                ", author_id=" + author_id +
                ", author=" + author +
                '}';
    }
}
