package me.justup.upme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import me.justup.upme.R;
import me.justup.upme.interfaces.OnCloseFragment;


public class UserFragment extends Fragment implements OnMapReadyCallback, OnCloseFragment {
    private FrameLayout mOrderingFragmentContainer;
    private Animation mFragmentSliderOut;
    private Animation mFragmentSliderIn;
    private Fragment mUserOrderingFragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button mGetOrder = (Button) view.findViewById(R.id.ordering_button);
        mGetOrder.setOnClickListener(new OnGetOrderListener());

        mOrderingFragmentContainer = (FrameLayout) view.findViewById(R.id.ordering_fragment_container);
        mFragmentSliderOut = AnimationUtils.loadAnimation(getActivity(), R.anim.order_slider_out);
        mFragmentSliderIn = AnimationUtils.loadAnimation(getActivity(), R.anim.order_slider_in);

        mUserOrderingFragment = UserOrderingFragment.newInstance(42);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng city = new LatLng(49.99356, 36.239519);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(city, 15));

        map.addMarker(new MarkerOptions()
                .title("Kharkov")
                .snippet("AppDragon")
                .position(city));
    }

    private class OnGetOrderListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            getChildFragmentManager().beginTransaction().add(R.id.ordering_fragment_container, mUserOrderingFragment).commit();

            mOrderingFragmentContainer.setVisibility(View.VISIBLE);
            mOrderingFragmentContainer.startAnimation(mFragmentSliderIn);
        }
    }

    @Override
    public void onCloseFragment() {
        mOrderingFragmentContainer.startAnimation(mFragmentSliderOut);
        mOrderingFragmentContainer.setVisibility(View.GONE);

        getChildFragmentManager().beginTransaction().remove(mUserOrderingFragment).commit();
    }

}
