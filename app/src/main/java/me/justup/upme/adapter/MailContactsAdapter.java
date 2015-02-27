package me.justup.upme.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import me.justup.upme.R;
import me.justup.upme.db.DBHelper;
import me.justup.upme.entity.SendNotificationQuery;
import me.justup.upme.fragments.MailFragment;
import me.justup.upme.services.PushIntentService;
import me.justup.upme.utils.AppPreferences;
import me.justup.upme.utils.CircularImageView;
import me.justup.upme.webrtc.RTCActivity;


public class MailContactsAdapter extends CursorAdapter {
    private LayoutInflater mInflater;
    final Fragment hostFragment;
    private Activity parentActivity;
    private Context context;

    public MailContactsAdapter(Fragment hostFragment, Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.hostFragment = hostFragment;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parentActivity = hostFragment.getActivity();
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
    public void bindView(View view, final Context context, Cursor cur) {
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
//                    final FragmentTransaction ft = hostFragment.getChildFragmentManager().beginTransaction();
//                    ft.replace(R.id.mail_messages_container_frameLayout, new ProductsFragment());
//                    ft.commit();
                }
            });


            holder.mCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG11", "setOnClickListener");
//                    final FragmentTransaction ft = hostFragment.getChildFragmentManager().beginTransaction();
//                    ft.replace(R.id.mail_messages_container_frameLayout, MailVideoFragment.newInstance(Integer.toString(id)));
//                    ft.commit();

                    final Dialog dialog = new Dialog(parentActivity);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.setContentView(R.layout.dialog_call);

                    final EditText roomId = (EditText) dialog.findViewById(R.id.room_id);
                    Button btnCancell = (Button) dialog.findViewById(R.id.cancell);
                    btnCancell.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    Button buttonCallOut = (Button) dialog.findViewById(R.id.button_call_out);
                    buttonCallOut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startNotificationIntent(id, 0);

                            parentActivity.startActivity(new Intent(parentActivity, RTCActivity.class));
                            dialog.dismiss();
                        }
                    });
                    Button buttonCallIn = (Button) dialog.findViewById(R.id.button_call_in);
                    buttonCallIn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(parentActivity, RTCActivity.class);
                            intent.putExtra("room_id", roomId.getText().toString());
                            parentActivity.startActivity(intent);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
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
        AppPreferences appPreferences = new AppPreferences(context);
        int ownerId = appPreferences.getUserId();
        String ownerName = appPreferences.getUserName();

        SendNotificationQuery push = new SendNotificationQuery();
        push.params.user_id = userId;
        push.params.data.owner_id = ownerId;
        push.params.data.owner_name = ownerName;
        push.params.data.connection_type = MailFragment.WEBRTC;
        push.params.data.room = roomNumber;

        Bundle bundle = new Bundle();
        bundle.putSerializable(PushIntentService.PUSH_INTENT_QUERY_EXTRA, push);

        Intent intent = new Intent(context, PushIntentService.class);
        context.startService(intent.putExtras(bundle));
    }

}
