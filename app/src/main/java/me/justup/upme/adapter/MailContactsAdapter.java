package me.justup.upme.adapter;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import me.justup.upme.JustUpApplication;
import me.justup.upme.MainActivity;
import me.justup.upme.R;
import me.justup.upme.db.DBHelper;
import me.justup.upme.http.ApiWrapper;

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
        holder.mImageView = (ImageView) convertView.findViewById(R.id.mail_contacts_item_imageView);
        holder.mName = (TextView) convertView.findViewById(R.id.mail_contacts_item_textView);
        holder.mCall = (ImageButton) convertView.findViewById(R.id.mail_contacts_item_call_button);
        convertView.setTag(holder);

        return convertView;
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cur) {
        final int id = cur.getInt(cur.getColumnIndex(DBHelper.MAIL_CONTACT_SERVER_ID));
        final String contactName = cur.getString(cur.getColumnIndex(DBHelper.MAIL_CONTACT_NAME));

        ViewHolder holder = (ViewHolder) view.getTag();

        if (holder != null) {
            holder.mName.setText(contactName);
            String imagePath = (cur.getString(cur.getColumnIndex(DBHelper.MAIL_CONTACT_IMG)) != null && cur.getString(cur.getColumnIndex(DBHelper.MAIL_CONTACT_IMG)).length() > 1) ? cur.getString(cur.getColumnIndex(DBHelper.MAIL_CONTACT_IMG)) : null;
            if (imagePath != null) {
                ApiWrapper.loadImage(imagePath, holder.mImageView);
            } else {
                int circleColor = context.getResources().getColor(R.color.settings_manual_button);

                TextDrawable drawable = TextDrawable.builder().beginConfig()
                        .withBorder(4)
                        .useFont(Typeface.SANS_SERIF)
                        .toUpperCase()
                        .endConfig()
                        .buildRound(Character.toString((contactName).charAt(0)), circleColor);

                holder.mImageView.setImageDrawable(drawable);
            }

            holder.mCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int roomId = JustUpApplication.getApplication().getRandomNum();

                    Fragment fragmentCall = mParentFragment.getActivity().getFragmentManager().findFragmentById(R.id.container_video_chat);
                    if (fragmentCall == null) {
                        ((MainActivity) mParentFragment.getActivity()).prepareAndCallRTC(String.valueOf(roomId), false, false, 0, id, contactName);
                    }

//                    startNotificationIntent(id, roomId);
                }
            });
        }
    }

    public static class ViewHolder {
        private ImageView mImageView;
        private TextView mName;
        private ImageButton mCall;
    }

}
