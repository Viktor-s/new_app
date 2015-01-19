package me.justup.upme.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import me.justup.upme.R;
import me.justup.upme.entity.NewsFeedEntity;
import me.justup.upme.utils.AppContext;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {

    private List<NewsFeedEntity> mNewsModelEntitiesList;
    private OnItemClickListener mItemClickListener;

    public NewsFeedAdapter(List<NewsFeedEntity> newsModelEntities) {
        this.mNewsModelEntitiesList = newsModelEntities;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_feed_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        NewsFeedEntity newsFeedEntity = mNewsModelEntitiesList.get(i);
        viewHolder.mDate.setText(newsFeedEntity.getNewsDate());
        viewHolder.mTitle.setText(newsFeedEntity.getNewsTitle());
        viewHolder.mText.setText(newsFeedEntity.getNewsText());
        viewHolder.mImage.setImageDrawable(newsFeedEntity.getNewsImage());
        viewHolder.mListViewComments.setAdapter(new NewsCommentsAdapter(AppContext.getAppContext(), newsFeedEntity.getNewsCommentEntityList()));
        viewHolder.mCommentsLength.setText(newsFeedEntity.getNewsCommentEntityList().size() + "");
        viewHolder.mListViewComments.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                v.onTouchEvent(event);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNewsModelEntitiesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mDate;
        private TextView mTitle;
        private TextView mText;
        private ImageView mImage;
        private ListView mListViewComments;
        private TextView mCommentsLength;
        private LinearLayout mClickArea;


        public ViewHolder(View itemView) {
            super(itemView);
            mDate = (TextView) itemView.findViewById(R.id.news_date_textView);
            mTitle = (TextView) itemView.findViewById(R.id.news_title_textView);
            mText = (TextView) itemView.findViewById(R.id.news_text_textView);
            mImage = (ImageView) itemView.findViewById(R.id.news_image_imageView);
            mListViewComments = (ListView) itemView.findViewById(R.id.comments_listView);
            mCommentsLength = (TextView) itemView.findViewById(R.id.comments_length_textView);
            mClickArea = (LinearLayout) itemView.findViewById(R.id.click_area_container_linearLayout);
            mClickArea.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getPosition());
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}