package me.justup.upme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.justup.upme.R;

import static me.justup.upme.utils.LogUtils.makeLogTag;


public class UserFragment extends Fragment {
    private static final String TAG = makeLogTag(UserFragment.class);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);


        return view;
    }

}