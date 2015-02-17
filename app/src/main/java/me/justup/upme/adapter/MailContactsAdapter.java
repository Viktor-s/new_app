package me.justup.upme.adapter;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import me.justup.upme.R;
import me.justup.upme.db.DBHelper;
import me.justup.upme.fragments.MailMessagesFragment;
import me.justup.upme.fragments.MailVideoFragment;
import me.justup.upme.utils.AppContext;
import me.justup.upme.utils.AppPreferences;
import me.justup.upme.utils.CircularImageView;

public class MailContactsAdapter extends CursorAdapter {


    private LayoutInflater mInflater;
    final Fragment hostFragment;

    public MailContactsAdapter(Fragment hostFragment, Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.hostFragment = hostFragment;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        final int id = cur.getInt(cur.getColumnIndex(DBHelper.MAIL_CONTACT_SERVER_ID));
        String imagePath = cur.getString(cur.getColumnIndex(DBHelper.MAIL_CONTACT_IMG));
        String contactName = cur.getString(cur.getColumnIndex(DBHelper.MAIL_CONTACT_NAME));
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder != null) {
            holder.mName.setText(contactName);
            Picasso.with(context).load(imagePath).into(holder.mImageView);
            holder.rowId = id;

            holder.mCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG11", "setOnClickListener");
                    final FragmentTransaction ft = hostFragment.getChildFragmentManager().beginTransaction();
                    ft.replace(R.id.mail_messages_container_frameLayout, MailVideoFragment.newInstance(Integer.toString(id)));
                    ft.commit();
                }
            });
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
