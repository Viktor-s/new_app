package me.justup.upme;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import me.justup.upme.fragments.BriefcaseFragment;
import me.justup.upme.fragments.CalendarFragment;
import me.justup.upme.fragments.MailFragment;
import me.justup.upme.fragments.NewsFragment;
import me.justup.upme.fragments.ProductsFragment;

import static me.justup.upme.utils.LogUtils.*;


public class MainActivity extends Activity {
    private static final String TAG = makeLogTag(MainActivity.class);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        // for convenience only

//        Fragment fragment = new NewsFragment();
//        Fragment fragment = new MailFragment();
        Fragment fragment = new CalendarFragment();
//        Fragment fragment = new ProductsFragment();
//        Fragment fragment = new BriefcaseFragment();


        getFragmentManager().beginTransaction().add(R.id.main_fragment_container, fragment).commit();
    }

}
