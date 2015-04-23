package me.justup.upme.view.dashboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class SlowlyScrollView extends ScrollView{
    private static final String TAG = SlowlyScrollView.class.getSimpleName();

    private static final int MAX_SCROLL_SPEED = 50;

    public SlowlyScrollView(Context context) {
        super(context);
    }

    public SlowlyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlowlyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void fling(int velocityY) {
        int topVelocityY = (int) ((Math.min(Math.abs(velocityY), MAX_SCROLL_SPEED) ) * Math.signum(velocityY));
        super.fling(topVelocityY);
    }
}
