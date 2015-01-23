package me.justup.upme.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import me.justup.upme.R;
import me.justup.upme.interfaces.OnCloseFragment;

import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class UserOrderingFragment extends Fragment {
    private static final String TAG = makeLogTag(UserOrderingFragment.class);
    private OnCloseFragment mOnCloseOrderingFragmentCallback;

    private static final String INDEX = "index";
    private int id;


    public static UserOrderingFragment newInstance(int index) {
        UserOrderingFragment f = new UserOrderingFragment();

        Bundle args = new Bundle();
        args.putInt(INDEX, index);
        f.setArguments(args);

        return f;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mOnCloseOrderingFragmentCallback = (OnCloseFragment) getParentFragment();
        } catch (ClassCastException e) {
            LOGE(TAG, "Must implement OnCloseFragment", e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id = getArguments().getInt(INDEX, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_ordering, container, false);

        Button mFragmentCloseButton = (Button) v.findViewById(R.id.order_fragment_close_button);
        mFragmentCloseButton.setOnClickListener(new OnCloseOrderListener());

        ((TextView) v.findViewById(R.id.order_fragment_textView)).setText(String.valueOf(id));

        return v;
    }

    private class OnCloseOrderListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mOnCloseOrderingFragmentCallback.onCloseFragment();
            // getParentFragment().getChildFragmentManager().beginTransaction().remove(UserOrderingFragment.this).commit();
        }
    }

}
