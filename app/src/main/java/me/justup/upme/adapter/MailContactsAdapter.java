package me.justup.upme.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import me.justup.upme.R;
import me.justup.upme.db.DBHelper;
import me.justup.upme.utils.CircularImageView;

public class MailContactsAdapter extends CursorAdapter {

    private Context context;
    private LayoutInflater mInflater; //нужен для создания объектов класса View

    public MailContactsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public View newView(Context ctx, Cursor cur, ViewGroup parent) {
        View convertView = mInflater.inflate(R.layout.mail_contacts_list_item, parent, false);
        ViewHolder holder = new ViewHolder();
        holder.mImageView = (CircularImageView) convertView.findViewById(R.id.mail_contacts_item_imageView);
        holder.mName = (TextView) convertView.findViewById(R.id.mail_contacts_item_textView);
        holder.mInfo = (Button) convertView.findViewById(R.id.mail_contacts_item_info_button);
        holder.mCall = (Button) convertView.findViewById(R.id.mail_contacts_item_call_button);
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cur) {
        int id = cur.getInt(cur.getColumnIndex(DBHelper.MAIL_CONTACT_SERVER_ID));
        String imagePath = cur.getString(cur.getColumnIndex(DBHelper.MAIL_CONTACT_IMG));
        String contactName = cur.getString(cur.getColumnIndex(DBHelper.MAIL_CONTACT_NAME));
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder != null) {
            holder.mName.setText(contactName);
            Picasso.with(context).load(imagePath).into(holder.mImageView);
            holder.rowId = id;
        }
    }

    public static class ViewHolder {
        private CircularImageView mImageView;
        private TextView mName;
        private Button mCall;
        private Button mInfo;
        private int rowId;
    }
}


//public class MailContactsAdapter extends ArrayAdapter<MailContactEntity> {
//}
//    private Context context;
//
//
//    private static class ViewHolder {
//        private CircularImageView mImageView;
//        private TextView mName;
//        private Button mCall;
//        private Button mInfo;
//    }
//
//    public MailContactsAdapter(Context context, List<MailContactEntity> contactEntityList) {
//        super(context, R.layout.mail_contacts_list_item, contactEntityList);
//        this.context = context;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        MailContactEntity contactEntity = getItem(position);
//        ViewHolder viewHolder;
//        if (convertView == null) {
//            viewHolder = new ViewHolder();
//            LayoutInflater inflater = LayoutInflater.from(getContext());
//            convertView = inflater.inflate(R.layout.mail_contacts_list_item, parent, false);
//            viewHolder.mImageView = (CircularImageView) convertView.findViewById(R.id.mail_contacts_item_imageView);
//            viewHolder.mName = (TextView) convertView.findViewById(R.id.mail_contacts_item_textView);
//            viewHolder.mInfo = (Button) convertView.findViewById(R.id.mail_contacts_item_info_button);
//            viewHolder.mCall = (Button) convertView.findViewById(R.id.mail_contacts_item_call_button);
//
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        Picasso.with(context).load(contactEntity.getImg()).into(viewHolder.mImageView);
//
//        viewHolder.mName.setText(contactEntity.getName());
//        return convertView;
//    }
//
//}
