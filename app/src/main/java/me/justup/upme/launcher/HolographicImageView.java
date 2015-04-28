package me.justup.upme.launcher;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class HolographicImageView extends ImageView {

    private final HolographicViewHelper mHolographicHelper;

    public HolographicImageView(Context context) {
        this(context, null);
    }

    public HolographicImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HolographicImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mHolographicHelper = new HolographicViewHelper(context);
    }

    public void invalidatePressedFocusedStates() {
        mHolographicHelper.invalidatePressedFocusedStates(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // One time call to generate the pressed/focused state -- must be called after
        // measure/layout
        mHolographicHelper.generatePressedFocusedStates(this);
    }
}
