package me.justup.upme.entity;

import android.graphics.drawable.Drawable;

public class NewsCommentEntity {
    private String commentTitle;
    private String commentText;
    private Drawable mCommentImage;

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public Drawable getCommentImage() {
        return mCommentImage;
    }

    public void setCommentImage(Drawable commentImage) {
        this.mCommentImage = commentImage;
    }

    public String getCommentTitle() {
        return commentTitle;
    }

    public void setCommentTitle(String commentTitle) {
        this.commentTitle = commentTitle;
    }


}
