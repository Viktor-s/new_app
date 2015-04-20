package me.justup.upme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import me.justup.upme.R;

import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class StudyFragment extends Fragment {
    private static final String TAG = makeLogTag(StudyFragment.class);
    private RelativeLayout youtubePlayerButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_study, container, false);
        LOGI(TAG, "StudyFragment onCreateView()");
        youtubePlayerButton = (RelativeLayout) v.findViewById(R.id.youtube_button_container);
        youtubePlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youtubePlayerButton.setVisibility(View.GONE);
                //YouTubePlayerFragment youTubePlayerFragment = YouTubePlayerFragment.newInstance("OMOVFvcNfvE");
                //getChildFragmentManager().beginTransaction().replace(R.id.youtube_player_container, YouTubeCustomPlayerFragment.newInstance("OMOVFvcNfvE")).addToBackStack(null).commit();
                getChildFragmentManager().beginTransaction().replace(R.id.youtube_player_container, YoutubeDefaultFragment.newInstance("OMOVFvcNfvE")).addToBackStack(null).commit();
            }
        });


        return v;
    }



}
