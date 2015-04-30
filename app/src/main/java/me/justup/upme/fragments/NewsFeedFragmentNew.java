package me.justup.upme.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import me.justup.upme.MainActivity;
import me.justup.upme.R;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.entity.ArticleFullQuery;
import me.justup.upme.entity.ArticleShortCommentEntity;
import me.justup.upme.entity.ArticleShortEntity;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.http.HttpIntentService;
import me.justup.upme.utils.CommonUtils;
import me.justup.upme.view.InteractiveScrollView;

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

public class NewsFeedFragmentNew extends Fragment {
    private static final String TAG = makeLogTag(NewsFeedFragmentNew.class);

    private List<ArticleShortEntity> mNewsFeedEntityList = null;
    private List<ArticleShortEntity> mNewsFeedEntityPartOfList = new ArrayList<>();
    private FrameLayout mNewsItemContainer = null;
    private int mLastChosenPosition = -1;
    private boolean isLoading = true;
    private int mVisibleItemCount, mTotalItemCount;
    private String mSelectQueryShortNews = null;
    private int mFrom = 0;
    private int mTo = 10;
    private ProgressBar mProgressBar = null;
    private boolean isFirstArticlesUpdate = true;
    private BroadcastReceiver mNewsFeedReceiver = null;
    private ArrayList<Integer> mReadNewsList = null;
    private SQLiteDatabase mDatabase = null;
    private GridLayout mGridLayout = null;
    private int mColumnLandscape = 3;
    private int mColumnPortrait = 2;
    private int mScreenWidth;
    private LayoutInflater mLayoutInflater = null;
    private boolean isProgressBarShown = true;

    // Children Fragment Param
    private boolean isChildFragmentShow = false;
    private Animation mFragmentSliderOut = null;
    private Animation mFadeIn = null;
    private Animation mFadeOut = null;

    private View mContentView = null;
    private NewsItemFragment mNewsItemFragment = null;

    // Instance
    public static NewsFeedFragmentNew newInstance() {
        return new NewsFeedFragmentNew();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (mDatabase != null) {
            if (!mDatabase.isOpen()) {
                mDatabase = DBAdapter.getInstance().openDatabase();
            }
        } else {
            mDatabase = DBAdapter.getInstance().openDatabase();
        }

        //  Cursor cursorReadNews = mDatabase.rawQuery("SELECT * FROM " + IS_SHORT_NEWS_READ_TABLE_NAME, null);
        //  mReadNewsList = getAllReadNewsFromCursor(cursorReadNews);
        //  if (cursorReadNews != null)
        //      cursorReadNews.close();
        mSelectQueryShortNews = "SELECT * FROM " + SHORT_NEWS_TABLE_NAME;
        Cursor cursorNews = mDatabase.rawQuery(mSelectQueryShortNews, null);
        // mNewsFeedEntityList = fillNewsFromCursor(cursorNews, mReadNewsList);
        mNewsFeedEntityList = fillNewsFromCursor(cursorNews);
        if (mNewsFeedEntityList.size() >= 10) {
            mNewsFeedEntityPartOfList = getNextArticlesPack();
        }

        if (cursorNews != null) {
            cursorNews.close();
        }

        mFragmentSliderOut = AnimationUtils.loadAnimation(getActivity(), R.anim.order_slider_out);
        mFragmentSliderOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mNewsItemContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out_fast));
                    }
                }, 500);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mNewsItemContainer.setVisibility(View.INVISIBLE);

                isChildFragmentShow = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out_fast);
        mFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mNewsItemContainer.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_fast);
        mFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mNewsItemContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(NewsFeedFragmentNew.this.getActivity()).unregisterReceiver(mNewsFeedReceiver);
        LOGI(TAG, "UnregisterRecNewsFeed");
    }

    @Override
    public void onDestroy() {
        LOGI(TAG, "Fragment Destroy");
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
                        Cursor cursorNews = mDatabase.rawQuery(mSelectQueryShortNews, null);
                        //  mNewsFeedEntityList = fillNewsFromCursor(cursorNews, mReadNewsList);
                        mNewsFeedEntityList = fillNewsFromCursor(cursorNews);
                        if (mNewsFeedEntityPartOfList.size() < 10) {
                            mNewsFeedEntityPartOfList.addAll(getNextArticlesPack());
                        }

                        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            int row = mNewsFeedEntityList.size() / mColumnLandscape;
                            mGridLayout.setColumnCount(mColumnLandscape);
                            mGridLayout.setRowCount(row + 1);
                            updateView(mColumnLandscape);
                        } else {
                            int row = mNewsFeedEntityList.size() / mColumnPortrait;
                            mGridLayout.setColumnCount(mColumnPortrait);
                            mGridLayout.setRowCount(row + 1);
                            updateView(mColumnPortrait);
                        }

                        if (cursorNews != null) {
                            cursorNews.close();
                        }

                        mProgressBar.setVisibility(View.GONE);
                        isProgressBarShown = false;
                        isFirstArticlesUpdate = false;
                    } else {
                        LOGI(TAG, "onReceive, second update");
                        Cursor cursorNews = mDatabase.rawQuery(mSelectQueryShortNews, null);
                        //mNewsFeedEntityList = fillNewsFromCursor(cursorNews, mReadNewsList);
                        mNewsFeedEntityList = fillNewsFromCursor(cursorNews);
                        cursorNews.close();
                        isFirstArticlesUpdate = true;
                    }
                }
                if (HttpIntentService.BROADCAST_INTENT_NEWS_FEED_SERVER_ERROR.equals(intent.getAction())) {
                    LOGI(TAG, "onReceive, error");
//                    Toast.makeText(getActivity().getApplicationContext(), "Server error", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                    isProgressBarShown = false;
                }

            }
        };

        IntentFilter filter = new IntentFilter(DBAdapter.NEWS_FEED_SQL_BROADCAST_INTENT);
        filter.addAction(HttpIntentService.BROADCAST_INTENT_NEWS_FEED_SERVER_ERROR);
        LocalBroadcastManager.getInstance(NewsFeedFragmentNew.this.getActivity()).registerReceiver(mNewsFeedReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = super.onCreateView(inflater, container, savedInstanceState);
        mLayoutInflater = LayoutInflater.from(getActivity());

        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_news_feed_new, container, false);
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
        updateScreenWidth();

        mGridLayout = (GridLayout) mContentView.findViewById(R.id.newsFeedGridLayout);
        mProgressBar = (ProgressBar) mContentView.findViewById(R.id.news_feed_progressbar);

        if (isProgressBarShown) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }

        if (mNewsFeedEntityList.size() > 0) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                int row = mNewsFeedEntityList.size() / mColumnLandscape;
                mGridLayout.setColumnCount(mColumnLandscape);
                mGridLayout.setRowCount(row + 1);
                updateView(mColumnLandscape);
            } else {
                int row = mNewsFeedEntityList.size() / mColumnPortrait;
                mGridLayout.setColumnCount(mColumnPortrait);
                mGridLayout.setRowCount(row + 1);
                updateView(mColumnPortrait);
            }
        }

        InteractiveScrollView interactiveScrollView = (InteractiveScrollView) mContentView.findViewById(R.id.interactiveScrollView);
        interactiveScrollView.setOnBottomReachedListener(
                new InteractiveScrollView.OnBottomReachedListener() {
                    @Override
                    public void onBottomReached() {
                        mVisibleItemCount = mGridLayout.getChildCount();
                        mTotalItemCount = mNewsFeedEntityList.size();
                        LOGI(TAG, "Scrollview on bottom reached");

                        if (isLoading) {
                            if (mVisibleItemCount >= mTotalItemCount) {
                                mNewsFeedEntityPartOfList.addAll(getNextArticlesPack());
                                if (mTotalItemCount >= mNewsFeedEntityPartOfList.size()) {
                                    isLoading = false;
                                }

                                LOGI(TAG, mNewsFeedEntityPartOfList.size() + " " + mVisibleItemCount + " " + mTotalItemCount);
                            }
                        }
                    }
                }
        );

        mNewsItemContainer = (FrameLayout) mContentView.findViewById(R.id.news_item_container_frameLayout);

        if (!ApiWrapper.isOnline()) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void updateView(int columnNumber) {
        if (mNewsFeedEntityPartOfList != null && mNewsFeedEntityPartOfList.size() > 0) {
            for (int i = 0, c = 0, r = 0; i < mNewsFeedEntityPartOfList.size(); i++, c++) {
                if (c == columnNumber) {
                    c = 0;
                    r++;
                }

                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = (mScreenWidth / columnNumber);
                param.rightMargin = CommonUtils.convertDpToPixels(getActivity(), 30);
                param.topMargin = CommonUtils.convertDpToPixels(getActivity(), 30);
                param.columnSpec = GridLayout.spec(c);
                param.rowSpec = GridLayout.spec(r);

                LinearLayout shortNewsLayout = (LinearLayout) mLayoutInflater.inflate(R.layout.news_feed_grid_row, null, false);
                TextView shortNewsId = (TextView) shortNewsLayout.findViewById(R.id.grid_hide_id);
                shortNewsId.setText(Integer.toString(mNewsFeedEntityPartOfList.get(i).getId()));
                ImageView shortNewsImage = (ImageView) shortNewsLayout.findViewById(R.id.grid_row_imageView);
                String imagePath = (mNewsFeedEntityPartOfList.get(i).getThumbnail() != null && mNewsFeedEntityPartOfList.get(i).getThumbnail().length() > 1) ? mNewsFeedEntityPartOfList.get(i).getThumbnail() : "fake";
                Picasso.with(getActivity()).load(imagePath).placeholder(R.color.white).fit().centerCrop().into(shortNewsImage);
                TextView shortNewsDate = (TextView) shortNewsLayout.findViewById(R.id.grid_row_date_textView);
                TextView shortNewsCommentsLenght = (TextView) shortNewsLayout.findViewById(R.id.grid_row_comments_lenght_textView);
                shortNewsDate.setText(mNewsFeedEntityPartOfList.get(i).getPosted_at());
                shortNewsCommentsLenght.setText(Integer.toString(mNewsFeedEntityPartOfList.get(i).getComments().size()));
                TextView shortNewsTitle = (TextView) shortNewsLayout.findViewById(R.id.grid_row_name_extView);
                shortNewsTitle.setText(mNewsFeedEntityPartOfList.get(i).getTitle());
                TextView shortNewsDescription = (TextView) shortNewsLayout.findViewById(R.id.grid_row_description_textView);
                shortNewsDescription.setText((mNewsFeedEntityPartOfList.get(i).getShort_descr()));
                shortNewsLayout.setLayoutParams(param);
                shortNewsLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int shortNewsId = Integer.parseInt(((TextView) v.findViewById(R.id.grid_hide_id)).getText().toString());
                            // mNewsFeedEntityPartOfList.get(position).setViewed(true);
                            // DBAdapter.getInstance().saveNewsReadValue(mNewsFeedEntityPartOfList.get(position).getId());

                            LOGI(TAG, "Last pos : " + mLastChosenPosition + ", New pos : " + shortNewsId);
                            if(mNewsItemFragment==null){
                                mNewsItemFragment = NewsItemFragment.newInstance(shortNewsId);
                                ((MainActivity) NewsFeedFragmentNew.this.getActivity()).startHttpIntent(getFullDescriptionQuery(shortNewsId), HttpIntentService.NEWS_PART_FULL);

                                getChildFragmentManager().beginTransaction().replace(R.id.news_item_container_frameLayout, mNewsItemFragment).commit();

                                mNewsItemContainer.setVisibility(View.VISIBLE);
                                // Show Anim
                                mNewsItemContainer.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fragment_slider_in));
                            }else{
                                if(mLastChosenPosition!=shortNewsId){
                                    // Create new Fragment
                                    mNewsItemFragment = NewsItemFragment.newInstance(shortNewsId);
                                    ((MainActivity) NewsFeedFragmentNew.this.getActivity()).startHttpIntent(getFullDescriptionQuery(shortNewsId), HttpIntentService.NEWS_PART_FULL);

                                    getChildFragmentManager().beginTransaction().replace(R.id.news_item_container_frameLayout, mNewsItemFragment).commit();

                                    mNewsItemContainer.setVisibility(View.VISIBLE);
                                    // Show Anim
                                    mNewsItemContainer.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fragment_slider_in));
                                }else{
                                    mNewsItemContainer.setVisibility(View.VISIBLE);
                                    mNewsItemContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_fast));
                                }
                            }

                            isChildFragmentShow = true;
                            mLastChosenPosition = shortNewsId;
                        }
                });

                mGridLayout.addView(shortNewsLayout);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LOGI(TAG, "Fragment Detach");

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Animation getFragmentCloseAnimation(){
        return mFragmentSliderOut;
    }

    public void showFullNews(){
        LOGI(TAG, "Show full news, isChildFragmentShow : " + isChildFragmentShow);

        if(isChildFragmentShow) {
            mNewsItemContainer.startAnimation(mFadeIn);
        }
    }

    public void closeFullNews(){
        LOGI(TAG, "Close full news, isChildFragmentShow : " + isChildFragmentShow);

        if(isChildFragmentShow) {
            mNewsItemContainer.startAnimation(mFadeOut);
        }
    }

    public void updateLastChosenPosition() {
        mLastChosenPosition = -1;
    }

    // private List<ArticleShortEntity> fillNewsFromCursor(Cursor cursorNews, ArrayList<Integer> readNewsList) {
    private List<ArticleShortEntity> fillNewsFromCursor(Cursor cursorNews) {

        ArrayList<ArticleShortEntity> newsList = new ArrayList<>();

        for (cursorNews.moveToFirst(); !cursorNews.isAfterLast(); cursorNews.moveToNext()) {
            ArticleShortEntity articlesResponse = new ArticleShortEntity();
            articlesResponse.setId(cursorNews.getInt(cursorNews.getColumnIndex(SHORT_NEWS_SERVER_ID)));
            articlesResponse.setTitle(cursorNews.getString(cursorNews.getColumnIndex(SHORT_NEWS_TITLE)));
            articlesResponse.setShort_descr(cursorNews.getString(cursorNews.getColumnIndex(SHORT_NEWS_SHORT_DESCR)));
            articlesResponse.setThumbnail(cursorNews.getString(cursorNews.getColumnIndex(SHORT_NEWS_THUMBNAIL)));
            articlesResponse.setPosted_at(cursorNews.getString(cursorNews.getColumnIndex(SHORT_NEWS_POSTED_AT)));
            int news_id = cursorNews.getInt(cursorNews.getColumnIndex(SHORT_NEWS_SERVER_ID));
//            if (readNewsList.contains(news_id)) {
//                articlesResponse.setViewed(true);
//            }

            String selectQueryShortNewsComments = "SELECT * FROM short_news_comments_table WHERE article_id=" + news_id;
            Cursor cursorComments = mDatabase.rawQuery(selectQueryShortNewsComments, null);
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

    public ArticleFullQuery getFullDescriptionQuery(int id) {
        ArticleFullQuery query = new ArticleFullQuery();
        query.params.article_id = id;

        return query;
    }

    private List<ArticleShortEntity> getNextArticlesPack() {
        List<ArticleShortEntity> shortEntityList = new ArrayList<>();
        LOGI(TAG, "getNextArticlesPack " + mNewsFeedEntityList.size() + " " + mNewsFeedEntityPartOfList.size());
        if (mFrom < mNewsFeedEntityList.size()) {
            if (mTo > mNewsFeedEntityList.size()) {
                mTo = mNewsFeedEntityList.size();
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
        Cursor cursorReadNews = mDatabase.rawQuery("SELECT * FROM " + IS_SHORT_NEWS_READ_TABLE_NAME, null);
        mReadNewsList = getAllReadNewsFromCursor(cursorReadNews);
        if (cursorReadNews != null) {
            cursorReadNews.close();
        }

        Cursor cursorNews = mDatabase.rawQuery(mSelectQueryShortNews, null);
        //  mNewsFeedEntityList = fillNewsFromCursor(cursorNews, mReadNewsList);
        mNewsFeedEntityList = fillNewsFromCursor(cursorNews);
        if (cursorNews != null) {
            cursorNews.close();
        }

        int oldSizeValue = mNewsFeedEntityPartOfList.size();
        mNewsFeedEntityPartOfList.clear();
        mNewsFeedEntityPartOfList.addAll(mNewsFeedEntityList.subList(0, oldSizeValue));
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            updateView(mColumnLandscape);
        } else {
            updateView(mColumnPortrait);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mProgressBar.setVisibility(View.GONE);
        mGridLayout.removeAllViews();
        updateScreenWidth();

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            int row = mNewsFeedEntityList.size() / mColumnLandscape;
            mGridLayout.setColumnCount(mColumnLandscape);
            mGridLayout.setRowCount(row + 1);
            updateView(mColumnLandscape);
        } else {
            int row = mNewsFeedEntityList.size() / mColumnPortrait;
            mGridLayout.setColumnCount(mColumnPortrait);
            mGridLayout.setRowCount(row + 1);
            updateView(mColumnPortrait);
        }
    }

    private void updateScreenWidth() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mScreenWidth = size.x - CommonUtils.convertDpToPixels(getActivity(), 165);
        } else {
            mScreenWidth = size.x - CommonUtils.convertDpToPixels(getActivity(), 135);
        }
    }

}
