package me.justup.upme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.justup.upme.R;
import me.justup.upme.adapter.NewsRecyclerAdapter;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.entity.NewsModelEntity;
import me.justup.upme.utils.AppContext;

import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class NewsFragment extends Fragment {
    private static final String TAG = makeLogTag(NewsFragment.class);

    private RecyclerView newsRecyclerView;
    private NewsRecyclerAdapter newsRecyclerAdapter;
    private DBAdapter mDBAdapter;
    private List<NewsModelEntity> newsModelEntityList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDBAdapter = new DBAdapter(AppContext.getAppContext());
        mDBAdapter.open();
        newsModelEntityList = mDBAdapter.getNewsModelsTestlist();

    }

    @Override
    public void onPause() {
        super.onPause();
        mDBAdapter.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        // logging example
        LOGI(TAG, "Fragment start");

        newsRecyclerView = (RecyclerView) view.findViewById(R.id.newsRecyclerView);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(AppContext.getAppContext()));
        newsRecyclerAdapter = new NewsRecyclerAdapter(newsModelEntityList);
        newsRecyclerView.setAdapter(newsRecyclerAdapter);

        return view;
    }

}
