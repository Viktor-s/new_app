package me.justup.upme.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
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

import me.justup.upme.ProductItemActivity;
import me.justup.upme.R;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.db.DBHelper;
import me.justup.upme.entity.CategoryProductEntityMock;
import me.justup.upme.entity.GroupProductEntity;
import me.justup.upme.entity.ListGroupProductMock;
import me.justup.upme.entity.ProductEntityMock;
import me.justup.upme.utils.AppContext;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class ProductsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = makeLogTag(ProductsFragment.class);

    List<CategoryProductEntityMock> listCategory;
    List<GroupProductEntity> listGroup;
    ListGroupProductMock listGroupProductMock;

    private DBAdapter mDBAdapter;
    private DBHelper mDBHelper;
    private BroadcastReceiver receiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDBHelper = new DBHelper(AppContext.getAppContext());
        mDBAdapter = new DBAdapter(AppContext.getAppContext());


        listGroupProductMock = ListGroupProductMock.getInstance(getActivity());
        listGroup = listGroupProductMock.getListGroupProduct();

        CategoryProductEntityMock cat1 = new CategoryProductEntityMock(1, getResources().getString(R.string.product_title_credit));
        CategoryProductEntityMock cat2 = new CategoryProductEntityMock(2, getResources().getString(R.string.product_title_insurance));
        CategoryProductEntityMock cat3 = new CategoryProductEntityMock(3, getResources().getString(R.string.product_title_tourism));
        CategoryProductEntityMock cat4 = new CategoryProductEntityMock(4, getResources().getString(R.string.product_title_education));

        listCategory = new ArrayList<>();
        listCategory.add(cat1);
        listCategory.add(cat2);
        listCategory.add(cat3);
        listCategory.add(cat4);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

//      LayoutInflater inflater = LayoutInflater.from(getActivity());

        for (CategoryProductEntityMock categoryProductEntityMock : listCategory) {

            RelativeLayout categoryProductLayout = (RelativeLayout) inflater.inflate(R.layout.category_product_layout, null, false);
            TextView categoryProductTitle = (TextView) categoryProductLayout.findViewById(R.id.category_product_title);
            categoryProductTitle.setText(categoryProductEntityMock.getName());
            LinearLayout categoryProductContainer = (LinearLayout) categoryProductLayout.findViewById(R.id.category_product_container);

            for (GroupProductEntity groupProductEntity : listGroupProductMock.getGroupProductByIdCategory(categoryProductEntityMock.getId())) {

                RelativeLayout groupProductLayout = (RelativeLayout) inflater.inflate(R.layout.group_product_layout, null, false);

                TextView idGroupProduct = (TextView) groupProductLayout.findViewById(R.id.id_group_product);
                idGroupProduct.setText(Integer.toString(groupProductEntity.getId()));

                ImageView groupProductPhoto = (ImageView) groupProductLayout.findViewById(R.id.group_product_photo);
                groupProductPhoto.setImageResource(R.drawable.risk_insurance);

                TextView groupProductTitle = (TextView) groupProductLayout.findViewById(R.id.group_product_title);
                groupProductTitle.setText(groupProductEntity.getName());

                TextView groupProductDescription = (TextView) groupProductLayout.findViewById(R.id.group_product_description);
                groupProductDescription.setText(groupProductEntity.getDescription());

                groupProductLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LOGD("TAG", "-------onClick");
                        int idCurrentGroup = Integer.parseInt(((TextView) view.findViewById(R.id.id_group_product)).getText().toString());
                        Intent intent = new Intent(getActivity(), ProductItemActivity.class);
                        intent.putExtra(ProductItemActivity.ID_CURRENT_GROUP, idCurrentGroup);
                        startActivity(intent);
                    }
                });

                categoryProductContainer.addView(groupProductLayout);


            }

            LinearLayout containerProductMain = (LinearLayout) view.findViewById(R.id.container_product_main);
            containerProductMain.addView(categoryProductLayout);
//            ((ViewGroup) view).addView(categoryProductLayout);

        }

        return view;
    }

    @Override
    public void onClick(View v) {



    }
}
