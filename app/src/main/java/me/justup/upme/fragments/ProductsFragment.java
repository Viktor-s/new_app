package me.justup.upme.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import me.justup.upme.ProductItemActivity;
import me.justup.upme.R;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.entity.ProductCategoryEntity;
import me.justup.upme.entity.ProductsCategoryBrandEntity;
import me.justup.upme.entity.ProductsProductEntity;

import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_BRAND_ID;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_IMAGE;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_NAME;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_SERVER_ID;
import static me.justup.upme.db.DBHelper.PRODUCTS_BRAND_CATEGORIES_SHORT_DESCRIPTION;
import static me.justup.upme.db.DBHelper.PRODUCTS_CATEGORIES_NAME;
import static me.justup.upme.db.DBHelper.PRODUCTS_CATEGORIES_SERVER_ID;
import static me.justup.upme.db.DBHelper.PRODUCTS_CATEGORIES_TABLE_NAME;
import static me.justup.upme.db.DBHelper.PRODUCTS_PRODUCT_DESCRIPTION;
import static me.justup.upme.db.DBHelper.PRODUCTS_PRODUCT_IMAGE;
import static me.justup.upme.db.DBHelper.PRODUCTS_PRODUCT_NAME;
import static me.justup.upme.db.DBHelper.PRODUCTS_PRODUCT_SERVER_ID;
import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class ProductsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = makeLogTag(ProductsFragment.class);
    private List<ProductCategoryEntity> listCategory;
    private SQLiteDatabase database;
    private BroadcastReceiver mProductsReceiver;
    private Cursor cursorProducts;
    private View view;
    private LayoutInflater layoutInflater;
    private LinearLayout containerProductMain;
    private ArrayList<ProductsCategoryBrandEntity> mAllBrandsList = new ArrayList<>();

    private String[] colorForProduct;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        colorForProduct = getResources().getStringArray(R.array.color_for_product);

        super.onCreate(savedInstanceState);
        database = DBAdapter.getInstance().openDatabase();
        updateProductsList();
    }


    @Override
    public void onResume() {
        super.onResume();
        mProductsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (listCategory.size() > 1) {
                    containerProductMain.removeAllViews();
                }
                updateProductsList();
                updateView();
            }
        };
        LocalBroadcastManager.getInstance(ProductsFragment.this.getActivity())
                .registerReceiver(mProductsReceiver, new IntentFilter(DBAdapter.PRODUCTS_SQL_BROADCAST_INTENT));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(ProductsFragment.this.getActivity()).unregisterReceiver(mProductsReceiver);
    }

    @Override
    public void onDestroy() {
        DBAdapter.getInstance().closeDatabase();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_products, container, false);
        layoutInflater = LayoutInflater.from(getActivity());
        if (listCategory.size() > 1) {
            updateView();
        }
        return view;
    }

    private void updateView() {
        for (int i = 0; i < listCategory.size(); i++) {
            RelativeLayout categoryProductLayout = (RelativeLayout) layoutInflater.inflate(R.layout.category_product_layout, null, false);
            TextView categoryProductTitle = (TextView) categoryProductLayout.findViewById(R.id.category_product_title);
            categoryProductTitle.setText(listCategory.get(i).getName());
            LinearLayout categoryProductContainer = (LinearLayout) categoryProductLayout.findViewById(R.id.category_product_container);
            categoryProductContainer.setBackgroundColor(Color.parseColor(colorForProduct[i]));
            for (int j = 0; j < listCategory.get(i).getBrandList().size(); j++) {
                RelativeLayout groupProductLayout = (RelativeLayout) layoutInflater.inflate(R.layout.group_product_layout, null, false);
                TextView idGroupProduct = (TextView) groupProductLayout.findViewById(R.id.id_group_product);
                idGroupProduct.setText(Integer.toString(listCategory.get(i).getBrandList().get(j).getId()));
                TextView categoryName = (TextView) groupProductLayout.findViewById(R.id.category_name);
                categoryName.setText(listCategory.get(i).getName());
                ImageView groupProductPhoto = (ImageView) groupProductLayout.findViewById(R.id.group_product_photo);
                String imagePath = (listCategory.get(i).getBrandList().get(j).getImage() != null && listCategory.get(i).getBrandList().get(j).getImage().length() > 1) ? listCategory.get(i).getBrandList().get(j).getImage() : "fake";
                Picasso.with(getActivity()).load(imagePath).placeholder(R.drawable.ic_launcher).into(groupProductPhoto);
                TextView groupProductTitle = (TextView) groupProductLayout.findViewById(R.id.group_product_title);
                groupProductTitle.setText(listCategory.get(i).getBrandList().get(j).getName());
                TextView groupProductDescription = (TextView) groupProductLayout.findViewById(R.id.group_product_description);
                groupProductDescription.setText(listCategory.get(i).getBrandList().get(j).getDescription());
                groupProductLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LOGD("TAG", "-------onClick");
                        int idCurrentGroup = Integer.parseInt(((TextView) view.findViewById(R.id.id_group_product)).getText().toString());
                        String categoryName = ((TextView) view.findViewById(R.id.category_name)).getText().toString();
                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.main_fragment_container, ProductsCategoryFragment.newInstance(getCurrentBrand(mAllBrandsList, idCurrentGroup), categoryName));
                        ft.addToBackStack(null);
                        ft.commit();

                    }
                });
                categoryProductContainer.addView(groupProductLayout);
            }
            containerProductMain = (LinearLayout) view.findViewById(R.id.container_product_main);
            containerProductMain.addView(categoryProductLayout);
        }
    }


    private void updateProductsList() {
        cursorProducts = database.rawQuery("SELECT * FROM " + PRODUCTS_CATEGORIES_TABLE_NAME, null);
        listCategory = fillProductsFromCursor(cursorProducts);
        Log.d("TAG_product", listCategory.toString());
        if (cursorProducts != null)
            cursorProducts.close();
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
            String selectQueryBrands = "SELECT * FROM products_brand_table WHERE category_id=" + category_id;
            Cursor cursorBrands = database.rawQuery(selectQueryBrands, null);
            ArrayList<ProductsCategoryBrandEntity> brandsList = new ArrayList<>();
            if (cursorBrands != null) {
                for (cursorBrands.moveToFirst(); !cursorBrands.isAfterLast(); cursorBrands.moveToNext()) {
                    int brand_id = cursorBrands.getInt(cursorBrands.getColumnIndex(PRODUCTS_BRAND_CATEGORIES_BRAND_ID));
                    ProductsCategoryBrandEntity productsCategoryBrandEntity = new ProductsCategoryBrandEntity();
                    productsCategoryBrandEntity.setId(cursorBrands.getInt(cursorBrands.getColumnIndex(PRODUCTS_BRAND_CATEGORIES_SERVER_ID)));
                    productsCategoryBrandEntity.setName(cursorBrands.getString(cursorBrands.getColumnIndex(PRODUCTS_BRAND_CATEGORIES_NAME)));
                    productsCategoryBrandEntity.setDescription(cursorBrands.getString(cursorBrands.getColumnIndex(PRODUCTS_BRAND_CATEGORIES_SHORT_DESCRIPTION)));
                    productsCategoryBrandEntity.setImage(cursorBrands.getString(cursorBrands.getColumnIndex(PRODUCTS_BRAND_CATEGORIES_IMAGE)));
                    String selectQueryProduct = "SELECT * FROM products_product_table WHERE product_brand_id=" + brand_id;
                    Cursor cursorBrandProduct = database.rawQuery(selectQueryProduct, null);
                    ArrayList<ProductsProductEntity> productsList = new ArrayList<>();
                    for (cursorBrandProduct.moveToFirst(); !cursorBrandProduct.isAfterLast(); cursorBrandProduct.moveToNext()) {
                        ProductsProductEntity productsProductEntity = new ProductsProductEntity();
                        productsProductEntity.setId(cursorBrandProduct.getInt(cursorBrandProduct.getColumnIndex(PRODUCTS_PRODUCT_SERVER_ID)));
                        productsProductEntity.setName(cursorBrandProduct.getString(cursorBrandProduct.getColumnIndex(PRODUCTS_PRODUCT_NAME)));
                        productsProductEntity.setDescription(cursorBrandProduct.getString(cursorBrandProduct.getColumnIndex(PRODUCTS_PRODUCT_DESCRIPTION)));
                        productsProductEntity.setImage(cursorBrandProduct.getString(cursorBrandProduct.getColumnIndex(PRODUCTS_PRODUCT_IMAGE)));
                        productsList.add(productsProductEntity);
                    }
                    productsCategoryBrandEntity.setProductEntityList(productsList);
                    //  LOGE("pavel", productsList.toString());
                    brandsList.add(productsCategoryBrandEntity);
                    cursorBrandProduct.close();
                }
                productCategoryEntity.setBrandList(brandsList);
                mAllBrandsList.addAll(brandsList);
            }
            categoryEntities.add(productCategoryEntity);

            if (cursorBrands != null) {
                cursorBrands.close();
            }
        }
        return categoryEntities;
    }

    public ProductsCategoryBrandEntity getCurrentBrand(List<ProductsCategoryBrandEntity> list, long id) {
        ProductsCategoryBrandEntity productsCategoryBrandEntity = new ProductsCategoryBrandEntity();
        for (ProductsCategoryBrandEntity object : list) {
            if (object.getId() == id) {
                productsCategoryBrandEntity = object;
            }
        }
        return productsCategoryBrandEntity;
    }

//    if (savedInstanceState == null)
//    replaceFragment(ListBanksFragment.newInstance(), false);

//    public void replaceFragment(Fragment fragment, boolean anim) {
//        String backStateName = fragment.getClass().getName();
//
//        FragmentManager manager = getFragmentManager();
//        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
//
//        if (!fragmentPopped) { //fragment not in back stack, create it.
//            FragmentTransaction ft = manager.beginTransaction();
//            if (anim)
//                ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
//            ft.replace(R.id.container, fragment);
//            ft.addToBackStack(backStateName);
//            ft.commit();
//        }
//    }

}



