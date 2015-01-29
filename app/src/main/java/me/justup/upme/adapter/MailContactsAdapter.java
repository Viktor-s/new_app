package me.justup.upme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import me.justup.upme.R;
import me.justup.upme.entity.MailContactEntity;
import me.justup.upme.utils.CircularImageView;

public class MailContactsAdapter extends ArrayAdapter<MailContactEntity> {
    private Picasso mPicasso;

    private static class ViewHolder {
        private CircularImageView mImageView;
        private TextView mName;
        private Button mCall;
        private Button mInfo;


    }

    public MailContactsAdapter(Context context, List<MailContactEntity> contactEntityList) {
        super(context, R.layout.mail_contacts_list_item, contactEntityList);
        this.mPicasso = Picasso.with(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MailContactEntity contactEntity = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.mail_contacts_list_item, parent, false);
            viewHolder.mImageView = (CircularImageView) convertView.findViewById(R.id.mail_contacts_item_imageView);
            viewHolder.mName = (TextView) convertView.findViewById(R.id.mail_contacts_item_textView);
            viewHolder.mInfo = (Button) convertView.findViewById(R.id.mail_contacts_item_info_button);
            viewHolder.mCall = (Button) convertView.findViewById(R.id.mail_contacts_item_call_button);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        mPicasso.load(contactEntity.getImg()).into(viewHolder.mImageView);

        viewHolder.mName.setText(contactEntity.getName());
        return convertView;
    }
}