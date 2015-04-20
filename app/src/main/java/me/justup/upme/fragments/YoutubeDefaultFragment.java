package me.justup.upme.fragments;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

public class YoutubeDefaultFragment  extends com.google.android.youtube.player.YouTubePlayerFragment {

    public YoutubeDefaultFragment() {
    }

    public static YoutubeDefaultFragment newInstance(String url) {

        YoutubeDefaultFragment f = new YoutubeDefaultFragment();

        Bundle b = new Bundle();
        b.putString("url", url);
        f.setArguments(b);
        f.init();

        return f;
    }

    private void init() {

        initialize("AIzaSyAZYUjtjOyE0I6fsiVSgjtFhjIz1cD3cCQ", new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
            }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    player.cueVideo(getArguments().getString("url"));
                }
            }
        });
    }
    // getFragmentManager().popBackStack();/



}