package me.justup.upme;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import me.justup.upme.fragments.BriefcaseFragment;
import me.justup.upme.fragments.CalendarFragment;
import me.justup.upme.fragments.MailFragment;
import me.justup.upme.fragments.NewsFragment;
import me.justup.upme.fragments.ProductsFragment;

import static me.justup.upme.utils.LogUtils.makeLogTag;


public class MainActivity extends Activity {
    private static final String TAG = makeLogTag(MainActivity.class);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onMainMenuClickHandler(View view) {
        Fragment fragment = null;

        switch (view.getId()) {
            case R.id.news_menu_item:
                fragment = new NewsFragment();
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
                // press on corner
                break;
        }

        if (fragment != null) {
            getFragmentManager().beginTransaction().replace(R.id.main_fragment_container, fragment).commit();
        }

    }

}
