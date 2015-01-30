package me.justup.upme.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.justup.upme.R;
import me.justup.upme.interfaces.OnCloseFragment;

import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class UserOrderingFragment extends Fragment {
    private static final String TAG = makeLogTag(UserOrderingFragment.class);

    private OnCloseFragment mOnCloseOrderingFragmentCallback;
    private LinearLayout mDaysPart;

    private static final String INDEX = "index";
    private int id;


    public static UserOrderingFragment newInstance(int index) {
        UserOrderingFragment f = new UserOrderingFragment();

        Bundle args = new Bundle();
        args.putInt(INDEX, index);
        f.setArguments(args);

        return f;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mOnCloseOrderingFragmentCallback = (OnCloseFragment) getParentFragment();
        } catch (ClassCastException e) {
            LOGE(TAG, "Must implement OnCloseFragment", e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id = getArguments().getInt(INDEX, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_ordering, container, false);

        Button mFragmentCloseButton = (Button) v.findViewById(R.id.order_fragment_close_button);
        mFragmentCloseButton.setOnClickListener(new OnCloseOrderListener());

        mDaysPart = (LinearLayout) v.findViewById(R.id.days_orders_layout);


        // for test
        // ((TextView) v.findViewById(R.id.order_fragment_textView)).setText(String.valueOf(id));

        addDateToList("Сегодня", "$ 5000", new String[]{"Кредит наличными (без залога)", "Кредит наличными (под залог)"});
        addDateToList("10 января 2015", "$ 4000", new String[]{"Рисковое страхование", "Новый участник\nАлександр Петрович"});
        addDateToList("1 января 2015", "$ 8000", new String[]{"Кредит наличными (без залога)", "Кредит наличными (под залог)", "Новый участник\nИван Петрович"});


        return v;
    }

    private void addDateToList(final String date, String amount, String[] itemsTitle) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dateItem = inflater.inflate(R.layout.item_ordering_date, null);

        TextView mDate = (TextView) dateItem.findViewById(R.id.ordering_date_textView);
        mDate.setText(date);

        TextView mDateAmount = (TextView) dateItem.findViewById(R.id.ordering_summ_textView);
        mDateAmount.setText(amount);

        LinearLayout mItemBlock = (LinearLayout) dateItem.findViewById(R.id.order_inner_items_layout);

        for (String anItemsTitle : itemsTitle) {
            addOrderItemsToList(anItemsTitle, mItemBlock);
        }

        mDaysPart.addView(dateItem);
    }

    private void addOrderItemsToList(final String itemTitle, LinearLayout layout) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dateItem = inflater.inflate(R.layout.item_ordering_orders, null);

        TextView mDate = (TextView) dateItem.findViewById(R.id.ordering_item_title_textView);
        mDate.setText(itemTitle);

        layout.addView(dateItem);
    }

    private class OnCloseOrderListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mOnCloseOrderingFragmentCallback.onCloseFragment();
            // getParentFragment().getChildFragmentManager().beginTransaction().remove(UserOrderingFragment.this).commit();
        }
    }

}
