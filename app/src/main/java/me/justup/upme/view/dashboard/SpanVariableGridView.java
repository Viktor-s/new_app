package me.justup.upme.view.dashboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.TransitionDrawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import me.justup.upme.R;

public class SpanVariableGridView extends AdapterView<BaseAdapter> {
    private static final String TAG = SpanVariableGridView.class.getSimpleName();
    private static final boolean USE_OLD = false;

    public static interface CalculateChildrenPosition {
        void onCalculatePosition(final View view, final int position, final int row, final int column);
    }

    private static final int NOT_DEFINED_VALUE = -1;
    private static final int INVALID_POSITION = NOT_DEFINED_VALUE;

    private static final int TOUCH_STATE_RESTING = 0;
    private static final int TOUCH_STATE_CLICK = 1;
    private static final int TOUCH_STATE_LONG_CLICK = 2;

    private int mTouchStartX;
    private int mTouchStartY;

    private int mTouchStartItemPosition;

    private Runnable mLongPressRunnable = null;

    public int mColCount = 2;

    private boolean mAnimTile = false;

    private int mItemMargin = 0;
    private int mControlHeight = 0;

    private Rect mRect = new Rect();

    private boolean mPopulating = false;
    private int mTouchState = TOUCH_STATE_RESTING;

    private BaseAdapter mAdapter = null;
    private TransitionDrawable mItemTransitionDrawable = null;
    private List<CalculateChildrenPosition> mCalculateChildrenPositionList = new LinkedList<CalculateChildrenPosition>();

    private HashMap<String, Integer> mapViewTopPadding = new HashMap<String, Integer>();
    private HashMap<String, String> mapViewType = new HashMap<String, String>();
    private int maxLeft = 0;

    private final DataSetObserver mObserver = new DataSetObserver() {

        @Override
        public void onChanged() {
            mPopulating = false;
            removeAllViewsInLayout();
            requestLayout();
        }

        @Override
        public void onInvalidated() { }
    };

    public static class LayoutParams extends AdapterView.LayoutParams {
        private static final int[] LAYOUT_ATTRS = new int[] { android.R.attr.layout_span };

        public static final int SPAN_INDEX = 0;
        public static final int ALL_COLUMNS = NOT_DEFINED_VALUE;

        public int hSpan = 1;
        public int wSpan = 2;

        public int position = NOT_DEFINED_VALUE;
        public int row = NOT_DEFINED_VALUE;
        public int column = NOT_DEFINED_VALUE;

        public LayoutParams(int height) {
            super(MATCH_PARENT, height);

            if (this.height == MATCH_PARENT) {
                this.height = WRAP_CONTENT;
            }
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            setupWidthAndHeight();

            TypedArray a = c.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
            hSpan = a.getInteger(SPAN_INDEX, 1);
            a.recycle();
        }

        public LayoutParams(AdapterView.LayoutParams other) {
            super(other);

            setupWidthAndHeight();
        }

        private void setupWidthAndHeight() {

            if (this.width != MATCH_PARENT) {
                this.width = MATCH_PARENT;
            }

            if (this.height == MATCH_PARENT) {
                this.height = WRAP_CONTENT;
            }
        }
    }

    public SpanVariableGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initialize(attrs);
    }

    public SpanVariableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initialize(attrs);
    }

    public SpanVariableGridView(Context context) {
        super(context);
    }

    private void initialize(final AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SpanVariableGridView);

            try {
                mColCount = a.getInteger(R.styleable.SpanVariableGridView_numColumns, 2);
                mItemMargin = a.getDimensionPixelSize(R.styleable.SpanVariableGridView_itemMargin, 0);
                mAnimTile = a.getBoolean(R.styleable.SpanVariableGridView_animation, false);
            } finally {
                a.recycle();
            }

        } else {
            mColCount = 2;
            mItemMargin = 0;
        }
    }

    public boolean addCalculateChildrenPositionListener(final CalculateChildrenPosition listener) {
        return mCalculateChildrenPositionList.add(listener);
    }

    public boolean removeCalculateChildrenPositionListener(final CalculateChildrenPosition listener) {
        return mCalculateChildrenPositionList.remove(listener);
    }

    @Override
    public BaseAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public View getSelectedView() {
        return null;
    }

    @Override
    public void setAdapter(BaseAdapter adapter) {

        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mObserver);
        }

        mAdapter = adapter;

        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(mObserver);
        }

        removeAllViewsInLayout();
        requestLayout();
    }

    @Override
    public void setSelection(int position) { }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        if (mAdapter == null) {
            return;
        }

        layoutChildren(INVALID_POSITION, false);
    }

    /**
     * Move children in Real Time
     * @param from - id Child
     * @param to - id Child
     */
    protected void performDragAndDropSwapping(int from, int to) {

        mPopulating = true;

        if (from != to) {
            int step = 0;

            if(from>to){
                step = from-to;
            }else if(from<to){
                step = to-from;
            }

            if(step>1) {
                for(int i=0;i<step;i++){
                    View removedChild = null;

                    if(i==0){
                        removedChild = getChildAt(from);
                        removedChild.clearAnimation();

                        removeViewInLayout(removedChild);
                        addViewInLayout(removedChild, to, removedChild.getLayoutParams());

                        mControlHeight = measureChildren(false);

                        layoutChildren(to, mAnimTile);

                        mPopulating = false;
                    }else{
                        if(from<to) {
                            removedChild = getChildAt(to - (i + 1));
                            removedChild.clearAnimation();

                            removeViewInLayout(removedChild);
                            addViewInLayout(removedChild, (to - i), removedChild.getLayoutParams());

                            mControlHeight = measureChildren(false);

                            layoutChildren((to - i), mAnimTile);

                            mPopulating = false;
                        }else{
                            if(i==1) {
                                removedChild = getChildAt(from-(step-1));
                                removedChild.clearAnimation();

                                removeViewInLayout(removedChild);
                                addViewInLayout(removedChild, from, removedChild.getLayoutParams());

                                mControlHeight = measureChildren(false);

                                layoutChildren(from, mAnimTile);

                                mPopulating = false;
                            }
                        }
                    }
                }
            }else{
                final View removedChild = getChildAt(from);
                removedChild.clearAnimation();

                removeViewInLayout(removedChild);
                addViewInLayout(removedChild, to, removedChild.getLayoutParams());

                mControlHeight = measureChildren(false);

                layoutChildren(to, true);

                mPopulating = false;
            }
        }else{
            mControlHeight = measureChildren(false);

            layoutChildren(to, mAnimTile);

            mPopulating = false;
        }
    }

    @Override
    public void requestLayout() {
        if (!mPopulating) {
            super.requestLayout();
        }
    }

    /**
     * Resize Content in parent View
     */
    protected Rect layoutChildren(final int draggedChild, final boolean animate) {
        int row = 0;
        int rowHeight = 0;
        int fullHeight = mItemMargin;
        int width = getMeasuredWidth() - 2 * mItemMargin;
        final int colWidth = (width - (mColCount - 1) * mItemMargin) / mColCount;

        Rect draggedChildRect = null;

        int itemOffset = mAdapter.getCount()-(((int)mAdapter.getCount()/mColCount)*mColCount);

        for (int position = 0; position < mAdapter.getCount(); position++) {
            final View childView = getChildAt(position);

            final Point prev = new Point(childView.getLeft(), childView.getTop());

            final LayoutParams lp = (LayoutParams) childView.getLayoutParams();

            final int column = lp.column;

            if (row != lp.row) {
                fullHeight += (rowHeight + mItemMargin);
                rowHeight = 0;
            }

            rowHeight = Math.max(rowHeight, childView.getMeasuredHeight());

            row = lp.row;

            final int height_ = column == LayoutParams.ALL_COLUMNS ? width : (lp.hSpan * (colWidth + mItemMargin) - mItemMargin);

            int left_ = 0;
            int top_ = 0;
            int right_ = 0;
            int bottom_ = 0;

            if(USE_OLD){
                left_ = mItemMargin + (column == LayoutParams.ALL_COLUMNS ? 0 : column * (colWidth + mItemMargin));
                top_ = fullHeight;
                right_ = left_ + height_;
                bottom_ = top_ + childView.getMeasuredHeight();
            }else {

                String viewIndex = String.valueOf(position);
                Integer viewIndexNum = position;

                left_ = mItemMargin + (column == LayoutParams.ALL_COLUMNS ? 0 : column * (colWidth + mItemMargin));

                if ((viewIndexNum - mColCount) < 0) {
                    top_ = fullHeight;
                }else{
                    top_ = mapViewTopPadding.get(String.valueOf((viewIndexNum - mColCount)));
                }

                right_ = left_ + height_;
                bottom_ = top_ + childView.getMeasuredHeight();

                if (viewIndex.equals("2")) {
                    maxLeft = Math.max(maxLeft, left_);
                }

                // Add View Padding to Map
                mapViewTopPadding.put(viewIndex, bottom_);

                if (maxLeft / (right_ - left_) == 1) {
                    mapViewType.put(viewIndex, "big");
                } else if (maxLeft / (right_ - left_) == 2) {
                    mapViewType.put(viewIndex, "normal");
                } else {
                    mapViewType.put(viewIndex, "normal");
                }
            }

            measureChildren(childView, height_, (lp.height * lp.wSpan));

            if (position != draggedChild) {
                final Point now = new Point(left_, top_);

                childView.layout(left_, top_, right_, bottom_);

                if (animate) {
                    translateChild(childView, prev, now);
                }

            } else {
                draggedChildRect = new Rect(left_, top_, right_, bottom_);
            }
        }

        mapViewType.clear();
        mapViewTopPadding.clear();

        return draggedChildRect;
    }

    protected final void translateChild(View v, Point prev, Point now) {

        TranslateAnimation translate = new TranslateAnimation(Animation.ABSOLUTE,
                -now.x + prev.x, Animation.ABSOLUTE,
                0,
                Animation.ABSOLUTE, -now.y + prev.y,
                Animation.ABSOLUTE,
                0);

        translate.setInterpolator(new AccelerateInterpolator(4f));
        translate.setDuration(350);
        translate.setFillEnabled(false);
        translate.setFillAfter(false);

        v.clearAnimation();
        v.startAnimation(translate);
    }

    protected void measureChildren(View child, final int heightView, final int widthView) {
        final int heightSpec;

        if (widthView == LayoutParams.WRAP_CONTENT) {
            heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        } else {
            heightSpec = MeasureSpec.makeMeasureSpec(widthView, MeasureSpec.EXACTLY);
        }

        final int widthSpec;

        if (heightView == LayoutParams.WRAP_CONTENT) {
            widthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        } else {
            widthSpec = MeasureSpec.makeMeasureSpec(heightView, MeasureSpec.EXACTLY);
        }

        // Measure Child view not Layout
        child.measure(widthSpec, heightSpec);
    }

    protected int pointToPosition(final int draggedChild, final int x, final int y) {
        for (int index = 0; index < getChildCount(); index++) {

            if (index == draggedChild) {
                continue;
            }

            getChildAt(index).getHitRect(mRect);

            if (mRect.contains(x, y)) {
                return index;
            }
        }

        return INVALID_POSITION;
    }

    private void clickChildAt() {
        final int index = pointToPosition(INVALID_POSITION, mTouchStartX, mTouchStartY);

        if (index != INVALID_POSITION && index == mTouchStartItemPosition) {
            final View itemView = getChildAt(index);
            final long id = mAdapter.getItemId(index);

            performItemClick(itemView, index, id);
        }
    }

    private void longClickChild(final int index) {
        final View itemView = getChildAt(index);
        final long id = mAdapter.getItemId(index);
        final OnItemLongClickListener listener = getOnItemLongClickListener();

        if (listener != null) {
            listener.onItemLongClick(this, itemView, index, id);
        }
    }

    private void startLongPressCheck() {
        if (mLongPressRunnable == null) {
            mLongPressRunnable = new Runnable() {
                public void run() {
                    if (mTouchState == TOUCH_STATE_CLICK) {
                        final int index = pointToPosition(INVALID_POSITION, mTouchStartX, mTouchStartY);

                        if (index != INVALID_POSITION && index == mTouchStartItemPosition) {
                            longClickChild(index);
                            mTouchState = TOUCH_STATE_LONG_CLICK;
                        }
                    }
                }
            };
        }

        postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());
    }

    protected void startLongClickTransition(View clickedChild) {
        if (clickedChild != null && mItemTransitionDrawable == null) {
            if (clickedChild.getBackground().getCurrent() instanceof TransitionDrawable) {
                mItemTransitionDrawable = (TransitionDrawable) clickedChild.getBackground().getCurrent();
                mItemTransitionDrawable.startTransition(ViewConfiguration.getLongPressTimeout());
            }
        }
    }

    protected void resetLongClickTransition() {
        if (mItemTransitionDrawable != null) {
            mItemTransitionDrawable.resetTransition();
            mItemTransitionDrawable = null;
        }
    }

    /**
     * Set width and height all view
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isInEditMode() || mAdapter == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        mControlHeight = measureChildren(false);

        final int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        // Not measure Layout
        setMeasuredDimension(measuredWidth, mControlHeight);
    }

    private void fireCalculateChildrenPositionEvent(final View view, final int position, final int row, final int column) {
        for (CalculateChildrenPosition listener : mCalculateChildrenPositionList) {
            listener.onCalculatePosition(view, position, row, column);
        }
    }

    /**
     * Change size parent View
     */
    private int measureChildren(final boolean justMeasure) {
        int row = 0;
        int col = 0;
        int rowHeight = 0;
        int spansFilled = 0;
        int fullHeight = mItemMargin;

        for (int position = 0; position < mAdapter.getCount(); position++) {
            View childView = getChildAt(position);

            if (childView == null) {
                childView = mAdapter.getView(position, null, this);

                LayoutParams params = (LayoutParams) childView.getLayoutParams();
                if (params == null) {
                    params = new LayoutParams(new AdapterView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                }

                if (!justMeasure) {
                    addViewInLayout(childView, NOT_DEFINED_VALUE, params);
                }
            }

            final LayoutParams lp = (LayoutParams) childView.getLayoutParams();

            measureChildren(childView, lp.width, (lp.height * lp.wSpan));

            lp.position = position;
            spansFilled += lp.hSpan;

            while (true) {

                if (spansFilled <= mColCount) {

                    lp.row = row;
                    lp.column = lp.hSpan == mColCount ? LayoutParams.ALL_COLUMNS : col;
                    col = spansFilled;

                    if (justMeasure) {
                        fireCalculateChildrenPositionEvent(childView, lp.position, lp.row, lp.column);
                    } else {
                        childView.setLayoutParams(lp);
                    }

                    rowHeight = Math.max(rowHeight, mItemMargin + childView.getMeasuredHeight());
                }

                if (spansFilled >= mColCount) {

                    fullHeight += rowHeight;
                    row++;

                    col = 0;
                    rowHeight = 0;

                    if (spansFilled != mColCount) {

                        spansFilled = lp.hSpan;
                        continue;
                    }

                    spansFilled = 0;
                }

                break;
            }
        }

        fullHeight += rowHeight;
        return fullHeight;
    }

    public void requestCalculateChildrenPositions() {
        measureChildren(true);
    }

    private void startTouch(final MotionEvent event) {
        mTouchStartX = (int) event.getX();
        mTouchStartY = (int) event.getY();
        mTouchStartItemPosition = pointToPosition(INVALID_POSITION, mTouchStartX, mTouchStartY);

        startLongPressCheck();

        mTouchState = TOUCH_STATE_CLICK;
    }

    @Override
    public void childDrawableStateChanged(View child) {
        startLongClickTransition(child);
        super.childDrawableStateChanged(child);
    }

    private void endTouch() {
        resetLongClickTransition();
        removeCallbacks(mLongPressRunnable);
        mTouchState = TOUCH_STATE_RESTING;
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        final boolean result = super.dispatchTouchEvent(event);

        if (getChildCount() == 0) {
            return result;
        }

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                startTouch(event);
                break;

            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_UP:
                if (mTouchState == TOUCH_STATE_CLICK) {
                    clickChildAt();
                }

                endTouch();
                break;

            default:
                endTouch();
                break;
        }

        return result;
    }

}
