package me.justup.upme.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.gorbin.asne.core.listener.OnLoginCompleteListener;
import com.vk.sdk.VKScope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.justup.upme.LoginActivity;
import me.justup.upme.MainActivity;
import me.justup.upme.R;
import me.justup.upme.interfaces.OnCloseFragment;
import me.justup.upme.social.FacebookSocialNetwork;
import me.justup.upme.social.SocialNetwork;
import me.justup.upme.social.SocialNetworkManager;
import me.justup.upme.social.VkSocialNetwork;
import me.justup.upme.utils.AppContext;
import me.justup.upme.utils.AppPreferences;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class SettingsFragment extends Fragment implements SocialNetworkManager.OnInitializationCompleteListener, OnLoginCompleteListener {
    private static final String TAG = makeLogTag(SettingsFragment.class);
    public static SocialNetworkManager mSocialNetworkManager;

    private OnCloseFragment mOnCloseSettingsFragmentCallback;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mOnCloseSettingsFragmentCallback = (OnCloseFragment) activity;
        } catch (ClassCastException e) {
            LOGE(TAG, "Must implement OnCloseFragment", e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Button mCloseFragment = (Button) view.findViewById(R.id.settings_close_button);
        mCloseFragment.setOnClickListener(new OnCloseListener());

        Button mExitButton = (Button) view.findViewById(R.id.settings_exit_button);
        mExitButton.setOnClickListener(new OnExitListener());

        Button FB = (Button) view.findViewById(R.id.fb_button);
        FB.setOnClickListener(loginClick);
        Button VK = (Button) view.findViewById(R.id.vk_button);
        VK.setOnClickListener(loginClick);

        String VK_KEY = getActivity().getString(R.string.vk_app_id);
        String[] vkScope = new String[]{
                VKScope.FRIENDS,
                VKScope.WALL,
                VKScope.PHOTOS,
                VKScope.NOHTTPS,
                VKScope.STATUS,
        };

        ArrayList<String> fbScope = new ArrayList<>();
        fbScope.addAll(Arrays.asList("public_profile, email, user_friends"));

        mSocialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(MainActivity.SOCIAL_NETWORK_TAG);

        if (mSocialNetworkManager == null) {
            mSocialNetworkManager = new SocialNetworkManager();

            VkSocialNetwork vkNetwork = new VkSocialNetwork(this, VK_KEY, vkScope);
            mSocialNetworkManager.addSocialNetwork(vkNetwork);

            FacebookSocialNetwork fbNetwork = new FacebookSocialNetwork(this, fbScope);
            mSocialNetworkManager.addSocialNetwork(fbNetwork);

            getFragmentManager().beginTransaction().add(mSocialNetworkManager, MainActivity.SOCIAL_NETWORK_TAG).commit();
            mSocialNetworkManager.setOnInitializationCompleteListener(this);
        } else {
            if (!mSocialNetworkManager.getInitializedSocialNetworks().isEmpty()) {
                List<SocialNetwork> socialNetworks = mSocialNetworkManager.getInitializedSocialNetworks();
                for (SocialNetwork socialNetwork : socialNetworks) {
                    socialNetwork.setOnLoginCompleteListener(this);
                    initSocialNetwork(socialNetwork);
                }
            }
        }

        return view;
    }

    @Override
    public void onSocialNetworkManagerInitialized() {
        LOGI(TAG, "onSocialNetworkManagerInitialized()");

        //when init SocialNetworks - get and setup login only for initialized SocialNetworks
        for (SocialNetwork socialNetwork : mSocialNetworkManager.getInitializedSocialNetworks()) {
            socialNetwork.setOnLoginCompleteListener(this);
            initSocialNetwork(socialNetwork);
        }
    }

    private void initSocialNetwork(SocialNetwork socialNetwork) {
        LOGI(TAG, "initSocialNetwork()");

        if (socialNetwork.isConnected()) {
            switch (socialNetwork.getID()) {
                case VkSocialNetwork.ID:
                    // vk.setText("Show VK profile");
                    LOGI(TAG, "VkSocialNetwork");
                    break;
                case FacebookSocialNetwork.ID:
                    // facebook.setText("Show Facebook profile");
                    LOGI(TAG, "FacebookSocialNetwork");
                    break;
                default:
                    LOGE(TAG, "SocialNetwork init unknown");
                    break;
            }
        }
    }

    private View.OnClickListener loginClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int networkId = 0;
            switch (view.getId()) {
                case R.id.fb_button:
                    networkId = FacebookSocialNetwork.ID;
                    break;

                case R.id.vk_button:
                    networkId = VkSocialNetwork.ID;
                    break;
            }

            SocialNetwork socialNetwork = mSocialNetworkManager.getSocialNetwork(networkId);
            if (!socialNetwork.isConnected()) {
                if (networkId != 0) {
                    socialNetwork.requestLogin();
                    // MainActivity.showProgress("Loading social person");
                } else {
                    Toast.makeText(getActivity(), "Wrong networkId", Toast.LENGTH_LONG).show();
                }
            } else {
                // startProfile(socialNetwork.getID());
            }
        }
    };

    @Override
    public void onLoginSuccess(int networkId) {
        // MainActivity.hideProgress();
    }

    @Override
    public void onError(int networkId, String requestID, String errorMessage, Object data) {
        // MainActivity.hideProgress();
        Toast.makeText(getActivity(), "ERROR: " + errorMessage, Toast.LENGTH_LONG).show();
    }

    private class OnCloseListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mOnCloseSettingsFragmentCallback.onCloseFragment();
        }
    }

    private class OnExitListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            new AppPreferences(AppContext.getAppContext()).clearPreferences();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
    }

}
