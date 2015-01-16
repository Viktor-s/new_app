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
    // View lookup cache
    private static class ViewHolder {
        ImageView image;
        TextView title;
        TextView mainText;

    }

    public NewsCommentsAdapter(Context context, List<NewsCommentEntity> users) {
        super(context, R.layout.news_comments_listview_item, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        NewsCommentEntity newsCommentEntity = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.news_comments_listview_item, parent, false);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.imageViewNewsComment);
            viewHolder.title = (TextView) convertView.findViewById(R.id.textViewNewsCommentTitle);
            viewHolder.mainText = (TextView) convertView.findViewById(R.id.textViewNewsCommentMainText);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.image.setImageDrawable(newsCommentEntity.getCommentImage());
        viewHolder.title.setText(newsCommentEntity.getCommentTitle());
        viewHolder.mainText.setText(newsCommentEntity.getCommentText());
        // Return the completed view to render on screen
        return convertView;
    }
}