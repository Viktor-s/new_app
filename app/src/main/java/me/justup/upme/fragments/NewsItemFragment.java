package me.justup.upme.fragments;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import me.justup.upme.JustUpApplication;
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
import me.justup.upme.utils.BackAwareEditText;
import me.justup.upme.utils.CommonUtils;
import me.justup.upme.view.cwvm.CWView;

import static me.justup.upme.db.DBHelper.FULL_NEWS_FULL_DESCR;
import static me.justup.upme.db.DBHelper.FULL_NEWS_SERVER_ID;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_ID;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_IMAGE;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_NAME;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_CONTENT;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_POSTED_AT;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_SERVER_ID;
import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class NewsItemFragment extends Fragment {
    private static final String TAG = makeLogTag(NewsItemFragment.class);

    private static final String ARG_NEWS_FEED_ENTITY = "news_feed_entity";

    private static final int LIST_DIVIDER_HEIGHT = 24;
    private static final int TIME_ANIM = 250;

    public static int mNewsFeedEntityId;
    private CWView mCWView = null;
    private BackAwareEditText mNewsItemCommentEditText = null;
    private Button mNewsItemAddCommentButton = null;
    private ListView mNewsItemCommentsListView = null;
    private List<ArticleShortCommentEntity> articleCommentsList = null;
    private ArticleFullEntity mArticleFullEntity = null;
    private int mServerId;
    private boolean isBroadcastUpdateFullArticle = true;
    private boolean isBroadcastAddComment = false;
    private boolean isBroadcastUpdateComments = false;
    private SocialAuthAdapter mSocialAuthdapter = null;
    private Button mShareButton = null;
    public  BroadcastReceiver mReceiver = null;
    private ArticleShortCommentEntity mLastShortComment = null;
    private ScrollView mNewsItemScrollView = null;
    private ProgressBar mProgressBar = null;
    private boolean isScreenOrienrtationChanged = false;
    private boolean isProgressBarShown = true;

    private View mContentView = null;
    private Button mNewsItemCloseButton = null;

    // Instance
    public static NewsItemFragment newInstance(int shortNewsId) {
        NewsItemFragment fragment = new NewsItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_NEWS_FEED_ENTITY, shortNewsId);
        fragment.setArguments(args);

        LOGI(TAG, "News id : " + shortNewsId + ", Fragment : " + fragment);
        return fragment;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onDetach() {
        super.onDetach();
        LOGI(TAG, "Fragment Detach");

        ((NewsFeedFragmentNew) getParentFragment()).updateLastChosenPosition();
        LocalBroadcastManager.getInstance(NewsItemFragment.this.getActivity()).unregisterReceiver(mReceiver);

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LOGI(TAG, "RegisterRecNewsItem");

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isBroadcastUpdateFullArticle) {
                    LOGI(TAG, "onReceive isBroadcastUpdateFullArticle");

                    Cursor cursorNews = JustUpApplication.getApplication().getTransferActionFullNews().getCursorOfFullNewsByServerId(getActivity().getApplicationContext(), mServerId);
                    mArticleFullEntity = fillFullNewsFromCursor(cursorNews);
                    fillViewsWithData();
                    //updateFullNewsCursor();

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
            LocalBroadcastManager.getInstance(NewsItemFragment.this.getActivity()).registerReceiver(mReceiver, new IntentFilter(DBAdapter.NEWS_ITEM_SQL_BROADCAST_INTENT)
            );

    }

    @Override
    public void onPause() {
        super.onPause();
        LOGI(TAG, "UnregisterRecNewsItem");
        LocalBroadcastManager.getInstance(NewsItemFragment.this.getActivity()).unregisterReceiver(mReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = super.onCreateView(inflater, container, savedInstanceState);

        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_news_item, container, false);
        }

        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mNewsFeedEntityId = bundle.getInt(ARG_NEWS_FEED_ENTITY);
        }

        // Init UI
        if (getActivity() != null) {
            initUI(mNewsFeedEntityId);
        }
    }

    public void initUI(int newsId){
        this.mServerId = newsId;

        mNewsItemScrollView = (ScrollView) mContentView.findViewById(R.id.news_item_scrollview);
        mProgressBar = (ProgressBar) mContentView.findViewById(R.id.news_feed_progressbar);

        if (isProgressBarShown) {
            mNewsItemScrollView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mNewsItemScrollView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }

        mCWView = (CWView) mContentView.findViewById(R.id.news_web_view);
        mCWView.init(getActivity().getApplicationContext());
        mCWView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                    isProgressBarShown = false;

                    mNewsItemScrollView.setVisibility(View.VISIBLE);
                    mNewsItemScrollView.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_fast));
                }
            }
        });

        mNewsItemCommentEditText = (BackAwareEditText) mContentView.findViewById(R.id.news_item_comment_editText);
        mNewsItemCommentEditText.setBackPressedListener(new BackAwareEditText.BackPressedListener() {
            @Override
            public void onImeBack(BackAwareEditText editText) {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).hideNavBar();
                }
            }
        });

        mNewsItemAddCommentButton = (Button) mContentView.findViewById(R.id.news_item_button_add_comment);
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

        mNewsItemCommentsListView = (ListView) mContentView.findViewById(R.id.news_item_comments_listView);
        mNewsItemCloseButton = (Button) mContentView.findViewById(R.id.news_item_close_button);
        mNewsItemCloseButton.setVisibility(View.INVISIBLE);
        mNewsItemCloseButton.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View view) {
                // ((NewsFeedFragmentNew) getParentFragment()).updateLastChosenPosition();
                LocalBroadcastManager.getInstance(NewsItemFragment.this.getActivity()).unregisterReceiver(mReceiver);
                NewsItemFragment.this.getView().startAnimation(((NewsFeedFragmentNew) getParentFragment()).getFragmentCloseAnimation());
            }
        });

        AnimateButtonClose.animateButtonClose(mNewsItemCloseButton, TIME_ANIM);

        // FB
        mSocialAuthdapter = new SocialAuthAdapter(new ResponseListener());
        mShareButton = (Button) mContentView.findViewById(R.id.fb_share_Button);
        mShareButton.setOnClickListener(new OnShareFBListener());

        if (!isProgressBarShown) {
            fillViewsWithData();
        }

    }

    private void fillViewsWithData() {
        // Set Data to Web View
        boolean onlyContent = false;
        int idNews = mArticleFullEntity.getId();
        LOGI(TAG, "Id news : " + idNews + ", Is Demo-mode : " + JustUpApplication.getApplication().getAppPreferences().isDemoMode() + ", HTML Data : " + mArticleFullEntity.getFull_descr());
        if(JustUpApplication.getApplication().getAppPreferences().isDemoMode()){
            switch (idNews){
                case 1:
                    if(onlyContent) {
                        InputStream is1 = null;
                        try {
                            is1 = getActivity().getAssets().open("news_item/article1.html");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        int size1 = 0;
                        try {
                            size1 = is1.available();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        byte[] buffer1 = new byte[size1];
                        try {
                            is1.read(buffer1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            is1.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String str1 = new String(buffer1);
                        mCWView.setText(str1);
                    }else{
                        mCWView.loadUrl("file:///android_asset/news_item/article1.html");
                    }

                    break;
                case 2:
                    if(onlyContent) {
                        InputStream is2 = null;
                        try {
                            is2 = getActivity().getAssets().open("news_item/article4.html");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        int size2 = 0;
                        try {
                            size2 = is2.available();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        byte[] buffer2 = new byte[size2];
                        try {
                            is2.read(buffer2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            is2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String str2 = new String(buffer2);
                        mCWView.setText(str2);
                    }else{
                        mCWView.loadUrl("file:///android_asset/news_item/article4.html");
                    }

                    break;
                case 3:
                    if(onlyContent) {
                        InputStream is3 = null;
                        try {
                            is3 = getActivity().getAssets().open("news_item/article2.html");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        int size3 = 0;
                        try {
                            size3 = is3.available();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        byte[] buffer3 = new byte[size3];
                        try {
                            is3.read(buffer3);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            is3.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String str3 = new String(buffer3);
                        mCWView.setText(str3);
                    }else{
                        mCWView.loadUrl("file:///android_asset/news_item/article2.html");
                    }

                    break;
                case 4:
                    if(onlyContent) {
                        InputStream is4 = null;
                        try {
                            is4 = getActivity().getAssets().open("news_item/article3.html");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        int size4 = 0;
                        try {
                            size4 = is4.available();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        byte[] buffer4 = new byte[size4];
                        try {
                            is4.read(buffer4);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            is4.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String str4 = new String(buffer4);
                        mCWView.setText(str4);
                    }else{
                        mCWView.loadUrl("file:///android_asset/news_item/article3.html");
                    }

                    break;
                case 5:
                    mCWView.setText(mArticleFullEntity.getFull_descr());
                    break;
            }

        }else{
            mCWView.setText(mArticleFullEntity.getFull_descr());
        }

        updateCommentsList();
    }

    public String ReadFromfile(String fileName, Context context) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets()
                    .open(fileName, Context.MODE_WORLD_READABLE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }

        return returnString.toString();
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
                    articleShortCommentEntity.setPosted_at(cursorComments.getString(cursorComments.getColumnIndex(SHORT_NEWS_COMMENTS_POSTED_AT)));
                    commentsList.add(articleShortCommentEntity);
                }

                articleFullEntity.setComments(commentsList);
            }
        }

        return articleFullEntity;
    }

/*    private List<ArticleShortCommentEntity> fillCommentsFromCursor(int newsId) {
        String selectQueryShortNewsComments = QUERY_COMMENTS_PATH + newsId;
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
        }
        if (cursorComments != null) {
            cursorComments.close();
        }
        return commentsList;
    }*/

    private void addComment(String message) {
        mLastShortComment = new ArticleShortCommentEntity();
        mLastShortComment.setAuthor_name(JustUpApplication.getApplication().getAppPreferences().getUserName());
        mLastShortComment.setAuthor_id(JustUpApplication.getApplication().getAppPreferences().getUserId());
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

    private class OnShareFBListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mSocialAuthdapter.authorize(getActivity(), Provider.FACEBOOK);
            mShareButton.setEnabled(false);
            mShareButton.setBackgroundResource(R.drawable.fb_share_ok);
        }
    }

    private final class ResponseListener implements DialogListener {
        @Override
        public void onComplete(Bundle values) {
            mSocialAuthdapter.updateStatus(mArticleFullEntity.getFull_descr(), new MessageListener(), false);
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


    private final class MessageListener implements SocialAuthListener<Integer> {
        @Override
        public void onExecute(String provider, Integer t) {
            if (t.intValue() == 200 || t.intValue() == 201 || t.intValue() == 204)
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
