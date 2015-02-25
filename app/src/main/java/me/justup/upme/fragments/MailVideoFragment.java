package me.justup.upme.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoRenderer;

import me.justup.upme.R;
import me.justup.upme.webrtc.VideoStreamsView;
import me.justup.upme.webrtc.WebRtcClient;

import static me.justup.upme.utils.LogUtils.makeLogTag;


public class MailVideoFragment extends Fragment implements WebRtcClient.RTCListener {
    private static final String TAG = makeLogTag(MailVideoFragment.class);
    private static final String FRIEND_ID = "mail_video_friend_id";

    private final static int VIDEO_CALL_SENT = 666;
    private VideoStreamsView vsv;
    private WebRtcClient client;
    private String mSocketAddress;
    private String callerId;


    private String mFriendId;

    public static MailVideoFragment newInstance(String idFriend) {
        MailVideoFragment fragment = new MailVideoFragment();
        Bundle args = new Bundle();
        args.putString(FRIEND_ID, idFriend);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFriendId = getArguments().getString(FRIEND_ID, "");

        mSocketAddress = "http://" + getResources().getString(R.string.host_webrtc);
        mSocketAddress += (":" + getResources().getString(R.string.port_webrtc) + "/");

        PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true, null);

        // for fragment
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);

        vsv = new VideoStreamsView(getActivity(), displaySize);

        client = new WebRtcClient(this, mSocketAddress);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mail_video, container, false);

        Log.d("TAG11", "onCreateView");
        Button mMailMessageCloseButton = (Button) view.findViewById(R.id.mail_video_close_button);
        mMailMessageCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragment().getChildFragmentManager().beginTransaction().remove(MailVideoFragment.this).commit();
            }
        });


        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        vsv.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        vsv.onResume();
    }

    @Override
    public void onCallReady(String callId) {
        if (callerId != null) {
            try {
                answer(callerId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            call(callId);
        }
    }

    public void answer(String callerId) throws JSONException {
        client.sendMessage(callerId, "init", null);
        startCam();
    }

    // dialog
    public void call(String callId) {
        Intent msg = new Intent(Intent.ACTION_SEND);
        msg.putExtra(Intent.EXTRA_TEXT, mSocketAddress + callId);
        msg.setType("text/plain");
        startActivityForResult(Intent.createChooser(msg, "Call someone :"), VIDEO_CALL_SENT);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d(TAG, "onActivityResult ----------");
//        if (requestCode == VIDEO_CALL_SENT) {
//            startCam();
//        }
//    }

    public void startCam() {
        getActivity().setContentView(vsv);
        // Camera settings
        client.setCamera("front", "640", "480");
        client.start("android_test", true);
    }

    @Override
    public void onStatusChanged(final String newStatus) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity().getApplicationContext(), newStatus, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLocalStream(MediaStream localStream) {
        localStream.videoTracks.get(0).addRenderer(new VideoRenderer(new VideoCallbacks(vsv, 0)));
    }

    @Override
    public void onAddRemoteStream(MediaStream remoteStream, int endPoint) {
        remoteStream.videoTracks.get(0).addRenderer(new VideoRenderer(new VideoCallbacks(vsv, endPoint)));
        vsv.shouldDraw[endPoint] = true;
    }

    @Override
    public void onRemoveRemoteStream(MediaStream remoteStream, int endPoint) {
        remoteStream.videoTracks.get(0).dispose();
        vsv.shouldDraw[endPoint] = false;
    }

    // Implementation detail: bridge the VideoRenderer.Callbacks interface to the
    // VideoStreamsView implementation.
    private class VideoCallbacks implements VideoRenderer.Callbacks {
        private final VideoStreamsView view;
        private final int stream;

        public VideoCallbacks(VideoStreamsView view, int stream) {
            this.view = view;
            this.stream = stream;
        }

        @Override
        public void setSize(final int width, final int height) {
            view.queueEvent(new Runnable() {
                public void run() {
                    view.setSize(stream, width, height);
                }
            });
        }

        @Override
        public void renderFrame(VideoRenderer.I420Frame frame) {
            view.queueFrame(stream, frame);
        }
    }

}
