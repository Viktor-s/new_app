package me.justup.upme.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import me.justup.upme.R;
import me.justup.upme.entity.BaseHttpQueryEntity;
import me.justup.upme.entity.GetProductHtmlByIdQuery;
import me.justup.upme.entity.ProductsCategoryBrandEntity;
import me.justup.upme.http.HttpIntentService;
import me.justup.upme.utils.CommonUtils;

public class ProductsCategoryFragment extends Fragment {
    private static final String ARG_PRODUCT_ID = "prod_id";
    private static final String ARG_PRODUCTS_LIST = "products_list";
    private ProductsCategoryBrandEntity productsCategoryBrandEntiti;
    private long productBrandId;
    private GridLayout gridLayout;
    private LayoutInflater layoutInflater;
    private View view;
    int column = 3;
    int screenWidth;

    public static ProductsCategoryFragment newInstance(ProductsCategoryBrandEntity productsCategoryBrandEntity, long productId) {
        ProductsCategoryFragment fragment = new ProductsCategoryFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCTS_LIST, productsCategoryBrandEntity);
        args.putLong(ARG_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            productsCategoryBrandEntiti = (ProductsCategoryBrandEntity) bundle.getSerializable(ARG_PRODUCTS_LIST);
            productBrandId = bundle.getLong(ARG_PRODUCT_ID);
        }
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x - CommonUtils.convertDpToPixels(getActivity(), 165);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_products_category, container, false);
        layoutInflater = LayoutInflater.from(getActivity());
        gridLayout = (GridLayout) view.findViewById(R.id.productItemsGridLayout);

        int row = productsCategoryBrandEntiti.getProductEntityList().size() / column;
        gridLayout.setColumnCount(column);
        gridLayout.setRowCount(row + 1);
        updateView();
        return view;
    }


    private void updateView() {
        for (int i = 0, c = 0, r = 0; i < productsCategoryBrandEntiti.getProductEntityList().size(); i++, c++) {
            if (c == column) {
                c = 0;
                r++;
            }
            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            param.width = (int) (screenWidth / 3);
            param.rightMargin = CommonUtils.convertDpToPixels(getActivity(), 30);
            // param.topMargin = 10;
            //  param.setGravity(Gravity.CENTER);
            param.columnSpec = GridLayout.spec(c);
            param.rowSpec = GridLayout.spec(r);

            LinearLayout categoryProductLayout = (LinearLayout) layoutInflater.inflate(R.layout.product_category_grid_row, null, false);
            TextView idGroupProduct = (TextView) categoryProductLayout.findViewById(R.id.grid_hide_id);
            idGroupProduct.setText(Integer.toString(productsCategoryBrandEntiti.getProductEntityList().get(i).getId()));
            ImageView groupProductPhoto = (ImageView) categoryProductLayout.findViewById(R.id.grid_row_imageView);
            String imagePath = (productsCategoryBrandEntiti.getProductEntityList().get(i).getImage() != null && productsCategoryBrandEntiti.getProductEntityList().get(i).getImage().length() > 1) ? productsCategoryBrandEntiti.getProductEntityList().get(i).getImage() : "fake";
            Picasso.with(getActivity()).load(imagePath).placeholder(R.drawable.ic_launcher).into(groupProductPhoto);
            TextView categoryProductTitle = (TextView) categoryProductLayout.findViewById(R.id.grid_row_name_extView);
            categoryProductTitle.setText(productsCategoryBrandEntiti.getProductEntityList().get(i).getName());
            TextView categoryProductDescr = (TextView) categoryProductLayout.findViewById(R.id.grid_row_description_textView);
            categoryProductDescr.setText(productsCategoryBrandEntiti.getProductEntityList().get(i).getDescription());
            categoryProductLayout.setLayoutParams(param);
            categoryProductLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int idCurrentGroup = Integer.parseInt(((TextView) v.findViewById(R.id.grid_hide_id)).getText().toString());
                    showProductHtmlFragment(idCurrentGroup);
                }
            });

//            LinearLayout categoryProductContainer = (LinearLayout) categoryProductLayout.findViewById(R.id.category_product_container);
//            categoryProductContainer.setBackgroundColor(Color.parseColor(colorForProduct[i]));
//            for (int j = 0; j < listCategory.get(i).getBrandList().size(); j++) {
//                RelativeLayout groupProductLayout = (RelativeLayout) layoutInflater.inflate(R.layout.group_product_layout, null, false);
//                TextView idGroupProduct = (TextView) groupProductLayout.findViewById(R.id.id_group_product);
//                idGroupProduct.setText(Integer.toString(listCategory.get(i).getBrandList().get(j).getId()));
//                ImageView groupProductPhoto = (ImageView) groupProductLayout.findViewById(R.id.group_product_photo);
//                String imagePath = (listCategory.get(i).getBrandList().get(j).getImage() != null && listCategory.get(i).getBrandList().get(j).getImage().length() > 1) ? listCategory.get(i).getBrandList().get(j).getImage() : "fake";
//                Picasso.with(getActivity()).load(imagePath).placeholder(R.drawable.ic_launcher).into(groupProductPhoto);
//                TextView groupProductTitle = (TextView) groupProductLayout.findViewById(R.id.group_product_title);
//                groupProductTitle.setText(listCategory.get(i).getBrandList().get(j).getName());
//                TextView groupProductDescription = (TextView) groupProductLayout.findViewById(R.id.group_product_description);
//                groupProductDescription.setText(listCategory.get(i).getBrandList().get(j).getDescription());
//
//                categoryProductContainer.addView(groupProductLayout);
//            }
            //containerProductMain = (LinearLayout) view.findViewById(R.id.container_product_main);
            // containerProductMain.addView(categoryProductLayout);


            // gridLayout.addView(oImageView);

            gridLayout.addView(categoryProductLayout);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    public void showProductHtmlFragment(int id) {
        startHttpIntent(getProductHtml(id), HttpIntentService.PRODUCTS_GET_HTML_BY_ID);
        // getFragmentManager().beginTransaction().replace(R.id.products_fragment_html_container, ProductHTMLFragment.newInstance(id)).commit();
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_fragment_container, ProductHTMLFragment.newInstance(id));
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
