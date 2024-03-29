package me.justup.upme.launcher;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import me.justup.upme.LauncherActivity;
import me.justup.upme.R;

public class Hotseat extends FrameLayout {
	@SuppressWarnings("unused")
	private static final String TAG = "Hotseat";

	private LauncherActivity mLauncher = null;
	private CellLayout mContent = null;

	private int mCellCountX;
	private int mCellCountY;
	private int mAllAppsButtonRank;

	private boolean mTransposeLayoutWithOrientation;
	private boolean mIsLandscape;

	public Hotseat(Context context) {
		this(context, null);
	}

	public Hotseat(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public Hotseat(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.Hotseat, defStyle, 0);
		Resources r = context.getResources();
		mCellCountX = a.getInt(R.styleable.Hotseat_cellCountX, -1);
		mCellCountY = a.getInt(R.styleable.Hotseat_cellCountY, -1);
		mAllAppsButtonRank = r.getInteger(R.integer.hotseat_all_apps_index);
		mTransposeLayoutWithOrientation = r
				.getBoolean(R.bool.hotseat_transpose_layout_with_orientation);
		mIsLandscape = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}

	public void setup(LauncherActivity launcher) {
		mLauncher = launcher;
		setOnKeyListener(new HotseatIconKeyEventListener());
	}

	public CellLayout getLayout() {
		return mContent;
	}

	private boolean hasVerticalHotseat() {
		return (mIsLandscape && mTransposeLayoutWithOrientation);
	}

	/**
	 * Get the orientation invariant order of the item in the hotseat for
	 * persistence.
	 */
	public int getOrderInHotseat(int x, int y) {
		return hasVerticalHotseat() ? (mContent.getCountY() - y - 1) : x;
	}

    public int getCellXFromOrder(int rank) {
		return hasVerticalHotseat() ? 0 : rank;
	}

    public int getCellYFromOrder(int rank) {
		return hasVerticalHotseat() ? (mContent.getCountY() - (rank + 1)) : 0;
	}

	public boolean isAllAppsButtonRank(int rank) {
		return rank == mAllAppsButtonRank;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (mCellCountX < 0)
			mCellCountX = LauncherModel.getCellCountX();
		if (mCellCountY < 0)
			mCellCountY = LauncherModel.getCellCountY();
		mContent = (CellLayout) findViewById(R.id.layout);
		mContent.setGridSize(mCellCountX, mCellCountY);
		mContent.setIsHotseat(true);

		resetLayout();
	}

    public void resetLayout() {
		mContent.removeAllViewsInLayout();

		Context context = getContext();
		LayoutInflater inflater = LayoutInflater.from(context);
		BubbleTextView allAppsButton = (BubbleTextView) inflater.inflate(
				R.layout.application, mContent, false);
		allAppsButton.setCompoundDrawablesWithIntrinsicBounds(null, context
				.getResources().getDrawable(R.drawable.all_apps_button_icon),
				null, null);
		allAppsButton.setContentDescription(context
				.getString(R.string.all_apps_button_label));
		allAppsButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mLauncher != null
						&& (event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
					mLauncher.onTouchDownAllAppsButton(v);
				}
				return false;
			}
		});

		allAppsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(android.view.View v) {
				if (mLauncher != null) {
					mLauncher.onClickAllAppsButton(v);
				}
			}
		});

		// Note: We do this to ensure that the hotseat is always laid out in the
		// orientation of
		// the hotseat in order regardless of which orientation they were added
		int x = getCellXFromOrder(mAllAppsButtonRank);
		int y = getCellYFromOrder(mAllAppsButtonRank);
		CellLayout.LayoutParams lp = new CellLayout.LayoutParams(x, y, 1, 1);
		lp.canReorder = false;
		mContent.addViewToCellLayout(allAppsButton, -1, 0, lp, true);
	}
}
