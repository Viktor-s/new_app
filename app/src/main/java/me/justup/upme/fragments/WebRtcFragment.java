package me.justup.upme.fragments;

import android.app.Activity;
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
import android.view.WindowManager;
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
import me.justup.upme.apprtc.ui.HudFragment;
import me.justup.upme.apprtc.util.LooperExecutor;
import me.justup.upme.entity.WebRtcStartCallQuery;
import me.justup.upme.services.PushIntentService;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.LOGW;
import static me.justup.upme.utils.LogUtils.makeLogTag;

/**
 * Fragment for peer connection call setup, call waiting
 * and call view.
 */
public class WebRtcFragment extends Fragment implements AppRTCClient.SignalingEvents, PeerConnectionClient.PeerConnectionEvents, UpMeCallFragment.OnCallEvents {
    public static final String TAG = makeLogTag(WebRtcFragment.class);

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

    private static final int TIMER = 35000;
    private CountDownTimer mTimerCall = null;

    private PeerConnectionClient mPeerConnectionClient = null;
    private AppRTCClient mAppRtcClient = null;
    private AppRTCClient.SignalingParameters mSignalingParameters = null;
    private AppRTCAudioManager mAudioManager = null;
    private VideoRenderer.Callbacks mLocalRender = null;
    private VideoRenderer.Callbacks mRemoteRender = null;
    private VideoRendererGui.ScalingType mScalingType = null;
    private Toast mLogToast = null;
    private boolean mCommandLineRun;
    private int mRunTimeMs;
    private boolean mActivityRunning;
    private AppRTCClient.RoomConnectionParameters mRoomConnectionParameters = null;
    private PeerConnectionClient.PeerConnectionParameters mPeerConnectionParameters = null;
    private boolean mIceConnected;
    private boolean mIsError;
    private boolean mCallControlFragmentVisible = true;

    // Controls
    private GLSurfaceView mVideoView = null;
    private UpMeCallFragment mUpMeCallFragment = null;
    private HudFragment mHudFragment = null;

    private View mContentView = null;

    // Room Person Param
    private String mRoomId = null;
    private int mIdPerson;
    private String mContactName = null;

    // Audio
    private SoundPool mSoundPool = null;
    private int mSoundID;
    private boolean mPlays = false;
    private boolean mLoaded = false;
    private float mActVolume, mMaxVolume, mVolume;
    private AudioManager mAudioManagerSing = null;
    private int mCounter;
    private long mCallStartedTimeMs = 0;

    public static WebRtcFragment newInstance(Bundle callParam) {
        WebRtcFragment fragment = new WebRtcFragment();
        fragment.setArguments(callParam);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        LOGI(TAG, "onAttach");

        mAudioManagerSing = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        mActVolume = (float) mAudioManagerSing.getStreamVolume(AudioManager.STREAM_MUSIC);
        mMaxVolume = (float) mAudioManagerSing.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mVolume = mMaxVolume / 4;

        //Hardware buttons setting to adjust the media sound
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // the mCounter will help us recognize the stream id of the sound played  now
        mCounter = 0;

        // Load the sounds
        mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mLoaded = true;
            }
        });

        mSoundID = mSoundPool.load(getActivity(), R.raw.beep, 1);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LOGI(TAG, "onActivityCreated");

        // Link : http://stackoverflow.com/questions/11182180/understanding-fragments-setretaininstanceboolean
        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new UnhandledExceptionHandler(getActivity()));
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

        mIceConnected = false;
        mSignalingParameters = null;
        mScalingType = VideoRendererGui.ScalingType.SCALE_ASPECT_FILL;

        // Create UI controls.
        mVideoView = (GLSurfaceView) mContentView.findViewById(R.id.glview_call);
        mUpMeCallFragment = new UpMeCallFragment();
        mHudFragment = new HudFragment();

        // Create video renders.
        VideoRendererGui.setView(mVideoView, new Runnable() {
            @Override
            public void run() {
                createPeerConnectionFactory();
            }
        });

        mRemoteRender = VideoRendererGui.create(REMOTE_X, REMOTE_Y, REMOTE_WIDTH, REMOTE_HEIGHT, mScalingType, false);
        mLocalRender = VideoRendererGui.create(LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING, LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING, mScalingType, true);

        // Show/hide call control fragment on view click.
        mVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleCallControlFragmentVisibility();
            }
        });

        // Get Bundle parameters.
        final Bundle args = getArguments();

        mIdPerson = args.getInt(JustUpApplication.EXTRA_IDPERSON);
        mContactName = args.getString(JustUpApplication.EXTRA_CONTACT_NAME);

        String roomUrlString = args.getString(JustUpApplication.EXTRA_ROOM_URL);
        Uri roomUri = null;
        if (roomUrlString == null) {
            logAndToast(getString(R.string.missing_url));
            LOGE(TAG, "Didn't get any URL in intent!");
            return;
        } else {
            roomUri = Uri.parse(roomUrlString);
        }

        mRoomId = args.getString(JustUpApplication.EXTRA_ROOMID);
        if (mRoomId == null || mRoomId.length() == 0) {
            logAndToast(getString(R.string.missing_url));
            LOGE(TAG, "Incorrect room ID in intent!");
            return;
        }

        boolean loopback = args.getBoolean(JustUpApplication.EXTRA_LOOPBACK, false);
        mPeerConnectionParameters = new PeerConnectionClient.PeerConnectionParameters(
                args.getBoolean(JustUpApplication.EXTRA_VIDEO_CALL, true),
                loopback,
                args.getInt(JustUpApplication.EXTRA_VIDEO_WIDTH, 0),
                args.getInt(JustUpApplication.EXTRA_VIDEO_HEIGHT, 0),
                args.getInt(JustUpApplication.EXTRA_VIDEO_FPS, 0),
                args.getInt(JustUpApplication.EXTRA_VIDEO_BITRATE, 0),
                args.getString(JustUpApplication.EXTRA_VIDEOCODEC),
                args.getBoolean(JustUpApplication.EXTRA_HWCODEC_ENABLED, true),
                args.getInt(JustUpApplication.EXTRA_AUDIO_BITRATE, 0),
                args.getString(JustUpApplication.EXTRA_AUDIOCODEC),
                args.getBoolean(JustUpApplication.EXTRA_CPUOVERUSE_DETECTION, true));

        mCommandLineRun = args.getBoolean(JustUpApplication.EXTRA_CMDLINE, false);
        mRunTimeMs = args.getInt(JustUpApplication.EXTRA_RUNTIME, 0);

        // Create connection client and connection parameters.
        mAppRtcClient = new WebSocketRTCClient(this, new LooperExecutor());
        mRoomConnectionParameters = new AppRTCClient.RoomConnectionParameters(roomUri.toString(), mRoomId, loopback);

        // Send intent arguments to fragment.
        mUpMeCallFragment.setArguments(args);
        mHudFragment.setArguments(args);
        // Activate call fragment and start the call.
        // Activate call and HUD fragments and start the call.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.call_fragment_container, mUpMeCallFragment);
        ft.add(R.id.hud_fragment_container, mHudFragment);
        ft.commit();

        startCall();

        // For command line execution run connection for <mRunTimeMs> and exit.
        if (mCommandLineRun && mRunTimeMs > 0) {
            mVideoView.postDelayed(new Runnable() {
                public void run() {
                    disconnect(false);
                }
            }, mRunTimeMs);
        }

        mTimerCall = new CountDownTimer(TIMER, 1000) {
            public void onTick(long millisUntilFinished) { }

            public void onFinish() {
                if (!mIceConnected) {
                    Toast.makeText(getActivity(), "Пользователь " + mContactName + " не отвечет", Toast.LENGTH_SHORT).show();
                    disconnect(false);
                }
            }
        }.start();

    }

    // TODO Preview onResume
    @Override
    public void onStart() {
        super.onStart();
        LOGI(TAG, "onStart");

        mVideoView.onResume();
        mActivityRunning = true;
        if (mPeerConnectionClient != null) {
            mPeerConnectionClient.startVideoSource();
        }
    }

    // TODO Preview onPause
    @Override
    public void onStop() {
        super.onStop();
        LOGI(TAG, "onStop");

        mVideoView.onPause();
        mActivityRunning = false;
        if (mPeerConnectionClient != null) {
            mPeerConnectionClient.stopVideoSource();
        }
    }

    // TODO Preview onDestroy
    @Override
    public void onDestroyView() {
        disconnect(false);
        super.onDestroyView();
        LOGI(TAG, "onDestroyView");

        if (mLogToast != null) {
            mLogToast.cancel();
        }
    }

    // CallFragment.OnCallEvents interface implementation.
    @Override
    public void onCallHangUp() {
        disconnect(false);
    }

    @Override
    public void onCameraSwitch() {
        if (mPeerConnectionClient != null) {
            mPeerConnectionClient.switchCamera();
        }
    }

    @Override
    public void onVideoScalingSwitch(VideoRendererGui.ScalingType scalingType) {
        this.mScalingType = scalingType;

        // New functionality
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

    private void updateVideoView() {
        VideoRendererGui.update(mRemoteRender, REMOTE_X, REMOTE_Y, REMOTE_WIDTH, REMOTE_HEIGHT, mScalingType, false);

        if (mIceConnected) {
            VideoRendererGui.update(mLocalRender, LOCAL_X_CONNECTED, LOCAL_Y_CONNECTED, LOCAL_WIDTH_CONNECTED, LOCAL_HEIGHT_CONNECTED, VideoRendererGui.ScalingType.SCALE_ASPECT_FIT, true);
        } else {
            VideoRendererGui.update(mLocalRender, LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING, LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING, mScalingType, true);
        }
    }

    // Helper functions.
    private void toggleCallControlFragmentVisibility() {
        if (!mIceConnected || !mUpMeCallFragment.isAdded()) {
            return;
        }

        // Show/hide call control fragment
        mCallControlFragmentVisible = !mCallControlFragmentVisible;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (mCallControlFragmentVisible) {
            ft.show(mUpMeCallFragment);
            ft.show(mHudFragment);
        } else {
            ft.hide(mUpMeCallFragment);
            ft.hide(mHudFragment);
        }

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private void startCall() {
        if (mAppRtcClient == null) {
            LOGE(TAG, "AppRTC client is not allocated for a call.");
            return;
        }

        mCallStartedTimeMs = System.currentTimeMillis();

        // Start room connection.
        logAndToast(getString(R.string.connecting_to, mRoomConnectionParameters.roomUrl));
        mAppRtcClient.connectToRoom(mRoomConnectionParameters);

        // Create and audio manager that will take care of audio routing,
        // audio modes, audio device enumeration etc.
        mAudioManager = AppRTCAudioManager.create(getActivity().getApplicationContext(), new Runnable() {
                    // This method will be called each time the audio state (number and
                    // type of devices) has been changed.
                    @Override
                    public void run() {
                        onAudioManagerChangedState();
                    }
                }
        );

        // Store existing audio settings and change audio mode to
        // MODE_IN_COMMUNICATION for best possible VoIP performance.
        LOGD(TAG, "Initializing the audio manager...");
        mAudioManager.init();
    }

    // Should be called from UI thread
    private void callConnected() {
        final long delta = System.currentTimeMillis() - mCallStartedTimeMs;
        LOGI(TAG, "Call connected: delay = " + delta + "ms");

        // Update video view.
        updateVideoView();
        // Enable statistics callback.
        mPeerConnectionClient.enableStatsEvents(true, STAT_CALLBACK_PERIOD);
    }

    private void onAudioManagerChangedState() {
        // TODO disable video if AppRTCAudioManager.AudioDevice.EARPIECE
    }

    // Create peer connection factory when EGL context is ready.
    private void createPeerConnectionFactory() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mPeerConnectionClient == null) {
                    final long delta = System.currentTimeMillis() - mCallStartedTimeMs;
                    LOGD(TAG, "Creating peer connection factory, delay = " + delta + "ms");

                    mPeerConnectionClient = new PeerConnectionClient();
                    mPeerConnectionClient.createPeerConnectionFactory(getActivity().getApplicationContext(),
                            VideoRendererGui.getEGLContext(), mPeerConnectionParameters,
                            WebRtcFragment.this);
                }

                if (mSignalingParameters != null) {
                    LOGW(TAG, "EGL context is ready after room connection.");
                    onConnectedToRoomInternal(mSignalingParameters);
                }
            }
        });
    }

    // Disconnect from remote resources, dispose of local resources, and exit.
    private void disconnect(boolean reCall) {
        LOGI(TAG, "Disconnect()");
        mActivityRunning = false;

        stopSound();
        if (mTimerCall != null) {
            mTimerCall.cancel();
        }

        if (mAppRtcClient != null) {
            mAppRtcClient.disconnectFromRoom();
            mAppRtcClient = null;
        }

        if (mPeerConnectionClient != null) {
            mPeerConnectionClient.close();
            mPeerConnectionClient = null;
        }

        if (mAudioManager != null) {
            mAudioManager.close();
            mAudioManager = null;
        }

        if (mIceConnected && !mIsError) {
            // Toast.makeText(getActivity(), "RESULT_OK", Toast.LENGTH_SHORT).show();
            LOGI(TAG, "RESULT_OK");
        } else {
            // Toast.makeText(getActivity(), "RESULT_CANCELED", Toast.LENGTH_SHORT).show();
            LOGE(TAG, "RESULT_CANCELED");
        }

        try {
            if (WebRtcFragment.this.isAdded()) {
              ((MainActivity) getActivity()).clearDataAfterCallRTC();
//            getActivity().findViewById(R.id.container_video_chat).setVisibility(View.GONE);
//            getActivity().getFragmentManager().beginTransaction().remove(this).commit();
            }
        }catch (NullPointerException e){
            LOGE(TAG, e.getMessage());
        }

        if(reCall){
            LOGI(TAG, "reCall");
            ((MainActivity) getActivity()).reCall();
        }
    }

    private void disconnectWithErrorMessage(final String errorMessage) {
        if (mCommandLineRun || !mActivityRunning) {
            LOGE(TAG, "Critical error : " + errorMessage);

            disconnect(false);
        } else {
            LOGE(TAG, "disconnectWithErrorMessage - method. DisconnectWithErrorMessage : " + errorMessage);

            if(errorMessage.toLowerCase().contains("ECONNRESET (Connection reset by peer)")){
                disconnect(true);
            }else if(errorMessage.toLowerCase().contains("error : null")) {
                disconnect(true);
//            }else if(errorMessage.toLowerCase().contains("HTTP/1.1 408 Request Timeout")) {
//                ((MainActivity) getActivity()).reCall();
            }else{
                if (WebRtcFragment.this.isAdded()) {
                    new AlertDialog.Builder(getActivity()).setTitle(getText(R.string.channel_error_title))
                            .setMessage(errorMessage)
                            .setCancelable(false)
                            .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    disconnect(false);
                                }
                            }).create().show();
                }
            }
        }
    }

    // Log |msg| and Toast about it.
    private void logAndToast(String msg) {
        Log.d(TAG, msg);
        if (mLogToast != null) {
            mLogToast.cancel();
        }

        mLogToast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
        mLogToast.show();
    }

    private void reportError(final String description) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!mIsError) {
                    mIsError = true;
                    disconnectWithErrorMessage(description);
                }
            }
        });
    }

    // -----Implementation of AppRTCClient.AppRTCSignalingEvents ---------------
    // All callbacks are invoked from websocket signaling looper thread and are routed to UI thread.
    private void onConnectedToRoomInternal(final AppRTCClient.SignalingParameters params) {
        final long delta = System.currentTimeMillis() - mCallStartedTimeMs;

        mSignalingParameters = params;
        if (mPeerConnectionClient == null) {
            LOGW(TAG, "Room is connected, but EGL context is not ready yet.");
            return;
        }

        logAndToast("Creating peer connection, delay = " + delta + "ms");
        mPeerConnectionClient.createPeerConnection(mLocalRender, mRemoteRender, mSignalingParameters);

        if (mSignalingParameters.initiator) {
            logAndToast("Creating OFFER...");
            // Create offer. Offer SDP will be sent to answering client in PeerConnectionEvents.onLocalDescription event.
            mPeerConnectionClient.createOffer();

            if (mIdPerson != 0) {
                playLoop();
                startNotificationIntent(mIdPerson, Integer.parseInt(mRoomId));
            } else {
                disconnect(false);
            }
        } else {
            if (params.offerSdp != null) {
                mPeerConnectionClient.setRemoteDescription(params.offerSdp);
                logAndToast("Creating ANSWER...");
                // Create answer. Answer SDP will be sent to offering client in PeerConnectionEvents.onLocalDescription event.
                mPeerConnectionClient.createAnswer();
            }

            if (params.iceCandidates != null) {
                // Add remote ICE candidates from room.
                for (IceCandidate iceCandidate : params.iceCandidates) {
                    mPeerConnectionClient.addRemoteIceCandidate(iceCandidate);
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
        final long delta = System.currentTimeMillis() - mCallStartedTimeMs;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mPeerConnectionClient == null) {
                    LOGE(TAG, "Received remote SDP for non-initilized peer connection.");
                    return;
                }

                logAndToast("Received remote " + sdp.type + ", delay = " + delta + "ms");
                mPeerConnectionClient.setRemoteDescription(sdp);

                if (!mSignalingParameters.initiator) {
                    logAndToast("Creating ANSWER...");
                    // Create answer. Answer SDP will be sent to offering client in
                    // PeerConnectionEvents.onLocalDescription event.
                    mPeerConnectionClient.createAnswer();
                }
            }
        });
    }

    @Override
    public void onRemoteIceCandidate(final IceCandidate candidate) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mPeerConnectionClient == null) {
                    Log.e(TAG, "Received ICE candidate for non-initilized peer connection.");
                    return;
                }

                mPeerConnectionClient.addRemoteIceCandidate(candidate);
            }
        });
    }

    @Override
    public void onChannelClose() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("Remote end hung up; dropping PeerConnection");
                disconnect(false);
            }
        });
    }

    @Override
    public void onChannelError(final String description) {
        reportError(description);
    }

    // -----Implementation of PeerConnectionClient.PeerConnectionEvents.---------
    // Send local peer connection SDP and ICE candidates to remote party.
    // All callbacks are invoked from peer connection client looper thread and
    // are routed to UI thread.
    @Override
    public void onLocalDescription(final SessionDescription sdp) {
        final long delta = System.currentTimeMillis() - mCallStartedTimeMs;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAppRtcClient != null) {
                    logAndToast("Sending " + sdp.type + ", delay = " + delta + "ms");
                    if (mSignalingParameters.initiator) {
                        mAppRtcClient.sendOfferSdp(sdp);
                    } else {
                        mAppRtcClient.sendAnswerSdp(sdp);
                    }
                }
            }
        });
    }

    @Override
    public void onIceCandidate(final IceCandidate candidate) {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAppRtcClient != null) {
                        mAppRtcClient.sendLocalIceCandidate(candidate);
                    }
                }
            });
        }catch (NullPointerException e){
            LOGE(TAG, e.getMessage());
        }
    }

    @Override
    public void onIceConnected() {
        final long delta = System.currentTimeMillis() - mCallStartedTimeMs;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("ICE connected, delay = " + delta + "ms");

                stopSound();
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                mIceConnected = true;
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
                mIceConnected = false;
                disconnect(false);
            }
        });
    }

    @Override
    public void onPeerConnectionClosed() { }

    @Override
    public void onPeerConnectionStatsReady(final StatsReport[] reports) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!mIsError && mIceConnected) {
                    mHudFragment.updateEncoderStatistics(reports);
                }
            }
        });
    }

    @Override
    public void onPeerConnectionError(final String description) {
        reportError(description);
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

    ///////////////////// SOUND ////////////////////////////////////////////
    public void playLoop() {
        if (mLoaded && !mPlays) {
            // the sound will play for ever if we put the loop parameter -1
            mSoundPool.play(mSoundID, mVolume, mVolume, 1, -1, 1f);
            mCounter = mCounter++;
            mPlays = true;
        }
    }

    public void stopSound() {
        if (mPlays) {
            mSoundPool.stop(mSoundID);
            mSoundID = mSoundPool.load(getActivity(), R.raw.beep, mCounter);
            mPlays = false;
        }
    }
}
