package me.justup.upme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.justup.upme.R;

import static me.justup.upme.utils.LogUtils.makeLogTag;


public class PayCommunicationFragment extends Fragment {
    private static final String TAG = makeLogTag(PayCommunicationFragment.class);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pay_communication, container, false);

        return view;
    }

}
