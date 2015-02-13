package me.justup.upme.entity;

import java.io.Serializable;
import java.util.List;

public class ArticleShortEntity implements Serializable {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getShort_descr() {
        return short_descr;
    }

    public void setShort_descr(String short_descr) {
        this.short_descr = short_descr;
    }

    public String getPosted_at() {
        return posted_at;
    }

    public void setPosted_at(String posted_at) {
        this.posted_at = posted_at;
    }

    public List<ArticleShortCommentEntity> getComments() {
        return comments;
    }

    public void setComments(List<ArticleShortCommentEntity> comments) {
        this.comments = comments;
    }

    private int id;
    private String title;
    private String short_descr;
    private String thumbnail;
    private String posted_at;
    private List<ArticleShortCommentEntity> comments;

    @Override
    public String toString() {
        return "ArticleShortEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", short_descr='" + short_descr + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", posted_at='" + posted_at + '\'' +
                ", comments=" + comments +
                '}';
    }
}