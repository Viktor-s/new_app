package me.justup.upme.fragments;

import android.app.Fragment;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import me.justup.upme.R;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class PayCommunicationFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = makeLogTag(PayCommunicationFragment.class);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pay_communication, container, false);

        TextView mExtractCompletions = (TextView) v.findViewById(R.id.extract_completions_textView);
        mExtractCompletions.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        Spinner spinner = (Spinner) v.findViewById(R.id.pay_communication_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_pay_panel, PayInternetFragment.mTestData);
        adapter.setDropDownViewResource(R.layout.spinner_pay_panel_dropdown);
        spinner.setAdapter(adapter);
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
