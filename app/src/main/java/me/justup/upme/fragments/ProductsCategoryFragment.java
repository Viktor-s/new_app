package me.justup.upme.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import me.justup.upme.R;
import me.justup.upme.entity.BaseHttpQueryEntity;
import me.justup.upme.entity.GetProductHtmlByIdQuery;
import me.justup.upme.entity.ProductsCategoryBrandEntity;
import me.justup.upme.http.HttpIntentService;
import me.justup.upme.utils.CommonUtils;

public class ProductsCategoryFragment extends Fragment {
    private static final String TAG = ProductsCategoryFragment.class.getSimpleName();

    private static final String ARG_PRODUCTS_LIST = "products_list";
    private static final String ARG_CATEGORY_NAME = "category_name";

    private ProductsCategoryBrandEntity mProductsCategoryBrandEntity = null;
    private GridLayout mGridLayout = null;
    private LayoutInflater mLayoutInflater = null;
    private int mColumn = 3;
    private int mScreenWidth;
    private String mCategoryName = null;
    private String mNamePath = null;

    public static ProductsCategoryFragment newInstance(ProductsCategoryBrandEntity productsCategoryBrandEntity, String categoryName) {
        ProductsCategoryFragment fragment = new ProductsCategoryFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCTS_LIST, productsCategoryBrandEntity);
        args.putString(ARG_CATEGORY_NAME, categoryName);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mProductsCategoryBrandEntity = (ProductsCategoryBrandEntity) bundle.getSerializable(ARG_PRODUCTS_LIST);
            mCategoryName = bundle.getString(ARG_CATEGORY_NAME);
        }

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x - CommonUtils.convertDpToPixels(getActivity(), 165);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products_category, container, false);
        mLayoutInflater = LayoutInflater.from(getActivity());
        TextView tvTitleMain = (TextView) view.findViewById(R.id.prod_category_top_title_main_textView);
        TextView tvTitle = (TextView) view.findViewById(R.id.prod_category_top_title_textView);
        tvTitleMain.setText("Продукты /" + " " + mCategoryName);
        tvTitle.setText(mProductsCategoryBrandEntity.getName());
        mNamePath = "Продукты / " + mCategoryName + " / " + mProductsCategoryBrandEntity.getName();

        mGridLayout = (GridLayout) view.findViewById(R.id.productItemsGridLayout);
        int row = mProductsCategoryBrandEntity.getProductEntityList().size() / mColumn;
        mGridLayout.setColumnCount(mColumn);
        mGridLayout.setRowCount(row + 1);
        updateView();
        return view;
    }


    private void updateView() {
        for (int i = 0, c = 0, r = 0; i < mProductsCategoryBrandEntity.getProductEntityList().size(); i++, c++) {
            if (c == mColumn) {
                c = 0;
                r++;
            }
            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            param.width = (mScreenWidth / 3);
            param.rightMargin = CommonUtils.convertDpToPixels(getActivity(), 30);
            // param.topMargin = 10;
            //  param.setGravity(Gravity.CENTER);
            param.columnSpec = GridLayout.spec(c);
            param.rowSpec = GridLayout.spec(r);

            LinearLayout categoryProductLayout = (LinearLayout) mLayoutInflater.inflate(R.layout.product_category_grid_row, null, false);
            TextView idGroupProduct = (TextView) categoryProductLayout.findViewById(R.id.grid_hide_id);
            idGroupProduct.setText(Integer.toString(mProductsCategoryBrandEntity.getProductEntityList().get(i).getId()));
            ImageView groupProductPhoto = (ImageView) categoryProductLayout.findViewById(R.id.grid_row_imageView);
            String imagePath = (mProductsCategoryBrandEntity.getProductEntityList().get(i).getImage() != null && mProductsCategoryBrandEntity.getProductEntityList().get(i).getImage().length() > 1) ? mProductsCategoryBrandEntity.getProductEntityList().get(i).getImage() : "fake";
            Picasso.with(getActivity()).load(imagePath).placeholder(R.color.white).fit().into(groupProductPhoto);
            TextView categoryProductTitle = (TextView) categoryProductLayout.findViewById(R.id.grid_row_name_extView);
            categoryProductTitle.setText(mProductsCategoryBrandEntity.getProductEntityList().get(i).getName());
            TextView categoryProductDescr = (TextView) categoryProductLayout.findViewById(R.id.grid_row_description_textView);
            categoryProductDescr.setText(mProductsCategoryBrandEntity.getProductEntityList().get(i).getDescription());
            categoryProductLayout.setLayoutParams(param);
            categoryProductLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int idCurrentGroup = Integer.parseInt(((TextView) v.findViewById(R.id.grid_hide_id)).getText().toString());
                    String nameCurrentGroup = ((TextView) v.findViewById(R.id.grid_row_name_extView)).getText().toString();
                    showProductHtmlFragment(idCurrentGroup, nameCurrentGroup, mNamePath);
                }
            });

            mGridLayout.addView(categoryProductLayout);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    public void showProductHtmlFragment(int id, String nameProduct, String namePath) {
        startHttpIntent(getProductHtml(id), HttpIntentService.PRODUCTS_GET_HTML_BY_ID);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_fragment_container, ProductHTMLFragment.newInstance(id, nameProduct, namePath));
        ft.addToBackStack(null);
        ft.commit();
    }


    public void startHttpIntent(BaseHttpQueryEntity entity, int dbTable) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(HttpIntentService.HTTP_INTENT_QUERY_EXTRA, entity);
        bundle.putInt(HttpIntentService.HTTP_INTENT_PART_EXTRA, dbTable);

        Intent intent = new Intent(getActivity(), HttpIntentService.class);
        getActivity().startService(intent.putExtras(bundle));
    }

    public static GetProductHtmlByIdQuery getProductHtml(int id) {
        GetProductHtmlByIdQuery query = new GetProductHtmlByIdQuery();
        query.params.id = id;
        return query;
    }
}
