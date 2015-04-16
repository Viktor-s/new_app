package me.justup.upme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import me.justup.upme.R;

import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class StudyFragment extends Fragment {
    private static final String TAG = makeLogTag(StudyFragment.class);
    private Button youtubePlayerButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_study, container, false);
        LOGI(TAG, "StudyFragment onCreateView()");
        youtubePlayerButton = (Button) v.findViewById(R.id.youtube_player_button);
        youtubePlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChildFragmentManager().beginTransaction().replace(R.id.youtube_player_container, YouTubePlayerFragment.newInstance("_oEA18Y8gM0")).commit();
            }
        });


        return v;
    }

}
