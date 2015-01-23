package me.justup.upme.fragments;

import android.app.Fragment;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.justup.upme.R;
import me.justup.upme.entity.PersonEntity;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class BriefcaseFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = makeLogTag(BriefcaseFragment.class);

    List<PersonEntity> listPerson;

    LinearLayout containerLayout;

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
        PersonEntity person9 = new PersonEntity(9, 3, "Андрей", "");
        PersonEntity person10 = new PersonEntity(10, 3, "Женя", "");
        PersonEntity person11 = new PersonEntity(11, 3, "Паша", "");
        PersonEntity person12 = new PersonEntity(12, 7, "Богдан", "");
        PersonEntity person13 = new PersonEntity(13, 7, "Глеб", "");
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

        PersonEntity personEntity = listPerson.get(0);
        RelativeLayout photoLayout = (RelativeLayout) view.findViewById(R.id.image1);
        TextView idObject = (TextView) photoLayout.findViewById(R.id.idObject);
        idObject.setText("" + personEntity.getId());
        TextView rowObject = (TextView) photoLayout.findViewById(R.id.row);
        rowObject.setText("0");
        TextView columnObject = (TextView) photoLayout.findViewById(R.id.column);
        columnObject.setText("0");

        TextView text = (TextView) view.findViewById(R.id.tv1);
        text.setText(personEntity.getName());
        photoLayout.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        RelativeLayout layoutPhoto;

        LOGD("TAG", "onClick");

        int idParent = Integer.parseInt(((TextView) v.findViewById(R.id.idObject)).getText().toString());
        int row = Integer.parseInt(((TextView) v.findViewById(R.id.row)).getText().toString());
        int column = Integer.parseInt(((TextView) v.findViewById(R.id.column)).getText().toString());

        List<PersonEntity> children = getChildrenOnParent(listPerson, 1);
        int countChildren = children.size();
        LOGD("TAG", "countChildren --- " + countChildren);

        // definition of the first cell to fill
        int x = (int) Math.round(countChildren / 2 - 0.1);
        LOGD("TAG", "X --- " + x);
        int startPosition = (x > column) ? 0 : column - x + 1;
        LOGD("TAG", "START --- " + startPosition);

        // line
        ViewGroup.LayoutParams layoutParamsImage = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //new GridLayout
        GridLayout gridLayout = new GridLayout(getActivity());

        for (int i = 0; i < startPosition; i++) {
            ImageView iv00 = new ImageView(getActivity());
            iv00.setImageResource(R.drawable.p00);
            iv00.setLayoutParams(layoutParamsImage);
            gridLayout.addView(iv00);
        }


        if (column == 0) {

            if (countChildren == 1) {
                ImageView iv13 = new ImageView(getActivity());
                iv13.setImageResource(R.drawable.p13);
                iv13.setLayoutParams(layoutParamsImage);
                gridLayout.addView(iv13);
            } else if (countChildren == 2) {
                ImageView iv123 = new ImageView(getActivity());
                iv123.setImageResource(R.drawable.p123);
                iv123.setLayoutParams(layoutParamsImage);
                gridLayout.addView(iv123);

                ImageView iv34 = new ImageView(getActivity());
                iv34.setImageResource(R.drawable.p34);
                iv34.setLayoutParams(layoutParamsImage);
                gridLayout.addView(iv34);

            } else if (countChildren > 2) {
                ImageView iv123 = new ImageView(getActivity());
                iv123.setImageResource(R.drawable.p123);
                iv123.setLayoutParams(layoutParamsImage);
                gridLayout.addView(iv123);

                for (int j = startPosition + 1; j < countChildren - 1; j++) {
                    ImageView iv234 = new ImageView(getActivity());
                    iv234.setImageResource(R.drawable.p234);
                    iv234.setLayoutParams(layoutParamsImage);
                    gridLayout.addView(iv234);
                }

                ImageView iv34 = new ImageView(getActivity());
                iv34.setImageResource(R.drawable.p34);
                iv34.setLayoutParams(layoutParamsImage);
                gridLayout.addView(iv34);
            }
        } else {

            if (countChildren == 1) {
                ImageView iv13 = new ImageView(getActivity());
                iv13.setImageResource(R.drawable.p13);
                iv13.setLayoutParams(layoutParamsImage);
                gridLayout.addView(iv13);
            } else if (countChildren == 2) {
                ImageView iv23 = new ImageView(getActivity());
                iv23.setImageResource(R.drawable.p23);
                iv23.setLayoutParams(layoutParamsImage);
                gridLayout.addView(iv23);

                ImageView iv134 = new ImageView(getActivity());
                iv134.setImageResource(R.drawable.p134);
                iv134.setLayoutParams(layoutParamsImage);
                gridLayout.addView(iv134);
            } else if (countChildren == 3) {
                ImageView iv23 = new ImageView(getActivity());
                iv23.setImageResource(R.drawable.p23);
                iv23.setLayoutParams(layoutParamsImage);
                gridLayout.addView(iv23);

                ImageView iv1234 = new ImageView(getActivity());
                iv1234.setImageResource(R.drawable.p1234);
                iv1234.setLayoutParams(layoutParamsImage);
                gridLayout.addView(iv1234);

                ImageView iv34 = new ImageView(getActivity());
                iv34.setImageResource(R.drawable.p34);
                iv34.setLayoutParams(layoutParamsImage);
                gridLayout.addView(iv34);

            } else if (countChildren > 3) {
                ImageView iv23 = new ImageView(getActivity());
                iv23.setImageResource(R.drawable.p23);
                iv23.setLayoutParams(layoutParamsImage);
                gridLayout.addView(iv23);

            for (int j = startPosition + 1; j < countChildren - 1; j++) {
                if (j != column) {
                    ImageView iv234 = new ImageView(getActivity());
                    iv234.setImageResource(R.drawable.p234);
                    iv234.setLayoutParams(layoutParamsImage);
                    gridLayout.addView(iv234);
                } else {
                    ImageView iv1234 = new ImageView(getActivity());
                    iv1234.setImageResource(R.drawable.p1234);
                    iv1234.setLayoutParams(layoutParamsImage);
                    gridLayout.addView(iv1234);
                }
            }

                ImageView iv34 = new ImageView(getActivity());
                iv34.setImageResource(R.drawable.p34);
                iv34.setLayoutParams(layoutParamsImage);
                gridLayout.addView(iv34);

            }

        }

        LayoutInflater inflater = LayoutInflater.from(v.getContext());

        for (int i = 0; i < countChildren; i++) {

            PersonEntity personEntity = children.get(i);

            layoutPhoto = (RelativeLayout) inflater.inflate(R.layout.item_briefcase, null, false);
            RelativeLayout photoLayout = (RelativeLayout) layoutPhoto.findViewById(R.id.image1);
            photoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), "Button Clicked", Toast.LENGTH_SHORT).show();
                }
            });
//            TextView idObject = (TextView) photoLayout.findViewById(R.id.idObject);
//            idObject.setText(""+personEntity.get());
//            TextView rowObject = (TextView) photoLayout.getChildAt(R.id.row);
//            rowObject.setText(""+personEntity.get());
//            TextView columnObject = (TextView) photoLayout.getChildAt(R.id.column);
//            columnObject.setText(""+personEntity.get());

            TextView text = (TextView) layoutPhoto.getChildAt(2);
            text.setText(personEntity.getName());

            if (i == 0) {
                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.columnSpec = GridLayout.spec(0);
                param.rowSpec = GridLayout.spec(row + 1);
                layoutPhoto.setLayoutParams(param);
            }

            gridLayout.addView(layoutPhoto);

        }

        containerLayout.addView(gridLayout);

//            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
//            param.columnSpec = GridLayout.spec(0);
////          param.height = LayoutParams.WRAP_CONTENT;
////          param.width = LayoutParams.WRAP_CONTENT;
////          param.rightMargin = 5;
////          param.topMargin = 5;
////          param.setGravity(Gravity.CENTER);
////          param.columnSpec = GridLayout.spec(c);
////          param.rowSpec = GridLayout.spec(r);
//
////          layoutPhoto.setLayoutParams(param);
//
//            RelativeLayout photoLayout = (RelativeLayout) layoutPhoto.getChildAt(0);
//            photoLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(getActivity(), "Button Clicked", Toast.LENGTH_SHORT).show();
//                }
//            });
//            TextView id = (TextView) photoLayout.getChildAt(1);
//            id.setText(""+personEntity.getId());
//            TextView row = (TextView) photoLayout.getChildAt(2);
//            row.setText(""+personEntity.getId());
//            TextView column = (TextView) photoLayout.getChildAt(3);
//            column.setText(""+personEntity.getId());
//
//            TextView text = (TextView) layoutPhoto.getChildAt(2);
//            text.setText(personEntity.getName());
//
//            matrix.addView(layoutPhoto);

//        }


    }
}
