package me.justup.upme.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import me.justup.upme.R;
import me.justup.upme.adapter.NewsCommentsAdapter;
import me.justup.upme.entity.NewsFeedEntity;
import me.justup.upme.utils.AnimateButtonClose;
import me.justup.upme.utils.AppContext;

public class NewsItemFragment extends Fragment {
    private static final String ARG_NEWS_FEED_ENTITY = "news_feed_entity";
    private static final int LIST_DIVIDER_HEIGHT = 24;
    private NewsFeedEntity mNewsFeedEntity;
    private TextView mNewsItemTitle;
    private TextView mNewsItemMainText;
    private ListView mNewsItemCommentsListView;
    private Button mNewsItemCloseButton;

    public static NewsItemFragment newInstance(NewsFeedEntity newsFeedEntity) {
        NewsItemFragment fragment = new NewsItemFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NEWS_FEED_ENTITY, newsFeedEntity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mNewsFeedEntity = (NewsFeedEntity) bundle.getSerializable(ARG_NEWS_FEED_ENTITY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_item, container, false);
        mNewsItemTitle = (TextView) view.findViewById(R.id.news_item_title_textView);
        mNewsItemMainText = (TextView) view.findViewById(R.id.news_item_main_text_textView);
        mNewsItemCommentsListView = (ListView) view.findViewById(R.id.news_item_comments_listView);
        mNewsItemCloseButton = (Button) view.findViewById(R.id.news_item_close_button);
        mNewsItemCloseButton.setVisibility(View.INVISIBLE);
        mNewsItemCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragment().getChildFragmentManager().beginTransaction().remove(NewsItemFragment.this).commit();
            }
        });
        fillViewsWithData();
        AnimateButtonClose.animateButtonClose(mNewsItemCloseButton);
        return view;
    }

    private void fillViewsWithData() {
        mNewsItemTitle.setText(mNewsFeedEntity.getNewsTitle());
        mNewsItemMainText.setText(mNewsFeedEntity.getNewsText());
        mNewsItemCommentsListView.setAdapter(new NewsCommentsAdapter(AppContext.getAppContext(), mNewsFeedEntity.getNewsCommentEntityList()));
        setListViewHeightBasedOnChildren(mNewsItemCommentsListView);
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        int listViewElementsHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View mView = listAdapter.getView(i, null, listView);
            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            listViewElementsHeight += mView.getMeasuredHeight() + LIST_DIVIDER_HEIGHT;
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = listViewElementsHeight + LIST_DIVIDER_HEIGHT;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
