package me.justup.upme.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import me.justup.upme.R;
import me.justup.upme.adapter.NewsFeedAdapter;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.entity.NewsFeedEntity;
import me.justup.upme.utils.AppContext;

import static me.justup.upme.utils.LogUtils.makeLogTag;


public class NewsFeedFragment extends Fragment {
    private static final String TAG = makeLogTag(NewsFeedFragment.class);

    private RecyclerView mNewsFeedView;
    private NewsFeedAdapter mNewsFeedAdapter;
    private DBAdapter mDBAdapter;
    private List<NewsFeedEntity> mNewsFeedEntityList;
    private FrameLayout mNewsItemContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDBAdapter = new DBAdapter(AppContext.getAppContext());
        mDBAdapter.open();
        mNewsFeedEntityList = mDBAdapter.getNewsModelsTestlist();
    }

    @Override
    public void onPause() {
        super.onPause();
        mDBAdapter.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
        mNewsItemContainer = (FrameLayout) view.findViewById(R.id.news_item_container_frameLayout);
        mNewsFeedView = (RecyclerView) view.findViewById(R.id.news_RecyclerView);
        mNewsFeedView.setLayoutManager(new LinearLayoutManager(AppContext.getAppContext()));
        mNewsFeedAdapter = new NewsFeedAdapter(mNewsFeedEntityList);
        mNewsFeedView.setAdapter(mNewsFeedAdapter);

        mNewsFeedAdapter.setOnItemClickListener(new NewsFeedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                ft.replace(R.id.news_item_container_frameLayout, NewsItemFragment.newInstance(mNewsFeedEntityList.get(position)));
                ft.commit();
            }
        });
        return view;
    }


}

