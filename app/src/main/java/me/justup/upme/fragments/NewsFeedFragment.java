package me.justup.upme.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.justup.upme.MainActivity;
import me.justup.upme.R;
import me.justup.upme.adapter.NewsFeedAdapter;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.entity.ArticleFullQuery;
import me.justup.upme.entity.ArticleShortCommentEntity;
import me.justup.upme.entity.ArticleShortEntity;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.http.HttpIntentService;
import me.justup.upme.utils.AppContext;
import me.justup.upme.utils.CommonUtils;

import static me.justup.upme.db.DBHelper.IS_SHORT_NEWS_READ_ARTICLE_ID;
import static me.justup.upme.db.DBHelper.IS_SHORT_NEWS_READ_TABLE_NAME;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_ID;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_IMAGE;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_NAME;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_CONTENT;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_SERVER_ID;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_POSTED_AT;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_SERVER_ID;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_SHORT_DESCR;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_TABLE_NAME;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_THUMBNAIL;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_TITLE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class NewsFeedFragment extends Fragment {
    private static final String TAG = makeLogTag(NewsFeedFragment.class);

    private RecyclerView mNewsFeedView;
    private NewsFeedAdapter mNewsFeedAdapter;
    private List<ArticleShortEntity> mNewsFeedEntityList;
    private List<ArticleShortEntity> mNewsFeedEntityPartOfList = new ArrayList<>();
    private FrameLayout mNewsItemContainer;
    private int lastChosenPosition = -1;
    private boolean isLoading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private LinearLayoutManager mLayoutManager;
    private String selectQueryShortNews;
    private int from = 0;
    private int to = 10;
    private ProgressBar mProgressBar;
    private boolean isFirstArticlesUpdate = true;
    private BroadcastReceiver mNewsFeedReceiver;
    private ArrayList<Integer> mReadNewsList;
    private SQLiteDatabase database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (database != null) {
            if (!database.isOpen()) {
                database = DBAdapter.getInstance().openDatabase();
            }
        } else {
            database = DBAdapter.getInstance().openDatabase();
        }
        Cursor cursorReadNews = database.rawQuery("SELECT * FROM " + IS_SHORT_NEWS_READ_TABLE_NAME, null);
        mReadNewsList = getAllReadNewsFromCursor(cursorReadNews);
        if (cursorReadNews != null)
            cursorReadNews.close();
        selectQueryShortNews = "SELECT * FROM " + SHORT_NEWS_TABLE_NAME;
        Cursor cursorNews = database.rawQuery(selectQueryShortNews, null);
        mNewsFeedEntityList = fillNewsFromCursor(cursorNews, mReadNewsList);
        if (mNewsFeedEntityList.size() >= 10) {
            mNewsFeedEntityPartOfList = getNextArticlesPack();
        }
        if (cursorNews != null)
            cursorNews.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(NewsFeedFragment.this.getActivity()).unregisterReceiver(mNewsFeedReceiver);
        LOGI(TAG, "unregisterRecNewsFeed");
    }

    @Override
    public void onDestroy() {
        DBAdapter.getInstance().closeDatabase();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        LOGI(TAG, "RegisterRecNewsFeed");
        mNewsFeedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (DBAdapter.NEWS_FEED_SQL_BROADCAST_INTENT.equals(intent.getAction())) {
                    if (isFirstArticlesUpdate) {
                        LOGI(TAG, "onReceive, first update");
                        Cursor cursorNews = database.rawQuery(selectQueryShortNews, null);
                        mNewsFeedEntityList = fillNewsFromCursor(cursorNews, mReadNewsList);
                        if (mNewsFeedEntityPartOfList.size() < 10) {
                            mNewsFeedEntityPartOfList.addAll(getNextArticlesPack());
                        }
                        updateAdapter();

                        if (cursorNews != null) {
                            cursorNews.close();
                        }
                        mProgressBar.setVisibility(View.GONE);
                        isFirstArticlesUpdate = false;
                    } else {
                        LOGI(TAG, "onReceive, second update");
                        Cursor cursorNews = database.rawQuery(selectQueryShortNews, null);
                        mNewsFeedEntityList = fillNewsFromCursor(cursorNews, mReadNewsList);
                        cursorNews.close();
                        isFirstArticlesUpdate = true;
                    }
                }
                if (HttpIntentService.BROADCAST_INTENT_NEWS_FEED_SERVER_ERROR.equals(intent.getAction())) {
                    LOGI(TAG, "onReceive, error");
                    // Toast.makeText(AppContext.getAppContext(), "Server error", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                }

            }
        };
        IntentFilter filter = new IntentFilter(DBAdapter.NEWS_FEED_SQL_BROADCAST_INTENT);
        filter.addAction(HttpIntentService.BROADCAST_INTENT_NEWS_FEED_SERVER_ERROR);
        LocalBroadcastManager.getInstance(NewsFeedFragment.this.getActivity())
                .registerReceiver(mNewsFeedReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);

        mNewsItemContainer = (FrameLayout) view.findViewById(R.id.news_item_container_frameLayout);
        mNewsFeedView = (RecyclerView) view.findViewById(R.id.news_RecyclerView);
        mProgressBar = (ProgressBar) view.findViewById(R.id.news_feed_progressbar);
        if (!ApiWrapper.isOnline()) {
            mProgressBar.setVisibility(View.GONE);
        }
        mLayoutManager = new LinearLayoutManager(AppContext.getAppContext());
        mNewsFeedView.setLayoutManager(mLayoutManager);
        if (mNewsFeedEntityPartOfList.size() > 0) {
            updateAdapter();
        }

        mNewsFeedView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
                if (isLoading) {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        int oldListSize = mNewsFeedEntityPartOfList.size();
                        mNewsFeedEntityPartOfList.addAll(getNextArticlesPack());
                        mNewsFeedAdapter.notifyItemRangeInserted(oldListSize, mNewsFeedEntityPartOfList.size());
                        if (totalItemCount >= mNewsFeedEntityPartOfList.size()) {
                            isLoading = false;
                        }
                        LOGI(TAG, mNewsFeedEntityPartOfList.size() + " " + pastVisibleItems + " " + visibleItemCount + " " + totalItemCount);
                    }
                }
            }
        });

        return view;
    }

    public void updateLastChosenPosition() {
        lastChosenPosition = -1;
    }

    private List<ArticleShortEntity> fillNewsFromCursor(Cursor cursorNews, ArrayList<Integer> readNewsList) {

        ArrayList<ArticleShortEntity> newsList = new ArrayList<>();

        for (cursorNews.moveToFirst(); !cursorNews.isAfterLast(); cursorNews.moveToNext()) {
            ArticleShortEntity articlesResponse = new ArticleShortEntity();
            articlesResponse.setId(cursorNews.getInt(cursorNews.getColumnIndex(SHORT_NEWS_SERVER_ID)));
            articlesResponse.setTitle(cursorNews.getString(cursorNews.getColumnIndex(SHORT_NEWS_TITLE)));
            articlesResponse.setShort_descr(cursorNews.getString(cursorNews.getColumnIndex(SHORT_NEWS_SHORT_DESCR)));
            articlesResponse.setThumbnail(cursorNews.getString(cursorNews.getColumnIndex(SHORT_NEWS_THUMBNAIL)));
            articlesResponse.setPosted_at(cursorNews.getString(cursorNews.getColumnIndex(SHORT_NEWS_POSTED_AT)));
            int news_id = cursorNews.getInt(cursorNews.getColumnIndex(SHORT_NEWS_SERVER_ID));
            if (readNewsList.contains(news_id)) {
                articlesResponse.setViewed(true);
            }
            String selectQueryShortNewsComments = "SELECT * FROM short_news_comments_table WHERE article_id=" + news_id;
            Cursor cursorComments = database.rawQuery(selectQueryShortNewsComments, null);
            ArrayList<ArticleShortCommentEntity> commentsList = new ArrayList<>();
            if (cursorComments != null) {
                for (cursorComments.moveToFirst(); !cursorComments.isAfterLast(); cursorComments.moveToNext()) {
                    ArticleShortCommentEntity articleShortCommentEntity = new ArticleShortCommentEntity();
                    articleShortCommentEntity.setId(cursorComments.getInt(cursorComments.getColumnIndex(SHORT_NEWS_COMMENTS_SERVER_ID)));
                    articleShortCommentEntity.setContent(cursorComments.getString(cursorComments.getColumnIndex(SHORT_NEWS_COMMENTS_CONTENT)));
                    articleShortCommentEntity.setAuthor_id(cursorComments.getInt(cursorComments.getColumnIndex(SHORT_NEWS_COMMENTS_AUTHOR_ID)));
                    articleShortCommentEntity.setAuthor_name(cursorComments.getString(cursorComments.getColumnIndex(SHORT_NEWS_COMMENTS_AUTHOR_NAME)));
                    articleShortCommentEntity.setAuthor_img(cursorComments.getString(cursorComments.getColumnIndex(SHORT_NEWS_COMMENTS_AUTHOR_IMAGE)));
                    commentsList.add(articleShortCommentEntity);
                }
                articlesResponse.setComments(commentsList);
            }
            newsList.add(articlesResponse);

            if (cursorComments != null) {
                cursorComments.close();
            }

        }
        return newsList;
    }

    private ArrayList<Integer> getAllReadNewsFromCursor(Cursor cursorIsRead) {
        ArrayList<Integer> allReadValues = new ArrayList<>();
        for (cursorIsRead.moveToFirst(); !cursorIsRead.isAfterLast(); cursorIsRead.moveToNext()) {
            allReadValues.add(cursorIsRead.getInt(cursorIsRead.getColumnIndex(IS_SHORT_NEWS_READ_ARTICLE_ID)));
        }
        return allReadValues;
    }

    private void updateAdapter() {
        mNewsFeedAdapter = new NewsFeedAdapter(mNewsFeedEntityPartOfList, getActivity());
        mNewsFeedView.setAdapter(mNewsFeedAdapter);
        mNewsFeedAdapter.setOnItemClickListener(new NewsFeedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CommonUtils.hideKeyboard(getActivity());
                if (lastChosenPosition != position) {
                    mNewsFeedEntityPartOfList.get(position).setViewed(true);
                    DBAdapter.getInstance().saveNewsReadValue(mNewsFeedEntityPartOfList.get(position).getId());
                    Animation mFragmentSliderFadeIn = AnimationUtils.loadAnimation(AppContext.getAppContext(), R.anim.fragment_item_slide_fade_in);
                    getChildFragmentManager().beginTransaction().replace(R.id.news_item_container_frameLayout, NewsItemFragment.newInstance(mNewsFeedEntityList.get(position))).commit();
                    mNewsItemContainer.startAnimation(mFragmentSliderFadeIn);
                    lastChosenPosition = position;
                    ((MainActivity) NewsFeedFragment.this.getActivity()).startHttpIntent(getFullDescriptionQuery(mNewsFeedEntityList.get(position).getId()), HttpIntentService.NEWS_PART_FULL);
                }
            }
        });
    }

    public static ArticleFullQuery getFullDescriptionQuery(int id) {
        ArticleFullQuery query = new ArticleFullQuery();
        query.params.id = id;
        return query;
    }

    private List<ArticleShortEntity> getNextArticlesPack() {
        List<ArticleShortEntity> shortEntityList = new ArrayList<>();
        LOGI(TAG, "getNextArticlesPack " + mNewsFeedEntityList.size() + " " + mNewsFeedEntityPartOfList.size());
        if (from < mNewsFeedEntityList.size()) {
            if (to > mNewsFeedEntityList.size()) {
                to = mNewsFeedEntityList.size() - 1;
            }
            for (int i = from; i < to; i++) {
                shortEntityList.add(mNewsFeedEntityList.get(i));
            }
            from = from + 10;
            to = to + 10;
        }
        return shortEntityList;
    }

    public void updateNewsComments() {
        Cursor cursorReadNews = database.rawQuery("SELECT * FROM " + IS_SHORT_NEWS_READ_TABLE_NAME, null);
        mReadNewsList = getAllReadNewsFromCursor(cursorReadNews);
        if (cursorReadNews != null)
            cursorReadNews.close();
        Cursor cursorNews = database.rawQuery(selectQueryShortNews, null);
        mNewsFeedEntityList = fillNewsFromCursor(cursorNews, mReadNewsList);
        if (cursorNews != null) {
            cursorNews.close();
        }
        int oldSizeValue = mNewsFeedEntityPartOfList.size();
        mNewsFeedEntityPartOfList.clear();
        mNewsFeedEntityPartOfList.addAll(mNewsFeedEntityList.subList(0, oldSizeValue));
        mNewsFeedAdapter.notifyDataSetChanged();
    }

}
