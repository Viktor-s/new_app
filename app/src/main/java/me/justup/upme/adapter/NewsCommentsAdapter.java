package me.justup.upme.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
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

        String name = newsCommentEntity.getAuthor_name();
        String postInfo = newsCommentEntity.getPosted_at();
        viewHolder.mTitle.setText(name + " " + postInfo);
        viewHolder.mMainText.setText(newsCommentEntity.getContent());

        String imagePath = (newsCommentEntity.getAuthor_img() != null && newsCommentEntity.getAuthor_img().length() > 1) ? newsCommentEntity.getAuthor_img() : null;
        if(imagePath!=null) {
            Picasso.with(context).load(imagePath).placeholder(R.mipmap.ic_launcher).into(viewHolder.mImageView);
        }else{
            int circleColor = context.getResources().getColor(R.color.settings_manual_button);

            TextDrawable drawable = TextDrawable.builder().beginConfig()
                    .withBorder(4)
                    .useFont(Typeface.SANS_SERIF)
                    .toUpperCase()
                    .endConfig()
                    .buildRound(Character.toString((name).charAt(0)), circleColor);

            viewHolder.mImageView.setImageDrawable(drawable);
        }

        return convertView;
    }
}