package me.justup.upme.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

import me.justup.upme.R;
import me.justup.upme.apprtc.AppRTCAudioManager;
import me.justup.upme.apprtc.AppRTCClient;
import me.justup.upme.apprtc.PeerConnectionClient;
import me.justup.upme.apprtc.UnhandledExceptionHandler;
import me.justup.upme.apprtc.WebSocketRTCClient;
import me.justup.upme.utils.LooperExecutor;

import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class WebRtcFragment extends Fragment implements AppRTCClient.SignalingEvents, PeerConnectionClient.PeerConnectionEvents, CallFragment.OnCallEvents {
    private static final String TAG = makeLogTag(WebRtcFragment.class);

    private static final String ROOM_ID = "room_id";


    private SharedPreferences sharedPref;
    private String keyprefResolution;
    private String keyprefFps;
    private String keyprefBitrateType;
    private String keyprefBitrateValue;
    private String keyprefVideoCodec;
    private String keyprefHwCodecAcceleration;
    private String keyprefCpuUsageDetection;
    private String keyprefDisplayHud;
    private String keyprefRoomServerUrl;
    private String keyprefRoom;
    private String keyprefRoomList;

    public static final String EXTRA_ROOMID = "apprtc.ROOMID";
    public static final String EXTRA_LOOPBACK = "apprtc.LOOPBACK";
    public static final String EXTRA_HWCODEC = "apprtc.HWCODEC";
    public static final String EXTRA_VIDEO_BITRATE = "apprtc.VIDEO_BITRATE";
    public static final String EXTRA_VIDEO_WIDTH = "apprtc.VIDEO_WIDTH";
    public static final String EXTRA_VIDEO_HEIGHT = "apprtc.VIDEO_HEIGHT";
    public static final String EXTRA_VIDEO_FPS = "apprtc.VIDEO_FPS";
    public static final String EXTRA_VIDEOCODEC = "apprtc.VIDEOCODEC";
    public static final String EXTRA_CPUOVERUSE_DETECTION = "apprtc.CPUOVERUSE_DETECTION";
    public static final String EXTRA_DISPLAY_HUD = "apprtc.DISPLAY_HUD";
    public static final String EXTRA_CMDLINE = "appspot.apprtc.CMDLINE";
    public static final String EXTRA_RUNTIME = "apprtc.RUNTIME";

    // Peer connection statistics callback period in ms.
    private static final int STAT_CALLBACK_PERIOD = 1000;
    // Local preview screen position before call is connected.
    private static final int LOCAL_X_CONNECTING = 0;
    private static final int LOCAL_Y_CONNECTING = 0;
    private static final int LOCAL_WIDTH_CONNECTING = 100;
    private static final int LOCAL_HEIGHT_CONNECTING = 100;
    // Local preview screen position after call is connected.
    private static final int LOCAL_X_CONNECTED = 72;
    private static final int LOCAL_Y_CONNECTED = 72;
    private static final int LOCAL_WIDTH_CONNECTED = 25;
    private static final int LOCAL_HEIGHT_CONNECTED = 25;
    // Remote video screen position
    private static final int REMOTE_X = 0;
    private static final int REMOTE_Y = 0;
    private static final int REMOTE_WIDTH = 100;
    private static final int REMOTE_HEIGHT = 100;

    private PeerConnectionClient peerConnectionClient = null;
    private AppRTCClient appRtcClient;
    private AppRTCClient.SignalingParameters signalingParameters;
    private AppRTCAudioManager audioManager = null;
    private VideoRenderer.Callbacks localRender;
    private VideoRenderer.Callbacks remoteRender;
    private VideoRendererGui.ScalingType scalingType;
    private Toast logToast;
    private boolean commandLineRun = false;
    private boolean activityRunning;
    private AppRTCClient.RoomConnectionParameters roomConnectionParameters;
    private PeerConnectionClient.PeerConnectionParameters peerConnectionParameters;
    private boolean hwCodecAcceleration;
    private String videoCodec;
    private boolean iceConnected;
    private boolean isError;
    private boolean callControlFragmentVisible = true;

    // Controls
    private GLSurfaceView videoView;
    CallFragment callFragment;

    private String roomId;

    public static WebRtcFragment newInstance(String roomId) {
        Bundle args = new Bundle();
        args.putString(ROOM_ID, roomId);
        WebRtcFragment fragment = new WebRtcFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomId = (String) getArguments().getSerializable(ROOM_ID);
        Log.d("TAG_11", "onCreate roomId" + roomId);

        // Get setting keys.
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        keyprefResolution = getString(R.string.pref_resolution_key);
        keyprefFps = getString(R.string.pref_fps_key);
        keyprefBitrateType = getString(R.string.pref_startbitrate_key);
        keyprefBitrateValue = getString(R.string.pref_startbitratevalue_key);
        keyprefVideoCodec = getString(R.string.pref_videocodec_key);
        keyprefHwCodecAcceleration = getString(R.string.pref_hwcodec_key);
        keyprefCpuUsageDetection = getString(R.string.pref_cpu_usage_detection_key);
        keyprefDisplayHud = getString(R.string.pref_displayhud_key);
        keyprefRoomServerUrl = getString(R.string.pref_room_server_url_key);
        keyprefRoom = getString(R.string.pref_room_key);
        keyprefRoomList = getString(R.string.pref_room_list_key);

        Thread.setDefaultUncaughtExceptionHandler(new UnhandledExceptionHandler(getActivity()));

        iceConnected = false;
        signalingParameters = null;
        scalingType = VideoRendererGui.ScalingType.SCALE_ASPECT_FILL;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_call, container, false);

        // Create UI controls.
        videoView = (GLSurfaceView) view.findViewById(R.id.glview_call);

        // Create video renderers.
        VideoRendererGui.setView(videoView, new Runnable() {
            @Override
            public void run() {
                createPeerConnectionFactory();
            }
        });
        remoteRender = VideoRendererGui.create(REMOTE_X, REMOTE_Y, REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, false);
        localRender = VideoRendererGui.create(LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING, LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING, scalingType, true);

        // Show/hide call control fragment on view click.
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleCallControlFragmentVisibility();
            }
        });

        ////////////////////// ////////////////////// ////////////////////// ///////////////////////

        String roomUrl = sharedPref.getString(keyprefRoomServerUrl, getString(R.string.pref_room_server_url_default));

        // Get default video codec.
        videoCodec = sharedPref.getString(keyprefVideoCodec, getString(R.string.pref_videocodec_default));

        // Check HW codec flag.
        hwCodecAcceleration = sharedPref.getBoolean(keyprefHwCodecAcceleration, Boolean.valueOf(getString(R.string.pref_hwcodec_default)));

        // Get video resolution from settings.
        int videoWidth = 0;
        int videoHeight = 0;
        String resolution = sharedPref.getString(keyprefResolution, getString(R.string.pref_resolution_default));
        String[] dimensions = resolution.split("[ x]+");
        if (dimensions.length == 2) {
            try {
                videoWidth = Integer.parseInt(dimensions[0]);
                videoHeight = Integer.parseInt(dimensions[1]);
            } catch (NumberFormatException e) {
                videoWidth = 0;
                videoHeight = 0;
                Log.e(TAG, "Wrong video resolution setting: " + resolution);
            }
        }

        // Get camera fps from settings.
        int cameraFps = 0;
        String fps = sharedPref.getString(keyprefFps, getString(R.string.pref_fps_default));
        String[] fpsValues = fps.split("[ x]+");
        if (fpsValues.length == 2) {
            try {
                cameraFps = Integer.parseInt(fpsValues[0]);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Wrong camera fps setting: " + fps);
            }
        }

        // Get start bitrate.
        int startBitrate = 0;
        String bitrateTypeDefault = getString(R.string.pref_startbitrate_default);
        String bitrateType = sharedPref.getString(keyprefBitrateType, bitrateTypeDefault);
        if (!bitrateType.equals(bitrateTypeDefault)) {
            String bitrateValue = sharedPref.getString(keyprefBitrateValue, getString(R.string.pref_startbitratevalue_default));
            startBitrate = Integer.parseInt(bitrateValue);
        }

        // Test if CpuOveruseDetection should be disabled. By default is on.
        boolean cpuOveruseDetection = sharedPref.getBoolean(keyprefCpuUsageDetection, Boolean.valueOf(getString(R.string.pref_cpu_usage_detection_default)));

        // Check statistics display option.
        boolean displayHud = sharedPref.getBoolean(keyprefDisplayHud, Boolean.valueOf(getString(R.string.pref_displayhud_default)));


        Log.d("TAG", "roomUrl - " + roomUrl + " videoCodec - " + videoCodec + " hwCodecAcceleration - " + hwCodecAcceleration +
                " resolution - " + resolution + " fps - " + fps + " bitrateType - " + bitrateType + " bitrateValue - " + startBitrate +
                " cpuOveruseDetection - " + cpuOveruseDetection + " displayHud - " + displayHud);
        ////////////////////////////////////////////////////////////////////////////////////////////

        Uri roomUri;
        if (validateUrl(roomUrl))
            roomUri = Uri.parse(roomUrl);
        else {
            roomUri = Uri.parse("");
            logAndToast(getString(R.string.missing_url));
            Log.e(TAG, "Didn't get any URL in intent!");
        }
        peerConnectionParameters = new PeerConnectionClient.PeerConnectionParameters(videoWidth, videoHeight, cameraFps, startBitrate, cpuOveruseDetection);


        // Create connection client and connection parameters.
        appRtcClient = new WebSocketRTCClient(this, new LooperExecutor());
        Boolean loopback = false; // сам себе
        roomConnectionParameters = new AppRTCClient.RoomConnectionParameters(roomUri.toString(), roomId, loopback);

        // Send intent arguments to fragment.
        callFragment = new CallFragment();
        Bundle bundle = new Bundle();
        bundle.putString(WebRtcFragment.EXTRA_ROOMID, roomId);
        bundle.putBoolean(WebRtcFragment.EXTRA_DISPLAY_HUD, displayHud);
        callFragment.setArguments(bundle);

        // Activate call fragment and start the call.
        // getActivity().getFragmentManager().beginTransaction().add(R.id.call_fragment_container, callFragment).commit();
        getChildFragmentManager().beginTransaction().add(R.id.call_fragment_container, callFragment).commit();

        startCall();

        // For command line execution run connection for <runTimeMs> and exit.
        int runTimeMs = 0;
        if (commandLineRun && runTimeMs > 0) {
            videoView.postDelayed(new Runnable() {
                public void run() {
                    disconnect();
                }
            }, runTimeMs);
        }

        LOGI(TAG, "WebRTC init");
//        videoView.onResume();
        activityRunning = true;
//        if (peerConnectionClient != null) {
//            peerConnectionClient.startVideoSource();
//        }

        return view;
    }

    // Activity interfaces
    /*
    @Override
    public void onPause() {
        super.onPause();

        videoView.onPause();
        activityRunning = false;
        if (peerConnectionClient != null) {
            peerConnectionClient.stopVideoSource();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        videoView.onResume();
        activityRunning = true;
        if (peerConnectionClient != null) {
            peerConnectionClient.startVideoSource();
        }
    }
    */

    @Override
    public void onStop() {
        super.onStop();

        disconnect();

        if (logToast != null) {
            logToast.cancel();
        }
        activityRunning = false;
    }

    /*
    @Override
    public void onDestroy() {

        super.onDestroy();
        if (logToast != null) {
            logToast.cancel();
        }
        activityRunning = false;
    }
    */

    // CallFragment.OnCallEvents interface implementation.
    @Override
    public void onCallHangUp() {
        disconnect();
    }

    @Override
    public void onCameraSwitch() {
        if (peerConnectionClient != null) {
            peerConnectionClient.switchCamera();
        }
    }

    @Override
    public void onVideoScalingSwitch(VideoRendererGui.ScalingType scalingType) {
        this.scalingType = scalingType;
        RelativeLayout containerVideoChat = (RelativeLayout) getActivity().findViewById(R.id.container_video_chat);
        if (scalingType == VideoRendererGui.ScalingType.SCALE_ASPECT_FIT)
            containerVideoChat.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        else {
            int dpValue = 250;
            float d = getActivity().getResources().getDisplayMetrics().density;
            int size = (int) (dpValue * d);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
            params.addRule(RelativeLayout.ALIGN_PARENT_END, 1);
            containerVideoChat.setLayoutParams(params);
        }
        // updateVideoView();
    }

    // Helper functions.
    private void toggleCallControlFragmentVisibility() {
        if (!iceConnected || !callFragment.isAdded()) {
            return;
        }
        // Show/hide call control fragment
        callControlFragmentVisible = !callControlFragmentVisible;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (callControlFragmentVisible) {
            ft.show(callFragment);
        } else {
            ft.hide(callFragment);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private void updateVideoView() {
        VideoRendererGui.update(remoteRender, REMOTE_X, REMOTE_Y, REMOTE_WIDTH, REMOTE_HEIGHT, scalingType);
        if (iceConnected) {
            VideoRendererGui.update(localRender, LOCAL_X_CONNECTED, LOCAL_Y_CONNECTED, LOCAL_WIDTH_CONNECTED, LOCAL_HEIGHT_CONNECTED, VideoRendererGui.ScalingType.SCALE_ASPECT_FIT);
        } else {
            VideoRendererGui.update(localRender, LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING, LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING, scalingType);
        }
    }

    private void startCall() {
        if (appRtcClient == null) {
            Log.e(TAG, "AppRTC client is not allocated for a call.");
            return;
        }
        // Start room connection.
        logAndToast(getString(R.string.connecting_to, roomConnectionParameters.roomUrl));
        appRtcClient.connectToRoom(roomConnectionParameters);

        // Create and audio manager that will take care of audio routing, audio modes, audio device enumeration etc.
        audioManager = AppRTCAudioManager.create(getActivity(), new Runnable() {
                    // This method will be called each time the audio state (number and type of devices) has been changed.
                    @Override
                    public void run() {
                        onAudioManagerChangedState();
                    }
                }
        );
        // Store existing audio settings and change audio mode to
        // MODE_IN_COMMUNICATION for best possible VoIP performance.
        Log.d(TAG, "Initializing the audio manager...");
        audioManager.init();
    }

    // Should be called from UI thread
    private void callConnected() {
        // Update video view.
        updateVideoView();
        // Enable statistics callback.
        peerConnectionClient.enableStatsEvents(true, STAT_CALLBACK_PERIOD);
    }

    private void onAudioManagerChangedState() {
        // TODO(henrika): disable video if AppRTCAudioManager.AudioDevice.EARPIECE
        // is active.
    }

    // Create peer connection factory when EGL context is ready.
    private void createPeerConnectionFactory() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient == null) {
                    peerConnectionClient = new PeerConnectionClient();
                    peerConnectionClient.createPeerConnectionFactory(getActivity(), videoCodec, hwCodecAcceleration, VideoRendererGui.getEGLContext(), WebRtcFragment.this);
                }
                if (signalingParameters != null) {
                    Log.w(TAG, "EGL context is ready after room connection.");
                    onConnectedToRoomInternal(signalingParameters);
                }
            }
        });
    }

    // Disconnect from remote resources, dispose of local resources, and exit.
    private void disconnect() {
        LOGI(TAG, "disconnect()");

        if (appRtcClient != null) {
            appRtcClient.disconnectFromRoom();
            appRtcClient = null;
        }

        if (peerConnectionClient != null) {
            peerConnectionClient.close();
            peerConnectionClient = null;
        }

        if (audioManager != null) {
            audioManager.close();
            audioManager = null;
        }

        if (iceConnected && !isError) {
            // Toast.makeText(getActivity(), "RESULT_OK", Toast.LENGTH_SHORT).show();
            LOGI(TAG, "RESULT_OK");
        } else {
            // Toast.makeText(getActivity(), "RESULT_CANCELED", Toast.LENGTH_SHORT).show();
            LOGE(TAG, "RESULT_CANCELED");
        }

        if (callFragment != null) {
            getChildFragmentManager().beginTransaction().remove(callFragment).commitAllowingStateLoss();
        }
        getActivity().getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
    }

    private void disconnectWithErrorMessage(final String errorMessage) {
        if (commandLineRun || !activityRunning) {
            LOGE(TAG, "Critical error: " + errorMessage);

            disconnect();
        } else {
            LOGE(TAG, "disconnectWithErrorMessage DIALOG");

            if (WebRtcFragment.this.isAdded()) {
                new AlertDialog.Builder(getActivity()).setTitle(getText(R.string.channel_error_title))
                        .setMessage(errorMessage)
                        .setCancelable(false)
                        .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                disconnect();
                            }
                        }).create().show();
            }

        }
    }

    // Log |msg| and Toast about it.
    private void logAndToast(String msg) {
        Log.d(TAG, msg);
        if (logToast != null) {
            logToast.cancel();
        }
        logToast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
        logToast.show();
    }

    // -----Implementation of AppRTCClient.AppRTCSignalingEvents ---------------
    // All callbacks are invoked from websocket signaling looper thread and are routed to UI thread.
    private void onConnectedToRoomInternal(final AppRTCClient.SignalingParameters params) {
        signalingParameters = params;
        if (peerConnectionClient == null) {
            Log.w(TAG, "Room is connected, but EGL context is not ready yet.");
            return;
        }
        logAndToast("Creating peer connection...");
        peerConnectionClient.createPeerConnection(localRender, remoteRender, signalingParameters, peerConnectionParameters);

        if (signalingParameters.initiator) {
            logAndToast("Creating OFFER...");
            // Create offer. Offer SDP will be sent to answering client in PeerConnectionEvents.onLocalDescription event.
            peerConnectionClient.createOffer();
        } else {
            if (params.offerSdp != null) {
                peerConnectionClient.setRemoteDescription(params.offerSdp);
                logAndToast("Creating ANSWER...");
                // Create answer. Answer SDP will be sent to offering client in PeerConnectionEvents.onLocalDescription event.
                peerConnectionClient.createAnswer();
            }
            if (params.iceCandidates != null) {
                // Add remote ICE candidates from room.
                for (IceCandidate iceCandidate : params.iceCandidates) {
                    peerConnectionClient.addRemoteIceCandidate(iceCandidate);
                }
            }
        }
    }

    @Override
    public void onConnectedToRoom(final AppRTCClient.SignalingParameters params) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onConnectedToRoomInternal(params);
            }
        });
    }

    @Override
    public void onRemoteDescription(final SessionDescription sdp) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient == null) {
                    Log.e(TAG, "Received remote SDP for non-initilized peer connection.");
                    return;
                }
                logAndToast("Received remote " + sdp.type + " ...");
                peerConnectionClient.setRemoteDescription(sdp);
                if (!signalingParameters.initiator) {
                    logAndToast("Creating ANSWER...");
                    // Create answer. Answer SDP will be sent to offering client in
                    // PeerConnectionEvents.onLocalDescription event.
                    peerConnectionClient.createAnswer();
                }
            }
        });
    }

    @Override
    public void onRemoteIceCandidate(final IceCandidate candidate) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient == null) {
                    Log.e(TAG, "Received ICE candidate for non-initilized peer connection.");
                    return;
                }
                peerConnectionClient.addRemoteIceCandidate(candidate);
            }
        });
    }

    @Override
    public void onChannelClose() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("Remote end hung up; dropping PeerConnection");
                disconnect();
            }
        });
    }

    @Override
    public void onChannelError(final String description) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isError) {
                    isError = true;
                    disconnectWithErrorMessage(description);
                }
            }
        });
    }

    // -----Implementation of PeerConnectionClient.PeerConnectionEvents.---------
    // Send local peer connection SDP and ICE candidates to remote party.
    // All callbacks are invoked from peer connection client looper thread and
    // are routed to UI thread.
    @Override
    public void onLocalDescription(final SessionDescription sdp) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (appRtcClient != null) {
                    logAndToast("Sending " + sdp.type + " ...");
                    if (signalingParameters.initiator) {
                        appRtcClient.sendOfferSdp(sdp);
                    } else {
                        appRtcClient.sendAnswerSdp(sdp);
                    }
                }
            }
        });
    }

    @Override
    public void onIceCandidate(final IceCandidate candidate) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (appRtcClient != null) {
                    appRtcClient.sendLocalIceCandidate(candidate);
                }
            }
        });
    }

    @Override
    public void onIceConnected() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("ICE connected");
                iceConnected = true;
                callConnected();
            }
        });
    }

    @Override
    public void onIceDisconnected() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("ICE disconnected");
                iceConnected = false;
                disconnect();
            }
        });
    }

    @Override
    public void onPeerConnectionClosed() {
    }

    @Override
    public void onPeerConnectionStatsReady(final StatsReport[] reports) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isError && iceConnected) {
                    callFragment.updateEncoderStatistics(reports);
                }
            }
        });
    }

    @Override
    public void onPeerConnectionError(final String description) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isError) {
                    isError = true;
                    disconnectWithErrorMessage(description);
                }
            }
        });
    }

    private boolean validateUrl(String url) {
        if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
            return true;
        }

        new AlertDialog.Builder(getActivity())
                .setTitle(getText(R.string.invalid_url_title))
                .setMessage(getString(R.string.invalid_url_text, url))
                .setCancelable(false)
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).create().show();
        return false;
    }

}
