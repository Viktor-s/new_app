package me.justup.upme.launcher;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import me.justup.upme.R;

public class HolographicLinearLayout extends LinearLayout {

    private HolographicViewHelper mHolographicHelper = null;
    private ImageView mImageView = null;
    private int mImageViewId;

    public HolographicLinearLayout(Context context) {
        this(context, null);
    }

    public HolographicLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HolographicLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HolographicLinearLayout,
                defStyle, 0);
        mImageViewId = a.getResourceId(R.styleable.HolographicLinearLayout_sourceImageViewId, -1);
        a.recycle();

        setWillNotDraw(false);
        mHolographicHelper = new HolographicViewHelper(context);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (mImageView != null) {
            Drawable d = mImageView.getDrawable();
            if (d instanceof StateListDrawable) {
                StateListDrawable sld = (StateListDrawable) d;
                sld.setState(getDrawableState());
            }
        }
    }

    public void invalidatePressedFocusedStates() {
        mHolographicHelper.invalidatePressedFocusedStates(mImageView);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // One time call to generate the pressed/focused state -- must be called after
        // measure/layout
        if (mImageView == null) {
            mImageView = (ImageView) findViewById(mImageViewId);
        }
        mHolographicHelper.generatePressedFocusedStates(mImageView);
    }
}
