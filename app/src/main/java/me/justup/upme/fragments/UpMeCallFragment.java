package me.justup.upme.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.webrtc.VideoRendererGui.ScalingType;

import me.justup.upme.JustUpApplication;
import me.justup.upme.R;

import static me.justup.upme.utils.LogUtils.LOGE;

/**
 * Fragment for call control.
 */
public class UpMeCallFragment extends Fragment {
    private static final String TAG = UpMeCallFragment.class.getSimpleName();

    private View controlView = null;
    private TextView roomIdView = null;
    private ImageButton disconnectButton = null;
    private ImageButton cameraSwitchButton = null;
    private ImageButton videoScalingButton = null;
    private OnCallEvents callEvents = null;
    private ScalingType scalingType = null;
    private boolean videoCallEnabled = true;

    /**
     * Call control interface for container activity.
     */
    public interface OnCallEvents {
        public void onCallHangUp();
        public void onCameraSwitch();
        public void onVideoScalingSwitch(ScalingType scalingType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        controlView = inflater.inflate(R.layout.fragment_call, container, false);

        // Create UI controls.
        roomIdView = (TextView) controlView.findViewById(R.id.contact_name_call);
        disconnectButton = (ImageButton) controlView.findViewById(R.id.button_call_disconnect);
        cameraSwitchButton = (ImageButton) controlView.findViewById(R.id.button_call_switch_camera);
        videoScalingButton = (ImageButton) controlView.findViewById(R.id.button_call_scaling_mode);

        // Add buttons click events.
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callEvents.onCallHangUp();
            }
        });

        cameraSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callEvents.onCameraSwitch();
            }
        });

        videoScalingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scalingType == ScalingType.SCALE_ASPECT_FILL) {
                    videoScalingButton.setBackgroundResource(R.drawable.ic_action_full_screen);
                    scalingType = ScalingType.SCALE_ASPECT_FIT;
                } else {
                    videoScalingButton.setBackgroundResource(R.drawable.ic_action_return_from_full_screen);
                    scalingType = ScalingType.SCALE_ASPECT_FILL;
                }

                callEvents.onVideoScalingSwitch(scalingType);
            }
        });

        scalingType = ScalingType.SCALE_ASPECT_FILL;

        return controlView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args != null) {
            String roomId = args.getString(JustUpApplication.EXTRA_ROOMID);
            roomIdView.setText(roomId);
            videoCallEnabled = args.getBoolean(JustUpApplication.EXTRA_VIDEO_CALL, true);
        }

        if (!videoCallEnabled) {
            cameraSwitchButton.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            callEvents = (OnCallEvents) getActivity().getFragmentManager().findFragmentById(R.id.container_video_chat);
        } catch (ClassCastException e) {
            LOGE(TAG, "Must implement OnCallEvents", e);
        }
    }

}
