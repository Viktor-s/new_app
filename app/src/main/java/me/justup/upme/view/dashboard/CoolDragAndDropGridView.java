package me.justup.upme.view.dashboard;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;

import static me.justup.upme.utils.LogUtils.LOGI;

public class CoolDragAndDropGridView extends SpanVariableGridView implements View.OnTouchListener {
    private static final String TAG = CoolDragAndDropGridView.class.getSimpleName();

    private static final int ITEM_HOVER_DELAY = 50;

    private int mDragPointX;
    private int mDragPointY;

    private int mDragOffsetX;
    private int mDragOffsetY;

    private int mDragPosition = AdapterView.INVALID_POSITION;
    private int mDropPosition = AdapterView.INVALID_POSITION;

    private int mCurrentPosition = AdapterView.INVALID_POSITION;

    private Runnable mDelayedOnDragRunnable = null;

    ScrollingStrategy mScrollingStrategy = null;
    WindowManager mWindowManager = null;
    WindowManager.LayoutParams mWindowParams = null;

    private ImageView mDragImageView = null;
    private boolean mDragAndDropStarted = false;

    private DragAndDropListener mDragAndDropListener = null;
    private OnTrackTouchEventsListener mOnTrackTouchEventsListener = null;

    public static interface OnTrackTouchEventsListener {
        void trackTouchEvents(final MotionEvent motionEvent);
    }

    public static interface DragAndDropListener {

        void onDrag(int x, int y, int rawX, int rawY);

        void onDragItem(int from);

        void onDraggingItem(int from, int to);

        void onDropItem(int from, int to);

        boolean isDragAndDropEnabled(int position);
    }

    public CoolDragAndDropGridView(Context context) {
        super(context);

        initialize();
    }

    public CoolDragAndDropGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initialize();
    }

    public CoolDragAndDropGridView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initialize();
    }

    private void initialize() {
        setOnTouchListener(this);
        setChildrenDrawingOrderEnabled(true);
    }

    public void startDragAndDrop() {
        mDragAndDropStarted = true;
    }

    public void setDragAndDropListener(DragAndDropListener dragAndDropListener) {
        mDragAndDropListener = dragAndDropListener;
    }

    private void destroyDragImageView() {
        if (mDragImageView != null) {
            mWindowManager.removeView(mDragImageView);

            BitmapDrawable bitmapDrawable = (BitmapDrawable) mDragImageView.getDrawable();

            if (bitmapDrawable != null) {
                final Bitmap bitmap = bitmapDrawable.getBitmap();
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }

            mDragImageView.setImageDrawable(null);
            mDragImageView = null;
        }
    }

    private ImageView createDragImageView(final View v, final int x, final int y) {
        v.destroyDrawingCache();
        v.setDrawingCacheEnabled(true);

        Bitmap bm = Bitmap.createBitmap(v.getDrawingCache());

        Matrix matrix = new Matrix();
        // matrix.postRotate(270);
        Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

        int orientation = this.getResources().getConfiguration().orientation;

        if(orientation==Configuration.ORIENTATION_PORTRAIT) {
            // Portrait
            mDragPointX = y - v.getTop();

            if(((v.getBottom() - v.getTop())/(v.getRight() - v.getLeft()))>=2){
                mDragPointY = (int) ((v.getRight() - v.getLeft())*1.5 - (x - v.getLeft()));
            }else{
                mDragPointY = (v.getRight() - v.getLeft()) - (x - v.getLeft());
            }
        }else {
            // Landscape
            mDragPointX = y - v.getTop();
            int height = v.getRight() - v.getLeft();

            if(((v.getBottom() - v.getTop())/(v.getRight() - v.getLeft()))>=3){
                mDragPointY = (int) ((height - (x - v.getLeft())) + height * 1.25);
            }else{
                mDragPointY = (v.getRight() - v.getLeft()) - (x - v.getLeft()) + height/3;
            }
        }

        LOGI(TAG, "createDragImageView : Start data : x " + x + ", y " + y + ", View button : " + v.getLeft() + ", View left : " + v.getTop() + ", View Top : " + v.getRight() + ", View Right : " + v.getBottom());

        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = Gravity.TOP | Gravity.LEFT;

        mWindowParams.x = x - mDragPointX + mDragOffsetX;
        mWindowParams.y = y + mDragOffsetY - mDragPointY;

        // Old
        // mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;

        mWindowParams.height = bm1.getHeight();
        mWindowParams.width = bm1.getHeight();

        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.alpha = 0.7f;
        mWindowParams.windowAnimations = 0;

        ImageView iv = new ImageView(getContext());
        // iv.setBackgroundColor(Color.parseColor("#ff555555"));
        iv.setRotation(270);
        iv.setImageBitmap(bm1);

        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);// "window"
        mWindowManager.addView(iv, mWindowParams);

        return iv;
    }

    private void startDrag(final int x, final int y) {
        View v = getChildAt(mDragPosition);

        destroyDragImageView();

        mDragImageView = createDragImageView(v, x, y);
        v.setVisibility(View.INVISIBLE);

        if (mDragAndDropListener != null) {
            mDragAndDropListener.onDragItem(mDragPosition);
        }
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (mCurrentPosition == -1)
            return i;
        else if (i == childCount - 1)
            return mCurrentPosition;
        else if (i >= mCurrentPosition)
            return i + 1;
        return i;
    }

    private void onDrop() {
        destroyDragImageView();
        removeCallbacks(mDelayedOnDragRunnable);

        View v = getChildAt(mDropPosition);
        v.setVisibility(View.VISIBLE);
        v.clearAnimation();

        if (mDragAndDropListener != null && mDropPosition != AdapterView.INVALID_POSITION) {
            mDragAndDropListener.onDropItem(mDragPosition, mDropPosition);
        }

        mDragPosition = mDropPosition = mCurrentPosition = AdapterView.INVALID_POSITION;
        mDragAndDropStarted = false;
    }

    public void setScrollingStrategy(ScrollingStrategy scrollingStrategy) {
        mScrollingStrategy = scrollingStrategy;
    }

    private void onDrag(final int x, final int y, final int rawX, final int rawY) {
        // Set coordinates to call activity
        if(mDragAndDropListener != null){
            mDragAndDropListener.onDrag(x, y, rawX, rawY);
        }

        if (mScrollingStrategy != null && mScrollingStrategy.performScrolling(x, y, this)) {
            removeCallbacks(mDelayedOnDragRunnable);

            return;
        }

        final int tempDropPosition = pointToPosition(mCurrentPosition, x, y);

        if (mDragAndDropListener != null && mDropPosition != tempDropPosition && tempDropPosition != AdapterView.INVALID_POSITION) {
            removeCallbacks(mDelayedOnDragRunnable);

            if (mDragAndDropListener.isDragAndDropEnabled(tempDropPosition)) {
                mDropPosition = tempDropPosition;
                mDelayedOnDragRunnable = new Runnable() {

                    @Override
                    public void run() {
                        mDragAndDropListener.onDraggingItem(mCurrentPosition, tempDropPosition);
                        // Start move element
                        performDragAndDropSwapping(mCurrentPosition, tempDropPosition);

                        final int nextDropPosition = pointToPosition(tempDropPosition, x, y);

                        if (nextDropPosition == AdapterView.INVALID_POSITION) {
                            mCurrentPosition = mDropPosition = tempDropPosition;
                        }
                    }
                };

                postDelayed(mDelayedOnDragRunnable, ITEM_HOVER_DELAY);
            } else {
                mDropPosition = mDragPosition;
            }
        }

        if (mDragImageView != null) {
            LOGI(TAG, "onDrag : mDragOffsetX : " + mDragOffsetX + ", mDragOffsetY : " + mDragOffsetY + ", X : " + x + ", Y : " + y);

            mWindowParams.x = x - mDragPointX + mDragOffsetX;
            mWindowParams.y = y + mDragOffsetY - mDragPointY;

            mWindowManager.updateViewLayout(mDragImageView, mWindowParams);
        }
    }

    public void setOnTrackTouchEventListener(OnTrackTouchEventsListener onTrackTouchEventsListener) {
        mOnTrackTouchEventsListener = onTrackTouchEventsListener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mOnTrackTouchEventsListener != null) {
            mOnTrackTouchEventsListener.trackTouchEvents(event);
        }

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (mDragAndDropListener != null && mDragAndDropStarted) {
                    mDragAndDropStarted = false;

                    getParent().requestDisallowInterceptTouchEvent(true);

                    return launchDragAndDrop(event);
                }

                break;

            default:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                mDragAndDropStarted = false;

                getParent().requestDisallowInterceptTouchEvent(false);

                break;
        }

        return super.onInterceptTouchEvent(event);
    }

    private boolean launchDragAndDrop(final MotionEvent event) {

        final int x = (int) event.getX();
        final int y = (int) event.getY();

        mCurrentPosition = mDragPosition = mDropPosition = pointToPosition(mDragPosition, x, y);

        if (mDragPosition != AdapterView.INVALID_POSITION && mDragAndDropListener.isDragAndDropEnabled(mDragPosition)) {

            mDragOffsetX = (int) (event.getRawX() - x);
            mDragOffsetY = (int) (event.getRawY() - y);

            startDrag(x, y);

            return true;
        }

        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        if (mDragPosition != AdapterView.INVALID_POSITION && mDragImageView != null) {

            final int x = (int) event.getX();
            final int y = (int) event.getY();

            switch (event.getAction()) {

                case MotionEvent.ACTION_MOVE:

                    mDragOffsetX = (int) (event.getRawX() - x);
                    mDragOffsetY = (int) (event.getRawY() - y);

                    onDrag(x, y, (int)event.getRawX(), (int)event.getRawY());

                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:

                    onDrop();

                    resetLongClickTransition();

                    getParent().requestDisallowInterceptTouchEvent(false);

                    return false;

                default:

            }

            return true;
        }

        return false;
    }
}
