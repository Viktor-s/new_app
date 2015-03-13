package me.justup.upme.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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
import me.justup.upme.entity.ProductCategoryEntity;
import me.justup.upme.entity.ProductsCategoryBrandEntity;

import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_NAME;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_SERVER_ID;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_SHORT_DESCRIPTION;
import static me.justup.upme.db.DBHelper.PRODUCTS_CATEGORIES_NAME;
import static me.justup.upme.db.DBHelper.PRODUCTS_CATEGORIES_SERVER_ID;
import static me.justup.upme.db.DBHelper.PRODUCTS_CATEGORIES_TABLE_NAME;
import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class ProductsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = makeLogTag(ProductsFragment.class);

    List<ProductCategoryEntity> listCategory;
    //List<GroupProductEntity> listGroup;
    //ListGroupProductMock listGroupProductMock;
    // private DBAdapter mDBAdapter;
    //private DBHelper mDBHelper;
    //private BroadcastReceiver receiver;
    private SQLiteDatabase database;
    private BroadcastReceiver mProductsReceiver;
    private Cursor cursorProducts;
    private View view;
    private LayoutInflater layoutInflater;
    private LinearLayout containerProductMain;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = DBAdapter.getInstance().openDatabase();
        cursorProducts = database.rawQuery("SELECT * FROM " + PRODUCTS_CATEGORIES_TABLE_NAME, null);
        listCategory = fillProductsFromCursor(cursorProducts);
        if (cursorProducts != null)
            cursorProducts.close();
    }


    @Override
    public void onResume() {
        super.onResume();
        mProductsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                containerProductMain.removeAllViews();
                updateView();
            }
        };
        LocalBroadcastManager.getInstance(ProductsFragment.this.getActivity())
                .registerReceiver(mProductsReceiver, new IntentFilter(DBAdapter.PRODUCTS_SQL_BROADCAST_INTENT));
    }

    @Override
    public void onPause() {
        super.onPause();
        DBAdapter.getInstance().closeDatabase();
        LocalBroadcastManager.getInstance(ProductsFragment.this.getActivity()).unregisterReceiver(mProductsReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_products, container, false);
        layoutInflater = LayoutInflater.from(getActivity());

//
//        for (CategoryProductEntityMock categoryProductEntityMock : listCategory) {
//
//            RelativeLayout categoryProductLayout = (RelativeLayout) inflater.inflate(R.layout.category_product_layout, null, false);
//            TextView categoryProductTitle = (TextView) categoryProductLayout.findViewById(R.id.category_product_title);
//            categoryProductTitle.setText(categoryProductEntityMock.getName());
//            LinearLayout categoryProductContainer = (LinearLayout) categoryProductLayout.findViewById(R.id.category_product_container);
//
//            for (GroupProductEntity groupProductEntity : listGroupProductMock.getGroupProductByIdCategory(categoryProductEntityMock.getId())) {
//
//                RelativeLayout groupProductLayout = (RelativeLayout) inflater.inflate(R.layout.group_product_layout, null, false);
//
//                TextView idGroupProduct = (TextView) groupProductLayout.findViewById(R.id.id_group_product);
//                idGroupProduct.setText(Integer.toString(groupProductEntity.getId()));
//
//                ImageView groupProductPhoto = (ImageView) groupProductLayout.findViewById(R.id.group_product_photo);
//                groupProductPhoto.setImageResource(R.drawable.risk_insurance);
//
//                TextView groupProductTitle = (TextView) groupProductLayout.findViewById(R.id.group_product_title);
//                groupProductTitle.setText(groupProductEntity.getName());
//
//                TextView groupProductDescription = (TextView) groupProductLayout.findViewById(R.id.group_product_description);
//                groupProductDescription.setText(groupProductEntity.getDescription());
//
//                groupProductLayout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        LOGD("TAG", "-------onClick");
//                        int idCurrentGroup = Integer.parseInt(((TextView) view.findViewById(R.id.id_group_product)).getText().toString());
//                        Intent intent = new Intent(getActivity(), ProductItemActivity.class);
//                        intent.putExtra(ProductItemActivity.ID_CURRENT_GROUP, idCurrentGroup);
//                        startActivity(intent);
//                    }
//                });
//
//                categoryProductContainer.addView(groupProductLayout);
//
//
//            }
//
//            LinearLayout containerProductMain = (LinearLayout) view.findViewById(R.id.container_product_main);
//            containerProductMain.addView(categoryProductLayout);
        updateView();

        return view;
    }

    private void updateView() {
        for (int i = 0; i < listCategory.size(); i++) {
            RelativeLayout categoryProductLayout = (RelativeLayout) layoutInflater.inflate(R.layout.category_product_layout, null, false);
            TextView categoryProductTitle = (TextView) categoryProductLayout.findViewById(R.id.category_product_title);
            categoryProductTitle.setText(listCategory.get(i).getName());
            LinearLayout categoryProductContainer = (LinearLayout) categoryProductLayout.findViewById(R.id.category_product_container);
            for (int j = 0; j < listCategory.get(i).getBrandList().size(); j++) {
                RelativeLayout groupProductLayout = (RelativeLayout) layoutInflater.inflate(R.layout.group_product_layout, null, false);
                TextView idGroupProduct = (TextView) groupProductLayout.findViewById(R.id.id_group_product);
                idGroupProduct.setText(Integer.toString(listCategory.get(i).getBrandList().get(j).getId()));
                ImageView groupProductPhoto = (ImageView) groupProductLayout.findViewById(R.id.group_product_photo);
                groupProductPhoto.setImageResource(R.drawable.risk_insurance);
                TextView groupProductTitle = (TextView) groupProductLayout.findViewById(R.id.group_product_title);
                groupProductTitle.setText(listCategory.get(i).getBrandList().get(j).getName());
                TextView groupProductDescription = (TextView) groupProductLayout.findViewById(R.id.group_product_description);
                groupProductDescription.setText(listCategory.get(i).getBrandList().get(j).getDescription());
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
            containerProductMain = (LinearLayout) view.findViewById(R.id.container_product_main);
            containerProductMain.addView(categoryProductLayout);
        }
    }

    @Override
    public void onClick(View v) {

    }

    private List<ProductCategoryEntity> fillProductsFromCursor(Cursor cursorProducts) {
        ArrayList<ProductCategoryEntity> categoryEntities = new ArrayList<>();
        for (cursorProducts.moveToFirst(); !cursorProducts.isAfterLast(); cursorProducts.moveToNext()) {
            ProductCategoryEntity productCategoryEntity = new ProductCategoryEntity();
            productCategoryEntity.setId(cursorProducts.getInt(cursorProducts.getColumnIndex(PRODUCTS_CATEGORIES_SERVER_ID)));
            productCategoryEntity.setName(cursorProducts.getString(cursorProducts.getColumnIndex(PRODUCTS_CATEGORIES_NAME)));
            int category_id = cursorProducts.getInt(cursorProducts.getColumnIndex(PRODUCTS_CATEGORIES_SERVER_ID));
            String selectQueryShortNewsComments = "SELECT * FROM products_brand_table WHERE category_id=" + category_id;
            Cursor cursorComments = database.rawQuery(selectQueryShortNewsComments, null);
            ArrayList<ProductsCategoryBrandEntity> brandsList = new ArrayList<>();
            if (cursorComments != null) {
                for (cursorComments.moveToFirst(); !cursorComments.isAfterLast(); cursorComments.moveToNext()) {
                    ProductsCategoryBrandEntity productsCategoryBrandEntity = new ProductsCategoryBrandEntity();
                    productsCategoryBrandEntity.setId(cursorComments.getInt(cursorComments.getColumnIndex(PRODUCTS_BRAND_CATEGORIES_SERVER_ID)));
                    productsCategoryBrandEntity.setName(cursorComments.getString(cursorComments.getColumnIndex(PRODUCTS_BRAND_CATEGORIES_NAME)));
                    productsCategoryBrandEntity.setDescription(cursorComments.getString(cursorComments.getColumnIndex(PRODUCTS_BRAND_CATEGORIES_SHORT_DESCRIPTION)));
                    brandsList.add(productsCategoryBrandEntity);
                }
                productCategoryEntity.setBrandList(brandsList);
            }
            categoryEntities.add(productCategoryEntity);

            if (cursorComments != null) {
                cursorComments.close();
            }
        }
        return categoryEntities;
    }
}



