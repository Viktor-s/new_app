package me.justup.upme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import me.justup.upme.R;

import static me.justup.upme.utils.LogUtils.*;


public class FastPayingFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = makeLogTag(FastPayingFragment.class);

    private LinearLayout mPayCommunicationButton;
    private LinearLayout mPayInternetButton;
    private LinearLayout mRemittancesButton;
    private LinearLayout mBankingButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fast_paying, container, false);

        mPayCommunicationButton = (LinearLayout) v.findViewById(R.id.pay_communication);
        mPayCommunicationButton.setOnClickListener(this);

        mPayInternetButton = (LinearLayout) v.findViewById(R.id.pay_internet);
        mPayInternetButton.setOnClickListener(this);

        mRemittancesButton = (LinearLayout) v.findViewById(R.id.pay_remittances);
        mRemittancesButton.setOnClickListener(this);

        mBankingButton = (LinearLayout) v.findViewById(R.id.pay_banking);
        mBankingButton.setOnClickListener(this);

        getChildFragmentManager().beginTransaction().add(R.id.paying_fragment_panel, new PayCommunicationFragment()).commit();

        return v;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment;

        switch (v.getId()) {
            case R.id.pay_communication:
                fragment = new PayCommunicationFragment();
                break;

            case R.id.pay_internet:
                fragment = new PayInternetFragment();
                break;

            case R.id.pay_remittances:
                fragment = new PayRemittancesFragment();
                break;

            default:
                fragment = new PayBankingFragment();
                break;
        }

        getChildFragmentManager().beginTransaction().
                setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right).
                replace(R.id.paying_fragment_panel, fragment).commit();
    }

}
