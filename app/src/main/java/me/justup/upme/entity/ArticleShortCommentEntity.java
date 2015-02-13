package me.justup.upme.entity;

import java.io.Serializable;

public class ArticleShortCommentEntity implements Serializable {

    private int id;
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPosted_at() {
        return posted_at;
    }

    public void setPosted_at(String posted_at) {
        this.posted_at = posted_at;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_img() {
        return author_img;
    }

    public void setAuthor_img(String author_img) {
        this.author_img = author_img;
    }

    private String posted_at;
    private int author_id;
    private String author_name;
    private String author_img;

    @Override
    public String toString() {
        return "ArticleShortCommentEntity{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", posted_at='" + posted_at + '\'' +
                ", author_id=" + author_id +
                ", author_name='" + author_name + '\'' +
                ", author_img='" + author_img + '\'' +
                '}';
    }
}
