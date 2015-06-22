package me.justup.upme.fragments;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import me.justup.upme.JustUpApplication;
import me.justup.upme.R;
import me.justup.upme.entity.FileEntity;
import me.justup.upme.utils.ExplorerUtils;
import me.justup.upme.view.dashboard.TileUtils;

import static me.justup.upme.utils.LogUtils.LOGE;

public class DocumentsSortPanelFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = DocumentsSortPanelFragment.class.getSimpleName();

    private DocumentsFragment mParentFragment = null;

    private ImageView mColumnFavorite = null;
    private TextView mColumnType = null;
    private TextView mColumnName = null;
    private TextView mColumnSize = null;
    private TextView mColumnDate = null;
    private ImageView mColumnTablet = null;
    private ImageView mColumnCloud = null;

    private ArrayList<TextView> mTextButtonArray = new ArrayList<>();
    private boolean isDesc;

    private static final int ICON_FAVORITE = 1;
    private static final int ICON_TABLET = 2;
    private static final int ICON_CLOUD = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_documents_sort_panel, container, false);

        mParentFragment = (DocumentsFragment) getParentFragment();

        mColumnFavorite = (ImageView) view.findViewById(R.id.sort_column_favorite);
        mColumnType = (TextView) view.findViewById(R.id.sort_column_type);
        mColumnName = (TextView) view.findViewById(R.id.sort_column_name);
        mColumnSize = (TextView) view.findViewById(R.id.sort_column_size);
        mColumnDate = (TextView) view.findViewById(R.id.sort_column_date);
        mColumnTablet = (ImageView) view.findViewById(R.id.sort_column_tab);
        mColumnCloud = (ImageView) view.findViewById(R.id.sort_column_cloud);

        mColumnFavorite.setOnClickListener(this);
        mColumnType.setOnClickListener(this);
        mColumnName.setOnClickListener(this);
        mColumnSize.setOnClickListener(this);
        mColumnDate.setOnClickListener(this);
        mColumnTablet.setOnClickListener(this);
        mColumnCloud.setOnClickListener(this);

        if (mTextButtonArray != null) {
            mTextButtonArray.add(mColumnType);
            mTextButtonArray.add(mColumnName);
            mTextButtonArray.add(mColumnSize);
            mTextButtonArray.add(mColumnDate);
        }

        initSortPanel(mParentFragment.initialSortPanelType(), mParentFragment.initialSortPanelIsDesc());

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sort_column_favorite:
                selectIconButton(mColumnFavorite, ICON_FAVORITE);
                FileSort.sort(mParentFragment.getFileArray(), FileSort.SORT_BY_FAVOR, isDesc);
                mParentFragment.updateFileExplorer();
                break;

            case R.id.sort_column_type:
                selectTextButton(mColumnType);
                FileSort.sort(mParentFragment.getFileArray(), FileSort.SORT_BY_TYPE, isDesc);
                mParentFragment.updateFileExplorer();
                break;

            case R.id.sort_column_name:
                selectTextButton(mColumnName);
                FileSort.sort(mParentFragment.getFileArray(), FileSort.SORT_BY_NAME, isDesc);
                mParentFragment.updateFileExplorer();
                break;

            case R.id.sort_column_size:
                selectTextButton(mColumnSize);
                FileSort.sort(mParentFragment.getFileArray(), FileSort.SORT_BY_SIZE, isDesc);
                mParentFragment.updateFileExplorer();
                break;

            case R.id.sort_column_date:
                selectTextButton(mColumnDate);
                FileSort.sort(mParentFragment.getFileArray(), FileSort.SORT_BY_DATE, isDesc);
                mParentFragment.updateFileExplorer();
                break;

            case R.id.sort_column_tab:
                selectIconButton(mColumnTablet, ICON_TABLET);
                FileSort.sort(mParentFragment.getFileArray(), FileSort.SORT_BY_IN_TABLET, isDesc);
                mParentFragment.updateFileExplorer();
                break;

            case R.id.sort_column_cloud:
                selectIconButton(mColumnCloud, ICON_CLOUD);
                FileSort.sort(mParentFragment.getFileArray(), FileSort.SORT_BY_IN_CLOUD, isDesc);
                mParentFragment.updateFileExplorer();
                break;

            default:
                break;
        }
    }

    private void selectTextButton(TextView view) {
        resetAllButtons();

        if (!isDesc) {
            view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sort_arrow_desc, 0, 0, 0);
            isDesc = true;
        } else {
            view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sort_arrow_asc, 0, 0, 0);
            isDesc = false;
        }
    }

    private void selectIconButton(ImageView view, int imageNumber) {
        resetAllButtons();

        if (!isDesc) {
            if (imageNumber == ICON_FAVORITE) {
                view.setImageResource(R.drawable.ic_file_star);
            } else if (imageNumber == ICON_TABLET) {
                view.setImageResource(R.drawable.ic_file_tab);
            } else {
                view.setImageResource(R.drawable.ic_file_cloud);
            }

            isDesc = true;
        } else {
            isDesc = false;
        }
    }

    private void resetAllButtons() {
        if (mTextButtonArray != null) {
            for (TextView button : mTextButtonArray) {
                button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
        }

        mColumnFavorite.setImageResource(R.drawable.ic_file_star_gray);
        mColumnTablet.setImageResource(R.drawable.ic_file_tab_gray);
        mColumnCloud.setImageResource(R.drawable.ic_file_cloud_gray);
    }

    private void initSortPanel(int sortType, boolean isDesc) {
        switch (sortType) {
            case FileSort.SORT_BY_TYPE:
                initTextButton(mColumnType, isDesc);
                break;

            case FileSort.SORT_BY_NAME:
                initTextButton(mColumnName, isDesc);
                break;

            case FileSort.SORT_BY_SIZE:
                initTextButton(mColumnSize, isDesc);
                break;

            case FileSort.SORT_BY_DATE:
                initTextButton(mColumnDate, isDesc);
                break;

            default:
                break;
        }
    }

    private void initTextButton(TextView view, boolean isDesc) {
        if (isDesc) {
            view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sort_arrow_desc, 0, 0, 0);
        } else {
            view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sort_arrow_asc, 0, 0, 0);
        }
    }


    public static class FileSort {
        public static final int SORT_BY_FAVOR = 1;
        public static final int SORT_BY_TYPE = 2;
        public static final int SORT_BY_NAME = 3;
        public static final int SORT_BY_SIZE = 4;
        public static final int SORT_BY_DATE = 5;
        public static final int SORT_BY_IN_TABLET = 6;
        public static final int SORT_BY_IN_CLOUD = 7;

        public static void sort(final ArrayList<FileEntity> array, final int sortType, final boolean isDesk) {
            JustUpApplication.getApplication().getAppPreferences().setFileSortType(sortType);
            JustUpApplication.getApplication().getAppPreferences().setDescFileSort(isDesk);

            switch (sortType) {
                case SORT_BY_FAVOR:
                    compare(array, ExplorerUtils.COMPARE_BY_FAVOR_ASC, ExplorerUtils.COMPARE_BY_FAVOR_DESC, isDesk);
                    break;

                case SORT_BY_TYPE:
                    compare(array, ExplorerUtils.COMPARE_BY_TYPE_ASC, ExplorerUtils.COMPARE_BY_TYPE_DESC, isDesk);
                    break;

                case SORT_BY_NAME:
                    compare(array, ExplorerUtils.COMPARE_BY_NAME_ASC, ExplorerUtils.COMPARE_BY_NAME_DESC, isDesk);
                    break;

                case SORT_BY_SIZE:
                    compare(array, ExplorerUtils.COMPARE_BY_SIZE_ASC, ExplorerUtils.COMPARE_BY_SIZE_DESC, isDesk);
                    break;

                case SORT_BY_DATE:
                    compare(array, ExplorerUtils.COMPARE_BY_DATE_ASC, ExplorerUtils.COMPARE_BY_DATE_DESC, isDesk);
                    break;

                case SORT_BY_IN_TABLET:
                    compare(array, ExplorerUtils.COMPARE_BY_IN_TAB_ASC, ExplorerUtils.COMPARE_BY_IN_TAB_DESC, isDesk);
                    break;

                case SORT_BY_IN_CLOUD:
                    compare(array, ExplorerUtils.COMPARE_BY_IN_CLOUD_ASC, ExplorerUtils.COMPARE_BY_IN_CLOUD_DESC, isDesk);
                    break;

                default:
                    break;
            }
        }

        private static void compare(final ArrayList<FileEntity> array, final Comparator<FileEntity> compareByAsc,
                                    final Comparator<FileEntity> compareByDesc, final boolean isDesk) {
            if (!isDesk)
                Collections.sort(array, compareByAsc);
            else
                Collections.sort(array, compareByDesc);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        try {
            super.onConfigurationChanged(newConfig);

            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) mColumnSize.getLayoutParams();
                llp.setMargins(0, 0, -1*TileUtils.dpToPx(5, getActivity().getApplicationContext()), 0);
                mColumnSize.setLayoutParams(llp);
                mColumnSize.invalidate();
            } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) mColumnSize.getLayoutParams();
                llp.setMargins(0, 0, TileUtils.dpToPx(130, getActivity().getApplicationContext()), 0);
                mColumnSize.setLayoutParams(llp);
                mColumnSize.invalidate();
            }
        } catch (Exception e) {
            LOGE(TAG, e.getMessage());
        }
    }
}
