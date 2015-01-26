package me.justup.upme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import me.justup.upme.R;


public class FastPayingFragment extends Fragment implements View.OnClickListener {
    private Button mPayCommunicationButton;
    private Button mPayInternetButton;
    private Button mRemittancesButton;
    private Button mBankingButton;

    private ArrayList<Button> mButtonList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fast_paying, container, false);

        mPayCommunicationButton = (Button) v.findViewById(R.id.pay_communication);
        mPayCommunicationButton.setOnClickListener(this);

        mPayInternetButton = (Button) v.findViewById(R.id.pay_internet);
        mPayInternetButton.setOnClickListener(this);

        mRemittancesButton = (Button) v.findViewById(R.id.pay_remittances);
        mRemittancesButton.setOnClickListener(this);

        mBankingButton = (Button) v.findViewById(R.id.pay_banking);
        mBankingButton.setOnClickListener(this);

        mButtonList.add(mPayCommunicationButton);
        mButtonList.add(mPayInternetButton);
        mButtonList.add(mRemittancesButton);
        mButtonList.add(mBankingButton);

        getChildFragmentManager().beginTransaction().add(R.id.paying_fragment_panel, new PayCommunicationFragment()).commit();
        changeButtonState(mPayCommunicationButton);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_communication:
                setFragment(mPayCommunicationButton, new PayCommunicationFragment());
                break;

            case R.id.pay_internet:
                setFragment(mPayInternetButton, new PayInternetFragment());
                break;

            case R.id.pay_remittances:
                setFragment(mRemittancesButton, new PayRemittancesFragment());
                break;

            default:
                setFragment(mBankingButton, new PayBankingFragment());
                break;
        }

    }

    private void setFragment(Button activeButton, Fragment fragment) {
        for (Button button : mButtonList) {
            button.setBackground(getResources().getDrawable(R.drawable.digit_button));
        }

        getChildFragmentManager().beginTransaction().
                setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right).
                replace(R.id.paying_fragment_panel, fragment).commit();

        changeButtonState(activeButton);
    }

    private void changeButtonState(Button activeButton) {
        activeButton.setBackground(getResources().getDrawable(R.drawable.pay_button_pressed));
    }

}
