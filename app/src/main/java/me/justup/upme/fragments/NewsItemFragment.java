package me.justup.upme.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import java.util.ArrayList;
import java.util.List;

import me.justup.upme.MainActivity;
import me.justup.upme.R;
import me.justup.upme.adapter.NewsCommentsAdapter;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.dialogs.WarningDialog;
import me.justup.upme.entity.ArticleFullEntity;
import me.justup.upme.entity.ArticleShortCommentEntity;
import me.justup.upme.entity.CommentAddQuery;
import me.justup.upme.entity.CommentsArticleFullQuery;
import me.justup.upme.http.HttpIntentService;
import me.justup.upme.utils.AnimateButtonClose;
import me.justup.upme.utils.AppPreferences;
import me.justup.upme.utils.CommonUtils;

import static me.justup.upme.db.DBHelper.FULL_NEWS_FULL_DESCR;
import static me.justup.upme.db.DBHelper.FULL_NEWS_SERVER_ID;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_ID;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_IMAGE;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_NAME;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_CONTENT;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_SERVER_ID;
import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class NewsItemFragment extends Fragment {
    private static final String TAG = makeLogTag(NewsItemFragment.class);
    private static final String ARG_NEWS_FEED_ENTITY = "news_feed_entity";
    private static final String QUERY_COMMENTS_PATH = "SELECT * FROM short_news_comments_table WHERE article_id=";
    private static final String QUERY_FULL_ARTICLE_PATH = "SELECT * FROM full_news_table WHERE server_id=";
    private static final int LIST_DIVIDER_HEIGHT = 24;
    public static int newsFeedEntityId;
    private WebView mNewsItemWebView;
    private EditText mNewsItemCommentEditText;
    private Button mNewsItemAddCommentButton;
    private ListView mNewsItemCommentsListView;
    private List<ArticleShortCommentEntity> articleCommentsList;
    private ArticleFullEntity mArticleFullEntity;
    private String selectQueryFullNews;
    private boolean isBroadcastUpdateFullArticle = true;
    private boolean isBroadcastAddComment = false;
    private boolean isBroadcastUpdateComments = false;
    private SocialAuthAdapter adapter;
    private Button mShareButton;
    private BroadcastReceiver receiver;
    private Animation mFragmentSliderOut;
    private SQLiteDatabase database;
    private ArticleShortCommentEntity mLastShortComment;
    private ScrollView mNewsItemScrollView;
    private ProgressBar mProgressBar;
    private boolean isScreenOrienrtationChanged = false;


    public static NewsItemFragment newInstance(int shortNewsId) {
        NewsItemFragment fragment = new NewsItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_NEWS_FEED_ENTITY, shortNewsId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            newsFeedEntityId = bundle.getInt(ARG_NEWS_FEED_ENTITY);
        }
        selectQueryFullNews = QUERY_FULL_ARTICLE_PATH + newsFeedEntityId;
        database = DBAdapter.getInstance().openDatabase();
    }


    @Override
    public void onResume() {
        super.onResume();
        // mDBAdapter.open();
        LOGI(TAG, "RegisterRecNewsItem");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isBroadcastUpdateFullArticle) {
                    LOGI(TAG, "onReceive isBroadcastUpdateFullArticle");
                    Cursor cursorNews = database.rawQuery(selectQueryFullNews, null);
                    mArticleFullEntity = fillFullNewsFromCursor(cursorNews);
                    fillViewsWithData();
                    //updateFullNewsCursor();
                    if (cursorNews != null) {
                        cursorNews.close();
                    }

                } else if (isBroadcastAddComment) {
                    isBroadcastUpdateFullArticle = false;
                    isBroadcastAddComment = false;
                    isBroadcastUpdateComments = true;
                    ArticleShortCommentEntity articleShortCommentEntity = mLastShortComment;
                    articleShortCommentEntity.setContent(CommonUtils.convertFromUTF8(mLastShortComment.getContent()));
                    articleCommentsList.add(articleShortCommentEntity);
                    mNewsItemCommentsListView.setAdapter(new NewsCommentsAdapter(getActivity().getApplicationContext(), articleCommentsList));
                    setListViewHeightBasedOnChildren(mNewsItemCommentsListView);
                    mNewsItemCommentEditText.setText("");
                    ((MainActivity) NewsItemFragment.this.getActivity()).startHttpIntent(getCommentsFullArticleQuery(mArticleFullEntity.getId(), 100, 0), HttpIntentService.GET_COMMENTS_FULL_ARTICLE);
                } else if (isBroadcastUpdateComments) {
                    ((NewsFeedFragmentNew) getParentFragment()).updateNewsComments();
                    isBroadcastUpdateFullArticle = true;
                    isBroadcastAddComment = false;
                    isBroadcastUpdateComments = false;
                    mNewsItemAddCommentButton.setEnabled(true);
                }
            }
        };
        LocalBroadcastManager.getInstance(NewsItemFragment.this.getActivity()).registerReceiver(receiver, new IntentFilter(DBAdapter.NEWS_ITEM_SQL_BROADCAST_INTENT)
        );

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(NewsItemFragment.this.getActivity()).unregisterReceiver(receiver);
        LOGI(TAG, "unregisterRecNewsItem");

    }

    @Override
    public void onDestroy() {
        DBAdapter.getInstance().closeDatabase();
        super.onDestroy();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_item, container, false);
        mNewsItemScrollView = (ScrollView) view.findViewById(R.id.news_item_scrollview);
        mProgressBar = (ProgressBar) view.findViewById(R.id.news_feed_progressbar);
        mNewsItemScrollView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);

        mFragmentSliderOut = AnimationUtils.loadAnimation(getActivity(), R.anim.order_slider_out);
        mFragmentSliderOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                getParentFragment().getChildFragmentManager().beginTransaction().remove(NewsItemFragment.this).commit();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mNewsItemWebView = (WebView) view.findViewById(R.id.news_item_webView);
        mNewsItemWebView.getSettings().setJavaScriptEnabled(true);
        mNewsItemWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                    mNewsItemScrollView.setVisibility(View.VISIBLE);
                }
            }

        });
        mNewsItemWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //  Toast.makeText(getActivity(), "Page loaded", Toast.LENGTH_SHORT).show();
            }
        });
        mNewsItemWebView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });


        mNewsItemCommentEditText = (EditText) view.findViewById(R.id.news_item_comment_editText);
        mNewsItemAddCommentButton = (Button) view.findViewById(R.id.news_item_button_add_comment);
        mNewsItemAddCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideKeyboard(getActivity());
                String comment = mNewsItemCommentEditText.getText().toString();
                if (comment != null && comment.length() > 1 && comment.trim().length() > 0) {
                    isBroadcastAddComment = true;
                    isBroadcastUpdateFullArticle = false;
                    isBroadcastUpdateComments = false;
                    mNewsItemAddCommentButton.setEnabled(false);
                    if (mArticleFullEntity != null) {

                        addComment(CommonUtils.convertToUTF8(comment));
                    }
                } else {
                    showWarningDialog(getString(R.string.warning_short_comment));
                }
            }
        });
        mNewsItemCommentsListView = (ListView) view.findViewById(R.id.news_item_comments_listView);
        Button mNewsItemCloseButton = (Button) view.findViewById(R.id.news_item_close_button);
        mNewsItemCloseButton.setVisibility(View.INVISIBLE);
        mNewsItemCloseButton.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View view) {
                ((NewsFeedFragmentNew) getParentFragment()).updateLastChosenPosition();
                LocalBroadcastManager.getInstance(NewsItemFragment.this.getActivity()).unregisterReceiver(receiver);
                NewsItemFragment.this.getView().startAnimation(mFragmentSliderOut);
            }
        });


        AnimateButtonClose.animateButtonClose(mNewsItemCloseButton);
        // FB
        adapter = new SocialAuthAdapter(new ResponseListener());
        mShareButton = (Button) view.findViewById(R.id.fb_share_Button);
        mShareButton.setOnClickListener(new OnShareFBListener());

        if (isScreenOrienrtationChanged) {
            fillViewsWithData();
        }


        return view;
    }


    private void updateFullNewsCursor() {

        Cursor cursorNews = database.rawQuery(selectQueryFullNews, null);
        if (cursorNews != null && cursorNews.moveToFirst()) {
            mArticleFullEntity = fillFullNewsFromCursor(cursorNews);
            fillViewsWithData();
        }
        if (cursorNews != null) {
            cursorNews.close();
        }
    }

    private void fillViewsWithData() {
        LOGI(TAG, "fillViewsWithData");
        mNewsItemWebView.getSettings().setLoadWithOverviewMode(true);
        mNewsItemWebView.getSettings().setUseWideViewPort(true);

        mNewsItemWebView.loadDataWithBaseURL("", mArticleFullEntity.getFull_descr(), "text/html", "UTF-8", "");

        updateCommentsList();
    }

    private void updateCommentsList() {
        if (mArticleFullEntity.getComments() != null) {
            articleCommentsList = mArticleFullEntity.getComments();
            mNewsItemCommentsListView.setAdapter(new NewsCommentsAdapter(getActivity().getApplicationContext(), articleCommentsList));
            setListViewHeightBasedOnChildren(mNewsItemCommentsListView);
        }
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

    private ArticleFullEntity fillFullNewsFromCursor(Cursor cursorNews) {
        ArticleFullEntity articleFullEntity = new ArticleFullEntity();
        LOGI(TAG, "fillFullNewsFromCursor");
        if (cursorNews != null && cursorNews.moveToFirst()) {
            articleFullEntity.setId(cursorNews.getInt(cursorNews.getColumnIndex(FULL_NEWS_SERVER_ID)));
            articleFullEntity.setFull_descr(cursorNews.getString(cursorNews.getColumnIndex(FULL_NEWS_FULL_DESCR)));
            int news_id = cursorNews.getInt(cursorNews.getColumnIndex(FULL_NEWS_SERVER_ID));
            String selectQueryShortNewsComments = QUERY_COMMENTS_PATH + news_id;
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
                articleFullEntity.setComments(commentsList);
                if (cursorComments != null) {
                    cursorComments.close();
                }
            }
        }
        return articleFullEntity;
    }

    private List<ArticleShortCommentEntity> fillCommentsFromCursor(int newsId) {
        String selectQueryShortNewsComments = QUERY_COMMENTS_PATH + newsId;
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
        }
        if (cursorComments != null) {
            cursorComments.close();
        }
        return commentsList;
    }

    private void addComment(String message) {
        mLastShortComment = new ArticleShortCommentEntity();
        mLastShortComment.setAuthor_name(new AppPreferences(getActivity()).getUserName());
        mLastShortComment.setAuthor_id(new AppPreferences(getActivity()).getUserId());
        mLastShortComment.setAuthor_img("http://droidtune.com/12535/luchshie-android-igry-2013-po-versii-hardcoredroid.html");
        mLastShortComment.setContent(message);
        ((MainActivity) NewsItemFragment.this.getActivity()).startHttpIntent(getAddCommentQuery(mArticleFullEntity.getId(), message), HttpIntentService.ADD_COMMENT);
    }

    public static CommentAddQuery getAddCommentQuery(int article_id, String content) {
        CommentAddQuery query = new CommentAddQuery();
        query.params.article_id = article_id;
        query.params.content = content;
        return query;
    }

    public static CommentsArticleFullQuery getCommentsFullArticleQuery(int article_id, int limit, int offset) {
        CommentsArticleFullQuery query = new CommentsArticleFullQuery();
        query.params.article_id = article_id;
        query.params.limit = limit;
        query.params.offset = offset;
        return query;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mProgressBar.setVisibility(View.GONE);
        isScreenOrienrtationChanged = true;
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //
        } else {
            //
        }
    }


    private class OnShareFBListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            adapter.authorize(getActivity(), Provider.FACEBOOK);
            mShareButton.setEnabled(false);
            mShareButton.setBackgroundResource(R.drawable.fb_share_ok);
        }
    }

    private final class ResponseListener implements DialogListener {
        @Override
        public void onComplete(Bundle values) {
            adapter.updateStatus(mArticleFullEntity.getFull_descr(), new MessageListener(), false);
        }

        @Override
        public void onError(SocialAuthError error) {
            LOGE(TAG, "Authentication Error: " + error.getMessage());
            updateShareButton();
        }

        @Override
        public void onCancel() {
            LOGD(TAG, "Authentication Cancelled");
            updateShareButton();
        }

        @Override
        public void onBack() {
            LOGD(TAG, "Dialog Closed by pressing Back Key");
            updateShareButton();
        }
    }

    private void updateShareButton() {
        mShareButton.setEnabled(true);
        mShareButton.setBackgroundResource(R.drawable.facebook);
    }


    // To get status of message after authentication
    private final class MessageListener implements SocialAuthListener<Integer> {
        @Override
        public void onExecute(String provider, Integer t) {
            Integer status = t;
            if (status.intValue() == 200 || status.intValue() == 201 || status.intValue() == 204)
                Toast.makeText(getActivity(), "Message posted on " + provider, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getActivity(), "Message not posted on " + provider, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(SocialAuthError e) {
            LOGE(TAG, "SocialAuthError", e);
        }
    }

    private void showWarningDialog(String message) {
        WarningDialog dialog = WarningDialog.newInstance(getString(R.string.warning), message);
        dialog.show(getChildFragmentManager(), WarningDialog.WARNING_DIALOG);
    }


}
