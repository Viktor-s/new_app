package me.justup.upme.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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

import me.justup.upme.JustUpApplication;
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
import static me.justup.upme.db.DBHelper.PRODUCTS_PRODUCT_DESCRIPTION;
import static me.justup.upme.db.DBHelper.PRODUCTS_PRODUCT_IMAGE;
import static me.justup.upme.db.DBHelper.PRODUCTS_PRODUCT_NAME;
import static me.justup.upme.db.DBHelper.PRODUCTS_PRODUCT_SERVER_ID;
import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.makeLogTag;

public class ProductsFragment extends Fragment {
    private static final String TAG = makeLogTag(ProductsFragment.class);

    private List<ProductCategoryEntity> mListCategory = null;
    private BroadcastReceiver mProductsReceiver = null;
    private Cursor mCursorProducts = null;
    private LayoutInflater mLayoutInflater = null;
    private LinearLayout mContainerProductMain = null;
    private ArrayList<ProductsCategoryBrandEntity> mAllBrandsList = new ArrayList<>();

    private String[] mColorForProduct = null;

    private View mContentView = null;

    // Instance
    public static ProductsFragment newInstance() {
        return new ProductsFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mColorForProduct = getResources().getStringArray(R.array.color_for_product);

        updateProductsList();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Link : http://stackoverflow.com/questions/11182180/understanding-fragments-setretaininstanceboolean
        setRetainInstance(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mProductsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mListCategory.size() > 1) {
                    mContainerProductMain.removeAllViews();
                }
                updateProductsList();
                updateView();
            }
        };

        LocalBroadcastManager.getInstance(ProductsFragment.this.getActivity()).registerReceiver(mProductsReceiver, new IntentFilter(DBAdapter.PRODUCTS_SQL_BROADCAST_INTENT));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(ProductsFragment.this.getActivity()).unregisterReceiver(mProductsReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = super.onCreateView(inflater, container, savedInstanceState);
        mLayoutInflater = LayoutInflater.from(getActivity());

        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_products, container, false);
        }

        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init UI
        if (getActivity() != null) {
            initUI();
        }
    }

    private void initUI(){
        if (mListCategory.size() > 1) {
            updateView();
        }
    }

    @SuppressLint("InflateParams")
    private void updateView() {
        for (int i = 0; i < mListCategory.size(); i++) {
            RelativeLayout categoryProductLayout = (RelativeLayout) mLayoutInflater.inflate(R.layout.category_product_layout, null, false);
            TextView categoryProductTitle = (TextView) categoryProductLayout.findViewById(R.id.category_product_title);
            categoryProductTitle.setText(mListCategory.get(i).getName());
            LinearLayout categoryProductContainer = (LinearLayout) categoryProductLayout.findViewById(R.id.category_product_container);
            categoryProductContainer.setBackgroundColor(Color.parseColor(mColorForProduct[i]));

            for (int j = 0; j < mListCategory.get(i).getBrandList().size(); j++) {
                RelativeLayout groupProductLayout = (RelativeLayout) mLayoutInflater.inflate(R.layout.group_product_layout, null, false);
                TextView idGroupProduct = (TextView) groupProductLayout.findViewById(R.id.id_group_product);
                idGroupProduct.setText(Integer.toString(mListCategory.get(i).getBrandList().get(j).getId()));
                TextView categoryName = (TextView) groupProductLayout.findViewById(R.id.category_name);
                categoryName.setText(mListCategory.get(i).getName());
                ImageView groupProductPhoto = (ImageView) groupProductLayout.findViewById(R.id.group_product_photo);
                TextView groupProductTitle = (TextView) groupProductLayout.findViewById(R.id.group_product_title);
                String title = mListCategory.get(i).getBrandList().get(j).getName();
                groupProductTitle.setText(title);

                String imagePath = "fake";

                if(JustUpApplication.getApplication().getAppPreferences().isDemoMode()){
                    if(title.equals("Кредит наличными без залога") || title.equals("Кредит наличными под залог")){
                        // Set Image to Product Item
                        Picasso.with(getActivity()).load(R.drawable.prod_nalich_bez_zaloga).placeholder(R.mipmap.ic_launcher).into(groupProductPhoto);
                    }else if(title.equals("Ипотека") || title.equals("Коммунальные платежи") || title.equals("Страхование квартиры")){
                        // Set Image to Product Item
                        Picasso.with(getActivity()).load(R.drawable.strahovanie_kvartiri).placeholder(R.mipmap.ic_launcher).into(groupProductPhoto);
                    }else if(title.equals("Микрозаймы")){
                        // Set Image to Product Item
                        Picasso.with(getActivity()).load(R.drawable.mikrozaimi).placeholder(R.mipmap.ic_launcher).into(groupProductPhoto);
                    }else if(title.equals("Кредитование бизнеса")){
                        // Set Image to Product Item
                        Picasso.with(getActivity()).load(R.drawable.kreditovanie_biznesa).placeholder(R.mipmap.ic_launcher).into(groupProductPhoto);
                    }else if(title.equals("Вералайф")){
                        // Set Image to Product Item
                        Picasso.with(getActivity()).load(R.drawable.veralaif).placeholder(R.mipmap.ic_launcher).into(groupProductPhoto);
                    }else if(title.equals("ОСАГО") || title.equals("КАСКО")){
                        // Set Image to Product Item
                        Picasso.with(getActivity()).load(R.drawable.osago_img).placeholder(R.mipmap.ic_launcher).into(groupProductPhoto);
                    }else if(title.equals("Страхование жизни")){
                        // Set Image to Product Item
                        Picasso.with(getActivity()).load(R.drawable.strahovanie_gizni).placeholder(R.mipmap.ic_launcher).into(groupProductPhoto);
                    }else if(title.equals("Авиабилеты")){
                        // Set Image to Product Item
                        Picasso.with(getActivity()).load(R.drawable.aviabilet).placeholder(R.mipmap.ic_launcher).into(groupProductPhoto);
                    }else if(title.equals("Круизы")){
                        // Set Image to Product Item
                        Picasso.with(getActivity()).load(R.drawable.kruizi).placeholder(R.mipmap.ic_launcher).into(groupProductPhoto);
                    }else if(title.equals("Отели")){
                        // Set Image to Product Item
                        Picasso.with(getActivity()).load(R.drawable.oteli).placeholder(R.mipmap.ic_launcher).into(groupProductPhoto);
                    }else if(title.equals("Экскурсии")){
                        // Set Image to Product Item
                        Picasso.with(getActivity()).load(R.drawable.ekscursii).placeholder(R.mipmap.ic_launcher).into(groupProductPhoto);
                    }else if(title.equals("Пакетные туры")){
                        // Set Image to Product Item
                        Picasso.with(getActivity()).load(R.drawable.paketnie_turi).placeholder(R.mipmap.ic_launcher).into(groupProductPhoto);
                    }else if(title.equals("Обучение профессии: HTML-верстка")){
                        // Set Image to Product Item
                        Picasso.with(getActivity()).load(R.drawable.html_verstka).placeholder(R.mipmap.ic_launcher).into(groupProductPhoto);
                    }else if(title.equals("Обучение профессии: Интернет маркетолог")){
                        // Set Image to Product Item
                        Picasso.with(getActivity()).load(R.drawable.internet_marketolog).placeholder(R.mipmap.ic_launcher).into(groupProductPhoto);
                    }else if(title.equals("Управление общественным транспортом")){
                        // Set Image to Product Item
                        Picasso.with(getActivity()).load(R.drawable.uprav_obsh_transport).placeholder(R.mipmap.ic_launcher).into(groupProductPhoto);
                    }else if(title.equals("Спортивная гребля")){
                        // Set Image to Product Item
                        Picasso.with(getActivity()).load(R.drawable.sport_grebla).placeholder(R.mipmap.ic_launcher).into(groupProductPhoto);
                    }else if(title.equals("Работа с почтовыми сообщениями")){
                        // Set Image to Product Item
                        Picasso.with(getActivity()).load(R.drawable.pochtov_slugba).placeholder(R.mipmap.ic_launcher).into(groupProductPhoto);
                    }else if(title.equals("Мобильная связь")){
                        // Set Image to Product Item
                        Picasso.with(getActivity()).load(R.drawable.mobil_svyaz).placeholder(R.mipmap.ic_launcher).into(groupProductPhoto);
                    }else if(title.equals("Интернет")){
                        // Set Image to Product Item
                        Picasso.with(getActivity()).load(R.drawable.internet).placeholder(R.mipmap.ic_launcher).into(groupProductPhoto);
                    }else if(title.equals("Онлайн-игры") || title.equals("Оффлайн-игры")){
                        // Set Image to Product Item
                        Picasso.with(getActivity()).load(R.drawable.offline_game).placeholder(R.mipmap.ic_launcher).into(groupProductPhoto);
                    }
                }else{
                    imagePath = (mListCategory.get(i).getBrandList().get(j).getImage() != null && mListCategory.get(i).getBrandList().get(j).getImage().length() > 1) ? mListCategory.get(i).getBrandList().get(j).getImage() : "fake";
                    // Set Image to Product Item
                    Picasso.with(getActivity()).load(imagePath).placeholder(R.mipmap.ic_launcher).into(groupProductPhoto);
                }

                LOGD(TAG, "Image Path : " + imagePath + ", Title : " + title);

                TextView groupProductDescription = (TextView) groupProductLayout.findViewById(R.id.group_product_description);
                groupProductDescription.setText(mListCategory.get(i).getBrandList().get(j).getDescription());
                groupProductLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LOGD(TAG, "-------onClick");
                        int idCurrentGroup = Integer.parseInt(((TextView) view.findViewById(R.id.id_group_product)).getText().toString());
                        String categoryName = ((TextView) view.findViewById(R.id.category_name)).getText().toString();

                        LOGD(TAG, "---idCurrentGroup--- " + String.valueOf(idCurrentGroup));
                        if (idCurrentGroup == 6) {
                            final FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.replace(R.id.main_fragment_container, ProductsCategoryFragment.newInstance(getCurrentBrand(mAllBrandsList, idCurrentGroup), categoryName));
                            ft.addToBackStack(null);
                            ft.commit();
                        }

                    }
                });

                categoryProductContainer.addView(groupProductLayout);
            }

            mContainerProductMain = (LinearLayout) mContentView.findViewById(R.id.container_product_main);
            mContainerProductMain.addView(categoryProductLayout);
        }
    }

    private void updateProductsList() {
        mCursorProducts = JustUpApplication.getApplication().getTransferActionProductsCategories().getCursorOfProductsCategories(getActivity().getApplicationContext());
        mListCategory = fillProductsFromCursor(mCursorProducts);

        LOGD(TAG, mListCategory.toString());
    }

    public List<ProductCategoryEntity> fillProductsFromCursor(Cursor cursorProducts) {
        ArrayList<ProductCategoryEntity> categoryEntities = new ArrayList<>();

        for (cursorProducts.moveToFirst(); !cursorProducts.isAfterLast(); cursorProducts.moveToNext()) {
            ProductCategoryEntity productCategoryEntity = new ProductCategoryEntity();
            productCategoryEntity.setId(cursorProducts.getInt(cursorProducts.getColumnIndex(PRODUCTS_CATEGORIES_SERVER_ID)));
            productCategoryEntity.setName(cursorProducts.getString(cursorProducts.getColumnIndex(PRODUCTS_CATEGORIES_NAME)));
            int category_id = cursorProducts.getInt(cursorProducts.getColumnIndex(PRODUCTS_CATEGORIES_SERVER_ID));

            Cursor cursorBrands = JustUpApplication.getApplication().getTransferActionBrandCategories().getCursorOfProductsBrandByCategoriesId(getActivity().getApplicationContext(), category_id);
            ArrayList<ProductsCategoryBrandEntity> brandsList = new ArrayList<>();

            if (cursorBrands != null) {
                for (cursorBrands.moveToFirst(); !cursorBrands.isAfterLast(); cursorBrands.moveToNext()) {
                    int brand_id = cursorBrands.getInt(cursorBrands.getColumnIndex(PRODUCTS_BRAND_CATEGORIES_BRAND_ID));
                    ProductsCategoryBrandEntity productsCategoryBrandEntity = new ProductsCategoryBrandEntity();
                    productsCategoryBrandEntity.setId(cursorBrands.getInt(cursorBrands.getColumnIndex(PRODUCTS_BRAND_CATEGORIES_SERVER_ID)));
                    productsCategoryBrandEntity.setName(cursorBrands.getString(cursorBrands.getColumnIndex(PRODUCTS_BRAND_CATEGORIES_NAME)));
                    productsCategoryBrandEntity.setDescription(cursorBrands.getString(cursorBrands.getColumnIndex(PRODUCTS_BRAND_CATEGORIES_SHORT_DESCRIPTION)));
                    productsCategoryBrandEntity.setImage(cursorBrands.getString(cursorBrands.getColumnIndex(PRODUCTS_BRAND_CATEGORIES_IMAGE)));

                    Cursor cursorBrandProduct = JustUpApplication.getApplication().getTransferActionProductsProduct().getCursorOfProductsProductByBrandId(getActivity().getApplicationContext(), brand_id);

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
                }

                productCategoryEntity.setBrandList(brandsList);
                mAllBrandsList.addAll(brandsList);
            }

            categoryEntities.add(productCategoryEntity);
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



