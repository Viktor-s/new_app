package me.justup.upme.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import me.justup.upme.JustUpApplication;
import me.justup.upme.R;

public class AnimateButtonClose {

    public static void animateButtonClose(final View view, final int time) {
        Animation animation = AnimationUtils.loadAnimation(JustUpApplication.getApplication().getApplicationContext(), R.anim.fragment_slider_in);
        animation.setStartOffset(time);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }
        });

        view.startAnimation(animation);

    }
}
