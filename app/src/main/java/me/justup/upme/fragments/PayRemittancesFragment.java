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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pay_remittances, container, false);

        Spinner spinner = (Spinner) v.findViewById(R.id.pay_remittances_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_pay_panel, PayInternetFragment.mTestData);
        adapter.setDropDownViewResource(R.layout.spinner_pay_panel_dropdown);
        spinner.setAdapter(adapter);
        // spinner.setSelection(2);
        spinner.setOnItemSelectedListener(this);

        return v;
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
