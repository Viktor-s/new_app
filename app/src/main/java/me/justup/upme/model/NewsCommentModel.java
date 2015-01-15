package me.justup.upme.model;

import android.graphics.drawable.Drawable;

public class NewsCommentModel {
    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public Drawable getCommentImage() {
        return commentImage;
    }

    public void setCommentImage(Drawable commentImage) {
        this.commentImage = commentImage;
    }

    public String getCommentTitle() {
        return commentTitle;
    }

    public void setCommentTitle(String commentTitle) {
        this.commentTitle = commentTitle;
    }

    private String commentTitle;
    private String commentText;
    private Drawable commentImage;
}
