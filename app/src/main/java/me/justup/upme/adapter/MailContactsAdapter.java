package me.justup.upme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import me.justup.upme.R;
import me.justup.upme.entity.ContactEntity;
import me.justup.upme.utils.CircularImageView;

public class MailContactsAdapter extends ArrayAdapter<ContactEntity> {
    private static class ViewHolder {
        private CircularImageView mImageView;
        private TextView mName;

    }

    public MailContactsAdapter(Context context, List<ContactEntity> contactEntityList) {
        super(context, R.layout.mail_contacts_list_item, contactEntityList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContactEntity contactEntity = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.mail_contacts_list_item, parent, false);
            viewHolder.mImageView = (CircularImageView) convertView.findViewById(R.id.mail_contacts_item_imageView);
            viewHolder.mName = (TextView) convertView.findViewById(R.id.mail_contacts_item_textView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mImageView.setImageDrawable(contactEntity.getmContactImage());
        viewHolder.mName.setText(contactEntity.getmContactName());
        return convertView;
    }
}