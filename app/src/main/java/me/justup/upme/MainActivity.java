package me.justup.upme;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import me.justup.upme.entity.ArticlesGetShortDescriptionQuery;
import me.justup.upme.entity.BaseHttpQueryEntity;
import me.justup.upme.entity.GetLoggedUserInfoQuery;
import me.justup.upme.entity.GetMailContactQuery;
import me.justup.upme.fragments.BriefcaseFragment;
import me.justup.upme.fragments.CalendarFragment;
import me.justup.upme.fragments.MailFragment;
import me.justup.upme.fragments.NewsFeedFragment;
import me.justup.upme.fragments.ProductsFragment;
import me.justup.upme.fragments.SettingsFragment;
import me.justup.upme.fragments.UserFragment;
import me.justup.upme.http.HttpIntentService;
import me.justup.upme.interfaces.OnCloseFragment;
import me.justup.upme.utils.LogUtils;


public class MainActivity extends Activity implements OnCloseFragment, View.OnClickListener {
    private FrameLayout mMainFragmentContainer;
    private Animation mFragmentSliderOut;
    private Animation mFragmentSliderIn;
    private boolean isShowMainFragmentContainer;

    private FrameLayout mSettingsFragmentContainer;
    private Button mSettingButton;
    private SettingsFragment mSettingsFragment;

    private ArrayList<Button> mButtonList = new ArrayList<>();
    private Button mNewsButton, mMailButton, mCalendarButton, mProductsButton, mBriefcaseButton;


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

        mSettingsFragmentContainer = (FrameLayout) findViewById(R.id.settings_fragment_container);
        mSettingButton = (Button) findViewById(R.id.settings_button);
        mSettingButton.setOnClickListener(new OnLoadSettingsListener());

        makeButtonSelector();

        Fragment fragment = UserFragment.newInstance(new GetLoggedUserInfoQuery());
        getFragmentManager().beginTransaction().add(R.id.mapAndUserFragment, fragment).commit();


        // DELETE - only for exit
        TextView exit = (TextView) findViewById(R.id.exit_menu_item);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onClick(View view) {
        Fragment fragment = null;

        switch (view.getId()) {
            case R.id.news_menu_item:
                startHttpIntent(new ArticlesGetShortDescriptionQuery(), HttpIntentService.NEWS_PART);
                changeButtonState(mNewsButton);
                fragment = new NewsFeedFragment();
                break;

            case R.id.mail_menu_item:
                startHttpIntent(new GetMailContactQuery(), HttpIntentService.MAIL_CONTACT_PART);
                changeButtonState(mMailButton);
                fragment = new MailFragment();
                break;

            case R.id.calendar_menu_item:
                changeButtonState(mCalendarButton);
                fragment = new CalendarFragment();
                break;

            case R.id.products_menu_item:
                startHttpIntent(new ArticlesGetShortDescriptionQuery(), HttpIntentService.PRODUCTS_PART);
                changeButtonState(mProductsButton);
                fragment = new ProductsFragment();
                break;

            case R.id.briefcase_menu_item:
                startHttpIntent(new ArticlesGetShortDescriptionQuery(), HttpIntentService.BRIEFCASE_PART);
                changeButtonState(mBriefcaseButton);
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

    private void makeButtonSelector() {
        mNewsButton = (Button) findViewById(R.id.news_menu_item);
        mMailButton = (Button) findViewById(R.id.mail_menu_item);
        mCalendarButton = (Button) findViewById(R.id.calendar_menu_item);
        mProductsButton = (Button) findViewById(R.id.products_menu_item);
        mBriefcaseButton = (Button) findViewById(R.id.briefcase_menu_item);

        mNewsButton.setOnClickListener(this);
        mMailButton.setOnClickListener(this);
        mCalendarButton.setOnClickListener(this);
        mProductsButton.setOnClickListener(this);
        mBriefcaseButton.setOnClickListener(this);

        mButtonList.add(mNewsButton);
        mButtonList.add(mMailButton);
        mButtonList.add(mCalendarButton);
        mButtonList.add(mProductsButton);
        mButtonList.add(mBriefcaseButton);
    }

    private void changeButtonState(Button activeButton) {
        for (Button button : mButtonList) {
            button.setBackground(getResources().getDrawable(R.drawable.user_fragment_block_gradient));
        }

        activeButton.setBackground(getResources().getDrawable(R.drawable.pay_button_pressed));
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

    private class OnLoadSettingsListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mSettingsFragment = new SettingsFragment();
            mSettingsFragmentContainer.setVisibility(View.VISIBLE);
            getFragmentManager().beginTransaction().add(R.id.settings_fragment_container, new SettingsFragment()).commit();
            mSettingButton.setEnabled(false);
        }
    }

    @Override
    public void onCloseFragment() {
        getFragmentManager().beginTransaction().remove(mSettingsFragment).commit();
        mSettingsFragmentContainer.setVisibility(View.GONE);
        mSettingButton.setEnabled(true);
    }

    private void startHttpIntent(BaseHttpQueryEntity entity, int dbTable) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(HttpIntentService.HTTP_INTENT_QUERY_EXTRA, entity);
        bundle.putInt(HttpIntentService.HTTP_INTENT_PART_EXTRA, dbTable);

        Intent intent = new Intent(this, HttpIntentService.class);
        startService(intent.putExtras(bundle));
    }

}
