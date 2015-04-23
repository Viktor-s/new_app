package me.justup.upme.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.justup.upme.R;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.view.dashboard.CoolDragAndDropGridView;
import me.justup.upme.view.dashboard.DashboardAdapter;
import me.justup.upme.view.dashboard.ItemChangeSizeListener;
import me.justup.upme.view.dashboard.MoveHistory;
import me.justup.upme.view.dashboard.SimpleScrollingStrategy;
import me.justup.upme.view.dashboard.SpanVariableGridView;
import me.justup.upme.view.dashboard.TileItem;
import me.justup.upme.view.dashboard.TileUtils;

public class TiledMenuFragment extends Fragment implements CoolDragAndDropGridView.DragAndDropListener,
        SpanVariableGridView.OnItemClickListener,
        SpanVariableGridView.OnItemLongClickListener,
        ItemChangeSizeListener {

    private static final String TAG = TiledMenuFragment.class.getSimpleName();

    private DashboardAdapter mTileAdapter = null;
    private CoolDragAndDropGridView mCoolDragAndDropGridView = null;

    private List<TileItem> mTileItems = new LinkedList<TileItem>();
    private List<TileItem> mTileItemsCopy = new LinkedList<TileItem>();

    private ArrayList<MoveHistory> mMoveHistoryList = new ArrayList<MoveHistory>();

    private View mRedactedView = null;

    private Button mSettingView, mDeleteView = null;
    private Button mBtnSave, mBtnCancel = null;

    private int mLayoutWidth, mLayoutHeight;

    private Dialog mSettingDialog = null;
    private EditText mEdtTileName = null;

    private View mContentView = null;

    private AlertDialog.Builder mDeleteDialog = null;
    private AlertDialog mAlertDialog = null;

    // Instance
    public static TiledMenuFragment newInstance() {
        return new TiledMenuFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Link : http://stackoverflow.com/questions/11182180/understanding-fragments-setretaininstanceboolean
        setRetainInstance(false);

        mTileAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        int measuredWidth = 0;
        int measuredHeight = 0;
        WindowManager w = activity.getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            w.getDefaultDisplay().getSize(size);
            measuredWidth = size.x;
            measuredHeight = size.y;
        } else {
            Display d = w.getDefaultDisplay();
            measuredWidth = d.getWidth();
            measuredHeight = d.getHeight();
        }

        mLayoutHeight = measuredHeight;
        mLayoutWidth = measuredWidth;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = super.onCreateView(inflater, container, savedInstanceState);

        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.tiled_fragment_layout, container, false);
        }

        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init UI
        if (getActivity() != null) {
            initTileMenu();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(mAlertDialog!=null){
            mAlertDialog.dismiss();
        }

        if(mSettingDialog!=null){
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEdtTileName.getWindowToken(), 0);

            mSettingDialog.dismiss();
        }

        mBtnCancel.performClick();
    }

    // Dashboard Tile Menu (down)
    //                       ||
    //                       \/

    private void initTileMenu(){
        mCoolDragAndDropGridView = (CoolDragAndDropGridView) mContentView.findViewById(R.id.coolDragAndDropGridView);

        // Get from db
        DBAdapter.getInstance().openDatabase();
        List<TileItem> tileItemList = DBAdapter.getInstance().getListTile();

        if(tileItemList!=null && !tileItemList.isEmpty() && tileItemList.size()>0){
            mTileItems = new ArrayList<TileItem>(tileItemList);
        }else{
            // Set dummy item
            mTileItems.add(new TileItem(1, 2, "Быстрая оплата.", "", R.drawable.ic_main_browser, R.color.action_bar_background, false, false, false));
            mTileItems.add(new TileItem(1, 2, "Авиабилеты", "", R.drawable.ic_main_demo, R.color.avio_sales_green, false, false, false));
            mTileItems.add(new TileItem(1, 2, "Авиабилеты", "", R.drawable.ic_main_demo, R.color.action_bar_background, false, false, false));
            mTileItems.add(new TileItem(1, 2, "Лотерея", "Джекпот\n" + Html.fromHtml("<![CDATA[12378450]]>"), R.drawable.ic_person, R.color.user_fragment_red_money_color, false, false, false));
            mTileItems.add(new TileItem(1, 2, "Страхование жизни", "", R.drawable.ic_shadow_umbrella, R.color.action_bar_background, false, false, false));
            mTileItems.add(new TileItem(1, 2, "Страхование жизни", "", R.drawable.ic_shadow_umbrella, R.color.action_bar_background, false, false, false));
            mTileItems.add(new TileItem(1, 2, "Широкий виджет", "", R.drawable.ic_main_study, R.color.action_bar_background, false, false, false));
            mTileItems.add(new TileItem(1, 2, "Авиабилеты", "", 0, R.drawable.test_aero_back, false, false, true));
            mTileItems.add(new TileItem(1, 2, "Авиабилеты", "", R.drawable.ic_main_demo, R.color.avio_sales_yellow, false, false, false));
            mTileItems.add(new TileItem(1, 2, "Страхование жизни", "", R.drawable.ic_shadow_umbrella, R.color.action_bar_background, false, false, false));
            mTileItems.add(new TileItem(1, 2, "Страхование жизни", "", R.drawable.ic_shadow_umbrella, R.color.action_bar_background, false, false, false));
            mTileItems.add(new TileItem(1, 2, "Страхование жизни", "", R.drawable.ic_shadow_umbrella, R.color.action_bar_background, false, false, false));
            mTileItems.add(new TileItem(1, 2, "Страхование жизни", "", R.drawable.test_ic_circle_image, R.color.action_bar_background, false, false, false));

            // Add Item Button
            mTileItems.add(new TileItem(1, 2, "+", "", R.drawable.ic_shadow_umbrella, R.color.action_bar_background, true, false, false));

            // Sort Item
            mTileItems = TileUtils.invert(mTileItems, 3);
        }

        mTileAdapter = new DashboardAdapter(getActivity().getApplicationContext(), mTileItems);
        mTileAdapter.setItemChangeSizeListener(this);

        mCoolDragAndDropGridView.setAdapter(mTileAdapter);
        mCoolDragAndDropGridView.setScrollingStrategy(new SimpleScrollingStrategy((ScrollView) mContentView.findViewById(R.id.scrollView)));
        mCoolDragAndDropGridView.setDragAndDropListener(this);
        mCoolDragAndDropGridView.setOnItemLongClickListener(this);
        mCoolDragAndDropGridView.setOnItemClickListener(this);

        Animation anim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fly_out_from_center);
        mCoolDragAndDropGridView.setAnimation(anim);
        anim.start();

        mBtnSave = (Button) getActivity().findViewById(R.id.btn_save);
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        mBtnCancel = (Button) getActivity().findViewById(R.id.btn_cancel);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<mTileItemsCopy.size();i++){
                    TileItem itemTo = mTileItemsCopy.get(i);

                    if(itemTo.getTitle().equals("+")){
                        mTileItems.set(i, new TileItem(itemTo.getHSpans(), itemTo.getWSpans(), itemTo.getTitle(), itemTo.getSecondTitle(), itemTo.getResId(), itemTo.getBackground(), true, false, itemTo.isImage()));
                    }else {
                        mTileItems.set(i, new TileItem(itemTo.getHSpans(), itemTo.getWSpans(), itemTo.getTitle(), itemTo.getSecondTitle(), itemTo.getResId(), itemTo.getBackground(), false, false, itemTo.isImage()));
                    }
                }

                mTileAdapter.notifyDataSetChanged();
                mTileItemsCopy.clear();

                getActivity().findViewById(R.id.delete_setting_menu).setVisibility(View.INVISIBLE);

                getActivity().findViewById(R.id.btn_save).setVisibility(View.INVISIBLE);
                getActivity().findViewById(R.id.save_cancel_menu).setVisibility(View.GONE);

                mRedactedView = null;
            }
        });

        mSettingView = (Button)getActivity().findViewById(R.id.btn_setting);
        mDeleteView = (Button)getActivity().findViewById(R.id.btn_delete);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
        if(mRedactedView==null || view.findViewById(R.id.img_btn_change_size).getVisibility()==View.VISIBLE){
            if(!((TextView)view.findViewById(R.id.txtTile)).getText().toString().equals("+")) {
                mRedactedView = view;

                mRedactedView.setBackground(getActivity().getApplicationContext().getResources().getDrawable(R.drawable.card_highlighted));

                mRedactedView.findViewById(R.id.img_btn_change_size).setVisibility(View.VISIBLE);
                YoYo.with(Techniques.Pulse)
                        .duration(700)
                        .playOn(mRedactedView.findViewById(R.id.img_btn_change_size));

                mCoolDragAndDropGridView.startDragAndDrop();
            }
        }

        return false;
    }

    @Override
    public void OnChangeSizeClick(int id) {
        TileItem item = mTileItems.get(id);
        if(!item.isAddItemButton()) {
            int width = item.getWSpans();
            int height = item.getHSpans();

            if (width == 2 && height == 1) {
                mTileItems.set(id, new TileItem(1, 4, item.getTitle(), item.getSecondTitle(), item.getResId(), item.getBackground(), false, true, item.isImage()));
            }else {
                mTileItems.set(id, new TileItem(1, 2, item.getTitle(), item.getSecondTitle(), item.getResId(), item.getBackground(), false, true, item.isImage()));
            }

            getActivity().findViewById(R.id.btn_save).setVisibility(View.VISIBLE);
            YoYo.with(Techniques.StandUp)
                    .duration(700)
                    .playOn(getActivity().findViewById(R.id.btn_save));
        }

        mTileAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mRedactedView==null) {
            TileItem item = mTileItems.get((int) id);
            if (item.isAddItemButton()) {
                int size = mTileItems.size();

                // Add new item
                mTileItems.set((int) id, new TileItem(1, 2, String.valueOf(size - 1), "Пример", R.drawable.ic_shadow_umbrella, R.color.action_bar_background, false, false, false));
                mTileItems.add(new TileItem(1, 2, "+", "", R.drawable.ic_shadow_umbrella, R.color.action_bar_background, true, false, false));

                mTileAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDrag(int x, int y, final int rawX, final int rawY) {

        y = (mLayoutWidth + TileUtils.dpToPx((int)getActivity().getApplicationContext().getResources().getDimension(R.dimen.tile_menu_margin_left), getActivity().getApplicationContext())) - rawX;
        x = (mLayoutHeight + 100) - rawY;

        // Setting (left, right, top, bottom)
        if (!TileUtils.isPointWithin(x, y, (mLayoutHeight - (getActivity().findViewById(R.id.delete_setting_menu).getBottom() + 100)), (mLayoutHeight - mSettingView.getTop()), (mLayoutWidth - mSettingView.getLeft()), (mLayoutWidth - mSettingView.getRight()))) {
            mSettingView.setBackgroundColor(getActivity().getApplicationContext().getResources().getColor(R.color.black));
        }

        if (TileUtils.isPointWithin(x, y, (mLayoutHeight - (getActivity().findViewById(R.id.delete_setting_menu).getBottom() + 100)), (mLayoutHeight - mSettingView.getTop()), (mLayoutWidth - mSettingView.getLeft()), (mLayoutWidth - mSettingView.getRight()))) {
            mSettingView.setBackgroundColor(getActivity().getApplicationContext().getResources().getColor(R.color.avio_sales_green));
        }

        // Delete
        if (!TileUtils.isPointWithin(x, y, (mLayoutHeight - (getActivity().findViewById(R.id.delete_setting_menu).getBottom() + 100)), (mLayoutHeight - mDeleteView.getTop()), (mLayoutWidth - mDeleteView.getLeft()), (mLayoutWidth - mDeleteView.getRight()))) {
            mDeleteView.setBackgroundColor(getActivity().getApplicationContext().getResources().getColor(R.color.black));
        }

        if (TileUtils.isPointWithin(x, y, (mLayoutHeight - (getActivity().findViewById(R.id.delete_setting_menu).getBottom() + 100)), (mLayoutHeight - mDeleteView.getTop()), (mLayoutWidth - mDeleteView.getLeft()), (mLayoutWidth - mDeleteView.getRight()))) {
            mDeleteView.setBackgroundColor(getActivity().getApplicationContext().getResources().getColor(R.color.user_fragment_red_money_color));
        }
    }

    @Override
    public void onDragItem(int from) {
        // Get instance of Vibrator from current Context
        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        // Output yes if can vibrate, no otherwise
        if (v.hasVibrator()) {
            // Vibrate for 100 milliseconds
            v.vibrate(100);
        } else {
            Log.e(TAG, "Not has vibro");
        }

        // Get reserve copy
        if(getActivity().findViewById(R.id.btn_save).getVisibility()==View.INVISIBLE){
            mTileItemsCopy = new ArrayList<TileItem>(mTileItems);
        }

        // Show menu
        if(!mTileItems.get(from).isAddItemButton() && getActivity().findViewById(R.id.delete_setting_menu).getVisibility()!=View.VISIBLE) {
            getActivity().findViewById(R.id.delete_setting_menu).setVisibility(View.VISIBLE);
            YoYo.with(Techniques.StandUp)
                    .duration(500)
                    .playOn(getActivity().findViewById(R.id.delete_setting_menu));
        }

        if(getActivity().findViewById(R.id.save_cancel_menu).getVisibility()!=View.VISIBLE) {
            getActivity().findViewById(R.id.save_cancel_menu).setVisibility(View.VISIBLE);
            YoYo.with(Techniques.StandUp)
                    .duration(300)
                    .playOn(getActivity().findViewById(R.id.save_cancel_menu));
        }

        if(getActivity().findViewById(R.id.btn_cancel).getVisibility()!=View.VISIBLE) {
            getActivity().findViewById(R.id.btn_cancel).setVisibility(View.VISIBLE);
            YoYo.with(Techniques.StandUp)
                    .duration(500)
                    .playOn(getActivity().findViewById(R.id.btn_cancel));
        }

    }

    @Override
    public void onDraggingItem(int from, int to) {
        if (from != to) {
            mMoveHistoryList.add(new MoveHistory(from, to));
        }
    }

    @Override
    public void onDropItem(int from, final int to) {

        if (from != to) {

            for (int i=0;i< mMoveHistoryList.size();i++) {
                MoveHistory moveHistory = mMoveHistoryList.get(i);

                TileItem itemFrom = mTileItems.get(moveHistory.getFromP());
                TileItem itemTo = mTileItems.get(moveHistory.getToP());

                // Set Old Item to New Case
                if(itemFrom.getTitle().equals("+")) {
                    mTileItems.set(moveHistory.getToP(), new TileItem(itemFrom.getHSpans(), itemFrom.getWSpans(), itemFrom.getTitle(), itemFrom.getSecondTitle(), itemFrom.getResId(), itemFrom.getBackground(), true, true, itemFrom.isImage()));
                }else{
                    mTileItems.set(moveHistory.getToP(), new TileItem(itemFrom.getHSpans(), itemFrom.getWSpans(), itemFrom.getTitle(), itemFrom.getSecondTitle(), itemFrom.getResId(), itemFrom.getBackground(), false, true, itemFrom.isImage()));
                }

                // Set New Item to Old Case
                if(itemTo.getTitle().equals("+")) {
                    mTileItems.set(moveHistory.getFromP(), new TileItem(itemTo.getHSpans(), itemTo.getWSpans(), itemTo.getTitle(), itemTo.getSecondTitle(), itemTo.getResId(), itemTo.getBackground(), true, false, itemTo.isImage()));
                }else{
                    mTileItems.set(moveHistory.getFromP(), new TileItem(itemTo.getHSpans(), itemTo.getWSpans(), itemTo.getTitle(), itemTo.getSecondTitle(), itemTo.getResId(), itemTo.getBackground(), false, false, itemTo.isImage()));
                }

                mTileAdapter.notifyDataSetChanged();
            }

            if(getActivity().findViewById(R.id.btn_save).getVisibility()!=View.VISIBLE) {
                getActivity().findViewById(R.id.btn_save).setVisibility(View.VISIBLE);
                YoYo.with(Techniques.StandUp)
                        .duration(500)
                        .playOn(getActivity().findViewById(R.id.btn_save));
            }

            mMoveHistoryList.clear();
        }

        if(((ColorDrawable) mSettingView.getBackground()).getColor()==getActivity().getApplicationContext().getResources().getColor(R.color.avio_sales_green)){
            mSettingView.setBackgroundColor(getActivity().getApplicationContext().getResources().getColor(R.color.black));

            mSettingDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent);
            mSettingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mSettingDialog.setCancelable(true);
            mSettingDialog.setContentView(R.layout.chenge_tile_layout);

            mEdtTileName = (EditText) mSettingDialog.findViewById(R.id.edt_tile_name);
            Button btnSave = (Button) mSettingDialog.findViewById(R.id.btn_save);
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TileItem tileItem = mTileItems.get(to);
                    String title = tileItem.getTitle();

                    if(mEdtTileName.getText().toString()!=null && !mEdtTileName.getText().toString().equals("")){
                        title = mEdtTileName.getText().toString();
                    }

                    if (tileItem.getTitle().equals("+")) {
                        mTileItems.set(to, new TileItem(tileItem.getHSpans(), tileItem.getWSpans(), title, tileItem.getSecondTitle(), tileItem.getResId(), tileItem.getBackground(), true, true, tileItem.isImage()));
                    } else {
                        mTileItems.set(to, new TileItem(tileItem.getHSpans(), tileItem.getWSpans(), title, tileItem.getSecondTitle(), tileItem.getResId(), tileItem.getBackground(), false, true, tileItem.isImage()));
                    }

                    if(getActivity().findViewById(R.id.btn_save).getVisibility()!=View.VISIBLE) {
                        getActivity().findViewById(R.id.btn_save).setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.StandUp)
                                .duration(500)
                                .playOn(getActivity().findViewById(R.id.btn_save));
                    }

                    mTileAdapter.notifyDataSetChanged();

                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEdtTileName.getWindowToken(), 0);

                    mSettingDialog.dismiss();
                }
            });

            Button btnCancel = (Button) mSettingDialog.findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSettingDialog.dismiss();
                }
            });

            mSettingDialog.show();
        }else if(((ColorDrawable) mDeleteView.getBackground()).getColor()==getActivity().getApplicationContext().getResources().getColor(R.color.user_fragment_red_money_color)){
            mDeleteView.setBackgroundColor(getActivity().getApplicationContext().getResources().getColor(R.color.black));

            showDeleteDialog(mTileItems.get(to));
        }

    }

    @Override
    public boolean isDragAndDropEnabled(int position) {
        return true;
    }

    private void save(){
        mTileItemsCopy.clear();

        for(int i=0;i< mTileItems.size();i++){
            TileItem itemTo = mTileItems.get(i);

            if(itemTo.getTitle().equals("+")){
                mTileItems.set(i, new TileItem(itemTo.getHSpans(), itemTo.getWSpans(), itemTo.getTitle(), itemTo.getSecondTitle(), itemTo.getResId(), itemTo.getBackground(), true, false, itemTo.isImage()));
            }else {
                mTileItems.set(i, new TileItem(itemTo.getHSpans(), itemTo.getWSpans(), itemTo.getTitle(), itemTo.getSecondTitle(), itemTo.getResId(), itemTo.getBackground(), false, false, itemTo.isImage()));
            }
        }

        mTileAdapter.notifyDataSetChanged();

        getActivity().findViewById(R.id.delete_setting_menu).setVisibility(View.INVISIBLE);

        getActivity().findViewById(R.id.btn_save).setVisibility(View.INVISIBLE);
        getActivity().findViewById(R.id.save_cancel_menu).setVisibility(View.GONE);

        mRedactedView = null;

        // Save Tile Menu in DB
        DBAdapter.getInstance().openDatabase();
        DBAdapter.getInstance().saveTileMenu(mTileItems);
        DBAdapter.getInstance().closeDatabase();
    }

    private void showDeleteDialog(final TileItem item){
        String title = "Удаление";
        String button1String = "Удалить";
        String button2String = "Отмена";

        mDeleteDialog = new AlertDialog.Builder(getActivity());
        mDeleteDialog.setTitle(title);  // заголовок
        mDeleteDialog.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                mTileAdapter.removeItem(item);
                mTileAdapter.notifyDataSetChanged();

                save();
            }
        });

        mDeleteDialog.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

            }
        });

        mDeleteDialog.setCancelable(true);
        mDeleteDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {

            }
        });

        mAlertDialog = mDeleteDialog.show();
    }

}
