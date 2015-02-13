package me.justup.upme.entity;

import java.util.List;

public class ArticleFullEntity {
    private int id;
    private String full_descr;
    private List<ArticleShortCommentEntity> comments;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFull_descr() {
        return full_descr;
    }

    public void setFull_descr(String full_descr) {
        this.full_descr = full_descr;
    }

    public List<ArticleShortCommentEntity> getComments() {
        return comments;
    }

    public void setComments(List<ArticleShortCommentEntity> comments) {
        this.comments = comments;
    }
}
