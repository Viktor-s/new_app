package me.justup.upme;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import me.justup.upme.entity.GroupProductEntity;
import me.justup.upme.entity.ListGroupProductMock;

public class ProductItemActivity extends FragmentActivity implements View.OnClickListener {

    public static final String ID_CURRENT_GROUP = "ID_CURRENT_GROUP";

    private int positionGroupProductInList;
    List<GroupProductEntity> listGroup;

    LinearLayout lockPanel;
    RelativeLayout rl;
    private TextView previousProductGroup;
    private TextView nextProductGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_group_products_item);

        lockPanel = (LinearLayout) findViewById(R.id.lock_panel);

        int idCurrentGroup = getIntent().getIntExtra(ID_CURRENT_GROUP, 0);

        ListGroupProductMock listGroupProductMock = ListGroupProductMock.getInstance(this);
        listGroup = listGroupProductMock.getListGroupProduct();
        GroupProductEntity currentGroupProduct = listGroupProductMock.getGroupProductById(idCurrentGroup);

        rl = (RelativeLayout) findViewById(R.id.container_group_product_item);
        rl.addView(generateGroupProduct(currentGroupProduct));

        previousProductGroup = (TextView) findViewById(R.id.previous_product_group);
        nextProductGroup = (TextView) findViewById(R.id.next_product_group);

        positionGroupProductInList = listGroup.indexOf(currentGroupProduct);
        ReferenceNextPrevious(positionGroupProductInList);

        previousProductGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReferenceNextPrevious(--positionGroupProductInList);
                rl.removeAllViews();
                rl.addView(generateGroupProduct(listGroup.get(positionGroupProductInList)));
            }
        });

        nextProductGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReferenceNextPrevious(++positionGroupProductInList);
                rl.removeAllViews();
                rl.addView(generateGroupProduct(listGroup.get(positionGroupProductInList)));
            }
        });

        CheckBox checkBox = (CheckBox) findViewById(R.id.check_lock);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                    if (isChecked)
                                                        lockPanel.setVisibility(View.VISIBLE);
                                                    else
                                                        lockPanel.setVisibility(View.GONE);
                                                }
                                            }
        );

    }


    private LinearLayout generateGroupProduct(GroupProductEntity currentGroupProduct) {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout groupProductExtendedLayout = (LinearLayout) inflater.inflate(R.layout.group_product_extended_layout, null, false);

        TextView groupProductExtendedTitle = (TextView) groupProductExtendedLayout.findViewById(R.id.group_product_extended_title);
        groupProductExtendedTitle.setText(currentGroupProduct.getName());

        TextView groupProductExtendedDescription = (TextView) groupProductExtendedLayout.findViewById(R.id.group_product_extended_description);
        groupProductExtendedDescription.setText(currentGroupProduct.getDescription());
        return groupProductExtendedLayout;
    }

    private void ReferenceNextPrevious(int i) {
        if (i == 0)
            previousProductGroup.setVisibility(View.GONE);
        else {
            previousProductGroup.setVisibility(View.VISIBLE);
            previousProductGroup.setText(listGroup.get(i - 1).getName());
        }

        if (i == listGroup.size() - 1)
            nextProductGroup.setVisibility(View.GONE);
        else {
            nextProductGroup.setVisibility(View.VISIBLE);
            nextProductGroup.setText(listGroup.get(i + 1).getName());
        }
    }

    @Override
    public void onClick(View v) {

    }
}


//        switch (idCurrentGroup)
//        {
//            case 1:
//                productGroup = (LinearLayout) inflater.inflate(R.layout.fragment_products_without_bail, null, false);
//                break;
//            case 2:
//                productGroup = (LinearLayout) inflater.inflate(R.layout.fragment_products_bail, null, false);
//                break;
//            case 3:
//                productGroup = (LinearLayout) inflater.inflate(R.layout.fragment_life_insurance, null, false);
//                break;
//            case 4:
//                productGroup = (LinearLayout) inflater.inflate(R.layout.fragment_risk_insurance, null, false);
//                break;
//            case 5:
//                productGroup = (LinearLayout) inflater.inflate(R.layout.fragment_airline_tickets, null, false);
//                break;
//            case 6:
//                productGroup = (LinearLayout) inflater.inflate(R.layout.fragment_education_online, null, false);
//                break;
//            default:
//                productGroup = (LinearLayout) inflater.inflate(R.layout.fragment_products_without_bail, null, false); ///
//        }
