package me.justup.upme;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by bogdan on 19.01.15.
 */
public class ProductItemActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_products_without_bail);
    }
}
