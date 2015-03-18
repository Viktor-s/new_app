package me.justup.upme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import me.justup.upme.R;
import me.justup.upme.entity.ProductsProductEntity;
import me.justup.upme.utils.AppContext;

public class ProductsAdapter extends ArrayAdapter<ProductsProductEntity> {
    private final Context context;

    private static class ViewHolder {
        private ImageView mImageView;
        private TextView mName;
        private TextView mDescription;
        private Button mInfoButton;
    }

    public ProductsAdapter(Context context, List<ProductsProductEntity> users) {
        super(context, R.layout.product_item_row, users);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ProductsProductEntity productsProductEntity = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.product_item_row, parent, false);
            viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.product_item_image);
            viewHolder.mName = (TextView) convertView.findViewById(R.id.product_item_name);
            viewHolder.mDescription = (TextView) convertView.findViewById(R.id.product_item_description);
            viewHolder.mInfoButton = (Button) convertView.findViewById(R.id.product_item_details_button);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String imagePath = (productsProductEntity.getImage() != null && productsProductEntity.getImage().length() > 1) ? productsProductEntity.getImage() : "fake";
        Picasso.with(context).load(imagePath).placeholder(R.drawable.ic_launcher).into(viewHolder.mImageView);
        viewHolder.mName.setText(productsProductEntity.getName());
        viewHolder.mDescription.setText(productsProductEntity.getDescription());
        viewHolder.mInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AppContext.getAppContext(), "Product id = " + " " + getItem(position).getId(), Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }
}