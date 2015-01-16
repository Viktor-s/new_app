package me.justup.upme.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import me.justup.upme.R;
import me.justup.upme.entity.NewsModelEntity;
import me.justup.upme.utils.AppContext;

public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.ViewHolder> {

    private List<NewsModelEntity> newsModelEntities;

    public NewsRecyclerAdapter(List<NewsModelEntity> records) {
        this.newsModelEntities = records;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_recicleview_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        NewsModelEntity newsModelEntity = newsModelEntities.get(i);
        viewHolder.date.setText(newsModelEntity.getNewsDate());
        viewHolder.title.setText(newsModelEntity.getNewsTitle());
        viewHolder.text.setText(newsModelEntity.getNewsText());
        viewHolder.image.setImageDrawable(newsModelEntity.getNewsImage());
        viewHolder.listViewComments.setAdapter(new NewsCommentsAdapter(AppContext.getAppContext(), newsModelEntity.getNewsCommentEntityList()));

    }

    @Override
    public int getItemCount() {
        return newsModelEntities.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView date;
        private TextView title;
        private TextView text;
        private ImageView image;
        private ListView listViewComments;

        public ViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.textViewNewsDate);
            title = (TextView) itemView.findViewById(R.id.textViewNewsTitle);
            text = (TextView) itemView.findViewById(R.id.textViewNewsText);
            image = (ImageView) itemView.findViewById(R.id.imageViewNews);
            listViewComments = (ListView) itemView.findViewById(R.id.listViewComments);
        }
    }
}