package me.justup.upme.launcher;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import me.justup.upme.LauncherActivity;
import me.justup.upme.R;

public class ButtonDropTarget extends TextView implements DropTarget,
		DragController.DragListener {

	protected final int mTransitionDuration;

	protected LauncherActivity mLauncher= null;
	private int mBottomDragPadding;
	protected TextView mText = null;
	protected SearchDropTargetBar mSearchDropTargetBar = null;

	protected boolean mActive;

	protected int mHoverColor = 0;

	public ButtonDropTarget(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ButtonDropTarget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		Resources r = getResources();
		mTransitionDuration = r
				.getInteger(R.integer.config_dropTargetBgTransitionDuration);
		mBottomDragPadding = r
				.getDimensionPixelSize(R.dimen.drop_target_drag_padding);
	}

	void setLauncher(LauncherActivity launcher) {
		mLauncher = launcher;
	}

	public boolean acceptDrop(DragObject d) {
		return false;
	}

	public void setSearchDropTargetBar(SearchDropTargetBar searchDropTargetBar) {
		mSearchDropTargetBar = searchDropTargetBar;
	}

	protected Drawable getCurrentDrawable() {
		Drawable[] drawables = getCompoundDrawables();
		for (int i = 0; i < drawables.length; ++i) {
			if (drawables[i] != null) {
				return drawables[i];
			}
		}

		return null;
	}

	public void onDrop(DragObject d) {

	}

	public void onFlingToDelete(DragObject d, int x, int y, PointF vec) {
		// Do nothing
	}

	public void onDragEnter(DragObject d) {
		d.dragView.setColor(mHoverColor);
	}

	public void onDragOver(DragObject d) {
		// Do nothing
	}

	public void onDragExit(DragObject d) {
		d.dragView.setColor(0);
	}

	public void onDragStart(DragSource source, Object info, int dragAction) {
		// Do nothing
	}

	public boolean isDropEnabled() {
		return mActive;
	}

	public void onDragEnd() {
		// Do nothing
	}

	@Override
	public void getHitRect(android.graphics.Rect outRect) {
		super.getHitRect(outRect);
		outRect.bottom += mBottomDragPadding;
	}

	Rect getIconRect(int itemWidth, int itemHeight, int drawableWidth,
			int drawableHeight) {
		DragLayer dragLayer = mLauncher.getDragLayer();

		// Find the rect to animate to (the view is center aligned)
		Rect to = new Rect();
		dragLayer.getViewRectRelativeToSelf(this, to);
		int width = drawableWidth;
		int height = drawableHeight;
		int left = to.left + getPaddingLeft();
		int top = to.top + (getMeasuredHeight() - height) / 2;
		to.set(left, top, left + width, top + height);

		// Center the destination rect about the trash icon
		int xOffset = (int) -(itemWidth - width) / 2;
		int yOffset = (int) -(itemHeight - height) / 2;
		to.offset(xOffset, yOffset);

		return to;
	}

	@Override
	public DropTarget getDropTargetDelegate(DragObject d) {
		return null;
	}

	public void getLocationInDragLayer(int[] loc) {
		mLauncher.getDragLayer().getLocationInDragLayer(this, loc);
	}
}
