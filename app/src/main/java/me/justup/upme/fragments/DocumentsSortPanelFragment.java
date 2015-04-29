package me.justup.upme.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import me.justup.upme.R;

import static me.justup.upme.utils.LogUtils.makeLogTag;


public class DocumentsSortPanelFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = makeLogTag(DocumentsSortPanelFragment.class);

    private DocumentsFragment mParentFragment;

    private ImageView mColumnFavorite;
    private TextView mColumnType;
    private TextView mColumnName;
    private TextView mColumnSize;
    private TextView mColumnDate;
    private ImageView mColumnTablet;
    private ImageView mColumnCloud;

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

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sort_column_favorite:
                selectIconButton(mColumnFavorite, ICON_FAVORITE);
                break;

            case R.id.sort_column_type:
                selectTextButton(mColumnType);
                break;

            case R.id.sort_column_name:
                selectTextButton(mColumnName);
                break;

            case R.id.sort_column_size:
                selectTextButton(mColumnSize);
                break;

            case R.id.sort_column_date:
                selectTextButton(mColumnDate);
                break;

            case R.id.sort_column_tab:
                selectIconButton(mColumnTablet, ICON_TABLET);
                break;

            case R.id.sort_column_cloud:
                selectIconButton(mColumnCloud, ICON_CLOUD);
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

}
