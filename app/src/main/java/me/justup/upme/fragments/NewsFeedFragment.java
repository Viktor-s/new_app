package me.justup.upme.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
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

import me.justup.upme.JustUpApplication;
import me.justup.upme.MainActivity;
import me.justup.upme.R;
import me.justup.upme.adapter.NewsFeedAdapter;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.entity.ArticleFullQuery;
import me.justup.upme.entity.ArticleShortCommentEntity;
import me.justup.upme.entity.ArticleShortEntity;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.http.HttpIntentService;
import me.justup.upme.utils.CommonUtils;

import static me.justup.upme.db.DBHelper.IS_SHORT_NEWS_READ_ARTICLE_ID;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_ID;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_IMAGE;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_NAME;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_CONTENT;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_SERVER_ID;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_POSTED_AT;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_SERVER_ID;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_SHORT_DESCR;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_THUMBNAIL;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_TITLE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class NewsFeedFragment extends Fragment {
    private static final String TAG = makeLogTag(NewsFeedFragment.class);

    private RecyclerView mNewsFeedView = null;
    private NewsFeedAdapter mNewsFeedAdapter = null;
    private List<ArticleShortEntity> mNewsFeedEntityList = null;
    private List<ArticleShortEntity> mNewsFeedEntityPartOfList = new ArrayList<>();
    private FrameLayout mNewsItemContainer = null;
    private int mLastChosenPosition = -1;
    private boolean isLoading = true;
    private int mPastVisibleItems, mVisibleItemCount, mTotalItemCount;
    private LinearLayoutManager mLayoutManager = null;
    private int mFrom = 0;
    private int mTo = 10;
    private ProgressBar mProgressBar = null;
    private boolean isFirstArticlesUpdate = true;
    private BroadcastReceiver mNewsFeedReceiver = null;
    private ArrayList<Integer> mReadNewsList = null;

    private View mContentView = null;

    // Instance
    public static NewsFeedFragment newInstance() {
        return new NewsFeedFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mReadNewsList = getAllReadNewsFromCursor(JustUpApplication.getApplication().getTransferActionIsShortNewsRead().getCursorOfIsShortNewsRead(getActivity().getApplicationContext()));

        Cursor cursorNews = JustUpApplication.getApplication().getTransferActionShortNews().getCursorOfShortNews(getActivity().getApplicationContext());
        mNewsFeedEntityList = fillNewsFromCursor(cursorNews, mReadNewsList);
        if (mNewsFeedEntityList.size() >= 10) {
            mNewsFeedEntityPartOfList = getNextArticlesPack();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = super.onCreateView(inflater, container, savedInstanceState);

        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_news_feed, container, false);
        }

        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init UI
        if (getActivity() != null) {
            initUI();
        }
    }

    private void initUI(){
        mNewsItemContainer = (FrameLayout) mContentView.findViewById(R.id.news_item_container_frameLayout);
        mNewsFeedView = (RecyclerView) mContentView.findViewById(R.id.news_RecyclerView);
        mProgressBar = (ProgressBar) mContentView.findViewById(R.id.news_feed_progressbar);

        if (!ApiWrapper.isOnline()) {
            mProgressBar.setVisibility(View.GONE);
        }

        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mNewsFeedView.setLayoutManager(mLayoutManager);
        if (mNewsFeedEntityPartOfList.size() > 0) {
            updateAdapter();
        }

        mNewsFeedView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mVisibleItemCount = mLayoutManager.getChildCount();
                mTotalItemCount = mLayoutManager.getItemCount();
                mPastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
                if (isLoading) {
                    if ((mVisibleItemCount + mPastVisibleItems) >= mTotalItemCount) {
                        int oldListSize = mNewsFeedEntityPartOfList.size();
                        mNewsFeedEntityPartOfList.addAll(getNextArticlesPack());
                        mNewsFeedAdapter.notifyItemRangeInserted(oldListSize, mNewsFeedEntityPartOfList.size());
                        if (mTotalItemCount >= mNewsFeedEntityPartOfList.size()) {
                            isLoading = false;
                        }
                        LOGI(TAG, mNewsFeedEntityPartOfList.size() + " " + mPastVisibleItems + " " + mVisibleItemCount + " " + mTotalItemCount);
                    }
                }
            }
        });
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
                        LOGI(TAG, "OnReceive, first update");

                        Cursor cursorNews = JustUpApplication.getApplication().getTransferActionShortNews().getCursorOfShortNews(getActivity().getApplicationContext());
                        mNewsFeedEntityList = fillNewsFromCursor(cursorNews, mReadNewsList);
                        if (mNewsFeedEntityPartOfList.size() < 10) {
                            mNewsFeedEntityPartOfList.addAll(getNextArticlesPack());
                        }

                        updateAdapter();

                        mProgressBar.setVisibility(View.GONE);
                        isFirstArticlesUpdate = false;
                    } else {
                        LOGI(TAG, "OnReceive, second update");

                        Cursor cursorNews = JustUpApplication.getApplication().getTransferActionShortNews().getCursorOfShortNews(getActivity().getApplicationContext());
                        mNewsFeedEntityList = fillNewsFromCursor(cursorNews, mReadNewsList);
                        isFirstArticlesUpdate = true;
                    }
                }
                if (HttpIntentService.BROADCAST_INTENT_NEWS_FEED_SERVER_ERROR.equals(intent.getAction())) {
                    LOGI(TAG, "OnReceive, error");

                    Toast.makeText(getActivity().getApplicationContext(), "Server error", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        };

        IntentFilter filter = new IntentFilter(DBAdapter.NEWS_FEED_SQL_BROADCAST_INTENT);
        filter.addAction(HttpIntentService.BROADCAST_INTENT_NEWS_FEED_SERVER_ERROR);
        LocalBroadcastManager.getInstance(NewsFeedFragment.this.getActivity()).registerReceiver(mNewsFeedReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(NewsFeedFragment.this.getActivity()).unregisterReceiver(mNewsFeedReceiver);
        LOGI(TAG, "UnregisterRecNewsFeed");
    }

    public void updateLastChosenPosition() {
        mLastChosenPosition = -1;
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

            Cursor cursorComments = JustUpApplication.getApplication().getTransferActionNewsComments().getCursorOfNewsCommentsByArticleId(getActivity().getApplicationContext(), news_id);
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
                if (mLastChosenPosition != position) {
                    mNewsFeedEntityPartOfList.get(position).setViewed(true);

                    // Save Is News is read
                    JustUpApplication.getApplication().getTransferActionIsShortNewsRead().insertNewsReadValue(getActivity(), mNewsFeedEntityPartOfList.get(position).getId());

                    Animation mFragmentSliderFadeIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fragment_item_slide_fade_in);
                    getChildFragmentManager().beginTransaction().replace(R.id.news_item_container_frameLayout, NewsItemFragment.newInstance(mNewsFeedEntityList.get(position).getId())).commit();
                    mNewsItemContainer.startAnimation(mFragmentSliderFadeIn);
                    mLastChosenPosition = position;
                    ((MainActivity) NewsFeedFragment.this.getActivity()).startHttpIntent(getFullDescriptionQuery(mNewsFeedEntityList.get(position).getId()), HttpIntentService.NEWS_PART_FULL);
                }
            }
        });
    }

    public static ArticleFullQuery getFullDescriptionQuery(int id) {
        ArticleFullQuery query = new ArticleFullQuery();
        // query.params.id = id;
        return query;
    }

    private List<ArticleShortEntity> getNextArticlesPack() {
        List<ArticleShortEntity> shortEntityList = new ArrayList<>();
        LOGI(TAG, "GetNextArticlesPack " + mNewsFeedEntityList.size() + " " + mNewsFeedEntityPartOfList.size());

        if (mFrom < mNewsFeedEntityList.size()) {
            if (mTo > mNewsFeedEntityList.size()) {
                mTo = mNewsFeedEntityList.size() - 1;
            }
            for (int i = mFrom; i < mTo; i++) {
                shortEntityList.add(mNewsFeedEntityList.get(i));
            }
            mFrom = mFrom + 10;
            mTo = mTo + 10;
        }

        return shortEntityList;
    }

    public void updateNewsComments() {
        Cursor cursorReadNews = JustUpApplication.getApplication().getTransferActionIsShortNewsRead().getCursorOfIsShortNewsRead(getActivity().getApplicationContext());
        mReadNewsList = getAllReadNewsFromCursor(cursorReadNews);

        Cursor cursorNews = JustUpApplication.getApplication().getTransferActionShortNews().getCursorOfShortNews(getActivity().getApplicationContext());
        mNewsFeedEntityList = fillNewsFromCursor(cursorNews, mReadNewsList);

        int oldSizeValue = mNewsFeedEntityPartOfList.size();
        mNewsFeedEntityPartOfList.clear();
        mNewsFeedEntityPartOfList.addAll(mNewsFeedEntityList.subList(0, oldSizeValue));
        mNewsFeedAdapter.notifyDataSetChanged();
    }

}
