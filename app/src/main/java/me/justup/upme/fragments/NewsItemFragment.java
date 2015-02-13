package me.justup.upme.fragments;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import me.justup.upme.MainActivity;
import me.justup.upme.R;
import me.justup.upme.adapter.NewsCommentsAdapter;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.db.DBHelper;
import me.justup.upme.entity.ArticleFullEntity;
import me.justup.upme.entity.ArticleShortCommentEntity;
import me.justup.upme.entity.ArticleShortEntity;
import me.justup.upme.entity.CommentAddQuery;
import me.justup.upme.entity.CommentsArticleFullQuery;
import me.justup.upme.http.HttpIntentService;
import me.justup.upme.utils.AnimateButtonClose;
import me.justup.upme.utils.AppContext;

import static me.justup.upme.db.DBHelper.FULL_NEWS_FULL_DESCR;
import static me.justup.upme.db.DBHelper.FULL_NEWS_SERVER_ID;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_ID;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_IMAGE;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_NAME;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_CONTENT;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_SERVER_ID;

public class NewsItemFragment extends Fragment {
    private static final String ARG_NEWS_FEED_ENTITY = "news_feed_entity";
    private static final String QUERY_COMMENTS_PATH = "SELECT * FROM short_news_comments_table WHERE article_id=";
    private static final String QUERY_FULL_ARTICLE_PATH = "SELECT * FROM full_news_table WHERE server_id=";
    private static final int LIST_DIVIDER_HEIGHT = 24;
    public static ArticleShortEntity mNewsFeedEntity;
    private WebView mNewsItemWebView;
    private EditText mNewsItemCommentEditText;
    private Button mNewsItemAddCommentButton;
    private ListView mNewsItemCommentsListView;
    private Button mNewsItemCloseButton;
    private DBHelper mDBHelper;
    private DBAdapter mDBAdapter;
    private List<ArticleShortCommentEntity> articleCommentsList;
    private ArticleFullEntity mArticleFullEntity;
    private String selectQueryFullNews;

    private boolean isBroadcastUpdateFullArticle = true;
    private boolean isBroadcastAddComment = false;
    private boolean isBroadcastUpdateComments = false;


    public static NewsItemFragment newInstance(ArticleShortEntity articleShortEntity) {
        NewsItemFragment fragment = new NewsItemFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NEWS_FEED_ENTITY, articleShortEntity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mNewsFeedEntity = (ArticleShortEntity) bundle.getSerializable(ARG_NEWS_FEED_ENTITY);
        }
        mDBHelper = new DBHelper(AppContext.getAppContext());
        mDBAdapter = new DBAdapter(AppContext.getAppContext());
        mDBAdapter.open();
    }

    @Override
    public void onPause() {
        super.onPause();
        mDBAdapter.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_item, container, false);
        mNewsItemWebView = (WebView) view.findViewById(R.id.news_item_webView);
        mNewsItemWebView.getSettings().setJavaScriptEnabled(true);
        mNewsItemWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        mNewsItemCommentEditText = (EditText) view.findViewById(R.id.news_item_comment_editText);
        mNewsItemAddCommentButton = (Button) view.findViewById(R.id.news_item_button_add_comment);
        mNewsItemAddCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNewsItemCommentEditText.length() > 1) {
                    isBroadcastAddComment = true;
                    isBroadcastUpdateFullArticle = false;
                    isBroadcastUpdateComments = false;
                    addComment(mNewsItemCommentEditText.getText().toString());
                }
            }
        });

        mNewsItemCommentsListView = (ListView) view.findViewById(R.id.news_item_comments_listView);
        mNewsItemCloseButton = (Button) view.findViewById(R.id.news_item_close_button);
        mNewsItemCloseButton.setVisibility(View.INVISIBLE);
        mNewsItemCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragment().getChildFragmentManager().beginTransaction().remove(NewsItemFragment.this).commit();

            }
        });

        selectQueryFullNews = QUERY_FULL_ARTICLE_PATH + mNewsFeedEntity.getId();
        Cursor cursorNews = mDBHelper.getWritableDatabase().rawQuery(selectQueryFullNews, null);
        if (cursorNews != null && cursorNews.moveToFirst())

        {
            mArticleFullEntity = fillFullNewsFromCursor(cursorNews);
            fillViewsWithData();
        }

        AnimateButtonClose.animateButtonClose(mNewsItemCloseButton);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isBroadcastUpdateFullArticle) {
                    Cursor cursorNews = mDBHelper.getWritableDatabase().rawQuery(selectQueryFullNews, null);
                    mArticleFullEntity = fillFullNewsFromCursor(cursorNews);
                    fillViewsWithData();
                    //LOGD("broadcast", "isBroadcastUpdateFullArticle");
                } else if (isBroadcastAddComment) {
                    isBroadcastUpdateFullArticle = false;
                    isBroadcastAddComment = false;
                    isBroadcastUpdateComments = true;
                    ((MainActivity) NewsItemFragment.this.getActivity()).startHttpIntent(getCommentsFullArticleQuery(mArticleFullEntity.getId(), 100, 0), HttpIntentService.GET_COMMENTS_FULL_ARTICLE);
                } else if (isBroadcastUpdateComments) {
                    isBroadcastUpdateFullArticle = true;
                    isBroadcastAddComment = false;
                    isBroadcastUpdateComments = false;
                    articleCommentsList = fillCommentsFromCursor(mArticleFullEntity.getId());
                    NewsCommentsAdapter newsCommentsAdapter = new NewsCommentsAdapter(AppContext.getAppContext(), articleCommentsList);
                    mNewsItemCommentsListView.setAdapter(newsCommentsAdapter);
                    newsCommentsAdapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(mNewsItemCommentsListView);
                    mNewsItemCommentEditText.setText("");
                }

            }
        };
        LocalBroadcastManager.getInstance(NewsItemFragment.this.getActivity()).registerReceiver(receiver, new IntentFilter(DBAdapter.NEWS_ITEM_SQL_BROADCAST_INTENT)
        );
        return view;
    }

    private void fillViewsWithData() {
        mNewsItemWebView.loadDataWithBaseURL("", convertToHtml(mArticleFullEntity.getFull_descr()), "text/html", "UTF-8", "");
        updateCommentsList();
    }

    private void updateCommentsList() {
        if (mArticleFullEntity.getComments() != null) {
            articleCommentsList = mArticleFullEntity.getComments();
            mNewsItemCommentsListView.setAdapter(new NewsCommentsAdapter(AppContext.getAppContext(), articleCommentsList));
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
        cursorNews.moveToFirst();
        ArticleFullEntity articleFullEntity = new ArticleFullEntity();
        articleFullEntity.setId(cursorNews.getInt(cursorNews.getColumnIndex(FULL_NEWS_SERVER_ID)));
        articleFullEntity.setFull_descr(cursorNews.getString(cursorNews.getColumnIndex(FULL_NEWS_FULL_DESCR)));
        int news_id = cursorNews.getInt(cursorNews.getColumnIndex(FULL_NEWS_SERVER_ID));
        String selectQueryShortNewsComments = QUERY_COMMENTS_PATH + news_id;
        Cursor cursorComments = mDBHelper.getWritableDatabase().rawQuery(selectQueryShortNewsComments, null);
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
        }
        return articleFullEntity;
    }

    private List<ArticleShortCommentEntity> fillCommentsFromCursor(int newsId) {
        String selectQueryShortNewsComments = QUERY_COMMENTS_PATH + newsId;
        Cursor cursorComments = mDBHelper.getWritableDatabase().rawQuery(selectQueryShortNewsComments, null);
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
        return commentsList;
    }

    private void addComment(String message) {
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

    private String convertToHtml(String fullDescr) {
        return "<html><body><h3>Articles title</h3><p>" + fullDescr + "</p></body></html>";
    }
}
