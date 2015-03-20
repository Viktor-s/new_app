package me.justup.upme;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

import me.justup.upme.adapter.ProductsAdapter;
import me.justup.upme.entity.BaseHttpQueryEntity;
import me.justup.upme.entity.GetProductHtmlByIdQuery;
import me.justup.upme.entity.ProductsCategoryBrandEntity;
import me.justup.upme.fragments.ProductHTMLFragment;
import me.justup.upme.http.HttpIntentService;


public class ProductItemActivity extends BaseActivity implements View.OnClickListener {

    public static final String ID_CURRENT_GROUP = "ID_CURRENT_GROUP";

    private int positionGroupProductInList;
    //  List<GroupProductEntity> listGroup;

    LinearLayout lockPanel;
    RelativeLayout rl;
    private TextView previousProductGroup;
    private TextView nextProductGroup;
    private List<ProductsCategoryBrandEntity> mProductsCategoryBrandEntities;
    private FrameLayout productHtmlContainer;


    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_group_products_item);

        lockPanel = (LinearLayout) findViewById(R.id.lock_panel);

        productHtmlContainer = (FrameLayout) findViewById(R.id.products_fragment_html_container);
        int idCurrentGroup = getIntent().getIntExtra(ID_CURRENT_GROUP, 0);
        mProductsCategoryBrandEntities = (List<ProductsCategoryBrandEntity>) getIntent().getSerializableExtra("AllBrandsList");
        // ListGroupProductMock listGroupProductMock = ListGroupProductMock.getInstance(this);
        // listGroup = listGroupProductMock.getListGroupProduct();
        //GroupProductEntity currentGroupProduct = listGroupProductMock.getGroupProductById(idCurrentGroup);
        ProductsCategoryBrandEntity currentProductBrand = getCurrentBrand(mProductsCategoryBrandEntities, idCurrentGroup);
        //LOGE("pavel", currentProductBrand.toString());

        rl = (RelativeLayout) findViewById(R.id.container_group_product_item);
        rl.addView(generateGroupProduct(currentProductBrand));

        previousProductGroup = (TextView) findViewById(R.id.previous_product_group);
        nextProductGroup = (TextView) findViewById(R.id.next_product_group);

        positionGroupProductInList = mProductsCategoryBrandEntities.indexOf(currentProductBrand);
        ReferenceNextPrevious(positionGroupProductInList);

        previousProductGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReferenceNextPrevious(--positionGroupProductInList);
                rl.removeAllViews();
                rl.addView(generateGroupProduct(mProductsCategoryBrandEntities.get(positionGroupProductInList)));
            }
        });

        nextProductGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReferenceNextPrevious(++positionGroupProductInList);
                rl.removeAllViews();
                rl.addView(generateGroupProduct(mProductsCategoryBrandEntities.get(positionGroupProductInList)));
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

        ((Button) findViewById(R.id.group_products_close_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private LinearLayout generateGroupProduct(ProductsCategoryBrandEntity currentGroupProduct) {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout groupProductExtendedLayout = (LinearLayout) inflater.inflate(R.layout.group_product_extended_layout, null, false);


        TextView groupProductExtendedTitle = (TextView) groupProductExtendedLayout.findViewById(R.id.group_product_extended_title);
        groupProductExtendedTitle.setText(currentGroupProduct.getName());

        TextView groupProductExtendedDescription = (TextView) groupProductExtendedLayout.findViewById(R.id.group_product_extended_description);
        groupProductExtendedDescription.setText(currentGroupProduct.getDescription());

        ListView productsListView = (ListView) groupProductExtendedLayout.findViewById(R.id.listViewProducts);
        ProductsAdapter productsAdapter = new ProductsAdapter(this, currentGroupProduct.getProductEntityList(), this);
        productsListView.setAdapter(productsAdapter);
        setListViewHeightBasedOnChildren(productsListView);

        ScrollView scrollView = (ScrollView) groupProductExtendedLayout.findViewById(R.id.group_product_scroll_view);
        scrollView.smoothScrollTo(0, 0);

        return groupProductExtendedLayout;
    }

    private void ReferenceNextPrevious(int i) {
        if (i == 0)
            previousProductGroup.setVisibility(View.GONE);
        else {
            previousProductGroup.setVisibility(View.VISIBLE);
            previousProductGroup.setText(mProductsCategoryBrandEntities.get(i - 1).getName());
        }

        if (i == mProductsCategoryBrandEntities.size() - 1)
            nextProductGroup.setVisibility(View.GONE);
        else {
            nextProductGroup.setVisibility(View.VISIBLE);
            nextProductGroup.setText(mProductsCategoryBrandEntities.get(i + 1).getName());
        }
    }

    @Override
    public void onClick(View v) {

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


    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        int listViewElementsHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View mView = listAdapter.getView(i, null, listView);
            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            listViewElementsHeight += mView.getMeasuredHeight() + 1;
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = listViewElementsHeight + 1;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public void showProductHtmlFragment(int id) {
        startHttpIntent(getProductHtml(id), HttpIntentService.PRODUCTS_GET_HTML_BY_ID);
        getFragmentManager().beginTransaction().replace(R.id.products_fragment_html_container, ProductHTMLFragment.newInstance(id)).commit();

    }


    public void startHttpIntent(BaseHttpQueryEntity entity, int dbTable) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(HttpIntentService.HTTP_INTENT_QUERY_EXTRA, entity);
        bundle.putInt(HttpIntentService.HTTP_INTENT_PART_EXTRA, dbTable);

        Intent intent = new Intent(this, HttpIntentService.class);
        startService(intent.putExtras(bundle));
    }

    public static GetProductHtmlByIdQuery getProductHtml(int id) {
        GetProductHtmlByIdQuery query = new GetProductHtmlByIdQuery();
        query.params.id = id;
        return query;
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
