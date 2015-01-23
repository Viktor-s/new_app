package me.justup.upme;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by bogdan on 19.01.15.
 */
public class ProductItemActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_products_item);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.container_product_item);

        LayoutInflater inflater = LayoutInflater.from(this);

        LinearLayout productsWithoutBail = (LinearLayout) inflater.inflate(R.layout.fragment_products_without_bail, null, false);
        LinearLayout productsBail = (LinearLayout) inflater.inflate(R.layout.fragment_products_bail, null, false);
        LinearLayout productsLifeInsurance = (LinearLayout) inflater.inflate(R.layout.fragment_life_insurance, null, false);
        LinearLayout productsRiskInsurance = (LinearLayout) inflater.inflate(R.layout.fragment_risk_insurance, null, false);
        LinearLayout productsAirlineTickets = (LinearLayout) inflater.inflate(R.layout.fragment_airline_tickets, null, false);
        LinearLayout productsEducationOnline = (LinearLayout) inflater.inflate(R.layout.fragment_education_online, null, false);

        rl.addView(productsWithoutBail);

        CheckBox checkBox = (CheckBox) findViewById(R.id.check_lock);






    }
}
