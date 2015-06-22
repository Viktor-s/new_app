package me.justup.upme.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import me.justup.upme.JustUpApplication;
import me.justup.upme.MainActivity;
import me.justup.upme.R;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.dialogs.ChooseReferralDialog;
import me.justup.upme.entity.CalendarAddEventQuery;
import me.justup.upme.entity.CalendarRemoveEventQuery;
import me.justup.upme.entity.CalendarUpdateEventQuery;
import me.justup.upme.entity.PersonBriefcaseEntity;
import me.justup.upme.http.HttpIntentService;
import me.justup.upme.utils.BackAwareEditText;
import me.justup.upme.utils.CommonUtils;
import me.justup.upme.weekview.WeekView;
import me.justup.upme.weekview.WeekViewEvent;

import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_DESCRIPTION;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_END_DATETIME;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_LOCATION;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_NAME;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_OWNER_ID;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_SERVER_ID;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_SHARED_WITH;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_START_DATETIME;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_TYPE;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_IMG;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_NAME;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_PARENT_ID;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_SERVER_ID;
import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;

public class CalendarFragment extends Fragment implements View.OnClickListener, WeekView.MonthChangeListener, WeekView.EventClickListener, WeekView.EmptyViewClickListener {
    private static final String TAG = makeLogTag(CalendarFragment.class);

    private static final int START_TIME_EVENT = 1;
    private static final int DURATION_EVENT = 2;
    private static final int REMIND_EVENT = 3;

    private WeekView mWeekView = null;
    private Dialog mDialogSteTimeCalendar = null;
    private Dialog mDialogInfoEvent = null;
    private List<WeekViewEvent> mWeekViewEvents = new ArrayList<>();

    private RelativeLayout mPanelAddEvent = null;
    private LinearLayout mChooseDateLayout = null;
    private TextView mStartDateEvent = null;
    private TextView mTvStartTimeEvent = null;
    private TextView mTvDurationEvent = null;
    private TextView mTvRemindEvent = null;
    private BackAwareEditText mEtNewEventName = null;
    private BackAwareEditText mEtNewEventLocation = null;
    private BackAwareEditText mEtNewEventDescription = null;

    private Calendar mStartTimeEvent = null;
    private int mDurationEventMin;
    private int mRemindEventMin;

    private TextView mSelectWeekTextView = null;
    private TextView mSelectMonthTextView = null;

    private final DateTime currentDate = new DateTime();
    private int mCurrentWeek;
    public static LocalDateTime mFirstDayCurrentWeek = null;

    private BroadcastReceiver mReceiver = null;
    private Spinner mCalendarTypesSpinner = null;
    private Button mAddNewEventButton = null;
    private Button mChooseReferralButton = null;

    private List<PersonBriefcaseEntity> mListPerson = null;

    private static String[] MONTH = new String[]{"ЯНВАРЬ", "ФЕВРАЛЬ", "МАРТ", "АПРЕЛЬ", "МАЙ", "ИЮНЬ", "ИЮЛЬ", "АВГУСТ", "СЕНТЯБРЬ", "ОКТЯБРЬ", "НОЯБРЬ", "ДЕКАБРЬ"};

    private boolean isEventNeedUpdate = false;

    private long mCurrentEventId;
    private int mCurrentUserId;

    private ArrayList<Integer> mListSharedId = new ArrayList<>();

    private View mContentView = null;

    // Instance
    public static CalendarFragment newInstance() {
        return new CalendarFragment();
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

        mCurrentUserId = JustUpApplication.getApplication().getAppPreferences().getUserId();

        mListPerson = fillPersonsFromCursor(JustUpApplication.getApplication().getTransferActionMailContact().getCursorOfMailContact(getActivity().getApplicationContext()));

        mFirstDayCurrentWeek = new LocalDateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withDayOfWeek(DateTimeConstants.MONDAY);
        mCurrentWeek = mFirstDayCurrentWeek.getWeekOfWeekyear();
    }

    @Override
    public void onResume() {
        super.onResume();

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                listEventsForWeek(mFirstDayCurrentWeek);
            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, new IntentFilter(DBAdapter.CALENDAR_SQL_BROADCAST_INTENT));
    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(CalendarFragment.this.getActivity()).unregisterReceiver(mReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = super.onCreateView(inflater, container, savedInstanceState);

        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_calendar, container, false);
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
        mWeekView = (WeekView) mContentView.findViewById(R.id.weekView);
        mWeekView.goToHour(6);

        TextView currentDateTextView = (TextView) mContentView.findViewById(R.id.current_date_textView);
        currentDateTextView.setText(currentDate.toString("d MMMM yyyy", new Locale("ru")));

        mSelectMonthTextView = (TextView) mContentView.findViewById(R.id.select_month_textView);
        String strMonthYear = String.format("%s %d", MONTH[currentDate.getMonthOfYear()], currentDate.getYear());
        mSelectMonthTextView.setText(strMonthYear);

        mSelectWeekTextView = (TextView) mContentView.findViewById(R.id.select_week_textView);
        mSelectWeekTextView.setText(Integer.toString(mCurrentWeek) + getResources().getString(R.string.week));

        mChooseReferralButton = (Button) mContentView.findViewById(R.id.choose_referral_button);
        mChooseReferralButton.setOnClickListener(this);

        mCalendarTypesSpinner = (Spinner) mContentView.findViewById(R.id.calendar_fragment_types_spinner);
        mCalendarTypesSpinner.setAdapter(new ArrayAdapter<>(CalendarFragment.this.getActivity(), R.layout.calendar_spinner_item, CalendarEventTypes.values()));
        mCalendarTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (CalendarEventTypes.values()[position]) {
                    case REMINDER:
                        mChooseReferralButton.setVisibility(View.GONE);
                        mListSharedId.clear();

                        break;
                    case TASK:
                        mChooseReferralButton.setVisibility(View.VISIBLE);

                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        Button previousWeekButton = (Button) mContentView.findViewById(R.id.previous_week_button);
        previousWeekButton.setOnClickListener(this);

        Button nextWeekButton = (Button) mContentView.findViewById(R.id.next_week_button);
        nextWeekButton.setOnClickListener(this);

        Button addButton = (Button) mContentView.findViewById(R.id.add_button);
        addButton.setOnClickListener(this);

        /////////////////////////////// RIGHT PANEL ////////////////////////////////////////////////

        mPanelAddEvent = (RelativeLayout) mContentView.findViewById(R.id.panel_add_event);

        Button calendarItemCloseButton = (Button) mContentView.findViewById(R.id.calendar_item_close_button);
        calendarItemCloseButton.setOnClickListener(this);

        mChooseDateLayout = (LinearLayout) mContentView.findViewById(R.id.choose_date_layout);

        mStartDateEvent = (TextView) mContentView.findViewById(R.id.start_date_event);
        mStartDateEvent.setText(currentDate.toString("dd/MM/yyyy"));
        mStartDateEvent.setOnClickListener(this);

        mTvStartTimeEvent = (TextView) mContentView.findViewById(R.id.start_time_event);
        mTvStartTimeEvent.setOnClickListener(this);

        mTvDurationEvent = (TextView) mContentView.findViewById(R.id.duration_event);
        mTvDurationEvent.setOnClickListener(this);

        mTvRemindEvent = (TextView) mContentView.findViewById(R.id.remind_event);
        mTvRemindEvent.setOnClickListener(this);

        mEtNewEventName = (BackAwareEditText) mContentView.findViewById(R.id.new_event_name);
        mEtNewEventName.setBackPressedListener(new BackAwareEditText.BackPressedListener() {
            @Override
            public void onImeBack(BackAwareEditText editText) {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).hideNavBar();
                }
            }
        });

        mEtNewEventLocation = (BackAwareEditText) mContentView.findViewById(R.id.new_event_location);
        mEtNewEventLocation.setBackPressedListener(new BackAwareEditText.BackPressedListener() {
            @Override
            public void onImeBack(BackAwareEditText editText) {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).hideNavBar();
                }
            }
        });

        mEtNewEventDescription = (BackAwareEditText) mContentView.findViewById(R.id.new_event_description);
        mEtNewEventDescription.setBackPressedListener(new BackAwareEditText.BackPressedListener() {
            @Override
            public void onImeBack(BackAwareEditText editText) {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).hideNavBar();
                }
            }
        });

        mAddNewEventButton = (Button) mContentView.findViewById(R.id.add_new_event_button);
        mAddNewEventButton.setOnClickListener(this);

        /////////////////////////////// CALENDAR ///////////////////////////////////////////////////

        mWeekView.setOnEventClickListener(this);
        mWeekView.setMonthChangeListener(this);
        mWeekView.setEmptyViewClickListener(this);

        listEventsForWeek(mFirstDayCurrentWeek);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LOGD(TAG, "OnConfigurationChanged");

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LOGI(TAG, "Landscape");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            LOGI(TAG, "Portrait");
        }

        mWeekView.invalidateWeekView(true);
    }

    private List<PersonBriefcaseEntity> fillPersonsFromCursor(Cursor cursorPersons) {
        ArrayList<PersonBriefcaseEntity> personsList = new ArrayList<>();

        for (cursorPersons.moveToFirst(); !cursorPersons.isAfterLast(); cursorPersons.moveToNext()) {
            PersonBriefcaseEntity personBriefcaseEntity = new PersonBriefcaseEntity();
            personBriefcaseEntity.setId(cursorPersons.getInt(cursorPersons.getColumnIndex(MAIL_CONTACT_SERVER_ID)));
            personBriefcaseEntity.setParentId(cursorPersons.getInt(cursorPersons.getColumnIndex(MAIL_CONTACT_PARENT_ID)));
            personBriefcaseEntity.setName(cursorPersons.getString(cursorPersons.getColumnIndex(MAIL_CONTACT_NAME)));
            personBriefcaseEntity.setPhoto(cursorPersons.getString(cursorPersons.getColumnIndex(MAIL_CONTACT_IMG)));
            personsList.add(personBriefcaseEntity);
        }

        cursorPersons.close();
        return personsList;
    }

    private void listEventsForWeek(LocalDateTime startWeek) {
        String startTime = Long.toString(startWeek.toDateTime(DateTimeZone.getDefault()).getMillis() / 1000);
        LocalDateTime lastDayCurrentWeek = startWeek.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withDayOfWeek(DateTimeConstants.SUNDAY);
        String endTime = Long.toString(lastDayCurrentWeek.toDateTime(DateTimeZone.getDefault()).getMillis() / 1000);

        mWeekViewEvents.clear();

        Cursor cursorEvents = JustUpApplication.getApplication().getTransferActionEventCalendar().getCursorEventCalendarWithTimeStamp(getActivity().getApplicationContext(), startTime, endTime);

        for (cursorEvents.moveToFirst(); !cursorEvents.isAfterLast(); cursorEvents.moveToNext()) {
            long id = cursorEvents.getInt(cursorEvents.getColumnIndex(EVENT_CALENDAR_SERVER_ID));
            String name = cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_NAME));
            String description = cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_DESCRIPTION));
            String type = cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_TYPE));
            String ownerId = cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_OWNER_ID));
            String startDatetime = cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_START_DATETIME)) + "000";
            String endDatetime = cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_END_DATETIME)) + "000";
            String location = cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_LOCATION));
            String sharedWith = cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_SHARED_WITH));

            Calendar startTimeCalendar = Calendar.getInstance();
            startTimeCalendar.setTimeInMillis(Long.parseLong(startDatetime));
            Calendar endTimeCalendar = Calendar.getInstance();
            endTimeCalendar.setTimeInMillis(Long.parseLong(endDatetime));

            // WeekViewEvent eventElement = new WeekViewEvent(id, name, startTimeCalendar, endTimeCalendar);
            WeekViewEvent eventElement = new WeekViewEvent(id, name, description, type, Integer.parseInt(ownerId), startTimeCalendar, endTimeCalendar, location, sharedWith);
            mWeekViewEvents.add(eventElement);
        }

        mWeekView.notifyDatasetChanged();
        cursorEvents.close();
    }

    /////////////////////////////// DIALOG ///////////////////////////////

    public void TimePickerDialog(final int typeDialog) {
        mDialogSteTimeCalendar = new Dialog(getActivity());
        mDialogSteTimeCalendar.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogSteTimeCalendar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialogSteTimeCalendar.setContentView(R.layout.dialog_time_picker);

        Button buttonOk = (Button) mDialogSteTimeCalendar.findViewById(R.id.buttonDialogCalendarTimeOk);

        final TextView textTitleDialogTime = (TextView) mDialogSteTimeCalendar.findViewById(R.id.textTitleDialogTime);
        final NumberPicker numberPickerHours = (NumberPicker) mDialogSteTimeCalendar.findViewById(R.id.numberPickerHours);
        final NumberPicker numberPickerMinutes = (NumberPicker) mDialogSteTimeCalendar.findViewById(R.id.numberPickerMinutes);

        numberPickerHours.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPickerMinutes.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        numberPickerHours.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
        numberPickerMinutes.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });

        numberPickerHours.setMaxValue(23);
        numberPickerMinutes.setMaxValue(59);

        if (typeDialog == START_TIME_EVENT) {
            textTitleDialogTime.setText("Установите начальное время события");
            numberPickerHours.setValue(mStartTimeEvent.get(Calendar.HOUR_OF_DAY));
            numberPickerMinutes.setValue(mStartTimeEvent.get(Calendar.MINUTE));
        } else if (typeDialog == DURATION_EVENT) {
            textTitleDialogTime.setText("Установите продолжительность события");
            int minute = mDurationEventMin % 60;
            int hour = (mDurationEventMin - minute) / 60;
            numberPickerHours.setValue(hour);
            numberPickerMinutes.setValue(minute);
        } else if (typeDialog == REMIND_EVENT) {
            textTitleDialogTime.setText("Установите интервал");
            int minute = mRemindEventMin % 60;
            int hour = (mRemindEventMin - minute) / 60;
            numberPickerHours.setValue(hour);
            numberPickerMinutes.setValue(minute);
        }

        buttonOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String convertedTime = convertTimeToString(numberPickerHours.getValue(), numberPickerMinutes.getValue());

                if (typeDialog == START_TIME_EVENT) {
                    mStartTimeEvent.set(Calendar.HOUR_OF_DAY, numberPickerHours.getValue());
                    mStartTimeEvent.set(Calendar.MINUTE, numberPickerMinutes.getValue());
                    mTvStartTimeEvent.setText(convertedTime);
                    mDialogSteTimeCalendar.dismiss();

                } else if (typeDialog == DURATION_EVENT) {
                    mDurationEventMin = numberPickerHours.getValue() * 60 + numberPickerMinutes.getValue();
                    mTvDurationEvent.setText(convertedTime);
                    mDialogSteTimeCalendar.dismiss();

                } else if (typeDialog == REMIND_EVENT) {
                    mRemindEventMin = numberPickerHours.getValue() * 60 + numberPickerMinutes.getValue();
                    mTvRemindEvent.setText(convertedTime);
                    mDialogSteTimeCalendar.dismiss();

                }
            }
        });

        mDialogSteTimeCalendar.show();
    }

    public void InfoDialog(final WeekViewEvent event) {
        mDialogInfoEvent = new Dialog(getActivity());
        mDialogInfoEvent.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogInfoEvent.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialogInfoEvent.setContentView(R.layout.dialog_info_event);

        final TextView eventNameTextView = (TextView) mDialogInfoEvent.findViewById(R.id.event_name_textView);
        eventNameTextView.setText(event.getName());

        final TextView eventDescriptionTextView = (TextView) mDialogInfoEvent.findViewById(R.id.event_description_textView);
        eventDescriptionTextView.setText(event.getDescription());

        String textOwner = "";
        if (mCurrentUserId == event.getOwnerId()) {
            textOwner = "мое событие";

            String nameSharedWith = "";
            String[] array = event.getmSharedWith().split(",");
            Set<String> mySet = new HashSet<>(Arrays.asList(array));
            for (PersonBriefcaseEntity itemPerson : mListPerson) {
                if (mySet.contains(String.valueOf(itemPerson.getId()))){
                    nameSharedWith += itemPerson.getName() + " ";
                }
            }

            final TextView eventSharedWithTextView = (TextView) mDialogInfoEvent.findViewById(R.id.event_sharedwith_textView);
            eventSharedWithTextView.setVisibility(View.VISIBLE);
            eventSharedWithTextView.setText("Расшаренно : \n" + nameSharedWith);
        } else {
            for (PersonBriefcaseEntity itemPerson : mListPerson) {
                if (itemPerson.getId() == mCurrentUserId) {
                    textOwner = "событие назначенно " + itemPerson.getName();
                    break;
                }
            }
        }

        final TextView eventOwnerTextView = (TextView) mDialogInfoEvent.findViewById(R.id.event_owner_textView);
        eventOwnerTextView.setText("Владелец : " + textOwner);

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", new Locale("ru"));
        String strStartTime = sdf.format(event.getStartTime().getTime());
        String strEndTime = sdf.format(event.getEndTime().getTime());

        final TextView eventTimeStartEndTextView = (TextView) mDialogInfoEvent.findViewById(R.id.event_time_start_end_textView);
        eventTimeStartEndTextView.setText(strStartTime + " - " + strEndTime);

        final TextView eventLocationTextView = (TextView) mDialogInfoEvent.findViewById(R.id.event_location_textView);
        eventLocationTextView.setText("Место события : " + event.getLocation());

        final Button cancelButton = (Button) mDialogInfoEvent.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogInfoEvent.dismiss();
            }
        });
        final Button editButton = (Button) mDialogInfoEvent.findViewById(R.id.edit_button);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEventNeedUpdate = true;
                // if (!isEventNeedUpdate) {
                mAddNewEventButton.setText("Изменить");
                mCurrentEventId = event.getmId();
                Calendar time = event.getStartTime();
                mStartTimeEvent = time;

                Animation mFragmentSliderFadeIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fragment_item_slide_fade_in);
                mPanelAddEvent.setVisibility(View.VISIBLE);
                mPanelAddEvent.startAnimation(mFragmentSliderFadeIn);

                mChooseDateLayout.setVisibility(View.VISIBLE);

                mStartDateEvent.setText(String.format("%02d/%02d/%d", time.get(Calendar.DAY_OF_MONTH), time.get(Calendar.MONTH) + 1, time.get(Calendar.YEAR)));
                mTvStartTimeEvent.setText(String.format("%02d:%02d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE)));

                mDurationEventMin = (int) (event.getEndTime().getTimeInMillis() - time.getTimeInMillis()) / (1000 * 60);
                int minute = mDurationEventMin % 60;
                int hour = (mDurationEventMin - minute) / 60;
                mTvDurationEvent.setText(convertTimeToString(hour, minute));

                mEtNewEventName.setText(event.getName());
                mEtNewEventDescription.setText(event.getDescription());
                mEtNewEventLocation.setText(event.getLocation());

                mCalendarTypesSpinner.setVisibility(View.GONE);
                mListSharedId.clear();
                if (event.getType().equals("task")) {
                    mChooseReferralButton.setVisibility(View.VISIBLE);
                    if (!event.getmSharedWith().equals("")) {
                        String[] array = event.getmSharedWith().split(",");

                        for (String i : array) {
                            mListSharedId.add(Integer.parseInt(i));
                        }
                    }
                } else {
                    mChooseReferralButton.setVisibility(View.GONE);
                }

                mDialogInfoEvent.dismiss();
            }
        });

        final Button deleteButton = (Button) mDialogInfoEvent.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteConfirmation(event.getmId());
            }
        });

        if (event.getOwnerId() != mCurrentUserId) {
            deleteButton.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
        }

        mDialogInfoEvent.show();
    }

    public void deleteConfirmation(final long id) {
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle("Удалить событие ?");
        // ad.setMessage("сообщение");
        ad.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                // Toast.makeText(getActivity(), "Удалено", Toast.LENGTH_LONG).show();
                CalendarRemoveEventQuery calendarRemoveEventQuery = new CalendarRemoveEventQuery();
                calendarRemoveEventQuery.params.id = Long.toString(id);

                // Delete Calendar Event
                JustUpApplication.getApplication().getTransferActionEventCalendar().deleteCalendarEvent(getActivity().getApplicationContext(), id);

                ((MainActivity) getActivity()).startHttpIntent(calendarRemoveEventQuery, HttpIntentService.CALENDAR_REMOVE_EVENT);
                mDialogInfoEvent.cancel();
            }
        });

        ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(getActivity(), "Отмена удаления", Toast.LENGTH_LONG).show();
            }
        });

        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(getActivity(), "Ничего не выбрано", Toast.LENGTH_LONG).show();
            }
        });

        ad.show();
    }

    public void datePickerDialog(String strDate) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_date_picker, null, false);

        final DatePicker myDatePicker = (DatePicker) view.findViewById(R.id.myDatePicker);
        myDatePicker.setCalendarViewShown(true);

        Calendar chooseDate = convertStringToDate(strDate);
        myDatePicker.updateDate(chooseDate.get(Calendar.YEAR), chooseDate.get(Calendar.MONTH), chooseDate.get(Calendar.DAY_OF_MONTH));

        new AlertDialog.Builder(getActivity()).setView(view)
                .setTitle("Выберите дату")
                .setPositiveButton("Установить дату", new DialogInterface.OnClickListener() {

                    @TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {
                        int day = myDatePicker.getDayOfMonth();
                        int month = myDatePicker.getMonth();
                        int year = myDatePicker.getYear();

                        mStartTimeEvent.set(Calendar.DAY_OF_MONTH, day);
                        mStartTimeEvent.set(Calendar.MONTH, month);
                        mStartTimeEvent.set(Calendar.YEAR, year);

                        mStartDateEvent.setText(String.format("%02d/%02d/%d", day, month + 1, year));

                        dialog.cancel();
                    }
                }).show();
    }

    public static String convertTimeToString(int hours, int minutes) {
        return String.format("%02d", hours) + ":" + String.format("%02d", minutes);
    }

    public static Calendar convertStringToDate(String strDate) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("ru"));

        try {
            calendar.setTime(sdf.parse(strDate));
        } catch (ParseException e) {
            LOGE(TAG, e.getMessage());
        }

        return calendar;
    }

    private String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH) + 1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        InfoDialog(event);
    }

    @Override
    public void onEmptyViewClicked(Calendar time) {
        mChooseDateLayout.setVisibility(View.GONE);
        CreateLayoutForNewEvent(time);
    }

    private void CreateLayoutForNewEvent(Calendar time) {
        mEtNewEventName.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEtNewEventName, InputMethodManager.SHOW_IMPLICIT);

        mCalendarTypesSpinner.setVisibility(View.VISIBLE);

        isEventNeedUpdate = false;
        mAddNewEventButton.setText("Добавить");

        time.set(Calendar.MINUTE, 0); // task #327 по наименьшему значению
        mStartTimeEvent = time;

        Animation mFragmentSliderFadeIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fragment_item_slide_fade_in);
        mPanelAddEvent.setVisibility(View.VISIBLE);
        mPanelAddEvent.startAnimation(mFragmentSliderFadeIn);

        mStartDateEvent.setText(String.format("%02d/%02d/%d", time.get(Calendar.DAY_OF_MONTH), time.get(Calendar.MONTH) + 1, time.get(Calendar.YEAR)));
        mTvStartTimeEvent.setText(String.format("%02d:%02d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE)));

        mDurationEventMin = 30;
        mTvDurationEvent.setText("00:30");

        mRemindEventMin = 30;
        mTvRemindEvent.setText("00:30");

        mEtNewEventName.setText("");
        mEtNewEventDescription.setText("");
        mEtNewEventLocation.setText("");

        mCalendarTypesSpinner.setSelection(0);
        mListSharedId.clear();
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        LOGD("TAG", "List<WeekViewEvent>");

        return mWeekViewEvents;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_button:
                mChooseDateLayout.setVisibility(View.VISIBLE);
                CreateLayoutForNewEvent(Calendar.getInstance());

                break;
            case R.id.previous_week_button:
                mFirstDayCurrentWeek = mFirstDayCurrentWeek.minusDays(Calendar.DAY_OF_WEEK);
                LOGD(TAG, "Previous_week_button : " + mFirstDayCurrentWeek.toString());

                mWeekView.goToDate(mFirstDayCurrentWeek.toDateTime(DateTimeZone.getDefault()).toGregorianCalendar());
                mSelectWeekTextView.setText(Integer.toString(mFirstDayCurrentWeek.getWeekOfWeekyear()) + getResources().getString(R.string.week));
                String strMonthYearPrev = String.format("%s %d", MONTH[mFirstDayCurrentWeek.getMonthOfYear() - 1], mFirstDayCurrentWeek.getYear());
                mSelectMonthTextView.setText(strMonthYearPrev);
                listEventsForWeek(mFirstDayCurrentWeek);
                ((MainActivity) getActivity()).startHttpIntent(MainActivity.getEventCalendarQuery(mFirstDayCurrentWeek), HttpIntentService.CALENDAR_PART);

                break;
            case R.id.next_week_button:
                mFirstDayCurrentWeek = mFirstDayCurrentWeek.plusDays(Calendar.DAY_OF_WEEK);
                LOGD(TAG, "Previous_week_button : " + mFirstDayCurrentWeek.toString());
                mWeekView.goToDate(mFirstDayCurrentWeek.toDateTime(DateTimeZone.getDefault()).toGregorianCalendar());
                mSelectWeekTextView.setText(Integer.toString(mFirstDayCurrentWeek.getWeekOfWeekyear()) + getResources().getString(R.string.week));
                String strMonthYearNext = String.format("%s %d", MONTH[mFirstDayCurrentWeek.getMonthOfYear() - 1], mFirstDayCurrentWeek.getYear());
                mSelectMonthTextView.setText(strMonthYearNext);
                listEventsForWeek(mFirstDayCurrentWeek);
                ((MainActivity) getActivity()).startHttpIntent(MainActivity.getEventCalendarQuery(mFirstDayCurrentWeek), HttpIntentService.CALENDAR_PART);

                break;
            case R.id.calendar_item_close_button:
                mPanelAddEvent.setVisibility(View.GONE);

                break;
            case R.id.start_date_event:
                datePickerDialog(mStartDateEvent.getText().toString());

                break;
            case R.id.start_time_event:
                TimePickerDialog(START_TIME_EVENT);

                break;
            case R.id.duration_event:
                TimePickerDialog(DURATION_EVENT);

                break;
            case R.id.remind_event:
                TimePickerDialog(REMIND_EVENT);

                break;
            case R.id.choose_referral_button:
                ChooseReferralDialog chooseReferralDialog = ChooseReferralDialog.newInstance(mListSharedId);
                chooseReferralDialog.show(getChildFragmentManager(), "choose_referral_dialog");

                break;
            case R.id.add_new_event_button:
                // if (startTimeEvent.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                //     Toast.makeText(getActivity(), "Событие не может быть установлено в прошлом", Toast.LENGTH_SHORT).show();
                //     break;
                // }

                Calendar endTimeEvent = (Calendar) mStartTimeEvent.clone();
                String eventName = mEtNewEventName.getText().toString();
                String eventDescription = mEtNewEventDescription.getText().toString();
                String eventLocation = mEtNewEventLocation.getText().toString();

                int minute = mDurationEventMin % 60;
                int hour = (mDurationEventMin - minute) / 60;

                if (hour == 0 && minute == 0) {
                    Toast.makeText(getActivity(), "Не установленна продолжительность события", Toast.LENGTH_SHORT).show();

                    break;
                }

                endTimeEvent.add(Calendar.HOUR_OF_DAY, hour);
                endTimeEvent.add(Calendar.MINUTE, minute);
                if (!isEventNeedUpdate) {
                    // mAddNewEventButton.setText("Добавить");
                    CalendarAddEventQuery calendarAddEventsQuery = new CalendarAddEventQuery();
                    calendarAddEventsQuery.params.name = CommonUtils.convertToUTF8(eventName);
                    calendarAddEventsQuery.params.description = CommonUtils.convertToUTF8(eventDescription);
                    calendarAddEventsQuery.params.type = mCalendarTypesSpinner.getSelectedItem().toString();
                    calendarAddEventsQuery.params.location = CommonUtils.convertToUTF8(eventLocation);
                    calendarAddEventsQuery.params.start = String.valueOf(mStartTimeEvent.getTimeInMillis() / 1000);
                    calendarAddEventsQuery.params.end = String.valueOf(endTimeEvent.getTimeInMillis() / 1000);
                    // calendarAddEventsQuery.params.duration = mDurationEventMin; !!!

                    if (mListSharedId.size() == 0)
                        calendarAddEventsQuery.params.shared_with = "";
                    else {
                        String sharedWith = mListSharedId.toString().replaceAll("(^\\[|\\]$)", "").replace(", ", ",");
                        calendarAddEventsQuery.params.shared_with = sharedWith;
                    }

                    Log.d(TAG, "CalendarAddEventsQuery : " + calendarAddEventsQuery.toString());
                    ((MainActivity) getActivity()).startHttpIntent(calendarAddEventsQuery, HttpIntentService.CALENDAR_ADD_EVENT);
                    mPanelAddEvent.setVisibility(View.GONE);
                } else {
                    // mAddNewEventButton.setText("Обновить");
                    CalendarUpdateEventQuery calendarUpdateEventQuery = new CalendarUpdateEventQuery();
                    calendarUpdateEventQuery.params.id = mCurrentEventId;
                    calendarUpdateEventQuery.params.name = CommonUtils.convertToUTF8(eventName);
                    calendarUpdateEventQuery.params.description = CommonUtils.convertToUTF8(eventDescription);
                    // calendarUpdateEventQuery.params.type = mCalendarTypesSpinner.getSelectedItem().toString();
                    calendarUpdateEventQuery.params.location = CommonUtils.convertToUTF8(eventLocation);
                    calendarUpdateEventQuery.params.start = String.valueOf(mStartTimeEvent.getTimeInMillis() / 1000);
                    calendarUpdateEventQuery.params.end = String.valueOf(endTimeEvent.getTimeInMillis() / 1000);
                    calendarUpdateEventQuery.params.shared_with = mListSharedId.toString().replaceAll("(^\\[|\\]$)", "").replace(", ", ",");
                    ((MainActivity) getActivity()).startHttpIntent(calendarUpdateEventQuery, HttpIntentService.CALENDAR_UPDATE_EVENT);
                    isEventNeedUpdate = false;
                    mPanelAddEvent.setVisibility(View.GONE);
                }

                break;
        }
    }

    private enum CalendarEventTypes {
        REMINDER("reminder"),
        TASK("task");

        private final String value;

        CalendarEventTypes(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public void setPersonIdForNewEvent(ArrayList<Integer> listId) {
        mListSharedId = listId;
    }
}

