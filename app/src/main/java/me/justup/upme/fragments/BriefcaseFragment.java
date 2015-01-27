package me.justup.upme.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.justup.upme.R;
import me.justup.upme.adapter.NewsFeedAdapter;
import me.justup.upme.entity.GetLoggedUserInfoQuery;
import me.justup.upme.entity.PersonEntity;
import me.justup.upme.utils.AppContext;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class BriefcaseFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = makeLogTag(BriefcaseFragment.class);

    List<PersonEntity> listPerson;

    LinearLayout containerLayout;

    private FrameLayout mNewsItemContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listPerson = new ArrayList<>();
        PersonEntity person1 = new PersonEntity(1, 0, "Ваня", "");
        PersonEntity person2 = new PersonEntity(2, 1, "Петя", "");
        PersonEntity person3 = new PersonEntity(3, 1, "Вася", "");
        PersonEntity person4 = new PersonEntity(4, 1, "Максим", "");
        PersonEntity person5 = new PersonEntity(5, 1, "Иван", "");
        PersonEntity person6 = new PersonEntity(6, 2, "Роман", "");
        PersonEntity person7 = new PersonEntity(7, 2, "Леха", "");
        PersonEntity person8 = new PersonEntity(8, 2, "Саша", "");
        PersonEntity person9 = new PersonEntity(9, 2, "Андрей", "");
        PersonEntity person10 = new PersonEntity(10, 3, "Женя", "");
        PersonEntity person11 = new PersonEntity(11, 3, "Паша", "");
        PersonEntity person12 = new PersonEntity(12, 3, "Богдан", "");
        PersonEntity person13 = new PersonEntity(13, 3, "Глеб", "");
        PersonEntity person14 = new PersonEntity(14, 7, "Глеб 1", "");
        PersonEntity person15 = new PersonEntity(15, 7, "Глеб 2", "");
        PersonEntity person16 = new PersonEntity(16, 5, "Глеб 3", "");
        PersonEntity person17 = new PersonEntity(17, 4, "Глеб 4", "");
        PersonEntity person18 = new PersonEntity(18, 4, "Глеб 5", "");
        PersonEntity person19 = new PersonEntity(19, 4, "Глеб 6", "");
        PersonEntity person20 = new PersonEntity(20, 4, "Глеб 7", "");
        PersonEntity person21 = new PersonEntity(21, 4, "Глеб 8", "");
        PersonEntity person22 = new PersonEntity(22, 4, "Глеб 9", "");
        PersonEntity person23 = new PersonEntity(23, 4, "Глеб 10", "");
        PersonEntity person24 = new PersonEntity(34, 8, "Глеб 11", "");
        PersonEntity person25 = new PersonEntity(25, 8, "Глеб 12", "");
        PersonEntity person26 = new PersonEntity(26, 8, "Глеб 13", "");
        PersonEntity person27 = new PersonEntity(27, 7, "Глеб 14", "");
        PersonEntity person28 = new PersonEntity(28, 7, "Глеб 15", "");
        PersonEntity person29 = new PersonEntity(29, 7, "Глеб 16", "");
        PersonEntity person30 = new PersonEntity(30, 7, "Глеб 17", "");
        PersonEntity person31 = new PersonEntity(31, 7, "Глеб 18", "");
        listPerson.add(person1);
        listPerson.add(person2);
        listPerson.add(person3);
        listPerson.add(person4);
        listPerson.add(person5);
        listPerson.add(person6);
        listPerson.add(person7);
        listPerson.add(person8);
        listPerson.add(person9);
        listPerson.add(person10);
        listPerson.add(person11);
        listPerson.add(person12);
        listPerson.add(person13);
        listPerson.add(person14);
        listPerson.add(person15);
        listPerson.add(person16);
        listPerson.add(person17);
        listPerson.add(person18);
        listPerson.add(person19);
        listPerson.add(person20);
        listPerson.add(person21);
        listPerson.add(person22);
        listPerson.add(person23);
        listPerson.add(person24);
        listPerson.add(person25);
        listPerson.add(person26);
        listPerson.add(person27);
        listPerson.add(person28);
        listPerson.add(person29);
        listPerson.add(person30);
        listPerson.add(person31);

    }

    public List<PersonEntity> getChildrenOnParent(List<PersonEntity> sourceList, int id) {
        List<PersonEntity> resultList = new ArrayList<>();
        for (PersonEntity person : sourceList) {
            if (person.getParentId() == id)
                resultList.add(person);
        }
        return resultList;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_briefcase, container, false);

        containerLayout = (LinearLayout) view.findViewById(R.id.containerLayout);

        RelativeLayout photoLayout = (RelativeLayout) view.findViewById(R.id.photo_main);
        containerLayout.addView(levelGenerate(photoLayout, listPerson));

        mNewsItemContainer = (FrameLayout) view.findViewById(R.id.briefcase_item_container_frameLayout);
        Button addUser = (Button) view.findViewById(R.id.add_new_user_button);
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation mFragmentSliderFadeIn = AnimationUtils.loadAnimation(AppContext.getAppContext(), R.anim.fragment_item_slide_fade_in);
                final FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                // Fragment fragment = UserFragment.newInstance(new GetLoggedUserInfoQuery());
                ft.replace(R.id.briefcase_item_container_frameLayout, new Fragment());
                ft.commit();
                mNewsItemContainer.startAnimation(mFragmentSliderFadeIn);
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
//        containerLayout.addView(levelGenerate(v, listPerson));


    }

    private ImageView createDirection(int resId) {
        ImageView resultView = new ImageView(getActivity());
        resultView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        resultView.setImageResource(resId);
        return resultView;

    }

    private GridLayout levelGenerate(final View v, final List<PersonEntity> listPerson) {

        int id = Integer.parseInt(((TextView) v.findViewById(R.id.idObject)).getText().toString());
        int row = Integer.parseInt(((TextView) v.findViewById(R.id.row)).getText().toString());
        int column = Integer.parseInt(((TextView) v.findViewById(R.id.column)).getText().toString());

        List<PersonEntity> children = getChildrenOnParent(listPerson, id);
        int countChildren = children.size();
        LOGD("TAG", "countChildren --- " + countChildren);

        // definition of the first cell to fill
        int x = (int) Math.round(countChildren / 2 - 0.1);
        LOGD("TAG", "X --- " + x);
        int startPosition = (x >= column) ? 0 : column - x;
        LOGD("TAG", "START POSITION --- " + startPosition);

        GridLayout gridLayout = new GridLayout(getActivity());

        for (int i = 0; i < startPosition; i++) {
            gridLayout.addView(createDirection(R.drawable.p00));
        }

        if (column == 0) {
            if (countChildren == 1) {
                gridLayout.addView(createDirection(R.drawable.p13));
            } else if (countChildren == 2) {
                gridLayout.addView(createDirection(R.drawable.p123));
                gridLayout.addView(createDirection(R.drawable.p34));
            } else if (countChildren > 2) {
                gridLayout.addView(createDirection(R.drawable.p123));
                for (int j = startPosition + 1; j < countChildren - 1; j++)
                    gridLayout.addView(createDirection(R.drawable.p234));
                gridLayout.addView(createDirection(R.drawable.p34));
            }
        } else {
            if (countChildren == 1) {
                gridLayout.addView(createDirection(R.drawable.p13));
            } else if (countChildren == 2) {
                gridLayout.addView(createDirection(R.drawable.p23));
                gridLayout.addView(createDirection(R.drawable.p134));
            } else if (countChildren == 3) {
                gridLayout.addView(createDirection(R.drawable.p23));
                gridLayout.addView(createDirection(R.drawable.p1234));
                gridLayout.addView(createDirection(R.drawable.p34));
            } else if (countChildren > 3) {
                gridLayout.addView(createDirection(R.drawable.p23));
                for (int j = 2; j < countChildren; j++) {
                    if (startPosition + j - 1 != column)
                        gridLayout.addView(createDirection(R.drawable.p234));
                    else
                        gridLayout.addView(createDirection(R.drawable.p1234));
                }
                gridLayout.addView(createDirection(R.drawable.p34));
            }
        }

        RelativeLayout layoutPhoto;
        LayoutInflater inflater = LayoutInflater.from(v.getContext());

        for (int i = 0; i < countChildren; i++) {
            PersonEntity personEntity = children.get(i);
            layoutPhoto = (RelativeLayout) inflater.inflate(R.layout.item_briefcase, null, false);
            RelativeLayout photoLayout = (RelativeLayout) layoutPhoto.findViewById(R.id.image_container);
            photoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int row = Integer.parseInt(((TextView) v.findViewById(R.id.row)).getText().toString());
                    LOGD("TAG", "With - " + (row + 1) + " to - '<' " + containerLayout.getChildCount());
                    for (int i = containerLayout.getChildCount()-1; i > row; i--) {
                        containerLayout.removeViewAt(i);
                    }
                    containerLayout.addView(levelGenerate(view, listPerson));
                }
            });

            TextView idObject = (TextView) photoLayout.getChildAt(1);
            idObject.setText(Integer.toString(personEntity.getId()));
            TextView rowObject = (TextView) photoLayout.getChildAt(2);
            rowObject.setText(Integer.toString(row + 1));
            TextView columnObject = (TextView) photoLayout.getChildAt(3);
            columnObject.setText(Integer.toString(startPosition + i));

            LOGD("TAG", "id - " + personEntity.getId() + "; row - " + (row + 1) + "; column - " + (startPosition + i));

            ImageView imageViewInfo = (ImageView) layoutPhoto.getChildAt(1);
            imageViewInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), "Вылазит боковое меню", Toast.LENGTH_SHORT).show();
                }
            });

            TextView text = (TextView) layoutPhoto.getChildAt(2);
            text.setText(personEntity.getName());

            if (i == 0) {
                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.columnSpec = GridLayout.spec(startPosition);
                param.rowSpec = GridLayout.spec(row + 1);
                layoutPhoto.setLayoutParams(param);
            }

            gridLayout.addView(layoutPhoto);

        }

        return gridLayout;
    }


}
