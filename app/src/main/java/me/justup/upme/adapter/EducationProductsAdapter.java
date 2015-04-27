package me.justup.upme.adapter;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import me.justup.upme.JustUpApplication;
import me.justup.upme.MainActivity;
import me.justup.upme.R;
import me.justup.upme.db.DBHelper;
import me.justup.upme.utils.CircularImageView;

public class EducationProductsAdapter extends CursorAdapter {
    private LayoutInflater mInflater = null;


    public EducationProductsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context ctx, Cursor cur, ViewGroup parent) {
        View convertView = mInflater.inflate(R.layout.education_products_list_item, parent, false);
        ViewHolder holder = new ViewHolder();
        holder.mName = (TextView) convertView.findViewById(R.id.education_products_item_textView);
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cur) {
        final int id = cur.getInt(cur.getColumnIndex(DBHelper.EDUCATION_PRODUCTS_SERVER_ID));
        final String programName = cur.getString(cur.getColumnIndex(DBHelper.EDUCATION_PRODUCTS_NAME));
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder != null) {
            holder.mName.setText(programName);
            holder.rowId = id;
        }
    }

    public static class ViewHolder {
        private TextView mName;
        private int rowId;
    }

}
