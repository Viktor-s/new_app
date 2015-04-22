package me.justup.upme.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import me.justup.upme.R;
import me.justup.upme.entity.ArticleShortCommentEntity;

public class NewsCommentsAdapter extends ArrayAdapter<ArticleShortCommentEntity> {
    private final Context context;

    private static class ViewHolder {
        private ImageView mImageView;
        private TextView mTitle;
        private TextView mMainText;
    }

    public NewsCommentsAdapter(Context context, List<ArticleShortCommentEntity> users) {
        super(context, R.layout.news_comments_list_item, users);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ArticleShortCommentEntity newsCommentEntity = getItem(position);
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
        String imagePath = (newsCommentEntity.getAuthor_img() != null && newsCommentEntity.getAuthor_img().length() > 1) ? newsCommentEntity.getAuthor_img() : "fake";
        Picasso.with(context).load(imagePath).placeholder(R.drawable.ic_launcher).into(viewHolder.mImageView);
        viewHolder.mTitle.setText(newsCommentEntity.getAuthor_name() + " " + newsCommentEntity.getPosted_at());
       // viewHolder.mTitle.setText(newsCommentEntity.getAuthor_name() );
        viewHolder.mMainText.setText(newsCommentEntity.getContent());
        return convertView;
    }
}