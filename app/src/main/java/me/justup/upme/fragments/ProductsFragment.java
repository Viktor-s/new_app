package me.justup.upme.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.justup.upme.ProductItemActivity;
import me.justup.upme.R;
import me.justup.upme.entity.GroupProductEntity;
import me.justup.upme.entity.ProductEntityMock;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class ProductsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = makeLogTag(ProductsFragment.class);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProductEntityMock p1 = new ProductEntityMock(1, getResources().getString(R.string.product_title_credit_without_bail), getResources().getString(R.string.product_description_credit_without_bail));
        ProductEntityMock p2 = new ProductEntityMock(2, getResources().getString(R.string.product_title_credit_bail), getResources().getString(R.string.product_description_credit_bail));
        ProductEntityMock p3 = new ProductEntityMock(3, getResources().getString(R.string.product_title_life_insurance), getResources().getString(R.string.product_description_life_insurance));
        ProductEntityMock p4 = new ProductEntityMock(4, getResources().getString(R.string.product_title_credit_risk_insurance), getResources().getString(R.string.product_description_credit_risk_insurance));
        ProductEntityMock p5 = new ProductEntityMock(5, getResources().getString(R.string.product_title_airline_tickets), getResources().getString(R.string.product_description_airline_tickets));
        ProductEntityMock p6 = new ProductEntityMock(6, getResources().getString(R.string.product_title_credit_education_online), getResources().getString(R.string.product_description_credit_education_online));

        List<ProductEntityMock> listP1 =  new ArrayList<>();
        listP1.add(p1);
        listP1.add(p2);
        GroupProductEntity gp1 = new GroupProductEntity(1, getResources().getString(R.string.product_title_credit), listP1);        
        
        List<ProductEntityMock> listP2 =  new ArrayList<>();
        listP2.add(p3);
        listP2.add(p4);
        GroupProductEntity gp2 = new GroupProductEntity(2, getResources().getString(R.string.product_title_insurance), listP2);

        List<ProductEntityMock> listP3 =  new ArrayList<>();
        listP3.add(p5);
        GroupProductEntity gp3 = new GroupProductEntity(3, getResources().getString(R.string.product_title_tourism), listP3);
        
        List<ProductEntityMock> listP4 =  new ArrayList<>();
        listP4.add(p6);
        GroupProductEntity gp4 = new GroupProductEntity(4, getResources().getString(R.string.product_title_education), listP4);
        
        List<GroupProductEntity> listGp = new ArrayList<>();
        listGp.add(gp1);
        listGp.add(gp2);
        listGp.add(gp3);
        listGp.add(gp4);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

//        LayoutInflater inflater = LayoutInflater.from(getActivity());




        RelativeLayout categoryProductLayout = (RelativeLayout) inflater.inflate(R.layout.category_product_layout, null, false);
        TextView categoryProductTitle = (TextView) categoryProductLayout.findViewById(R.id.category_product_title);
        categoryProductTitle.setText("11111111");
        LinearLayout categoryProductContainer = (LinearLayout) categoryProductLayout.findViewById(R.id.category_product_container);


        RelativeLayout groupProductLayout = (RelativeLayout) inflater.inflate(R.layout.group_product_layout, null, false);
        ImageView groupProductPhoto = (ImageView) groupProductLayout.findViewById(R.id.group_product_photo);
        groupProductPhoto.setImageResource(R.drawable.risk_insurance);
        TextView groupProductTitle = (TextView) groupProductLayout.findViewById(R.id.group_product_title);
        groupProductTitle.setText("Title");
        TextView groupProductDescription = (TextView) groupProductLayout.findViewById(R.id.group_product_description);
        groupProductDescription.setText("Desription");

        categoryProductContainer.addView(groupProductLayout);

        ((ViewGroup) view).addView(categoryProductLayout);




//        View imageWithoutBail = view.findViewById(R.id.product_without_bail_image);
//        View textWithoutBail = view.findViewById(R.id.product_without_bail_text);
//        imageWithoutBail.setOnClickListener(this);
//        textWithoutBail.setOnClickListener(this);
//
//        View imageBail = view.findViewById(R.id.product_bail_image);
//        View textBail = view.findViewById(R.id.product_bail_text);
//        imageBail.setOnClickListener(this);
//        textBail.setOnClickListener(this);
//
//        View imageLifeInsurance = view.findViewById(R.id.product_life_insurance_image);
//        View textLifeInsurance = view.findViewById(R.id.product_life_insurance_text);
//        imageLifeInsurance.setOnClickListener(this);
//        textLifeInsurance.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        startActivity(new Intent(getActivity(), ProductItemActivity.class));

    }
}
