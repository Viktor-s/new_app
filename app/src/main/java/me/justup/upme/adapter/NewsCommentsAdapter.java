package me.justup.upme.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.justup.upme.R;
import me.justup.upme.entity.NewsCommentEntity;

public class NewsCommentsAdapter extends ArrayAdapter<NewsCommentEntity> {
    private static class ViewHolder {
        private ImageView mImageView;
        private TextView mTitle;
        private TextView mMainText;
    }

    public NewsCommentsAdapter(Context context, List<NewsCommentEntity> users) {
        super(context, R.layout.news_comments_list_item, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewsCommentEntity newsCommentEntity = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.news_comments_list_item, parent, false);
            viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.news_comment_imageView);
            viewHolder.mTitle = (TextView) convertView.findViewById(R.id.news_comment_title_textView);
            viewHolder.mMainText = (TextView) convertView.findViewById(R.id.news_comment_main_text_textView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mImageView.setImageDrawable(newsCommentEntity.getCommentImage());
        viewHolder.mTitle.setText(newsCommentEntity.getCommentTitle());
        viewHolder.mMainText.setText(newsCommentEntity.getCommentText());
        return convertView;
    }
}