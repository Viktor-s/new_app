package me.justup.upme.adapter;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import me.justup.upme.entity.WebRtcStartCallQuery;
import me.justup.upme.services.PushIntentService;
import me.justup.upme.utils.CircularImageView;

public class MailContactsAdapter extends CursorAdapter {
    private LayoutInflater mInflater = null;
    private Fragment mParentFragment = null;

    public MailContactsAdapter(Fragment fragment, Context context, Cursor c, int flags) {
        super(context, c, flags);

        this.mParentFragment = fragment;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public void bindView(final View view, final Context context, Cursor cur) {
        final int id = cur.getInt(cur.getColumnIndex(DBHelper.MAIL_CONTACT_SERVER_ID));
        //String imagePath = cur.getString(cur.getColumnIndex(DBHelper.MAIL_CONTACT_IMG));
        String contactName = cur.getString(cur.getColumnIndex(DBHelper.MAIL_CONTACT_NAME));
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder != null) {
            holder.mName.setText(contactName);
            String imagePath = (cur.getString(cur.getColumnIndex(DBHelper.MAIL_CONTACT_IMG)) != null && cur.getString(cur.getColumnIndex(DBHelper.MAIL_CONTACT_IMG)).length() > 1) ? cur.getString(cur.getColumnIndex(DBHelper.MAIL_CONTACT_IMG)) : "fake";
            Picasso.with(context).load(imagePath).placeholder(R.drawable.ic_launcher).into(holder.mImageView);
            // Picasso.with(context).load(imagePath).into(holder.mImageView);
            holder.rowId = id;

            holder.mInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            holder.mCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final int roomId = JustUpApplication.getApplication().getRandomNum();

                    ((MainActivity) mParentFragment.getActivity()).prepareAndCallRTC(String.valueOf(roomId), false, false, 0);

//                    handler.postDelayed(new Runnable() {
//                        public void run() {
//                            doStuff();
//                        }
//                    }, 5000);
                    
                    startNotificationIntent(id, roomId);
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

    public void startNotificationIntent(int userId, int roomNumber) {
        WebRtcStartCallQuery push = new WebRtcStartCallQuery();
        push.params.setUserIds(userId);
        push.params.room_id = String.valueOf(roomNumber);

        Bundle bundle = new Bundle();
        bundle.putSerializable(PushIntentService.PUSH_INTENT_QUERY_EXTRA, push);

        Intent intent = new Intent(mParentFragment.getActivity().getApplicationContext(), PushIntentService.class);
        mParentFragment.getActivity().getApplicationContext().startService(intent.putExtras(bundle));
    }

}
