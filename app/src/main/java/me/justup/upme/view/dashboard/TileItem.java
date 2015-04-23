package me.justup.upme.view.dashboard;

public class TileItem {
    private int mHSpans, mWSpans; // Height and Width

    private String mTitle = " ";
    private String mSecondTitle = " ";
    private int mResId = 0;
    private int mBackground = 0;

    private boolean isAddItemButton = false; // + button
    private boolean isRedacted = false; // Redacted now
    private boolean isImage = false;

    public TileItem(int hSpans, int wSpans, String title, String secondTitle, int resId, int background, boolean isAddItemButton, boolean isRedacted, boolean isImage) {
        this.mHSpans = hSpans;
        this.mWSpans = wSpans;

        this.mTitle = title;
        this.mSecondTitle = secondTitle;
        this.mResId = resId;
        this.mBackground = background;

        this.isAddItemButton = isAddItemButton;
        this.isRedacted = isRedacted;
        this.isImage = isImage;
    }

    public void setRedacted (boolean redacted){
        this.isRedacted = redacted;
    }

    public int getHSpans() { return mHSpans; }

    public int getWSpans() { return mWSpans; }

    public String getTitle() {
        return mTitle;
    }

    public String getSecondTitle() {
        return mSecondTitle;
    }

    public int getResId() {
        return mResId;
    }

    public int getBackground() {
        return mBackground;
    }

    public boolean isAddItemButton() { return isAddItemButton; }

    public boolean isRedacted() { return isRedacted; }

    public boolean isImage() { return isImage; }

    @Override
    public String toString() {
        return "TileItem{" +
                "HSpans=" + mHSpans +
                ", WSpans=" + mWSpans +
                ", Title='" + mTitle + '\'' +
                ", SecondTitle='" + mSecondTitle + '\'' +
                ", ResId=" + mResId +
                ", BackgroundColor=" + mBackground +
                ", isAddItemButton=" + isAddItemButton +
                ", isRedacted=" + isRedacted +
                ", isImage=" + isImage +
                '}';
    }
}
