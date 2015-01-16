package me.justup.upme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import me.justup.upme.R;

import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class ProductsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = makeLogTag(ProductsFragment.class);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        View imageWithoutBail = view.findViewById(R.id.product_without_bail_image);
        View textWithoutBail = view.findViewById(R.id.credit_without_bail_text);
        imageWithoutBail.setOnClickListener(this);
        textWithoutBail.setOnClickListener(this);

        // logging example
        LOGI(TAG, "Fragment start");

        return view;
    }

    @Override
    public void onClick(View v) {

    }
}
