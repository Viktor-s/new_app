package me.justup.upme;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

import me.justup.upme.entity.GroupProductEntity;
import me.justup.upme.entity.ListGroupProductMock;

public class ProductItemActivity extends Activity {

    public static final String ID_CURRENT_GROUP = "ID_CURRENT_GROUP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_products_item);

        int idCurrentGroup = getIntent().getIntExtra(ID_CURRENT_GROUP, 0);

        List<GroupProductEntity> listGroup = ListGroupProductMock.getInstance(this).getListGroupProduct();

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.container_product_item);

        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout productGroup;

        switch (idCurrentGroup)
        {
            case 1:
                productGroup = (LinearLayout) inflater.inflate(R.layout.fragment_products_without_bail, null, false);
                break;
            case 2:
                productGroup = (LinearLayout) inflater.inflate(R.layout.fragment_products_bail, null, false);
                break;
            case 3:
                productGroup = (LinearLayout) inflater.inflate(R.layout.fragment_life_insurance, null, false);
                break;
            case 4:
                productGroup = (LinearLayout) inflater.inflate(R.layout.fragment_risk_insurance, null, false);
                break;
            case 5:
                productGroup = (LinearLayout) inflater.inflate(R.layout.fragment_airline_tickets, null, false);
                break;
            case 6:
                productGroup = (LinearLayout) inflater.inflate(R.layout.fragment_education_online, null, false);
                break;
            default:
                productGroup = (LinearLayout) inflater.inflate(R.layout.fragment_products_without_bail, null, false); ///
        }

        rl.addView(productGroup);

        CheckBox checkBox = (CheckBox) findViewById(R.id.check_lock);






    }
}
