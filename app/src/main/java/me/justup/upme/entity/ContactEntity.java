package me.justup.upme.entity;

import android.graphics.drawable.Drawable;

public class ContactEntity {

    private String mContactName;
    private Drawable mContactImage;

    public Drawable getmContactImage() {
        return mContactImage;
    }

    public void setmContactImage(Drawable mContactImage) {
        this.mContactImage = mContactImage;
    }

    public String getmContactName() {
        return mContactName;
    }

    public void setmContactName(String mContactName) {
        this.mContactName = mContactName;
    }


}
