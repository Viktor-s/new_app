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

import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class BriefcaseFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = makeLogTag(BriefcaseFragment.class);

    List<PersonEntity> listPerson;

    GridLayout matrix;

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

        matrix = (GridLayout) view.findViewById(R.id.matrix);

        ImageView imageView = (ImageView) view.findViewById(R.id.image1);
        imageView.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

//        (RelativeLayout)v.getParent()

        RelativeLayout layoutPhoto;

        LayoutInflater inflater = LayoutInflater.from(v.getContext());

        for (PersonEntity personEntity : getChildrenOnParent(listPerson, 1)) {

            layoutPhoto = (RelativeLayout) inflater.inflate(R.layout.item_briefcase, null, false);

            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.columnSpec = GridLayout.spec(0);
//          param.height = LayoutParams.WRAP_CONTENT;
//          param.width = LayoutParams.WRAP_CONTENT;
//          param.rightMargin = 5;
//          param.topMargin = 5;
//          param.setGravity(Gravity.CENTER);
//          param.columnSpec = GridLayout.spec(c);
//          param.rowSpec = GridLayout.spec(r);

//          layoutPhoto.setLayoutParams(param);

            ImageView photo = (ImageView) layoutPhoto.getChildAt(0);
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), "Button Clicked", Toast.LENGTH_SHORT).show();
                }
            });
            TextView text = (TextView) layoutPhoto.getChildAt(2);
            text.setText(personEntity.getName());

            matrix.addView(layoutPhoto);

        }


    }
}
