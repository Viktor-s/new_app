package me.justup.upme.view.dashboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import me.justup.upme.JustUpApplication;
import me.justup.upme.R;

public class DashboardAdapter extends ArrayAdapter<TileItem> implements SpanVariableGridView.CalculateChildrenPosition {
    private static final String TAG = DashboardAdapter.class.getSimpleName();

    public ItemChangeSizeListener itemChangeSizeListener;

    public void setItemChangeSizeListener(ItemChangeSizeListener itemChangeSizeListener){
        this.itemChangeSizeListener = itemChangeSizeListener;
    }

    private final class ItemViewHolder {
        public TextView itemTitle;
        public TextView itemSecondTitle;
        public ImageView imgIcon;
        public ImageButton imgButtonChangeSize;

        public RotateLayout mParentLayout;

        public RelativeLayout mTileLayout;
        public LinearLayout mAddViewLayout;
    }

    private LayoutInflater mLayoutInflater = null;

    private View.OnClickListener onRemoveItemListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            Integer position = (Integer) view.getTag();
            removeItem(getItem(position));
        }
    };

    public void insertItem(TileItem item, int where) {
        if (where < 0 || where > (getCount() - 1)) {
            return;
        }

        insert(item, where);
    }

    public boolean removeItem(TileItem item) {
        remove(item);

        return true;
    }

    public DashboardAdapter(Context context, List<TileItem> plugins) {
        super(context, R.layout.tile_item, plugins);

        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ItemViewHolder itemViewHolder;

        final TileItem item = getItem(position);
        final boolean isRedacted = item.isRedacted();

        if (convertView == null) {
            if(JustUpApplication.getScreenDensityDpi()==240) { // Sony Z
                convertView = mLayoutInflater.inflate(R.layout.tile_item_sony_z, parent, false);
            }else{
                convertView = mLayoutInflater.inflate(R.layout.tile_item, parent, false);
            }

            itemViewHolder = new ItemViewHolder();

            itemViewHolder.itemTitle = (TextView) convertView.findViewById(R.id.txtTile);
            itemViewHolder.itemTitle.setTextColor(Color.WHITE);

            itemViewHolder.itemSecondTitle = (TextView) convertView.findViewById(R.id.txtSecondTile);
            itemViewHolder.itemSecondTitle.setTextColor(Color.WHITE);

            itemViewHolder.mParentLayout = (RotateLayout) convertView.findViewById(R.id.content);

            itemViewHolder.mTileLayout = (RelativeLayout) convertView.findViewById(R.id.content_layout);
            itemViewHolder.mAddViewLayout = (LinearLayout) convertView.findViewById(R.id.add_layout);

            itemViewHolder.imgIcon = (ImageView) convertView.findViewById(R.id.img_ico);
            itemViewHolder.imgButtonChangeSize = (ImageButton) convertView.findViewById(R.id.img_btn_change_size);

            convertView.setTag(itemViewHolder);
        } else {
            itemViewHolder = (ItemViewHolder) convertView.getTag();
        }

        itemViewHolder.imgButtonChangeSize.setTag(position);
        itemViewHolder.imgButtonChangeSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemChangeSizeListener!=null){
                    itemChangeSizeListener.OnChangeSizeClick(Integer.valueOf(v.getTag().toString()));
                }
            }
        });

        SpanVariableGridView.LayoutParams lp = new SpanVariableGridView.LayoutParams(convertView.getLayoutParams());
        int width = item.getWSpans();
        int height = item.getHSpans();

        lp.hSpan = height;
        lp.wSpan = width;

        convertView.setLayoutParams(lp);

        if(item.isAddItemButton() || item.getTitle().equals("+")){
            itemViewHolder.mParentLayout.setBackgroundColor(Color.BLACK);

            itemViewHolder.mTileLayout.setVisibility(View.GONE);
            itemViewHolder.mAddViewLayout.setVisibility(View.VISIBLE);

            itemViewHolder.imgButtonChangeSize.setVisibility(View.INVISIBLE);

            return convertView;
        }else {
            itemViewHolder.mAddViewLayout.setVisibility(View.GONE);
            itemViewHolder.mTileLayout.setVisibility(View.VISIBLE);

            if(isRedacted){
                itemViewHolder.imgButtonChangeSize.setVisibility(View.VISIBLE);
                convertView.setBackground(getContext().getResources().getDrawable(R.drawable.card_highlighted));
            }else{
                itemViewHolder.imgButtonChangeSize.setVisibility(View.INVISIBLE);
                convertView.setBackground(getContext().getResources().getDrawable(R.drawable.tile_item_selector));
            }

            if (width == 2 && height == 1) {
                itemViewHolder.imgButtonChangeSize.setBackground(getContext().getResources().getDrawable(R.drawable.navigation_forward));
            }else {
                itemViewHolder.imgButtonChangeSize.setBackground(getContext().getResources().getDrawable(R.drawable.navigation_back));
            }

            itemViewHolder.itemTitle.setText(item.getTitle());
            itemViewHolder.itemTitle.setTextColor(Color.WHITE);

            itemViewHolder.itemSecondTitle.setText(item.getSecondTitle());
            itemViewHolder.itemSecondTitle.setTextColor(Color.WHITE);

            if(item.getResId()!=0) {
                itemViewHolder.imgIcon.setBackground(getContext().getResources().getDrawable(item.getResId()));
            }else{
                itemViewHolder.imgIcon.setBackground(null);
            }

            if(item.getBackground()!=0) {
                if(item.isImage()){
                    itemViewHolder.mParentLayout.setBackground(decodeImg(getContext(), item.getBackground(), 90));
                }else{
                    itemViewHolder.mParentLayout.setBackgroundColor(getContext().getResources().getColor(item.getBackground()));
                }
            }

            return convertView;
        }
    }

    @Override
    public void onCalculatePosition(View view, int position, int row, int column) { }

    private Drawable decodeImg(Context context, int resId, float x ){
        Bitmap bitmapOrg = BitmapFactory.decodeResource(context.getResources(), resId);

        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();

        // calculate the scale - in this case = 0.4f
        float scaleWidth = ((float) height) / width;
        float scaleHeight = ((float) width) / height;

        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);
        matrix.postRotate(x);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,width, height, matrix, true);

        return new BitmapDrawable(context.getResources(), resizedBitmap);
    }
}
