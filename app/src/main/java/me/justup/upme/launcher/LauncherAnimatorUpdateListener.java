package me.justup.upme.launcher;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

public abstract class LauncherAnimatorUpdateListener implements AnimatorUpdateListener {
    public void onAnimationUpdate(ValueAnimator animation) {
        final float b = (Float) animation.getAnimatedValue();
        final float a = 1f - b;
        onAnimationUpdate(a, b);
    }

    abstract void onAnimationUpdate(float a, float b);
}