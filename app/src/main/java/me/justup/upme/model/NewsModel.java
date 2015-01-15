package me.justup.upme.model;

import android.graphics.drawable.Drawable;

import java.util.List;

public class NewsModel {
    private String newsTitle;
    private String newsText;
    private Drawable newsImage;
    private List<NewsCommentModel> newsCommentModelList;

    public String getNewsTitle() {
        return newsTitle;
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

}
