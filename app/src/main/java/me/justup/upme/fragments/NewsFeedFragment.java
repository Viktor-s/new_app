package me.justup.upme.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import java.util.List;

import me.justup.upme.R;
import me.justup.upme.adapter.NewsFeedAdapter;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.entity.NewsFeedEntity;
import me.justup.upme.utils.AppContext;

import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class NewsFeedFragment extends Fragment {
    private static final String TAG = makeLogTag(NewsFeedFragment.class);
    private RecyclerView mNewsFeedView;
    private NewsFeedAdapter mNewsFeedAdapter;
    private DBAdapter mDBAdapter;
    private List<NewsFeedEntity> mNewsFeedEntityList;
    private FrameLayout mNewsItemContainer;
    private int lastChosenPosition = -1;
    private boolean isLoading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private LinearLayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDBAdapter = new DBAdapter(AppContext.getAppContext());
        mDBAdapter.open();
        mNewsFeedEntityList = mDBAdapter.getNewsModelsTestList();
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

        mLayoutManager = new LinearLayoutManager(AppContext.getAppContext());
        mNewsFeedView.setLayoutManager(mLayoutManager);
        mNewsFeedAdapter = new NewsFeedAdapter(mNewsFeedEntityList);
        mNewsFeedView.setAdapter(mNewsFeedAdapter);

        mNewsFeedAdapter.setOnItemClickListener(new NewsFeedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (lastChosenPosition != position) {
                    Animation mFragmentSliderFadeIn = AnimationUtils.loadAnimation(AppContext.getAppContext(), R.anim.fragment_item_slide_fade_in);
                    final FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                    ft.replace(R.id.news_item_container_frameLayout, NewsItemFragment.newInstance(mNewsFeedEntityList.get(position)));
                    ft.commit();
                    mNewsItemContainer.startAnimation(mFragmentSliderFadeIn);
                    lastChosenPosition = position;
                }
            }
        });
        mNewsFeedView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
                if (isLoading) {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        mNewsFeedEntityList.addAll(mDBAdapter.getNextPackOfNewsModelsTestList());
                        mNewsFeedAdapter.notifyDataSetChanged();
                        if (mNewsFeedEntityList.size() >= 50) {
                            isLoading = false;
                        }
                        LOGI(TAG, mNewsFeedEntityList.size() + " " + pastVisibleItems + " " + visibleItemCount + " " + totalItemCount);
                    }
                }
            }
        });
        return view;
    }

}

