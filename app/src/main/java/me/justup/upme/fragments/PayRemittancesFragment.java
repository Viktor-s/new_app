package me.justup.upme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import me.justup.upme.R;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class PayRemittancesFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = makeLogTag(PayRemittancesFragment.class);

    private View mContentView = null;

    // Instance
    public static PayRemittancesFragment newInstance() {
        return new PayRemittancesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = super.onCreateView(inflater, container, savedInstanceState);

        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_pay_remittances, container, false);
        }

        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init UI
        if (getActivity() != null) {
            initUI();
        }
    }

    private void initUI(){
        Spinner spinner = (Spinner) mContentView.findViewById(R.id.pay_remittances_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_pay_panel, PayInternetFragment.mTestData);
        adapter.setDropDownViewResource(R.layout.spinner_pay_panel_dropdown);
        spinner.setAdapter(adapter);
        // spinner.setSelection(2);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        LOGD(TAG, "position:" + position + "\nparent.getItemAtPosition(pos):" + parent.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}
