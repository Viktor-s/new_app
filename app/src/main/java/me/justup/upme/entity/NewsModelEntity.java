package me.justup.upme.entity;

import android.graphics.drawable.Drawable;

import java.util.List;

public class NewsModelEntity {
    private String newsDate;
    private String newsTitle;
    private String newsText;
    private Drawable newsImage;
    private List<NewsCommentEntity> newsCommentEntityList;

    public String getNewsTitle() {
        return newsTitle;
    }

    public String getNewsDate() {
        return newsDate;
    }

    public void setNewsDate(String newsDate) {
        this.newsDate = newsDate;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsText() {
        return newsText;
    }

    public void setNewsText(String newsText) {
        this.newsText = newsText;
    }

    public Drawable getNewsImage() {
        return newsImage;
    }

    public void setNewsImage(Drawable newsImage) {
        this.newsImage = newsImage;
    }

    public List<NewsCommentEntity> getNewsCommentEntityList() {
        return newsCommentEntityList;
    }

    public void setNewsCommentEntityList(List<NewsCommentEntity> newsCommentEntityList) {
        this.newsCommentEntityList = newsCommentEntityList;
    }
}
