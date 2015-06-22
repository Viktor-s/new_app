package me.justup.upme.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Typeface;
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
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.List;

import me.justup.upme.JustUpApplication;
import me.justup.upme.MainActivity;
import me.justup.upme.R;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.entity.GetAccountPanelInfoQuery;
import me.justup.upme.entity.PersonBriefcaseEntity;
import me.justup.upme.entity.ReferralAddQuery;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.http.HttpIntentService;
import me.justup.upme.utils.CommonUtils;
import me.justup.upme.view.dashboard.TileUtils;

import static me.justup.upme.db.DBHelper.MAIL_CONTACT_IMG;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_NAME;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_PARENT_ID;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_SERVER_ID;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_STATUS;
import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;

public class BriefcaseFragment extends Fragment {
    private static final String TAG = makeLogTag(BriefcaseFragment.class);

    private LinearLayout mContainerLayout = null;
    private RelativeLayout mPhotoLayout = null;
    private FrameLayout mUserContainer = null;
    private TableLayout mTableLayout = null;

    private TextView mUserContactsCountTextView = null;
    private Button mCloseUserFragmentButton = null;

    private List<PersonBriefcaseEntity> mListPerson = null;
    private BroadcastReceiver mReceiver = null;

    private Animation mFragmentSliderFadeIn = null;
    private Animation mFragmentSliderOut = null;

    private int mLastChooseItem;
    private int mUserId;
    private int mTotalItemCount;

    private View mContentView = null;
    private View viewId = null;

    // Instance
    public static BriefcaseFragment newInstance() {
        return new BriefcaseFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Link : http://stackoverflow.com/questions/11182180/understanding-fragments-setretaininstanceboolean
        setRetainInstance(false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mFragmentSliderFadeIn = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fragment_item_slide_fade_in);

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

        mUserId = JustUpApplication.getApplication().getAppPreferences().getUserId();
    }

    @Override
    public void onResume() {
        super.onResume();
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mLastChooseItem = -1;
                updatePersonsList();
                mContainerLayout.removeAllViews();
                mContainerLayout.addView(levelGenerate(mPhotoLayout, mListPerson));
            }
        };

        LocalBroadcastManager.getInstance(BriefcaseFragment.this.getActivity()).registerReceiver(mReceiver, new IntentFilter(DBAdapter.MAIL_SQL_BROADCAST_INTENT));
    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(BriefcaseFragment.this.getActivity()).unregisterReceiver(mReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = super.onCreateView(inflater, container, savedInstanceState);

        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_briefcase, container, false);
        }

        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init UI
        if (getActivity() != null) {
            initUI();
        }
    }

    private void initUI(){
        mTableLayout = (TableLayout) mContentView.findViewById(R.id.user_briefcase_tableLayout);

        TextView mObjectIdTextView = (TextView) mContentView.findViewById(R.id.briefcase_fragment_idObject);
        TextView mUserNameTextView = (TextView) mContentView.findViewById(R.id.briefcase_fragment_user_name);

        mUserContactsCountTextView = (TextView) mContentView.findViewById(R.id.briefcase_fragment_user_contacts_count);
        mUserContainer = (FrameLayout) mContentView.findViewById(R.id.briefcase_user_info_container_frameLayout);

        mObjectIdTextView.setText(String.valueOf(JustUpApplication.getApplication().getAppPreferences().getUserId()));
        mUserNameTextView.setText(JustUpApplication.getApplication().getAppPreferences().getUserName());

        updatePersonsList();

        mContainerLayout = (LinearLayout) mContentView.findViewById(R.id.containerLayout);
        mPhotoLayout = (RelativeLayout) mContentView.findViewById(R.id.photo_main);
        mContainerLayout.addView(levelGenerate(mPhotoLayout, mListPerson));

        mCloseUserFragmentButton = (Button) mContentView.findViewById(R.id.briefcase_close_button);
        mCloseUserFragmentButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mUserContainer.startAnimation(mFragmentSliderOut);
            }
        });

        final LinearLayout addUserContainer = (LinearLayout) mContentView.findViewById(R.id.add_user_container);
        addUserContainer.setVisibility(View.GONE);

        final TextView nameField = (TextView) mContentView.findViewById(R.id.new_user_name);
        final TextView surnameField = (TextView) mContentView.findViewById(R.id.new_user_surname);
        final TextView phoneField = (TextView) mContentView.findViewById(R.id.new_user_phone);

        Button addNewUserButton = (Button) mContentView.findViewById(R.id.add_new_user_button);
        addNewUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addUserContainer.getVisibility() == View.GONE) {
                    addUserContainer.setVisibility(View.VISIBLE);
                    nameField.setText("");
                    surnameField.setText("");
                    phoneField.setText("");

                } else {
                    addUserContainer.setVisibility(View.GONE);

                }
            }
        });

        Button sendCreateUserButton = (Button) mContentView.findViewById(R.id.new_user_send_button);
        sendCreateUserButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                addUserContainer.setVisibility(View.GONE);

                String name = nameField.getText().toString();
                String surname = surnameField.getText().toString();
                String phone = phoneField.getText().toString();

                if (name.length() > 1 && phone.length() > 10) {
                    ((MainActivity) BriefcaseFragment.this.getActivity()).startHttpIntent(getReferralAddQuery(CommonUtils.convertToUTF8(name + " " + surname), phone), HttpIntentService.ADD_REFERRAL);
                } else {
                    Toast.makeText(BriefcaseFragment.this.getActivity(), "Неверно заполненны поля !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageView userPhoto = (ImageView) mContentView.findViewById(R.id.briefcase_fragment_user_photo);
        String imageUrl = JustUpApplication.getApplication().getAppPreferences().getUserAvatarUrl();
        if (imageUrl != null) {
            ApiWrapper.loadImage(imageUrl, userPhoto);
        }
    }

    public List<PersonBriefcaseEntity> getChildrenOnParent(List<PersonBriefcaseEntity> sourceList, int id) {
        List<PersonBriefcaseEntity> resultList = new ArrayList<>();

        for (PersonBriefcaseEntity person : sourceList) {
            if (person.getParentId() == id) {
                resultList.add(person);
            }
        }

        return resultList;
    }

    private void updatePersonsList() {
        mListPerson = fillPersonsFromCursor(JustUpApplication.getApplication().getTransferActionMailContact().getCursorOfMailContact(getActivity().getApplicationContext()));
        mUserContactsCountTextView.setText(mListPerson.size() - 1 + " " + "people in your network");
        LOGI(TAG, mListPerson.toString());
    }

    private ImageView createDirection(int resId) {
        ImageView resultView = new ImageView(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        resultView.setLayoutParams(layoutParams);
        resultView.setImageResource(resId);

        return resultView;
    }

    public static ReferralAddQuery getReferralAddQuery(String name, String phone) {
        ReferralAddQuery query = new ReferralAddQuery();
        query.params.name = name;
        query.params.phone = phone;

        return query;
    }

    private List<PersonBriefcaseEntity> fillPersonsFromCursor(Cursor cursorPersons) {
        ArrayList<PersonBriefcaseEntity> personsList = new ArrayList<>();
        int userId = JustUpApplication.getApplication().getAppPreferences().getUserId();
        String userName = JustUpApplication.getApplication().getAppPreferences().getUserName();
        PersonBriefcaseEntity personBriefcaseEntityUser = new PersonBriefcaseEntity(userId, 0, userName, " ");
        personsList.add(personBriefcaseEntityUser);

        for (cursorPersons.moveToFirst(); !cursorPersons.isAfterLast(); cursorPersons.moveToNext()) {
            PersonBriefcaseEntity personBriefcaseEntity = new PersonBriefcaseEntity();
            personBriefcaseEntity.setId(cursorPersons.getInt(cursorPersons.getColumnIndex(MAIL_CONTACT_SERVER_ID)));
            personBriefcaseEntity.setParentId(cursorPersons.getInt(cursorPersons.getColumnIndex(MAIL_CONTACT_PARENT_ID)));
            personBriefcaseEntity.setName(cursorPersons.getString(cursorPersons.getColumnIndex(MAIL_CONTACT_NAME)));
            personBriefcaseEntity.setPhoto(cursorPersons.getString(cursorPersons.getColumnIndex(MAIL_CONTACT_IMG)));
            personBriefcaseEntity.setStatus(cursorPersons.getInt(cursorPersons.getColumnIndex(MAIL_CONTACT_STATUS)));
            personsList.add(personBriefcaseEntity);
        }

        if (cursorPersons != null) {
            cursorPersons.close();
        }

        return personsList;
    }

    private GridLayout levelGenerate(final View v, final List<PersonBriefcaseEntity> listPersonInner) {
        int id = Integer.parseInt(((TextView) v.findViewById(R.id.briefcase_fragment_idObject)).getText().toString());
        int row = Integer.parseInt(((TextView) v.findViewById(R.id.row)).getText().toString());
        int column = Integer.parseInt(((TextView) v.findViewById(R.id.column)).getText().toString());

        List<PersonBriefcaseEntity> children = getChildrenOnParent(listPersonInner, id);

        int countChildren = children.size();
        LOGD(TAG, "Сount children : " + countChildren);

        // Definition of the first cell to fill
        int x = (int) Math.round(countChildren / 2 - 0.1);
        LOGD(TAG, "X : " + x);

        int startPosition = (x >= column) ? 0 : column - x;
        LOGD(TAG, "START POSITION : " + startPosition);

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

                for (int j = startPosition + 1; j < countChildren - 1; j++) {
                    gridLayout.addView(createDirection(R.drawable.p234));
                }

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
                    if (startPosition + j - 1 != column) {
                        gridLayout.addView(createDirection(R.drawable.p234));
                    }else {
                        gridLayout.addView(createDirection(R.drawable.p1234));
                    }
                }

                gridLayout.addView(createDirection(R.drawable.p34));
            }
        }

        RelativeLayout briefcaseItemLayout;
        LayoutInflater inflater = LayoutInflater.from(v.getContext());
        for (int i = 0; i < countChildren; i++) {
            PersonBriefcaseEntity personBriefcaseEntity = children.get(i);

            if(JustUpApplication.getScreenDensityDpi()==240){ // Sony Z
                briefcaseItemLayout = (RelativeLayout) inflater.inflate(R.layout.item_briefcase_sony_z, null, false);
            }else {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mTableLayout.getLayoutParams();
                layoutParams.setMargins(TileUtils.dpToPx((int) getResources().getDimension(R.dimen.base10dp720sw), getActivity().getApplicationContext()), 0, 0 ,0);

                mTableLayout.setLayoutParams(layoutParams);
                briefcaseItemLayout = (RelativeLayout) inflater.inflate(R.layout.item_briefcase, null, false);
            }

            ImageView ellipsisImageView = (ImageView) briefcaseItemLayout.findViewById(R.id.briefcase_ellipsis_imageView);
            if (checkListHaveObjectWithValue(listPersonInner, personBriefcaseEntity.getId())) {
                ellipsisImageView.setVisibility(View.VISIBLE);
            } else {
                ellipsisImageView.setVisibility(View.GONE);
            }

            RelativeLayout photoLayoutMain = (RelativeLayout) briefcaseItemLayout.findViewById(R.id.r_layout);
            RelativeLayout photoLayoutInner = (RelativeLayout) briefcaseItemLayout.findViewById(R.id.image_container);


            TextView text = (TextView) briefcaseItemLayout.getChildAt(2);
            text.setText(personBriefcaseEntity.getName());

            ImageView personPhoto = (ImageView) briefcaseItemLayout.findViewById(R.id.briefcase_fragment_user_photo);
            String imagePath = (personBriefcaseEntity.getPhoto() != null && personBriefcaseEntity.getPhoto().length() > 1) ? personBriefcaseEntity.getPhoto() : null;

            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);

            if (imagePath == null) {
                if (personBriefcaseEntity.getStatus() != 1) {
                    // Person is disabled
                    TextDrawable drawable = TextDrawable.builder().beginConfig()
                            .textColor(Color.WHITE)
                            .withBorder(4)
                            .useFont(Typeface.SANS_SERIF)
                            .toUpperCase()
                            .endConfig()
                            .buildRound(Character.toString((personBriefcaseEntity.getName()).charAt(0)), Color.LTGRAY);

                    personPhoto.setImageDrawable(drawable);

                } else {
                    ColorGenerator generator = ColorGenerator.MATERIAL; // Or use DEFAULT
                    int color = generator.getColor(personBriefcaseEntity.getName());

                    TextDrawable drawable = TextDrawable.builder().beginConfig()
                            .withBorder(4)
                            .useFont(Typeface.SANS_SERIF)
                            .toUpperCase()
                            .endConfig()
                            .buildRound(Character.toString((personBriefcaseEntity.getName()).charAt(0)), color);

                    personPhoto.setImageDrawable(drawable);
                }

            } else {
                // Person is disabled
                if (personBriefcaseEntity.getStatus() != 1) {
                    personPhoto.setColorFilter(filter);
                }

                ApiWrapper.loadImage(imagePath, personPhoto);
            }

            final TextView itemId = (TextView) photoLayoutInner.getChildAt(1);
            itemId.setText(Integer.toString(personBriefcaseEntity.getId()));

            final TextView parentId = (TextView) photoLayoutInner.getChildAt(4);
            parentId.setText(Integer.toString(personBriefcaseEntity.getParentId()));

            photoLayoutMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int containerChildCount = mContainerLayout.getChildCount();
                    final int itemID = Integer.parseInt(itemId.getText().toString());

                    LOGD(TAG, "ContainerChildCount :" + containerChildCount + ", LastChooseItem :" + mLastChooseItem + ", ItemID : " + itemID);

                    if (mLastChooseItem != itemID) {
                        if (mTotalItemCount > 1 && Integer.parseInt(parentId.getText().toString()) == mUserId) {
                            mContainerLayout.removeAllViews();
                            mContainerLayout.addView(levelGenerate(mPhotoLayout, listPersonInner));
                            mContainerLayout.addView(levelGenerate(view, listPersonInner));
                            mTotalItemCount = 1;

                        } else {
                            int row = Integer.parseInt(((TextView) v.findViewById(R.id.row)).getText().toString());
                            for (int i = containerChildCount - 1; i > row; i--) {
                                mContainerLayout.removeViewAt(i);
                            }

                            viewId = levelGenerate(view, listPersonInner);
                            mContainerLayout.addView(viewId);

                            mTotalItemCount = containerChildCount;
                        }

                        mLastChooseItem = itemID;
                        view.findViewById(R.id.briefcase_ellipsis_imageView).setVisibility(View.GONE);

                    } else {
                        if (mTotalItemCount > 1) {
                            for (int i = containerChildCount - 1; i > Integer.parseInt(((TextView) v.findViewById(R.id.row)).getText().toString()); i--) {
                                mContainerLayout.removeViewAt(i);
                            }

                            mTotalItemCount = containerChildCount;
                            view.findViewById(R.id.briefcase_ellipsis_imageView).setVisibility(View.VISIBLE);
                        }

                        mLastChooseItem = -1;

                        LOGD(TAG, "TotalItemCount : " + mTotalItemCount);
                    }
                }
            });

            TextView rowObject = (TextView) photoLayoutInner.getChildAt(2);
            rowObject.setText(Integer.toString(row + 1));

            TextView columnObject = (TextView) photoLayoutInner.getChildAt(3);
            columnObject.setText(Integer.toString(startPosition + i));

            LOGD(TAG, "Id : " + personBriefcaseEntity.getId() + "; row : " + (row + 1) + "; column : " + (startPosition + i));

            ImageView imageViewInfo = (ImageView) briefcaseItemLayout.getChildAt(1);
            imageViewInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int idPersonal = Integer.parseInt(itemId.getText().toString());
                    LOGI(TAG, "Id personal : " + idPersonal);

                    GetAccountPanelInfoQuery getLoggedUserInfoQuery = new GetAccountPanelInfoQuery();
                    getLoggedUserInfoQuery.params.user_id = idPersonal;
                    Animation mFragmentSliderFadeIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fragment_item_slide_fade_in);
                    mFragmentSliderFadeIn.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) { }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mCloseUserFragmentButton.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) { }
                    });

                    Fragment fragment = UserFragment.newInstance(getLoggedUserInfoQuery, false);
                    getChildFragmentManager().beginTransaction().replace(R.id.briefcase_user_info_container_frameLayout, fragment, "UserFragmentBriefcase").commit();
                    mUserContainer.startAnimation(mFragmentSliderFadeIn);

                }
            });

            if (i == 0) {
                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.columnSpec = GridLayout.spec(startPosition);
                param.rowSpec = GridLayout.spec(row + 1);
                briefcaseItemLayout.setLayoutParams(param);
            }

            gridLayout.addView(briefcaseItemLayout);
        }

        return gridLayout;
    }

    private boolean checkListHaveObjectWithValue(List<PersonBriefcaseEntity> list, int id) {
        for (PersonBriefcaseEntity object : list) {
            if (object.getParentId() == id) {
                return true;
            }
        }

        return false;
    }

}