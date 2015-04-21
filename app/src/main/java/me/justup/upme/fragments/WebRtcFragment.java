package me.justup.upme.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;

import me.justup.upme.JustUpApplication;
import me.justup.upme.MainActivity;
import me.justup.upme.R;
import me.justup.upme.apprtc.AppRTCAudioManager;
import me.justup.upme.apprtc.AppRTCClient;
import me.justup.upme.apprtc.PeerConnectionClient;
import me.justup.upme.apprtc.UnhandledExceptionHandler;
import me.justup.upme.apprtc.WebSocketRTCClient;
import me.justup.upme.entity.WebRtcStartCallQuery;
import me.justup.upme.services.PushIntentService;
import me.justup.upme.utils.LooperExecutor;

import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;

/**
 * Fragment for peer connection call setup, call waiting
 * and call view.
 */
public class WebRtcFragment extends Fragment implements AppRTCClient.SignalingEvents, PeerConnectionClient.PeerConnectionEvents, CallFragment.OnCallEvents {
    private static final String TAG = makeLogTag(WebRtcFragment.class);

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

    private static final int TIMER = 15000;
    private volatile boolean callAccepted = true;
    private CountDownTimer timerCall;

    private PeerConnectionClient peerConnectionClient = null;
    private AppRTCClient appRtcClient = null;
    private AppRTCClient.SignalingParameters signalingParameters = null;
    private AppRTCAudioManager audioManager = null;
    private VideoRenderer.Callbacks localRender = null;
    private VideoRenderer.Callbacks remoteRender = null;
    private VideoRendererGui.ScalingType scalingType = null;
    private Toast logToast = null;
    private boolean commandLineRun;
    private int runTimeMs;
    private boolean activityRunning;
    private AppRTCClient.RoomConnectionParameters roomConnectionParameters = null;
    private PeerConnectionClient.PeerConnectionParameters peerConnectionParameters = null;
    private boolean hwCodecAcceleration;
    private String videoCodec = null;
    private boolean iceConnected;
    private boolean isError;
    private boolean callControlFragmentVisible = true;

    // Controls
    private GLSurfaceView videoView = null;
    CallFragment callFragment = null;

    private View mContentView = null;

    private String roomId;
    private int idPerson;
    private String contactName;

    // Audio
    private SoundPool soundPool;
    private int soundID;
    private boolean plays = false;
    private boolean loaded = false;
    private float actVolume, maxVolume, volume;
    private AudioManager audioManagerSing;
    private int counter;
    private boolean isRorated = false;


    public static WebRtcFragment newInstance(Bundle callParam) {
        WebRtcFragment fragment = new WebRtcFragment();
        fragment.setArguments(callParam);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        audioManagerSing = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        actVolume = (float) audioManagerSing.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManagerSing.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;

        //Hardware buttons setting to adjust the media sound
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // the counter will help us recognize the stream id of the sound played  now
        counter = 0;

        // Load the sounds
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });
        soundID = soundPool.load(getActivity(), R.raw.beep, 1);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = super.onCreateView(inflater, container, savedInstanceState);

        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.activity_call, container, false);
        }

        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init UI
        if (getActivity() != null) {

            // Init Data
            initUI();
        }
    }

    private void initUI() {
        Thread.setDefaultUncaughtExceptionHandler(new UnhandledExceptionHandler(getActivity()));

        iceConnected = false;
        signalingParameters = null;
        scalingType = VideoRendererGui.ScalingType.SCALE_ASPECT_FILL;

        // Create UI controls.
        videoView = (GLSurfaceView) mContentView.findViewById(R.id.glview_call);
        callFragment = new CallFragment();

        // Create video renders.
        VideoRendererGui.setView(videoView, new Runnable() {
            @Override
            public void run() {
                createPeerConnectionFactory();
            }
        });

        remoteRender = VideoRendererGui.create(
                REMOTE_X, REMOTE_Y,
                REMOTE_WIDTH, REMOTE_HEIGHT, scalingType, false);

        localRender = VideoRendererGui.create(
                LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING,
                LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING, scalingType, true);

        // Show/hide call control fragment on view click.
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleCallControlFragmentVisibility();
            }
        });

        // Get Bundle parameters.
        final Bundle args = getArguments();

        String roomUrlString = args.getString(JustUpApplication.EXTRA_ROOM_URL);
        Uri roomUri = null;
        if (roomUrlString == null) {
            logAndToast(getString(R.string.missing_url));
            Log.e(TAG, "Didn't get any URL in intent!");

            // setResult(RESULT_CANCELED);
            // finish();

            return;
        } else {
            roomUri = Uri.parse(roomUrlString);
        }

        roomId = args.getString(JustUpApplication.EXTRA_ROOMID);
        if (roomId == null || roomId.length() == 0) {
            logAndToast(getString(R.string.missing_url));
            Log.e(TAG, "Incorrect room ID in intent!");

            // setResult(RESULT_CANCELED);
            // finish();

            return;
        }
        idPerson = args.getInt(JustUpApplication.EXTRA_IDPERSON);
        contactName = args.getString(JustUpApplication.EXTRA_CONTACT_NAME);

        boolean loopback = args.getBoolean(JustUpApplication.EXTRA_LOOPBACK, false);
        hwCodecAcceleration = args.getBoolean(JustUpApplication.EXTRA_HWCODEC, true);

        if (args.getString(JustUpApplication.EXTRA_VIDEOCODEC) != null) {
            videoCodec = args.getString(JustUpApplication.EXTRA_VIDEOCODEC);
        } else {
            videoCodec = PeerConnectionClient.VIDEO_CODEC_VP8; // use VP8 by default.
        }

        peerConnectionParameters = new PeerConnectionClient.PeerConnectionParameters(
                args.getInt(JustUpApplication.EXTRA_VIDEO_WIDTH, 0),
                args.getInt(JustUpApplication.EXTRA_VIDEO_HEIGHT, 0),
                args.getInt(JustUpApplication.EXTRA_VIDEO_FPS, 0),
                args.getInt(JustUpApplication.EXTRA_VIDEO_BITRATE, 0),
                args.getBoolean(JustUpApplication.EXTRA_CPUOVERUSE_DETECTION, true));

        commandLineRun = args.getBoolean(JustUpApplication.EXTRA_CMDLINE, false);

        runTimeMs = args.getInt(JustUpApplication.EXTRA_RUNTIME, 0);

        // Create connection client and connection parameters.
        appRtcClient = new WebSocketRTCClient(this, new LooperExecutor());
        roomConnectionParameters = new AppRTCClient.RoomConnectionParameters(roomUri.toString(), roomId, loopback);

        // Send intent arguments to fragment.
        callFragment.setArguments(args);
        // Activate call fragment and start the call.
        getFragmentManager().beginTransaction().add(R.id.call_fragment_container, callFragment).commit();
        startCall();

        // For command line execution run connection for <runTimeMs> and exit.
        if (commandLineRun && runTimeMs > 0) {
            videoView.postDelayed(new Runnable() {
                public void run() {
                    disconnect();
                }
            }, runTimeMs);
        }

        timerCall = new CountDownTimer(TIMER, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (!iceConnected) {
                    Toast.makeText(getActivity(), "Пользователь " + contactName + " не отвечет", Toast.LENGTH_SHORT).show();
                    disconnect();
                }
            }
        }.start();


    }

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

//    @Override
//    public void onStop() {
//        super.onStop();
//
//        disconnect();
//
//        if (logToast != null) {
//            logToast.cancel();
//        }
//
//        activityRunning = false;
//    }

    @Override
    public void onDestroy() {
        disconnect();
        super.onDestroy();
        if (logToast != null) {
            logToast.cancel();
        }

        activityRunning = false;
    }

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

        // New functionality
        RelativeLayout containerVideoChat = (RelativeLayout) getActivity().findViewById(R.id.container_video_chat);
        if (scalingType == VideoRendererGui.ScalingType.SCALE_ASPECT_FIT)
            containerVideoChat.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        else {
            // int dpValue = (int)getActivity().getResources().getDimension(R.dimen.base250dp720sw);
            int dpValue = 160;
            float d = getActivity().getResources().getDisplayMetrics().density;
            int size = (int) (dpValue * d);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
            params.addRule(RelativeLayout.ALIGN_PARENT_END, 1);
            containerVideoChat.setLayoutParams(params);
        }

        // updateVideoView();

    }

    private void updateVideoView() {
        VideoRendererGui.update(remoteRender, REMOTE_X, REMOTE_Y, REMOTE_WIDTH, REMOTE_HEIGHT, scalingType);

        if (iceConnected) {
            VideoRendererGui.update(localRender, LOCAL_X_CONNECTED, LOCAL_Y_CONNECTED, LOCAL_WIDTH_CONNECTED, LOCAL_HEIGHT_CONNECTED, VideoRendererGui.ScalingType.SCALE_ASPECT_FIT);
        } else {
            VideoRendererGui.update(localRender, LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING, LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING, scalingType);
        }
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

    private void startCall() {
        if (appRtcClient == null) {
            Log.e(TAG, "AppRTC client is not allocated for a call.");
            return;
        }

        // Start room connection.
        logAndToast(getString(R.string.connecting_to, roomConnectionParameters.roomUrl));
        appRtcClient.connectToRoom(roomConnectionParameters);
        if (!isRorated) {
            // Create and audio manager that will take care of audio routing, audio modes, audio device enumeration etc.
//            audioManager = AppRTCAudioManager.create(getActivity(), new Runnable() {
//                        // This method will be called each time the audio state (number and type of devices) has been changed.
//                        @Override
//                        public void run() {
//                            onAudioManagerChangedState();
//                        }
//                    }
//            );
//
//            // Store existing audio settings and change audio mode to
//            // MODE_IN_COMMUNICATION for best possible VoIP performance.
//            Log.d(TAG, "Initializing the audio manager...");
//            audioManager.init();
        }
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
//        callAccepted = false;
        stopSound();

        LOGI(TAG, "Disconnect()");

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

//        ((MainActivity) getActivity()).clearDataAfterCallRTC();
        timerCall.cancel();
        getActivity().findViewById(R.id.container_video_chat).setVisibility(View.GONE);
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();

    }

    private void disconnectWithErrorMessage(final String errorMessage) {
        if (commandLineRun || !activityRunning) {
            LOGE(TAG, "Critical error : " + errorMessage);

            disconnect();
        } else {
            LOGE(TAG, "DisconnectWithErrorMessage DIALOG");

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
            // logAndToast("Person ID -> " + idPerson);
            if (idPerson != 0) {
                playLoop();
                startNotificationIntent(idPerson, Integer.parseInt(roomId));
            } else
                disconnect();
        } else {
            if (params.offerSdp != null) {
                peerConnectionClient.setRemoteDescription(params.offerSdp);
                logAndToast("Creating ANSWER...");
                // Create answer. Answer SDP will be sent to offering client in PeerConnectionEvents.onLocalDescription event.
                peerConnectionClient.createAnswer();
            }

            if (params.iceCandidates != null) {
//                callAccepted = false;
                // Add remote ICE candidates from room.
                for (IceCandidate iceCandidate : params.iceCandidates) {
                    peerConnectionClient.addRemoteIceCandidate(iceCandidate);
                }
            }
        }
        // isRorated = true;
    }

    public void startNotificationIntent(int userId, int roomNumber) {
        WebRtcStartCallQuery push = new WebRtcStartCallQuery();
        push.params.setUserIds(userId);
        push.params.room_id = String.valueOf(roomNumber);

        Bundle bundle = new Bundle();
        bundle.putSerializable(PushIntentService.PUSH_INTENT_QUERY_EXTRA, push);

        Intent intent = new Intent(getActivity().getApplicationContext(), PushIntentService.class);
        getActivity().getApplicationContext().startService(intent.putExtras(bundle));
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
                if (appRtcClient != null && !isRorated) {
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

    ///////////////////// SOUND ////////////////////////////////////////////
    public void playLoop() {
        if (loaded && !plays) {
            // the sound will play for ever if we put the loop parameter -1
            soundPool.play(soundID, volume, volume, 1, -1, 1f);
            counter = counter++;
            plays = true;
        }
    }

    public void stopSound() {
        if (plays) {
            soundPool.stop(soundID);
            soundID = soundPool.load(getActivity(), R.raw.beep, counter);
            plays = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isRorated = true;
    }
}
