package me.justup.upme.fragments;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;

public class YoutubeDefaultFragment  extends com.google.android.youtube.player.YouTubePlayerFragment {
    private static final String TAG = YoutubeDefaultFragment.class.getSimpleName();
    private static final String URL = "url";

    public static final String API_KEY = "AIzaSyAZYUjtjOyE0I6fsiVSgjtFhjIz1cD3cCQ";

    private MyPlayerStateChangeListener myPlayerStateChangeListener = null;
    private MyPlaybackEventListener myPlaybackEventListener = null;

    private static final int RQS_ErrorDialog = 1;

    private YouTubePlayer mYouTubePlayer = null;

    public static YoutubeDefaultFragment newInstance(String url) {
        YoutubeDefaultFragment f = new YoutubeDefaultFragment();
        LOGI(TAG, "Url : " + url);

        Bundle b = new Bundle();
        b.putString(URL, url);
        f.setArguments(b);
        f.init();

        return f;
    }

    private void init() {

        initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult result) {
                if (result.isUserRecoverableError()) {
                    result.getErrorDialog(getActivity(), RQS_ErrorDialog).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "YouTubePlayer.onInitializationFailure() : " + result.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                mYouTubePlayer = player;

                mYouTubePlayer.setPlayerStateChangeListener(myPlayerStateChangeListener);
                mYouTubePlayer.setPlaybackEventListener(myPlaybackEventListener);

                if (!wasRestored) {
                    player.cueVideo(getArguments().getString(URL));
                }
            }
        });

        myPlayerStateChangeListener = new MyPlayerStateChangeListener();
        myPlaybackEventListener = new MyPlaybackEventListener();
    }

    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        private void updateLog(String prompt){

        }

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onError(
            com.google.android.youtube.player.YouTubePlayer.ErrorReason arg0) {
            LOGE(TAG, "onError() : " + arg0.toString());
        }

        @Override
        public void onLoaded(String arg0) {
            LOGD(TAG, "onLoaded() : " + arg0);
        }

        @Override
        public void onLoading() {

        }

        @Override
        public void onVideoEnded() {

        }

        @Override
        public void onVideoStarted() {

        }

    }

    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        private void updateLog(String prompt){

        }

        @Override
        public void onBuffering(boolean arg0) {
            LOGD(TAG, "onBuffering() : " + String.valueOf(arg0));
        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onPlaying() {

        }

        @Override
        public void onSeekTo(int arg0) {

        }

        @Override
        public void onStopped() {

        }
    }
}