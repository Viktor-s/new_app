package me.justup.upme.launcher;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;

import me.justup.upme.LauncherActivity;
import me.justup.upme.R;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;

/**
 * Class for initiating a drag within a view or across multiple views.
 */
public class DragController {
    private static final String TAG = DragController.class.getSimpleName();

    public static int DRAG_ACTION_MOVE = 0;
    public static int DRAG_ACTION_COPY = 1;

    private static final int SCROLL_DELAY = 500;
    private static final int RESCROLL_DELAY = 750;
    private static final int VIBRATE_DURATION = 15;

    private static final boolean PROFILE_DRAWING_DURING_DRAG = false;

    private static final int SCROLL_OUTSIDE_ZONE = 0;
    private static final int SCROLL_WAITING_IN_ZONE = 1;

    static final int SCROLL_NONE = -1;
    static final int SCROLL_LEFT = 0;
    static final int SCROLL_RIGHT = 1;

    private static final float MAX_FLING_DEGREES = 35f;

    private LauncherActivity mLauncher = null;
    private Handler mHandler = null;
    private Vibrator mVibrator = null;

    // Temporaries to avoid gc thrash
    private Rect mRectTemp = new Rect();
    private final int[] mCoordinatesTemp = new int[2];

    private boolean mDragging;

    private int mMotionDownX;

    private int mMotionDownY;

    private int mScrollZone;

    private DropTarget.DragObject mDragObject = null;

    private ArrayList<DropTarget> mDropTargets = new ArrayList<DropTarget>();
    private ArrayList<DragListener> mListeners = new ArrayList<DragListener>();
    private DropTarget mFlingToDeleteDropTarget = null;

    /** The window token used as the parent for the DragView. */
    private IBinder mWindowToken = null;

    private View mScrollView = null;

    private View mMoveTarget = null;

    private DragScroller mDragScroller = null;
    private int mScrollState = SCROLL_OUTSIDE_ZONE;
    private ScrollRunnable mScrollRunnable = new ScrollRunnable();

    private DropTarget mLastDropTarget = null;

    private InputMethodManager mInputMethodManager = null;

    private int mLastTouch[] = new int[2];
    private long mLastTouchUpTime = -1;
    private int mDistanceSinceScroll = 0;

    private int mTmpPoint[] = new int[2];
    private Rect mDragLayerRect = new Rect();

    protected int mFlingToDeleteThresholdVelocity;
    private VelocityTracker mVelocityTracker = null;

    public interface DragListener {

        /**
         * A drag has begun
         *
         * @param source An object representing where the drag originated
         * @param info The data associated with the object that is being dragged
         * @param dragAction
         *            The drag action: either
         *            {@link DragController#DRAG_ACTION_MOVE} or
         *            {@link DragController#DRAG_ACTION_COPY}
         */
        void onDragStart(DragSource source, Object info, int dragAction);

        /**
         * The drag has ended
         */
        void onDragEnd();
    }

    /**
     * Used to create a new DragLayer from XML.
     */
    public DragController(LauncherActivity launcher) {
        Resources r = launcher.getResources();
        mLauncher = launcher;
        mHandler = new Handler();
        mScrollZone = r.getDimensionPixelSize(R.dimen.scroll_zone);
        mVelocityTracker = VelocityTracker.obtain();
        mVibrator = (Vibrator) launcher.getSystemService(Context.VIBRATOR_SERVICE);

        float density = r.getDisplayMetrics().density;
        mFlingToDeleteThresholdVelocity = (int) (r.getInteger(R.integer.config_flingToDeleteMinVelocity) * density);
    }

    public boolean dragging() {
        return mDragging;
    }

    /**
     *
     * @param v view
     * @param bmp bitmap The bitmap that represents the view being dragged
     * @param source An object representing where the drag originated
     * @param dragInfo The data associated with the object that is being dragged
     * @param dragAction either {@link #DRAG_ACTION_MOVE} or {@link #DRAG_ACTION_COPY}
     * @param dragRegion Coordinates within the bitmap b for the position
     *            of item being dragged. Makes dragging feel more precise, e.g.
     *            you can clip out a transparent border
     */
    public void startDrag(View v, Bitmap bmp, DragSource source,
                          Object dragInfo, int dragAction, Rect dragRegion,
                          float initialDragViewScale) {

        int[] loc = mCoordinatesTemp;
        mLauncher.getDragLayer().getLocationInDragLayer(v, loc);
        int dragLayerX = loc[0]
                + v.getPaddingLeft()
                + (int) ((initialDragViewScale * bmp.getWidth() - bmp
                .getWidth()) / 2);

        int dragLayerY = loc[1]
                + v.getPaddingTop()
                + (int) ((initialDragViewScale * bmp.getHeight() - bmp
                .getHeight()) / 2);

        startDrag(bmp, dragLayerX, dragLayerY, source, dragInfo, dragAction, null, dragRegion, initialDragViewScale);

        if (dragAction == DRAG_ACTION_MOVE) {
            v.setVisibility(View.GONE);
        }
    }

    /**
     * Starts a drag.
     *
     * @param b
     * @param dragLayerX
     * @param dragLayerY
     * @param source
     * @param dragInfo
     * @param dragAction
     * @param dragRegion Coordinates within the bitmap b for the position
     *            of item being dragged. Makes dragging feel more precise, e.g.
     *            you can clip out a transparent border
     */
    public void startDrag(Bitmap b, int dragLayerX, int dragLayerY,
                          DragSource source, Object dragInfo, int dragAction,
                          Point dragOffset, Rect dragRegion, float initialDragViewScale) {

        if (PROFILE_DRAWING_DURING_DRAG) {
            android.os.Debug.startMethodTracing("Launcher");
        }

        if (mInputMethodManager == null) {
            mInputMethodManager = (InputMethodManager) mLauncher.getSystemService(Context.INPUT_METHOD_SERVICE);
        }

        mInputMethodManager.hideSoftInputFromWindow(mWindowToken, 0);

        for (DragListener listener : mListeners) {
            listener.onDragStart(source, dragInfo, dragAction);
        }

        final int registrationX = mMotionDownX - dragLayerX;
        final int registrationY = mMotionDownY - dragLayerY;

        final int dragRegionLeft = dragRegion == null ? 0 : dragRegion.left;
        final int dragRegionTop = dragRegion == null ? 0 : dragRegion.top;

        mDragging = true;

        mDragObject = new DropTarget.DragObject();

        mDragObject.dragComplete = false;
        mDragObject.xOffset = mMotionDownX - (dragLayerX + dragRegionLeft);
        mDragObject.yOffset = mMotionDownY - (dragLayerY + dragRegionTop);
        mDragObject.dragSource = source;
        mDragObject.dragInfo = dragInfo;

        mVibrator.vibrate(VIBRATE_DURATION);

        final DragView dragView = mDragObject.dragView = new DragView(
                mLauncher, b, registrationX, registrationY, 0, 0, b.getWidth(),
                b.getHeight(), initialDragViewScale);

        if (dragOffset != null) {
            dragView.setDragVisualizeOffset(new Point(dragOffset));
        }
        if (dragRegion != null) {
            dragView.setDragRegion(new Rect(dragRegion));
        }

        dragView.show(mMotionDownX, mMotionDownY);
        handleMoveEvent(mMotionDownX, mMotionDownY);
    }

    Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        float alpha = v.getAlpha();
        v.setAlpha(1.0f);

        if (color != 0) {
            v.destroyDrawingCache();
        }

        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            LOGE(TAG, "Failed getViewBitmap(" + v + ")", new RuntimeException());

            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setAlpha(alpha);
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }


    /**
     * Call this from a drag source view like this:
     *
     * <pre>
     *  @Override
     *  public boolean dispatchKeyEvent(KeyEvent event) { return mDragController.dispatchKeyEvent(this, event) || super.dispatchKeyEvent(event);
     * </pre>
     */
    public boolean dispatchKeyEvent(KeyEvent event) {
        return mDragging;
    }

    public boolean isDragging() {
        return mDragging;
    }

    public void cancelDrag() {
        if (mDragging) {
            if (mLastDropTarget != null) {
                mLastDropTarget.onDragExit(mDragObject);
            }

            mDragObject.deferDragViewCleanupPostAnimation = false;
            mDragObject.cancelled = true;
            mDragObject.dragComplete = true;
            mDragObject.dragSource.onDropCompleted(null, mDragObject, false, false);
        }

        endDrag();
    }

    public void onAppsRemoved(ArrayList<String> packageNames, Context context) {
        if (mDragObject != null) {
            Object rawDragInfo = mDragObject.dragInfo;
            if (rawDragInfo instanceof ShortcutInfo) {
                ShortcutInfo dragInfo = (ShortcutInfo) rawDragInfo;
                for (String pn : packageNames) {
                    // Added null checks to prevent NPE we've seen in the wild
                    if (dragInfo != null && dragInfo.intent != null) {
                        boolean isSamePackage = dragInfo.getPackageName().equals(pn);
                        if (isSamePackage) {
                            cancelDrag();
                            return;
                        }
                    }
                }
            }
        }
    }

    private void endDrag() {
        if (mDragging) {
            mDragging = false;
            clearScrollRunnable();
            boolean isDeferred = false;
            if (mDragObject.dragView != null) {
                isDeferred = mDragObject.deferDragViewCleanupPostAnimation;
                if (!isDeferred) {
                    mDragObject.dragView.remove();
                }

                mDragObject.dragView = null;
            }

            // Only end the drag if we are not deferred
            if (!isDeferred) {
                for (DragListener listener : mListeners) {
                    listener.onDragEnd();
                }
            }
        }

        releaseVelocityTracker();
    }

    public void onDeferredEndDrag(DragView dragView) {
        dragView.remove();

        // If we skipped calling onDragEnd() before, do it now
        for (DragListener listener : mListeners) {
            listener.onDragEnd();
        }
    }

    public void onDeferredEndFling(DropTarget.DragObject d) {
        d.dragSource.onFlingToDeleteCompleted();
    }

    /**
     * Clamps the position to the drag layer bounds.
     */
    private int[] getClampedDragLayerPos(float x, float y) {
        mLauncher.getDragLayer().getLocalVisibleRect(mDragLayerRect);

        mTmpPoint[0] = (int) Math.max(mDragLayerRect.left,
                Math.min(x, mDragLayerRect.right - 1));
        mTmpPoint[1] = (int) Math.max(mDragLayerRect.top,
                Math.min(y, mDragLayerRect.bottom - 1));

        return mTmpPoint;
    }

    public long getLastGestureUpTime() {
        if (mDragging) {
            return System.currentTimeMillis();
        } else {
            return mLastTouchUpTime;
        }
    }

    public void resetLastGestureUpTime() {
        mLastTouchUpTime = -1;
    }

    /**
     * Call this from a drag source view.
     */
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        @SuppressWarnings("all")
        // suppress dead code warning
        final boolean debug = false;
        LOGD(LauncherActivity.TAG, "DragController.onInterceptTouchEvent " + ev + " mDragging = " + mDragging);

        // Update the velocity tracker
        acquireVelocityTrackerAndAddMovement(ev);

        final int action = ev.getAction();
        final int[] dragLayerPos = getClampedDragLayerPos(ev.getX(), ev.getY());
        final int dragLayerX = dragLayerPos[0];
        final int dragLayerY = dragLayerPos[1];

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_DOWN:
                // Remember location of down touch
                mMotionDownX = dragLayerX;
                mMotionDownY = dragLayerY;
                mLastDropTarget = null;
                break;
            case MotionEvent.ACTION_UP:
                mLastTouchUpTime = System.currentTimeMillis();
                if (mDragging) {
                    PointF vec = isFlingingToDelete(mDragObject.dragSource);
                    if (vec != null) {
                        dropOnFlingToDeleteTarget(dragLayerX, dragLayerY, vec);
                    } else {
                        drop(dragLayerX, dragLayerY);
                    }
                }

                endDrag();
                break;
            case MotionEvent.ACTION_CANCEL:
                cancelDrag();
                break;
        }

        return mDragging;
    }

    public void setMoveTarget(View view) {
        mMoveTarget = view;
    }

    public boolean dispatchUnhandledMove(View focused, int direction) {
        return mMoveTarget != null && mMoveTarget.dispatchUnhandledMove(focused, direction);
    }

    private void clearScrollRunnable() {
        mHandler.removeCallbacks(mScrollRunnable);
        if (mScrollState == SCROLL_WAITING_IN_ZONE) {
            mScrollState = SCROLL_OUTSIDE_ZONE;
            mScrollRunnable.setDirection(SCROLL_RIGHT);
            mDragScroller.onExitScrollArea();
            mLauncher.getDragLayer().onExitScrollArea();
        }
    }

    private void handleMoveEvent(int x, int y) {
        mDragObject.dragView.move(x, y);

        final int[] coordinates = mCoordinatesTemp;
        DropTarget dropTarget = findDropTarget(x, y, coordinates);
        mDragObject.x = coordinates[0];
        mDragObject.y = coordinates[1];
        if (dropTarget != null) {
            DropTarget delegate = dropTarget.getDropTargetDelegate(mDragObject);
            if (delegate != null) {
                dropTarget = delegate;
            }

            if (mLastDropTarget != dropTarget) {
                if (mLastDropTarget != null) {
                    mLastDropTarget.onDragExit(mDragObject);
                }

                dropTarget.onDragEnter(mDragObject);
            }

            dropTarget.onDragOver(mDragObject);
        } else {
            if (mLastDropTarget != null) {
                mLastDropTarget.onDragExit(mDragObject);
            }
        }

        mLastDropTarget = dropTarget;

        final int slop = ViewConfiguration.get(mLauncher).getScaledWindowTouchSlop();
        mDistanceSinceScroll += Math.sqrt(Math.pow(mLastTouch[0] - x, 2) + Math.pow(mLastTouch[1] - y, 2));
        mLastTouch[0] = x;
        mLastTouch[1] = y;
        final int delay = mDistanceSinceScroll < slop ? RESCROLL_DELAY : SCROLL_DELAY;

        if (x < mScrollZone) {
            if (mScrollState == SCROLL_OUTSIDE_ZONE) {
                mScrollState = SCROLL_WAITING_IN_ZONE;
                if (mDragScroller.onEnterScrollArea(x, y, SCROLL_LEFT)) {
                    mLauncher.getDragLayer().onEnterScrollArea(SCROLL_LEFT);
                    mScrollRunnable.setDirection(SCROLL_LEFT);
                    mHandler.postDelayed(mScrollRunnable, delay);
                }
            }
        } else if (x > mScrollView.getWidth() - mScrollZone) {
            if (mScrollState == SCROLL_OUTSIDE_ZONE) {
                mScrollState = SCROLL_WAITING_IN_ZONE;
                if (mDragScroller.onEnterScrollArea(x, y, SCROLL_RIGHT)) {
                    mLauncher.getDragLayer().onEnterScrollArea(SCROLL_RIGHT);
                    mScrollRunnable.setDirection(SCROLL_RIGHT);
                    mHandler.postDelayed(mScrollRunnable, delay);
                }
            }
        } else {
            clearScrollRunnable();
        }
    }

    public void forceMoveEvent() {
        if (mDragging) {
            handleMoveEvent(mDragObject.x, mDragObject.y);
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (!mDragging) {
            return false;
        }

        // Update the velocity tracker
        acquireVelocityTrackerAndAddMovement(ev);

        final int action = ev.getAction();
        final int[] dragLayerPos = getClampedDragLayerPos(ev.getX(), ev.getY());
        final int dragLayerX = dragLayerPos[0];
        final int dragLayerY = dragLayerPos[1];

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Remember where the motion event started
                mMotionDownX = dragLayerX;
                mMotionDownY = dragLayerY;

                if ((dragLayerX < mScrollZone) || (dragLayerX > mScrollView.getWidth() - mScrollZone)) {
                    mScrollState = SCROLL_WAITING_IN_ZONE;
                    mHandler.postDelayed(mScrollRunnable, SCROLL_DELAY);
                } else {
                    mScrollState = SCROLL_OUTSIDE_ZONE;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                handleMoveEvent(dragLayerX, dragLayerY);
                break;
            case MotionEvent.ACTION_UP:
                handleMoveEvent(dragLayerX, dragLayerY);
                mHandler.removeCallbacks(mScrollRunnable);

                if (mDragging) {
                    PointF vec = isFlingingToDelete(mDragObject.dragSource);
                    if (vec != null) {
                        dropOnFlingToDeleteTarget(dragLayerX, dragLayerY, vec);
                    } else {
                        drop(dragLayerX, dragLayerY);
                    }
                }

                endDrag();
                break;
            case MotionEvent.ACTION_CANCEL:
                mHandler.removeCallbacks(mScrollRunnable);
                cancelDrag();
                break;
        }

        return true;
    }

    /**
     * Determines whether the user flung the current item to delete it.
     *
     * @return the vector at which the item was flung, or null if no fling was detected.
     */
    private PointF isFlingingToDelete(DragSource source) {
        if (mFlingToDeleteDropTarget == null)
            return null;
        if (!source.supportsFlingToDelete())
            return null;

        ViewConfiguration config = ViewConfiguration.get(mLauncher);
        mVelocityTracker.computeCurrentVelocity(1000, config.getScaledMaximumFlingVelocity());

        if (mVelocityTracker.getYVelocity() < mFlingToDeleteThresholdVelocity) {
            // Do a quick dot product test to ensure that we are flinging upwards
            PointF vel = new PointF(mVelocityTracker.getXVelocity(), mVelocityTracker.getYVelocity());
            PointF upVec = new PointF(0f, -1f);
            float theta = (float) Math
                    .acos(((vel.x * upVec.x) + (vel.y * upVec.y))
                            / (vel.length() * upVec.length()));
            if (theta <= Math.toRadians(MAX_FLING_DEGREES)) {
                return vel;
            }
        }

        return null;
    }

    private void dropOnFlingToDeleteTarget(float x, float y, PointF vel) {
        final int[] coordinates = mCoordinatesTemp;

        mDragObject.x = coordinates[0];
        mDragObject.y = coordinates[1];

        // Clean up dragging on the target if it's not the current fling delete
        // target otherwise,
        // start dragging to it.
        if (mLastDropTarget != null && mFlingToDeleteDropTarget != mLastDropTarget) {
            mLastDropTarget.onDragExit(mDragObject);
        }

        // Drop onto the fling-to-delete target
        boolean accepted = false;
        mFlingToDeleteDropTarget.onDragEnter(mDragObject);
        // We must set dragComplete to true _only_ after we "enter" the
        // fling-to-delete target for
        // "drop"
        mDragObject.dragComplete = true;
        mFlingToDeleteDropTarget.onDragExit(mDragObject);
        if (mFlingToDeleteDropTarget.acceptDrop(mDragObject)) {
            mFlingToDeleteDropTarget.onFlingToDelete(mDragObject, mDragObject.x, mDragObject.y, vel);
            accepted = true;
        }

        mDragObject.dragSource.onDropCompleted((View) mFlingToDeleteDropTarget, mDragObject, true, accepted);
    }

    private void drop(float x, float y) {
        final int[] coordinates = mCoordinatesTemp;
        final DropTarget dropTarget = findDropTarget((int) x, (int) y, coordinates);

        mDragObject.x = coordinates[0];
        mDragObject.y = coordinates[1];
        boolean accepted = false;
        if (dropTarget != null) {
            mDragObject.dragComplete = true;
            dropTarget.onDragExit(mDragObject);
            if (dropTarget.acceptDrop(mDragObject)) {
                dropTarget.onDrop(mDragObject);
                accepted = true;
            }
        }

        mDragObject.dragSource.onDropCompleted((View) dropTarget, mDragObject, false, accepted);
    }

    private DropTarget findDropTarget(int x, int y, int[] dropCoordinates) {
        final Rect r = mRectTemp;

        final ArrayList<DropTarget> dropTargets = mDropTargets;
        final int count = dropTargets.size();
        for (int i = count - 1; i >= 0; i--) {
            DropTarget target = dropTargets.get(i);
            if (!target.isDropEnabled())
                continue;

            target.getHitRect(r);

            // Convert the hit rect to DragLayer coordinates
            target.getLocationInDragLayer(dropCoordinates);
            r.offset(dropCoordinates[0] - target.getLeft(), dropCoordinates[1] - target.getTop());

            mDragObject.x = x;
            mDragObject.y = y;
            if (r.contains(x, y)) {
                DropTarget delegate = target.getDropTargetDelegate(mDragObject);
                if (delegate != null) {
                    target = delegate;
                    target.getLocationInDragLayer(dropCoordinates);
                }

                // Make dropCoordinates relative to the DropTarget
                dropCoordinates[0] = x - dropCoordinates[0];
                dropCoordinates[1] = y - dropCoordinates[1];

                return target;
            }
        }

        return null;
    }

    public void setDragScoller(DragScroller scroller) {
        mDragScroller = scroller;
    }

    public void setWindowToken(IBinder token) {
        mWindowToken = token;
    }

    public void addDragListener(DragListener l) {
        mListeners.add(l);
    }

    public void removeDragListener(DragListener l) {
        mListeners.remove(l);
    }

    public void addDropTarget(DropTarget target) {
        mDropTargets.add(target);
    }

    public void removeDropTarget(DropTarget target) {
        mDropTargets.remove(target);
    }

    /**
     * Sets the current fling-to-delete drop target.
     */
    public void setFlingToDeleteDropTarget(DropTarget target) {
        mFlingToDeleteDropTarget = target;
    }

    private void acquireVelocityTrackerAndAddMovement(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        mVelocityTracker.addMovement(ev);
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public void setScrollView(View v) {
        mScrollView = v;
    }

    DragView getDragView() {
        return mDragObject.dragView;
    }

    private class ScrollRunnable implements Runnable {
        private int mDirection;

        ScrollRunnable() { }

        public void run() {
            if (mDragScroller != null) {
                if (mDirection == SCROLL_LEFT) {
                    mDragScroller.scrollLeft();
                } else {
                    mDragScroller.scrollRight();
                }

                mScrollState = SCROLL_OUTSIDE_ZONE;
                mDistanceSinceScroll = 0;
                mDragScroller.onExitScrollArea();
                mLauncher.getDragLayer().onExitScrollArea();

                if (isDragging()) {
                    forceMoveEvent();
                }
            }
        }

        void setDirection(int direction) {
            mDirection = direction;
        }
    }
}
