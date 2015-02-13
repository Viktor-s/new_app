package me.justup.upme.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import me.justup.upme.R;
import me.justup.upme.entity.ArticleShortEntity;
import me.justup.upme.utils.AppContext;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {

    private List<ArticleShortEntity> mNewsModelEntitiesList;
    private OnItemClickListener mItemClickListener;
    private Context context;

    public NewsFeedAdapter(List<ArticleShortEntity> newsModelEntities, Context context) {
        this.mNewsModelEntitiesList = newsModelEntities;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_feed_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ArticleShortEntity newsFeedEntity = mNewsModelEntitiesList.get(i);
        viewHolder.mDate.setText(newsFeedEntity.getPosted_at());
        viewHolder.mTitle.setText(newsFeedEntity.getTitle());
        viewHolder.mText.setText(newsFeedEntity.getShort_descr());
        Picasso.with(context).load(newsFeedEntity.getThumbnail()).into(viewHolder.mImage);
        //viewHolder.mImage.setImageDrawable(newsFeedEntity.result.get(i).thumbnail);
        if (newsFeedEntity.getComments() != null) {
            viewHolder.mListViewComments.setAdapter(new NewsCommentsAdapter(AppContext.getAppContext(), newsFeedEntity.getComments()));
            viewHolder.mCommentsLength.setText(newsFeedEntity.getComments().size() + "");
        }

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
        private ImageView mIsNewsViewed;


        public ViewHolder(View itemView) {
            super(itemView);
            mDate = (TextView) itemView.findViewById(R.id.news_date_textView);
            mTitle = (TextView) itemView.findViewById(R.id.news_title_textView);
            mText = (TextView) itemView.findViewById(R.id.news_text_textView);
            mImage = (ImageView) itemView.findViewById(R.id.news_image_imageView);
            mListViewComments = (ListView) itemView.findViewById(R.id.comments_listView);
            mCommentsLength = (TextView) itemView.findViewById(R.id.comments_length_textView);
            mIsNewsViewed = (ImageView) itemView.findViewById(R.id.is_news_viewed_imageView);
            mClickArea = (LinearLayout) itemView.findViewById(R.id.click_area_container_linearLayout);
            mClickArea.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getPosition());
                mItemClickListener.onItemClick(view, getPosition());
                if (mIsNewsViewed.getVisibility() == View.VISIBLE) {
                    Animation animation = AnimationUtils.loadAnimation(AppContext.getAppContext(), R.anim.news_is_viewed_scale);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mIsNewsViewed.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationStart(Animation animation) {

                        }
                    });
                    mIsNewsViewed.startAnimation(animation);
                }
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