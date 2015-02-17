package me.justup.upme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.justup.upme.R;


public class SettingsSocialFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        /*
        Button FB = (Button) view.findViewById(R.id.fb_button);
        Button VK = (Button) view.findViewById(R.id.vk_button);
        // VK.setOnClickListener(loginClick);

        String VK_KEY = getActivity().getString(R.string.vk_app_id);

        //Chose permissions
        ArrayList<String> fbScope = new ArrayList<>();
        fbScope.addAll(Arrays.asList("public_profile, email, user_friends"));
        */

        return view;
    }

}
