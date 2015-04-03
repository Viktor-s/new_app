package me.justup.upme.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import me.justup.upme.MainActivity;
import me.justup.upme.R;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.entity.GetAccountPanelInfoQuery;
import me.justup.upme.entity.PersonBriefcaseEntity;
import me.justup.upme.entity.ReferalAddQuery;
import me.justup.upme.http.HttpIntentService;
import me.justup.upme.utils.AppPreferences;
import me.justup.upme.utils.CircularImageView;

import static me.justup.upme.db.DBHelper.MAIL_CONTACT_IMG;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_NAME;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_PARENT_ID;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_SERVER_ID;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_TABLE_NAME;
import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class BriefcaseFragment extends Fragment {
    private static final String TAG = makeLogTag(BriefcaseFragment.class);
    private List<PersonBriefcaseEntity> listPerson;
    private LinearLayout containerLayout;
    private RelativeLayout photoLayout;
    private Button mCloseUserFragmentButton;
    private BroadcastReceiver receiver;
    private String selectQuery;
    private TextView mUserContactsCountTextView;
    private FrameLayout mUserContainer;
    private Animation mFragmentSliderFadeIn;
    private Animation mFragmentSliderOut;
    private SQLiteDatabase database;
    private Cursor cursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (database != null) {
            if (!database.isOpen()) {
                database = DBAdapter.getInstance().openDatabase();
            }
        } else {
            database = DBAdapter.getInstance().openDatabase();
        }
        selectQuery = "SELECT * FROM " + MAIL_CONTACT_TABLE_NAME;
        cursor = database.rawQuery(selectQuery, null);
    }

    public List<PersonBriefcaseEntity> getChildrenOnParent(List<PersonBriefcaseEntity> sourceList, int id) {
        List<PersonBriefcaseEntity> resultList = new ArrayList<>();
        for (PersonBriefcaseEntity person : sourceList) {
            if (person.getParentId() == id)
                resultList.add(person);
        }
        return resultList;
    }

    @Override
    public void onPause() {
        super.onPause();
        DBAdapter.getInstance().closeDatabase();
        LocalBroadcastManager.getInstance(BriefcaseFragment.this.getActivity()).unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LOGI(TAG, "onReceive briefcase");
                updatePersonsList();
                containerLayout.removeAllViews();
                containerLayout.addView(levelGenerate(photoLayout, listPerson));
            }
        };
        LocalBroadcastManager.getInstance(BriefcaseFragment.this.getActivity())
                .registerReceiver(receiver, new IntentFilter(DBAdapter.MAIL_SQL_BROADCAST_INTENT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_briefcase, container, false);
        mFragmentSliderFadeIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fragment_item_slide_fade_in);

        mFragmentSliderOut = AnimationUtils.loadAnimation(getActivity(), R.anim.order_slider_out);
        mFragmentSliderOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mCloseUserFragmentButton.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Fragment fragment = getChildFragmentManager().findFragmentByTag("UserFragmentBriefcase");
                if (fragment != null) {

                    getChildFragmentManager().beginTransaction().remove(fragment).commit();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        TextView mObjectIdTextView = (TextView) view.findViewById(R.id.briefcase_fragment_idObject);
        TextView mUserNameTextView = (TextView) view.findViewById(R.id.briefcase_fragment_user_name);
        mUserContactsCountTextView = (TextView) view.findViewById(R.id.briefcase_fragment_user_contacts_count);
        mUserContainer = (FrameLayout) view.findViewById(R.id.briefcase_user_info_container_frameLayout);
        CircularImageView mUserImageImageView = (CircularImageView) view.findViewById(R.id.briefcase_fragment_user_photo);
        mObjectIdTextView.setText("" + new AppPreferences(getActivity().getApplicationContext()).getUserId());
        mUserNameTextView.setText(new AppPreferences(getActivity().getApplicationContext()).getUserName());
        updatePersonsList();
        containerLayout = (LinearLayout) view.findViewById(R.id.containerLayout);
        photoLayout = (RelativeLayout) view.findViewById(R.id.photo_main);
        containerLayout.addView(levelGenerate(photoLayout, listPerson));
        FrameLayout mNewsItemContainer = (FrameLayout) view.findViewById(R.id.briefcase_item_container_frameLayout);
        mCloseUserFragmentButton = (Button) view.findViewById(R.id.briefcase_close_button);
        mCloseUserFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserContainer.startAnimation(mFragmentSliderOut);
            }
        });

        final LinearLayout addUserContainer = (LinearLayout) view.findViewById(R.id.add_user_container);
        addUserContainer.setVisibility(View.GONE);
        final TextView nameField = (TextView) view.findViewById(R.id.new_user_name);
        final TextView surnameField = (TextView) view.findViewById(R.id.new_user_surname);
        final TextView phoneField = (TextView) view.findViewById(R.id.new_user_phone);
        Button addNewUserButton = (Button) view.findViewById(R.id.add_new_user_button);
        addNewUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addUserContainer.getVisibility() == View.GONE) {
                    addUserContainer.setVisibility(View.VISIBLE);
                    nameField.setText("");
                    surnameField.setText("");
                    phoneField.setText("");

                } else
                    addUserContainer.setVisibility(View.GONE);
            }
        });

        Button sendCreateUserButton = (Button) view.findViewById(R.id.new_user_send_button);
        sendCreateUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUserContainer.setVisibility(View.GONE);
                // PersonEntity newPerson = new PersonEntity(100, 1, nameField.getText().toString(), "");  // 100 - generate Id
                // listPerson.add(newPerson);
                // containerLayout.removeAllViews();
                // containerLayout.addView(levelGenerate(photoLayout, listPerson));
                if (nameField.getText().toString().length() > 1 && phoneField.getText().toString().length() == 13) {
                    ((MainActivity) BriefcaseFragment.this.getActivity()).startHttpIntent(getReferalAddQuery(nameField.getText().toString() + " " + surnameField.getText().toString(), phoneField.getText().toString()), HttpIntentService.ADD_REFERAL);
                } else {
                    Toast.makeText(BriefcaseFragment.this.getActivity(), "wrong params", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }


    private void updatePersonsList() {

        listPerson = fillPersonsFromCursor(cursor);
        mUserContactsCountTextView.setText(listPerson.size() - 1 + " " + "people in your network");
        LOGI(TAG, listPerson.toString());
        cursor.close();
    }

    private ImageView createDirection(int resId) {
        ImageView resultView = new ImageView(getActivity());
        resultView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        resultView.setImageResource(resId);
        return resultView;
    }

    public static ReferalAddQuery getReferalAddQuery(String name, String phone) {
        ReferalAddQuery query = new ReferalAddQuery();
        query.params.name = name;
        query.params.phone = phone;
        return query;
    }

    private List<PersonBriefcaseEntity> fillPersonsFromCursor(Cursor cursorPersons) {
        ArrayList<PersonBriefcaseEntity> personsList = new ArrayList<>();
        AppPreferences appPreferences = new AppPreferences(getActivity().getApplicationContext());
        int userId = appPreferences.getUserId();
        String userName = appPreferences.getUserName();
        PersonBriefcaseEntity personBriefcaseEntityUser = new PersonBriefcaseEntity(userId, 0, userName, " ");
        personsList.add(personBriefcaseEntityUser);
        for (cursorPersons.moveToFirst(); !cursorPersons.isAfterLast(); cursorPersons.moveToNext()) {
            PersonBriefcaseEntity personBriefcaseEntity = new PersonBriefcaseEntity();
            personBriefcaseEntity.setId(cursorPersons.getInt(cursorPersons.getColumnIndex(MAIL_CONTACT_SERVER_ID)));
            personBriefcaseEntity.setParentId(cursorPersons.getInt(cursorPersons.getColumnIndex(MAIL_CONTACT_PARENT_ID)));
            personBriefcaseEntity.setName(cursorPersons.getString(cursorPersons.getColumnIndex(MAIL_CONTACT_NAME)));
            personBriefcaseEntity.setPhoto(cursorPersons.getString(cursorPersons.getColumnIndex(MAIL_CONTACT_IMG)));
            personsList.add(personBriefcaseEntity);
        }
        if (cursorPersons != null) {
            cursorPersons.close();
        }
        return personsList;
    }

    private GridLayout levelGenerate(final View v, final List<PersonBriefcaseEntity> listPerson) {
        int id = Integer.parseInt(((TextView) v.findViewById(R.id.briefcase_fragment_idObject)).getText().toString());
        int row = Integer.parseInt(((TextView) v.findViewById(R.id.row)).getText().toString());
        int column = Integer.parseInt(((TextView) v.findViewById(R.id.column)).getText().toString());
        List<PersonBriefcaseEntity> children = getChildrenOnParent(listPerson, id);
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
            PersonBriefcaseEntity personBriefcaseEntity = children.get(i);
            layoutPhoto = (RelativeLayout) inflater.inflate(R.layout.item_briefcase, null, false);
            RelativeLayout photoLayout = (RelativeLayout) layoutPhoto.findViewById(R.id.image_container);
            CircularImageView personPhoto = (CircularImageView) layoutPhoto.findViewById(R.id.briefcase_fragment_user_photo);
            String imagePath = (personBriefcaseEntity.getPhoto() != null && personBriefcaseEntity.getPhoto().length() > 1) ? personBriefcaseEntity.getPhoto() : "fake";
            Picasso.with(BriefcaseFragment.this.getActivity()).load(imagePath).placeholder(R.drawable.ic_launcher).into(personPhoto);
            photoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int row = Integer.parseInt(((TextView) v.findViewById(R.id.row)).getText().toString());
                    LOGD("TAG", "With - " + (row + 1) + " to - '<' " + containerLayout.getChildCount());
                    for (int i = containerLayout.getChildCount() - 1; i > row; i--) {
                        containerLayout.removeViewAt(i);
                    }
                    containerLayout.addView(levelGenerate(view, listPerson));
                }
            });

            final TextView idObject = (TextView) photoLayout.getChildAt(1);
            idObject.setText(Integer.toString(personBriefcaseEntity.getId()));
            TextView rowObject = (TextView) photoLayout.getChildAt(2);
            rowObject.setText(Integer.toString(row + 1));
            TextView columnObject = (TextView) photoLayout.getChildAt(3);
            columnObject.setText(Integer.toString(startPosition + i));
            LOGD("TAG", "id - " + personBriefcaseEntity.getId() + "; row - " + (row + 1) + "; column - " + (startPosition + i));
            ImageView imageViewInfo = (ImageView) layoutPhoto.getChildAt(1);
            imageViewInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int idPersonal = Integer.parseInt(idObject.getText().toString());
                    LOGI(TAG, "id personal " + idPersonal);
                    GetAccountPanelInfoQuery getLoggedUserInfoQuery = new GetAccountPanelInfoQuery();
                    getLoggedUserInfoQuery.params.id = idPersonal;
                    Animation mFragmentSliderFadeIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fragment_item_slide_fade_in);
                    mFragmentSliderFadeIn.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mCloseUserFragmentButton.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    Fragment fragment = UserFragment.newInstance(getLoggedUserInfoQuery, false);
                    getChildFragmentManager().beginTransaction().replace(R.id.briefcase_user_info_container_frameLayout, fragment, "UserFragmentBriefcase").commit();
                    mUserContainer.startAnimation(mFragmentSliderFadeIn);


                }
            });
            TextView text = (TextView) layoutPhoto.getChildAt(2);
            text.setText(personBriefcaseEntity.getName());
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
