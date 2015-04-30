package me.justup.upme.fragments;

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

    private List<ArticleShortEntity> mNewsFeedEntityList;
    private List<ArticleShortEntity> mNewsFeedEntityPartOfList = new ArrayList<>();
    private FrameLayout mNewsItemContainer;
    private int lastChosenPosition = -1;
    private boolean isLoading = true;
    private int visibleItemCount, totalItemCount;
    private String selectQueryShortNews;
    private int from = 0;
    private int to = 10;
    private ProgressBar mProgressBar;
    private boolean isFirstArticlesUpdate = true;
    private BroadcastReceiver mNewsFeedReceiver;
    private ArrayList<Integer> mReadNewsList;
    private SQLiteDatabase database;
    private GridLayout gridLayout;
    private int columnLandscape = 3;
    private int columnPortrait = 2;
    private int screenWidth;
    private LayoutInflater layoutInflater;
    private boolean isProgressBarShown = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (database != null) {
            if (!database.isOpen()) {
                database = DBAdapter.getInstance().openDatabase();
            }
        } else {
            database = DBAdapter.getInstance().openDatabase();
        }
        //  Cursor cursorReadNews = database.rawQuery("SELECT * FROM " + IS_SHORT_NEWS_READ_TABLE_NAME, null);
        //   mReadNewsList = getAllReadNewsFromCursor(cursorReadNews);
        //  if (cursorReadNews != null)
        //      cursorReadNews.close();
        selectQueryShortNews = "SELECT * FROM " + SHORT_NEWS_TABLE_NAME;
        Cursor cursorNews = database.rawQuery(selectQueryShortNews, null);
        // mNewsFeedEntityList = fillNewsFromCursor(cursorNews, mReadNewsList);
        mNewsFeedEntityList = fillNewsFromCursor(cursorNews);
        if (mNewsFeedEntityList.size() >= 10) {
            mNewsFeedEntityPartOfList = getNextArticlesPack();
        }
        if (cursorNews != null)
            cursorNews.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(NewsFeedFragmentNew.this.getActivity()).unregisterReceiver(mNewsFeedReceiver);
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
                        //  mNewsFeedEntityList = fillNewsFromCursor(cursorNews, mReadNewsList);
                        mNewsFeedEntityList = fillNewsFromCursor(cursorNews);
                        if (mNewsFeedEntityPartOfList.size() < 10) {
                            mNewsFeedEntityPartOfList.addAll(getNextArticlesPack());
                        }
                        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            int row = mNewsFeedEntityList.size() / columnLandscape;
                            gridLayout.setColumnCount(columnLandscape);
                            gridLayout.setRowCount(row + 1);
                            updateView(columnLandscape);
                        } else {
                            int row = mNewsFeedEntityList.size() / columnPortrait;
                            gridLayout.setColumnCount(columnPortrait);
                            gridLayout.setRowCount(row + 1);
                            updateView(columnPortrait);
                        }
                        if (cursorNews != null) {
                            cursorNews.close();
                        }
                        mProgressBar.setVisibility(View.GONE);
                        isProgressBarShown = false;
                        isFirstArticlesUpdate = false;
                    } else {
                        LOGI(TAG, "onReceive, second update");
                        Cursor cursorNews = database.rawQuery(selectQueryShortNews, null);
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
        LocalBroadcastManager.getInstance(NewsFeedFragmentNew.this.getActivity())
                .registerReceiver(mNewsFeedReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_feed_new, container, false);
        updateScreenWidth();
        layoutInflater = LayoutInflater.from(getActivity());
        gridLayout = (GridLayout) view.findViewById(R.id.newsFeedGridLayout);
        mProgressBar = (ProgressBar) view.findViewById(R.id.news_feed_progressbar);

        if (isProgressBarShown) {
            mProgressBar.setVisibility(View.VISIBLE);

        } else {
            mProgressBar.setVisibility(View.GONE);
        }
        if (mNewsFeedEntityList.size() > 0) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                int row = mNewsFeedEntityList.size() / columnLandscape;
                gridLayout.setColumnCount(columnLandscape);
                gridLayout.setRowCount(row + 1);
                updateView(columnLandscape);
            } else {
                int row = mNewsFeedEntityList.size() / columnPortrait;
                gridLayout.setColumnCount(columnPortrait);
                gridLayout.setRowCount(row + 1);
                updateView(columnPortrait);
            }

        }

        InteractiveScrollView interactiveScrollView = (InteractiveScrollView) view.findViewById(R.id.interactiveScrollView);
        interactiveScrollView.setOnBottomReachedListener(
                new InteractiveScrollView.OnBottomReachedListener() {
                    @Override
                    public void onBottomReached() {
                        visibleItemCount = gridLayout.getChildCount();
                        totalItemCount = mNewsFeedEntityList.size();
                        LOGI(TAG, "scrollview on bottom reached");

                        if (isLoading) {
                            if (visibleItemCount >= totalItemCount) {
                                mNewsFeedEntityPartOfList.addAll(getNextArticlesPack());
                                if (totalItemCount >= mNewsFeedEntityPartOfList.size()) {
                                    isLoading = false;
                                }
                                LOGI(TAG, mNewsFeedEntityPartOfList.size() + " " + visibleItemCount + " " + totalItemCount);
                            }
                        }
                    }
                }
        );

        mNewsItemContainer = (FrameLayout) view.findViewById(R.id.news_item_container_frameLayout);

        if (!ApiWrapper.isOnline()) {
            mProgressBar.setVisibility(View.GONE);
        }
        return view;
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
                param.width = (screenWidth / columnNumber);
                param.rightMargin = CommonUtils.convertDpToPixels(getActivity(), 30);
                param.topMargin = CommonUtils.convertDpToPixels(getActivity(), 30);
                param.columnSpec = GridLayout.spec(c);
                param.rowSpec = GridLayout.spec(r);

                LinearLayout shortNewsLayout = (LinearLayout) layoutInflater.inflate(R.layout.news_feed_grid_row, null, false);
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
                        if (lastChosenPosition != shortNewsId) {
                            // mNewsFeedEntityPartOfList.get(position).setViewed(true);
                            //  DBAdapter.getInstance().saveNewsReadValue(mNewsFeedEntityPartOfList.get(position).getId());
                            Animation mFragmentSliderFadeIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fragment_slider_in);
                            getChildFragmentManager().beginTransaction().replace(R.id.news_item_container_frameLayout, NewsItemFragment.newInstance(shortNewsId)).commit();
                            mNewsItemContainer.startAnimation(mFragmentSliderFadeIn);
                            lastChosenPosition = shortNewsId;
                            ((MainActivity) NewsFeedFragmentNew.this.getActivity()).startHttpIntent(getFullDescriptionQuery(shortNewsId), HttpIntentService.NEWS_PART_FULL);
                        }
                    }
                });
                gridLayout.addView(shortNewsLayout);
            }
        }
    }


    public void updateLastChosenPosition() {
        lastChosenPosition = -1;
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

    public ArticleFullQuery getFullDescriptionQuery(int id) {
        ArticleFullQuery query = new ArticleFullQuery();
        query.params.article_id = id;
        return query;
    }

    private List<ArticleShortEntity> getNextArticlesPack() {
        List<ArticleShortEntity> shortEntityList = new ArrayList<>();
        LOGI(TAG, "getNextArticlesPack " + mNewsFeedEntityList.size() + " " + mNewsFeedEntityPartOfList.size());
        if (from < mNewsFeedEntityList.size()) {
            if (to > mNewsFeedEntityList.size()) {
                to = mNewsFeedEntityList.size();
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
        //  mNewsFeedEntityList = fillNewsFromCursor(cursorNews, mReadNewsList);
        mNewsFeedEntityList = fillNewsFromCursor(cursorNews);
        if (cursorNews != null) {
            cursorNews.close();
        }
        int oldSizeValue = mNewsFeedEntityPartOfList.size();
        mNewsFeedEntityPartOfList.clear();
        mNewsFeedEntityPartOfList.addAll(mNewsFeedEntityList.subList(0, oldSizeValue));
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            updateView(columnLandscape);
        } else {
            updateView(columnPortrait);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mProgressBar.setVisibility(View.GONE);
        gridLayout.removeAllViews();
        updateScreenWidth();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            int row = mNewsFeedEntityList.size() / columnLandscape;
            gridLayout.setColumnCount(columnLandscape);
            gridLayout.setRowCount(row + 1);
            updateView(columnLandscape);
        } else {
            int row = mNewsFeedEntityList.size() / columnPortrait;
            gridLayout.setColumnCount(columnPortrait);
            gridLayout.setRowCount(row + 1);
            updateView(columnPortrait);
        }

    }

    private void updateScreenWidth() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            screenWidth = size.x - CommonUtils.convertDpToPixels(getActivity(), 165);
        } else {
            screenWidth = size.x - CommonUtils.convertDpToPixels(getActivity(), 135);
        }

    }

}
