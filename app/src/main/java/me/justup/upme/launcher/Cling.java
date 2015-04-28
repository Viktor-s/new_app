package me.justup.upme.launcher;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.FocusFinder;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import me.justup.upme.JustUpApplication;
import me.justup.upme.LauncherActivity;
import me.justup.upme.R;

import static me.justup.upme.utils.LogUtils.LOGE;

public class Cling extends FrameLayout {
    private static final String TAG = Cling.class.getSimpleName();

    public static final String WORKSPACE_CLING_DISMISSED_KEY = "cling.workspace.dismissed";
    public static final String ALLAPPS_CLING_DISMISSED_KEY = "cling.allapps.dismissed";
    public static final String FOLDER_CLING_DISMISSED_KEY = "cling.folder.dismissed";

    private static String WORKSPACE_PORTRAIT = "workspace_portrait";
    private static String WORKSPACE_LANDSCAPE = "workspace_landscape";
    private static String WORKSPACE_LARGE = "workspace_large";
    private static String WORKSPACE_CUSTOM = "workspace_custom";

    private static String ALLAPPS_PORTRAIT = "all_apps_portrait";
    private static String ALLAPPS_LANDSCAPE = "all_apps_landscape";
    private static String ALLAPPS_LARGE = "all_apps_large";

    private static String FOLDER_PORTRAIT = "folder_portrait";
    private static String FOLDER_LANDSCAPE = "folder_landscape";
    private static String FOLDER_LARGE = "folder_large";

    private LauncherActivity mLauncher = null;
    private boolean mIsInitialized;
    private String mDrawIdentifier = null;
    private Drawable mBackground = null;
    private Drawable mPunchThroughGraphic = null;
    private Drawable mHandTouchGraphic = null;
    private int mPunchThroughGraphicCenterRadius;
    private int mAppIconSize;
    private int mButtonBarHeight;
    private float mRevealRadius;
    private int[] mPositionData;

    private Paint mErasePaint = null;

    public Cling(Context context) {
        this(context, null, 0);
    }

    public Cling(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Cling(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Cling, defStyle, 0);
        mDrawIdentifier = a.getString(R.styleable.Cling_drawIdentifier);
        a.recycle();

        setClickable(true);
    }

    public void init(LauncherActivity l, int[] positionData) {
        if (!mIsInitialized) {
            mLauncher = l;
            mPositionData = positionData;

            Resources r = getContext().getResources();

            mPunchThroughGraphic = r.getDrawable(R.drawable.cling);
            mPunchThroughGraphicCenterRadius = r.getDimensionPixelSize(R.dimen.clingPunchThroughGraphicCenterRadius);
            mAppIconSize = r.getDimensionPixelSize(R.dimen.app_icon_size);
            mRevealRadius = r.getDimensionPixelSize(R.dimen.reveal_radius) * 1f;
            mButtonBarHeight = r.getDimensionPixelSize(R.dimen.button_bar_height);

            mErasePaint = new Paint();
            mErasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
            mErasePaint.setColor(0xFFFFFF);
            mErasePaint.setAlpha(0);

            mIsInitialized = true;
        }
    }

    public void cleanup() {
        mBackground = null;
        mPunchThroughGraphic = null;
        mHandTouchGraphic = null;
        mIsInitialized = false;
    }

    public String getDrawIdentifier() {
        return mDrawIdentifier;
    }

    private int[] getPunchThroughPositions() {
        if (mDrawIdentifier.equals(WORKSPACE_PORTRAIT)) {
            return new int[]{getMeasuredWidth() / 2, getMeasuredHeight() - (mButtonBarHeight / 2)};
        } else if (mDrawIdentifier.equals(WORKSPACE_LANDSCAPE)) {
            return new int[]{getMeasuredWidth() - (mButtonBarHeight / 2), getMeasuredHeight() / 2};
        } else if (mDrawIdentifier.equals(WORKSPACE_LARGE)) {
            final float scale = JustUpApplication.getScreenDensity();
            final int cornerXOffset = (int) (scale * 15);
            final int cornerYOffset = (int) (scale * 10);
            return new int[]{getMeasuredWidth() - cornerXOffset, cornerYOffset};
        } else if (mDrawIdentifier.equals(ALLAPPS_PORTRAIT) ||
                   mDrawIdentifier.equals(ALLAPPS_LANDSCAPE) ||
                   mDrawIdentifier.equals(ALLAPPS_LARGE)) {
            return mPositionData;
        }

        return new int[]{-1, -1};
    }

    @Override
    public View focusSearch(int direction) {
        return this.focusSearch(this, direction);
    }

    @Override
    public View focusSearch(View focused, int direction) {
        return FocusFinder.getInstance().findNextFocus(this, focused, direction);
    }

    @Override
    public boolean onHoverEvent(MotionEvent event) {
        return (mDrawIdentifier.equals(WORKSPACE_PORTRAIT)
                || mDrawIdentifier.equals(WORKSPACE_LANDSCAPE)
                || mDrawIdentifier.equals(WORKSPACE_LARGE)
                || mDrawIdentifier.equals(ALLAPPS_PORTRAIT)
                || mDrawIdentifier.equals(ALLAPPS_LANDSCAPE)
                || mDrawIdentifier.equals(ALLAPPS_LARGE)
                || mDrawIdentifier.equals(WORKSPACE_CUSTOM));
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
        if (mDrawIdentifier.equals(WORKSPACE_PORTRAIT) ||
                mDrawIdentifier.equals(WORKSPACE_LANDSCAPE) ||
                mDrawIdentifier.equals(WORKSPACE_LARGE) ||
                mDrawIdentifier.equals(ALLAPPS_PORTRAIT) ||
                mDrawIdentifier.equals(ALLAPPS_LANDSCAPE) ||
                mDrawIdentifier.equals(ALLAPPS_LARGE)) {

            int[] positions = getPunchThroughPositions();

            for (int i = 0; i < positions.length; i += 2) {
                double diff = Math.sqrt(Math.pow(event.getX() - positions[i], 2) +
                        Math.pow(event.getY() - positions[i + 1], 2));
                if (diff < mRevealRadius) {
                    return false;
                }
            }

        } else if (mDrawIdentifier.equals(FOLDER_PORTRAIT) ||
                mDrawIdentifier.equals(FOLDER_LANDSCAPE) ||
                mDrawIdentifier.equals(FOLDER_LARGE)) {

            Folder f = null;

            try{
                f = mLauncher.getWorkspace().getOpenFolder();
            }catch (NullPointerException e){
                LOGE(TAG, e.getMessage());
            }

            if (f != null) {
                Rect r = new Rect();
                f.getHitRect(r);
                if (r.contains((int) event.getX(), (int) event.getY())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mIsInitialized) {
            DisplayMetrics metrics = new DisplayMetrics();
            mLauncher.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            // Initialize the draw buffer (to allow punching through)
            Bitmap b = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);

            // Draw the background
            if (mBackground == null) {
                if (mDrawIdentifier.equals(WORKSPACE_PORTRAIT) ||
                        mDrawIdentifier.equals(WORKSPACE_LANDSCAPE) ||
                        mDrawIdentifier.equals(WORKSPACE_LARGE)) {

                    mBackground = getResources().getDrawable(R.drawable.bg_cling1);
                } else if (mDrawIdentifier.equals(ALLAPPS_PORTRAIT) ||
                        mDrawIdentifier.equals(ALLAPPS_LANDSCAPE) ||
                        mDrawIdentifier.equals(ALLAPPS_LARGE)) {

                    mBackground = getResources().getDrawable(R.drawable.bg_cling2);
                } else if (mDrawIdentifier.equals(FOLDER_PORTRAIT) ||
                        mDrawIdentifier.equals(FOLDER_LANDSCAPE)) {

                    mBackground = getResources().getDrawable(R.drawable.bg_cling3);
                } else if (mDrawIdentifier.equals(FOLDER_LARGE)) {

                    mBackground = getResources().getDrawable(R.drawable.bg_cling4);
                } else if (mDrawIdentifier.equals(WORKSPACE_CUSTOM)) {

                    mBackground = getResources().getDrawable(R.drawable.bg_cling5);
                }
            }

            if (mBackground != null) {
                mBackground.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                mBackground.draw(c);
            } else {
                c.drawColor(0x99000000);
            }

            int cx = -1;
            int cy = -1;
            float scale = mRevealRadius / mPunchThroughGraphicCenterRadius;
            int dw = (int) (scale * mPunchThroughGraphic.getIntrinsicWidth());
            int dh = (int) (scale * mPunchThroughGraphic.getIntrinsicHeight());

            // Determine where to draw the punch through graphic
            int[] positions = getPunchThroughPositions();
            for (int i = 0; i < positions.length; i += 2) {
                cx = positions[i];
                cy = positions[i + 1];
                if (cx > -1 && cy > -1) {
                    c.drawCircle(cx, cy, mRevealRadius, mErasePaint);
                    mPunchThroughGraphic.setBounds(cx - dw/2, cy - dh/2, cx + dw/2, cy + dh/2);
                    mPunchThroughGraphic.draw(c);
                }
            }

            // Draw the hand graphic in All Apps
            if (mDrawIdentifier.equals(ALLAPPS_PORTRAIT) ||
                    mDrawIdentifier.equals(ALLAPPS_LANDSCAPE) ||
                    mDrawIdentifier.equals(ALLAPPS_LARGE)) {
                if (mHandTouchGraphic == null) {
                    mHandTouchGraphic = getResources().getDrawable(R.drawable.hand);
                }

                int offset = mAppIconSize / 4;
                mHandTouchGraphic.setBounds(cx + offset, cy + offset,
                        cx + mHandTouchGraphic.getIntrinsicWidth() + offset,
                        cy + mHandTouchGraphic.getIntrinsicHeight() + offset);
                mHandTouchGraphic.draw(c);
            }

            canvas.drawBitmap(b, 0, 0, null);
            c.setBitmap(null);
            b = null;
        }

        // Draw the rest of the cling
        super.dispatchDraw(canvas);
    }
}
