package me.justup.upme.launcher;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;

import me.justup.upme.LauncherActivity;

import static me.justup.upme.utils.LogUtils.LOGE;

/**
 * Interface defining an object that can receive a drag.
 */
public interface DropTarget {
    static final String TAG = DropTarget.class.getSimpleName();

    public class DragObject {
        public int x = -1;
        public int y = -1;

        /**
         * X offset from the upper-left corner of the cell to where we touched.
         */
        public int xOffset = -1;

        /**
         * Y offset from the upper-left corner of the cell to where we touched.
         */
        public int yOffset = -1;

        /**
         * This indicates whether a drag is in final stages, either drop or
         * cancel. It differentiates onDragExit, since this is called when the
         * drag is ending, above the current drag target, or when the drag moves
         * off the current drag object.
         */
        public boolean dragComplete = false;

        public DragView dragView = null;

        public Object dragInfo = null;

        public DragSource dragSource = null;

        public Runnable postAnimationRunnable = null;

        public boolean cancelled = false;

        /**
         * Drop animation DragView
         */
        public boolean deferDragViewCleanupPostAnimation = true;

        public DragObject() { }
    }

    public static class DragEnforcer implements DragController.DragListener {
        int dragParity = 0;

        public DragEnforcer(Context context) {
            LauncherActivity mainActivity = (LauncherActivity) context;
            try {
                mainActivity.getDragController().addDragListener(this);
            }catch (NullPointerException e){
                LOGE(TAG, e.getMessage());
            }
        }

        void onDragEnter() {
            dragParity++;
            if (dragParity != 1) {
                LOGE(TAG, "onDragEnter: Drag contract violated: " + dragParity);
            }
        }

        void onDragExit() {
            dragParity--;
            if (dragParity != 0) {
                LOGE(TAG, "onDragExit: Drag contract violated: " + dragParity);
            }
        }

        @Override
        public void onDragStart(DragSource source, Object info, int dragAction) {
            if (dragParity != 0) {
                LOGE(TAG, "onDragEnter: Drag contract violated: " + dragParity);
            }
        }

        @Override
        public void onDragEnd() {
            if (dragParity != 0) {
                LOGE(TAG, "onDragExit: Drag contract violated: " + dragParity);
            }
        }
    }

    public boolean isDropEnabled();

    public void onDrop(DragObject dragObject);

    public void onDragEnter(DragObject dragObject);

    public void onDragOver(DragObject dragObject);

    public void onDragExit(DragObject dragObject);

    /**
     * Handle an object being dropped as a result of flinging to delete and will
     * be called in place of onDrop(). (This is only called on objects that are
     * set as the DragController's fling-to-delete target.
     */
    public void onFlingToDelete(DragObject dragObject, int x, int y, PointF vec);

    public DropTarget getDropTargetDelegate(DragObject dragObject);

    public boolean acceptDrop(DragObject dragObject);

    public void getHitRect(Rect outRect);

    public void getLocationInDragLayer(int[] loc);

    public int getLeft();

    public int getTop();
}
