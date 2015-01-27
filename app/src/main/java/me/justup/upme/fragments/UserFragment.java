package me.justup.upme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import me.justup.upme.R;
import me.justup.upme.entity.BaseHttpQueryEntity;
import me.justup.upme.entity.GetLoggedUserInfoResponse;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.interfaces.OnCloseFragment;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class UserFragment extends Fragment implements OnMapReadyCallback, OnCloseFragment {
    private static final String TAG = makeLogTag(UserFragment.class);
    private static final String ENTITY_KEY = "user_fragment_entity_key";

    private FrameLayout mOrderingFragmentContainer;
    private Animation mFragmentSliderOut;
    private Animation mFragmentSliderIn;
    private Fragment mUserOrderingFragment;
    private TextView mUserName;


    public static UserFragment newInstance(BaseHttpQueryEntity entity) {
        UserFragment fragment = new UserFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(ENTITY_KEY, entity);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        BaseHttpQueryEntity mEntity = (BaseHttpQueryEntity) getArguments().getSerializable(ENTITY_KEY);
        ApiWrapper.query(mEntity, new OnGetUserInfoResponse());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    MapFragment mMapFragment = MapFragment.newInstance();
                    getChildFragmentManager().beginTransaction().replace(R.id.map, mMapFragment).commit();
                    mMapFragment.getMapAsync(UserFragment.this);
                }
            }
        }, 500);

        mUserName = (TextView) view.findViewById(R.id.user_name_textView);

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

    private class OnGetUserInfoResponse extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGD(TAG, "onSuccess(): " + content);

            GetLoggedUserInfoResponse response = null;
            try {
                response = ApiWrapper.gson.fromJson(content, GetLoggedUserInfoResponse.class);
            } catch (JsonSyntaxException e) {
                LOGE(TAG, "gson.fromJson:\n" + content);
            }

            if (response != null && response.result != null) {
                mUserName.setText(response.result.name);
            }

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGE(TAG, "onFailure(): " + content);
        }
    }

}
