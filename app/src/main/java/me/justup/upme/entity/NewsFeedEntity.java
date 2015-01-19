package me.justup.upme.entity;

import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.List;

public class NewsFeedEntity implements Serializable {
    private String mNewsDate;
    private String mNewsTitle;
    private String mNewsText;
    private Drawable mNewsImage;
    private List<NewsCommentEntity> mNewsCommentEntityList;

    public String getNewsTitle() {
        return mNewsTitle;
    }

    public String getNewsDate() {
        return mNewsDate;
    }

    public void setNewsDate(String newsDate) {
        this.mNewsDate = newsDate;
    }

    public void setNewsTitle(String newsTitle) {
        this.mNewsTitle = newsTitle;
    }

    public String getNewsText() {
        return mNewsText;
    }

    public void setNewsText(String newsText) {
        this.mNewsText = newsText;
    }

    public Drawable getNewsImage() {
        return mNewsImage;
    }

    public void setNewsImage(Drawable newsImage) {
        this.mNewsImage = newsImage;
    }

    public List<NewsCommentEntity> getNewsCommentEntityList() {
        return mNewsCommentEntityList;
    }

    public void setNewsCommentEntityList(List<NewsCommentEntity> mNewsCommentEntityList) {
        this.mNewsCommentEntityList = mNewsCommentEntityList;
    }
}
