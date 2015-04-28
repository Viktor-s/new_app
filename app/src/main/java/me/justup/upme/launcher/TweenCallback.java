package me.justup.upme.launcher;

public interface TweenCallback {
    void onTweenValueChanged(float value, float oldValue);
    void onTweenStarted();
    void onTweenFinished();
}

