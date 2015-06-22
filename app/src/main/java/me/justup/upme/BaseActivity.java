package me.justup.upme;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import me.justup.upme.api_rpc.request_model.service.RequestBaseServiceHelper;
import me.justup.upme.api_rpc.request_model.service.RequestServiceCallbackListener;
import me.justup.upme.fragments.ProgressDialog;

public abstract class BaseActivity extends FragmentActivity implements RequestServiceCallbackListener {
    private static final String TAG = BaseActivity.class.getSimpleName();

    // Service Helper
    private RequestBaseServiceHelper mRequestBaseServiceHelper = null;

    // Default 4.4.2
    private int currentApiVersion = 19;

    public Animation mFragmentSliderOut = null;
    public Animation mFragmentSliderIn = null;
    public Animation mAnimOpenUserPanel = null;
    public Animation mAnimCloseUserPanel = null;

    private static final int UP_ME_ANIMATION_VALUE = 112;
    private static final int UP_ME_ANIMATION_VALUE_SONY_Z = 100;
    private static final int UP_ME_ANIMATION_DURATION = 1000;

    public Animation mAnimCloseLogo = null;
    public Animation mAnimOpenLogo = null;

    public Boolean isShowMainFragmentContainer = false;
    public Boolean isOrderingPanelOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Init Animation
        mFragmentSliderOut = AnimationUtils.loadAnimation(this, R.anim.fragment_slider_out);
        mFragmentSliderOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isShowMainFragmentContainer = null;

                if(mAnimationOpenFragmentListener!=null){
                    mAnimationOpenFragmentListener.onStartAnim();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isShowMainFragmentContainer = false;

                if(mAnimationOpenFragmentListener!=null){
                    mAnimationOpenFragmentListener.onEndAnim();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        mAnimOpenUserPanel = AnimationUtils.loadAnimation(this, R.anim.fragment_slider_out);
        mAnimOpenUserPanel.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isOrderingPanelOpen = null;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isOrderingPanelOpen = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        mFragmentSliderIn = AnimationUtils.loadAnimation(this, R.anim.fragment_slider_in);
        mFragmentSliderIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isShowMainFragmentContainer = null;

                if(mAnimationCloseFragmentListener!=null){
                    mAnimationCloseFragmentListener.onStartAnim();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isShowMainFragmentContainer = true;

                if(mAnimationCloseFragmentListener!=null){
                    mAnimationCloseFragmentListener.onEndAnim();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        mAnimCloseUserPanel = AnimationUtils.loadAnimation(this, R.anim.fragment_slider_in);
        mAnimCloseUserPanel.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isOrderingPanelOpen = null;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isOrderingPanelOpen = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        if(JustUpApplication.getScreenDensityDpi()==240){ // Sony Z
            mAnimOpenLogo = new TranslateAnimation(0, -UP_ME_ANIMATION_VALUE_SONY_Z, 0, 0);
            mAnimCloseLogo = new TranslateAnimation(-UP_ME_ANIMATION_VALUE_SONY_Z, 0, 0, 0);
        }else {
            mAnimOpenLogo = new TranslateAnimation(0, -UP_ME_ANIMATION_VALUE, 0, 0);
            mAnimCloseLogo = new TranslateAnimation(-UP_ME_ANIMATION_VALUE, 0, 0, 0);
        }

        mAnimCloseLogo.setDuration(UP_ME_ANIMATION_DURATION);
        mAnimCloseLogo.setFillAfter(true);

        mAnimOpenLogo.setDuration(UP_ME_ANIMATION_DURATION);
        mAnimOpenLogo.setFillAfter(true);

        // Get device API version
        currentApiVersion = android.os.Build.VERSION.SDK_INT;

//        if (LogUtils.DEVELOPER_MODE) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                    .detectDiskReads()
//                    .detectDiskWrites()
//                    .detectNetwork()
//                    .penaltyLog()
//                    .build());
//
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                    .detectLeakedSqlLiteObjects()
//                    .detectLeakedClosableObjects()
//                    .penaltyLog()
//                    .penaltyDeath()
//                    .build());
//        }

        super.onCreate(savedInstanceState);

        mRequestBaseServiceHelper = JustUpApplication.getApplication().getApiHelper();
    }

    public RequestBaseServiceHelper getServiceHelper() {
        return mRequestBaseServiceHelper;
    }

    protected void onResume() {
        super.onResume();

        mRequestBaseServiceHelper.addListener(this);
    }

    protected void onPause() {
        super.onPause();

        mRequestBaseServiceHelper.removeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRequestBaseServiceHelper.removeListener(this);
    }

    public void hideNavBar() {
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT) {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility)
                        {
                            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                            {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if(currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void removeCurrentFragment(int layout){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment currentFrag =  getSupportFragmentManager().findFragmentById(layout);

        String fragName = "NONE";

        if (currentFrag!=null) {
            fragName = currentFrag.getClass().getSimpleName();
        }

        if (currentFrag != null) {
            transaction.remove(currentFrag);
        }

        transaction.commit();
    }

    public void replaceFragment (Fragment fragment, int layout){
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped){ // fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(layout, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    // Animation Listener
    public AnimationOpenFragmentListener mAnimationOpenFragmentListener = null;
    public AnimationCloseFragmentListener mAnimationCloseFragmentListener = null;

    public void setAnimationOpenFragmentListener(AnimationOpenFragmentListener listener) {
        mAnimationOpenFragmentListener = listener;
    }
    public void setAnimationCloseFragmentListener(AnimationCloseFragmentListener listener) {
        mAnimationCloseFragmentListener = listener;
    }

    public interface AnimationOpenFragmentListener {
        void onStartAnim();

        void onEndAnim();
    }

    public interface AnimationCloseFragmentListener {
        void onStartAnim();

        void onEndAnim();
    }

    public void showWaitDialog(String message, String tag) {
        ProgressDialog df = ProgressDialog.newInstance(message);
        df.show(getSupportFragmentManager(), tag);

        getFragmentManager().executePendingTransactions();
    }

    public void dismissDialog(String tag) {
        ProgressDialog dialogFragment = (ProgressDialog) getSupportFragmentManager().findFragmentByTag(tag);
        if (dialogFragment != null) {
            dialogFragment.dismissAllowingStateLoss();
            dialogFragment.dismiss();
        }
    }

    public void changeDialogText(int text, String tag){
        ProgressDialog dialogFragment = (ProgressDialog) getSupportFragmentManager().findFragmentByTag(tag);
        if (dialogFragment != null) {
            dialogFragment.setText(text);
        }
    }

    public void changeDialogText(String text, String tag){
        ProgressDialog dialogFragment = (ProgressDialog) getSupportFragmentManager().findFragmentByTag(tag);
        if (dialogFragment != null) {
            dialogFragment.setText(text);
        }
    }

    @Override
    public void onServiceCallback(int requestId, Intent requestIntent, int resultCode, Bundle data) {

    }
}
