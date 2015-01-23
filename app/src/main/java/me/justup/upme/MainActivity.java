package me.justup.upme;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import me.justup.upme.fragments.BriefcaseFragment;
import me.justup.upme.fragments.CalendarFragment;
import me.justup.upme.fragments.MailFragment;
import me.justup.upme.fragments.NewsFeedFragment;
import me.justup.upme.fragments.ProductsFragment;
import me.justup.upme.utils.LogUtils;

import static me.justup.upme.utils.LogUtils.makeLogTag;


public class MainActivity extends Activity {
    private static final String TAG = makeLogTag(MainActivity.class);

    private FrameLayout mMainFragmentContainer;
    private Animation mFragmentSliderOut;
    private Animation mFragmentSliderIn;
    private boolean isShowMainFragmentContainer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (LogUtils.DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainFragmentContainer = (FrameLayout) findViewById(R.id.main_fragment_container);
        ImageView mCornerButton = (ImageView) findViewById(R.id.upme_corner_button);
        mCornerButton.setOnClickListener(new OnCornerButtonListener());

        mFragmentSliderOut = AnimationUtils.loadAnimation(this, R.anim.fragment_slider_out);
        mFragmentSliderIn = AnimationUtils.loadAnimation(this, R.anim.fragment_slider_in);

        // DELETE - only for exit
        TextView exit = (TextView) findViewById(R.id.exit_menu_item);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @SuppressWarnings("UnusedDeclaration")
    public void onMainMenuClickHandler(View view) {
        Fragment fragment = null;

        switch (view.getId()) {
            case R.id.news_menu_item:
                fragment = new NewsFeedFragment();
                break;

            case R.id.mail_menu_item:
                fragment = new MailFragment();
                break;

            case R.id.calendar_menu_item:
                fragment = new CalendarFragment();
                break;

            case R.id.products_menu_item:
                fragment = new ProductsFragment();
                break;

            case R.id.briefcase_menu_item:
                fragment = new BriefcaseFragment();
                break;

            default:
                break;
        }

        if (fragment != null) {
            getFragmentManager().beginTransaction().replace(R.id.main_fragment_container, fragment).commit();
        }
        if (!isShowMainFragmentContainer) {
            showMainFragmentContainer();
        }

    }

    private class OnCornerButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (isShowMainFragmentContainer) {
                mMainFragmentContainer.startAnimation(mFragmentSliderOut);
                mMainFragmentContainer.setVisibility(View.GONE);
                isShowMainFragmentContainer = false;
            } else {
                showMainFragmentContainer();
            }
        }
    }

    private void showMainFragmentContainer() {
        mMainFragmentContainer.setVisibility(View.VISIBLE);
        mMainFragmentContainer.startAnimation(mFragmentSliderIn);
        isShowMainFragmentContainer = true;
    }

}
