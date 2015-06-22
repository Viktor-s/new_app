package me.justup.upme.db_upme.transfers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.justup.upme.api_rpc.response_object.NewsObject;
import me.justup.upme.api_rpc.response_object.sub_object.Comment;
import me.justup.upme.db_upme.MetaData;
import me.justup.upme.db_upme.SyncAdapterMetaData;
import me.justup.upme.entity.ArticleFullResponse;
import me.justup.upme.entity.ArticlesGetShortDescriptionResponse;
import me.justup.upme.entity.CommentsArticleFullResponse;

import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_ARTICLE_ID;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_ID;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_IMAGE;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_AUTHOR_NAME;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_CONTENT;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_POSTED_AT;
import static me.justup.upme.db.DBHelper.SHORT_NEWS_COMMENTS_SERVER_ID;
import static me.justup.upme.utils.LogUtils.LOGD;

public class TransferActionNewsComments implements SyncAdapterMetaData {
    private static final String TAG = TransferActionNewsComments.class.getSimpleName();

    private Uri uriNewsComments = null;
    private Uri uriOneNewsComment = null;
    private Uri uriNewsListComments = null;

    public TransferActionNewsComments(CharSequence name) {
        uriNewsComments = Uri.parse("content://" + MetaData.AUTHORITY_SHORT_NEWS_COMMENTS + "/" + ShortNewsCommentsConstance.URI_PATH2_SHORT_NEWS_COMMENTS.replace("#", name));
        uriOneNewsComment = Uri.parse("content://" + MetaData.AUTHORITY_SHORT_NEWS_COMMENTS + "/" + ShortNewsCommentsConstance.URI_PATH2_SHORT_NEWS_COMMENTS.replace("#", name) + "/" + 2);
        uriNewsListComments = Uri.parse("content://" + MetaData.AUTHORITY_SHORT_NEWS_COMMENTS + "/" + ShortNewsCommentsConstance.URI_PATH2_SHORT_NEWS_COMMENTS.replace("#", name) + "/" + 1);
    }

    public void insertNewsComments(Activity activity, NewsObject newsObject, int articleId) {
        ContentValues[] contentValues = newsToContentValues(newsObject, articleId);

        if(contentValues!=null) {
            activity.getContentResolver().bulkInsert(uriOneNewsComment, contentValues);
        }
    }

    public void insertNewsComments(Activity activity, NewsObject newsObject) {
        ContentValues[] contentValues = newsToContentValues(newsObject, null);

        if(contentValues!=null) {
            activity.getContentResolver().bulkInsert(uriOneNewsComment, newsToContentValues(newsObject, null));
        }
    }

    public void insertNewsCommentsOld(Context context, ArticlesGetShortDescriptionResponse.ResultList resultList, int index) {
        context.getContentResolver().insert(uriOneNewsComment, newsToContentValuesOld(resultList, index));
    }

    public void insertNewsCommentsOld(Context context, ArticleFullResponse.ResultFull resultFull, int index) {
        context.getContentResolver().insert(uriOneNewsComment, newsToContentValuesOldFull(resultFull, index));
    }

    public void insertNewsCommentsOldFullWithArticleId(Context context, CommentsArticleFullResponse.Comment comment, int article_id) {
        context.getContentResolver().insert(uriOneNewsComment, newsToContentValuesOldFull(comment, article_id));
    }

    public void insertNewsCommentsList(Activity activity, ArrayList<NewsObject> newsObjectArrayList) {
        ContentValues[] contentValues = newsListToContentValues(newsObjectArrayList);

        if(contentValues!=null) {
            activity.getContentResolver().bulkInsert(uriNewsListComments, newsListToContentValues(newsObjectArrayList));
        }
    }

    public Cursor getCursorOfNewsCommentsByArticleId(Context context, int articleId){
        String sql = "SELECT * FROM " + ShortNewsCommentsConstance.TABLE_SHORT_NEWS_COMMENTS + " WHERE article_id = " + articleId;

        return context.getContentResolver().query(uriNewsComments, null, sql, null, null);
    }

    /**
     * Convert NewsObject Comments to ContentValues
     */
    public ContentValues[] newsToContentValues(@NonNull NewsObject newsObject, Integer articleId) {
        // Get comments
        List<Comment> commentList = newsObject.getComments();

        if(commentList!=null && !commentList.isEmpty()){
            // Init List ContentValues
            ArrayList<ContentValues> contentValuesArrayList = new ArrayList<ContentValues>(commentList.size());

            for (int i = 0; i < commentList.size(); i++) {
                // Get One Comment
                Comment comment = commentList.get(i);

                // Init ContentValues
                ContentValues cv = new ContentValues();

                cv.put(SHORT_NEWS_COMMENTS_SERVER_ID, comment.getId());
                if(articleId!=null){
                    cv.put(SHORT_NEWS_COMMENTS_ARTICLE_ID, articleId);
                }else{
                    cv.put(SHORT_NEWS_COMMENTS_ARTICLE_ID, newsObject.getId());
                }
                cv.put(SHORT_NEWS_COMMENTS_CONTENT, comment.getContent());
                cv.put(SHORT_NEWS_COMMENTS_AUTHOR_ID, comment.getAuthor_id());
                cv.put(SHORT_NEWS_COMMENTS_AUTHOR_NAME, comment.getAuthor().getName());
                cv.put(SHORT_NEWS_COMMENTS_AUTHOR_IMAGE, comment.getAuthor().getImg());
                cv.put(SHORT_NEWS_COMMENTS_POSTED_AT, comment.getPosted_at());

                // Add to List
                contentValuesArrayList.add(cv);
            }


            ContentValues[] contentValues = contentValuesArrayList.toArray(new ContentValues[contentValuesArrayList.size()]);
            LOGD(TAG, "ContentValues : " + Arrays.toString(contentValues));

            return contentValues;
        }

        return null;
    }

    /**
     * Convert ArticlesGetShortDescriptionResponse.ResultList to ContentValues
     */
    public ContentValues newsToContentValuesOld(@NonNull ArticlesGetShortDescriptionResponse.ResultList resultList, int index) {
        LOGD(TAG, "ResultList : " + resultList.toString());

        ContentValues cv = new ContentValues();

        cv.put(SHORT_NEWS_COMMENTS_SERVER_ID, resultList.comments.get(index).id);
        cv.put(SHORT_NEWS_COMMENTS_ARTICLE_ID, resultList.id);
        cv.put(SHORT_NEWS_COMMENTS_CONTENT, resultList.comments.get(index).content);
        cv.put(SHORT_NEWS_COMMENTS_AUTHOR_ID, resultList.comments.get(index).author_id);
        cv.put(SHORT_NEWS_COMMENTS_AUTHOR_NAME, resultList.comments.get(index).author.name);
        cv.put(SHORT_NEWS_COMMENTS_AUTHOR_IMAGE, resultList.comments.get(index).author.img);
        cv.put(SHORT_NEWS_COMMENTS_POSTED_AT, resultList.comments.get(index).posted_at);

        LOGD(TAG, "Content Value : " + cv.toString());

        return cv;
    }

    /**
     * Convert CommentsArticleFullResponse.Comment to ContentValues
     */
    public ContentValues newsToContentValuesOldFull(@NonNull CommentsArticleFullResponse.Comment comment, int articleId) {
        LOGD(TAG, "ResultList : " + comment.toString());

        ContentValues cv = new ContentValues();

        cv.put(SHORT_NEWS_COMMENTS_SERVER_ID, comment.id);
        cv.put(SHORT_NEWS_COMMENTS_ARTICLE_ID, articleId);
        cv.put(SHORT_NEWS_COMMENTS_CONTENT, comment.content);
        cv.put(SHORT_NEWS_COMMENTS_AUTHOR_ID, comment.author_id);
        cv.put(SHORT_NEWS_COMMENTS_AUTHOR_NAME, comment.author.name);
        cv.put(SHORT_NEWS_COMMENTS_AUTHOR_IMAGE, comment.author.img);
        cv.put(SHORT_NEWS_COMMENTS_POSTED_AT, comment.posted_at);

        LOGD(TAG, "Content Value : " + cv.toString());

        return cv;
    }

    /**
     * Convert ArticleFullResponse.ResultFull to ContentValues
     */
    public ContentValues newsToContentValuesOldFull(@NonNull ArticleFullResponse.ResultFull resultFull, int index) {
        LOGD(TAG, "ResultList : " + resultFull.toString());

        ContentValues cv = new ContentValues();

        cv.put(SHORT_NEWS_COMMENTS_SERVER_ID, resultFull.comments.get(index).id);
        cv.put(SHORT_NEWS_COMMENTS_ARTICLE_ID, resultFull.id);
        cv.put(SHORT_NEWS_COMMENTS_CONTENT, resultFull.comments.get(index).content);
        cv.put(SHORT_NEWS_COMMENTS_AUTHOR_ID, resultFull.comments.get(index).author_id);
        cv.put(SHORT_NEWS_COMMENTS_AUTHOR_NAME, resultFull.comments.get(index).author.name);
        cv.put(SHORT_NEWS_COMMENTS_AUTHOR_IMAGE, resultFull.comments.get(index).author.img);
        cv.put(SHORT_NEWS_COMMENTS_POSTED_AT, resultFull.comments.get(index).posted_at);

        LOGD(TAG, "Content Value : " + cv.toString());

        return cv;
    }

    /**
     * Convert NewsObject Comments List to ContentValues
     */
    public ContentValues[] newsListToContentValues(@NonNull ArrayList<NewsObject> newsObjectArrayList) {
        // Init List ContentValues
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<ContentValues>();

        for (int i = 0; i < newsObjectArrayList.size(); i++) {
            // Get One Object
            NewsObject newsObject = newsObjectArrayList.get(i);

            // Get comments
            List<Comment> commentList = newsObject.getComments();

            if(commentList!=null && !commentList.isEmpty()){
                for (int c = 0; c < commentList.size(); c++) {
                    // Get One Comment
                    Comment comment = commentList.get(c);

                    // Init ContentValues
                    ContentValues cv = new ContentValues();

                    cv.put(SHORT_NEWS_COMMENTS_SERVER_ID, comment.getId());
                    cv.put(SHORT_NEWS_COMMENTS_ARTICLE_ID, newsObject.getId());
                    cv.put(SHORT_NEWS_COMMENTS_CONTENT, comment.getContent());
                    cv.put(SHORT_NEWS_COMMENTS_AUTHOR_ID, comment.getAuthor_id());
                    cv.put(SHORT_NEWS_COMMENTS_AUTHOR_NAME, comment.getAuthor().getName());
                    cv.put(SHORT_NEWS_COMMENTS_AUTHOR_IMAGE, comment.getAuthor().getImg());
                    cv.put(SHORT_NEWS_COMMENTS_POSTED_AT, comment.getPosted_at());

                    // Add to List
                    contentValuesArrayList.add(cv);
                }
            }
        }

        ContentValues[] contentValues = contentValuesArrayList.toArray(new ContentValues[contentValuesArrayList.size()]);
        LOGD(TAG, "ContentValues : " + Arrays.toString(contentValues));

        return contentValues;
    }
}
