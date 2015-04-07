package me.justup.upme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.justup.upme.R;

import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class StudyFragment extends Fragment {
    private static final String TAG = makeLogTag(StudyFragment.class);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_study, container, false);

        LOGI(TAG, "StudyFragment onCreateView()");

        return v;
    }

}
